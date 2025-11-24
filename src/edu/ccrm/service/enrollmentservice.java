package edu.ccrm.service;

import edu.ccrm.domain.*;
import edu.ccrm.exception.*;

import java.util.List;
import java.util.Optional;

public interface EnrollmentService {
    void enrollStudent(Long studentId, String courseCode) 
        throws StudentNotFoundException, CourseNotFoundException, 
               DuplicateEnrollmentException, MaxCreditLimitExceededException;
    
    void unenrollStudent(Long studentId, String courseCode) 
        throws EnrollmentNotFoundException;
    
    void recordGrade(Long studentId, String courseCode, Grade grade) 
        throws EnrollmentNotFoundException;
    
    void updateGrade(Long studentId, String courseCode, Grade grade) 
        throws EnrollmentNotFoundException;
    
    Optional<Enrollment> findEnrollment(Long studentId, String courseCode);
    List<Enrollment> getStudentEnrollments(Long studentId);
    List<Enrollment> getCourseEnrollments(String courseCode);
    List<Enrollment> getAllEnrollments();
    
    // Business logic
    boolean isStudentEnrolled(Long studentId, String courseCode);
    int getStudentCreditHours(Long studentId);
    double calculateStudentGPA(Long studentId);
}

// File: src/edu/ccrm/service/EnrollmentServiceImpl.java
package edu.ccrm.service;

import edu.ccrm.domain.*;
import edu.ccrm.exception.*;

import java.util.*;
import java.util.stream.Collectors;

public class EnrollmentServiceImpl implements EnrollmentService {
    private final List<Enrollment> enrollments;
    private final StudentService studentService;
    private final CourseService courseService;
    
    // Business rules
    private static final int MAX_CREDITS_PER_SEMESTER = 18;
    
    public EnrollmentServiceImpl(StudentService studentService, CourseService courseService) {
        this.enrollments = new ArrayList<>();
        this.studentService = Objects.requireNonNull(studentService);
        this.courseService = Objects.requireNonNull(courseService);
    }
    
    @Override
    public void enrollStudent(Long studentId, String courseCode) 
        throws StudentNotFoundException, CourseNotFoundException, 
               DuplicateEnrollmentException, MaxCreditLimitExceededException {
        
        // Validate student exists and is active
        Optional<Student> optStudent = studentService.findStudentById(studentId);
        if (optStudent.isEmpty()) {
            throw new StudentNotFoundException("Student with ID " + studentId + " not found");
        }
        
        Student student = optStudent.get();
        if (student.getStatus() != StudentStatus.ACTIVE) {
            throw new IllegalStateException("Cannot enroll inactive student");
        }
        
        // Validate course exists and is active
        Optional<Course> optCourse = courseService.findCourseByCode(courseCode);
        if (optCourse.isEmpty()) {
            throw new CourseNotFoundException("Course with code " + courseCode + " not found");
        }
        
        Course course = optCourse.get();
        if (!course.isActive()) {
            throw new IllegalStateException("Cannot enroll in inactive course");
        }
        
        // Check for duplicate enrollment
        if (isStudentEnrolled(studentId, courseCode)) {
            throw new DuplicateEnrollmentException("Student is already enrolled in course " + courseCode);
        }
        
        // Check credit limit
        int currentCredits = getStudentCreditHours(studentId);
        if (currentCredits + course.getCredits() > MAX_CREDITS_PER_SEMESTER) {
            throw new MaxCreditLimitExceededException(
                String.format("Enrollment would exceed credit limit. Current: %d, Limit: %d", 
                    currentCredits + course.getCredits(), MAX_CREDITS_PER_SEMESTER));
        }
        
        // Create enrollment
        Enrollment enrollment = new Enrollment(student, course);
        enrollments.add(enrollment);
        
        // Update student's enrolled courses
        student.addCourse(courseCode);
    }
    
    @Override
    public void unenrollStudent(Long studentId, String courseCode) throws EnrollmentNotFoundException {
        Optional<Enrollment> optEnrollment = findEnrollment(studentId, courseCode);
        if (optEnrollment.isEmpty()) {
            throw new EnrollmentNotFoundException("Enrollment not found for student " + studentId + 
                " in course " + courseCode);
        }
        
        Enrollment enrollment = optEnrollment.get();
        
        // Check if grades have been assigned
        if (enrollment.hasGrade()) {
            enrollment.withdraw(); // Mark as withdrawn but keep record
        } else {
            enrollments.remove(enrollment); // Remove completely if no grades
        }
        
        // Update student's enrolled courses
        Student student = enrollment.getStudent();
        student.removeCourse(courseCode);
    }
    
    @Override
    public void recordGrade(Long studentId, String courseCode, Grade grade) throws EnrollmentNotFoundException {
        Optional<Enrollment> optEnrollment = findEnrollment(studentId, courseCode);
        if (optEnrollment.isEmpty()) {
            throw new EnrollmentNotFoundException("Enrollment not found for student " + studentId + 
                " in course " + courseCode);
        }
        
        Enrollment enrollment = optEnrollment.get();
        enrollment.assignGrade(grade);
    }
    
    @Override
    public void updateGrade(Long studentId, String courseCode, Grade grade) throws EnrollmentNotFoundException {
        Optional<Enrollment> optEnrollment = findEnrollment(studentId, courseCode);
        if (optEnrollment.isEmpty()) {
            throw new EnrollmentNotFoundException("Enrollment not found for student " + studentId + 
                " in course " + courseCode);
        }
        
        Enrollment enrollment = optEnrollment.get();
        enrollment.updateGrade(grade);
    }
    
    @Override
    public Optional<Enrollment> findEnrollment(Long studentId, String courseCode) {
        return enrollments.stream()
            .filter(e -> e.getStudent().getId().equals(studentId) && 
                        e.getCourse().getCode().equals(courseCode))
            .findFirst();
    }
    
    @Override
    public List<Enrollment> getStudentEnrollments(Long studentId) {
        return enrollments.stream()
            .filter(e -> e.getStudent().getId().equals(studentId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Enrollment> getCourseEnrollments(String courseCode) {
        return enrollments.stream()
            .filter(e -> e.getCourse().getCode().equals(courseCode))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Enrollment> getAllEnrollments() {
        return new ArrayList<>(enrollments);
    }
    
    @Override
    public boolean isStudentEnrolled(Long studentId, String courseCode) {
        return findEnrollment(studentId, courseCode).isPresent();
    }
    
    @Override
    public int getStudentCreditHours(Long studentId) {
        return getStudentEnrollments(studentId).stream()
            .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
            .mapToInt(e -> e.getCourse().getCredits())
            .sum();
    }
    
    @Override
    public double calculateStudentGPA(Long studentId) {
        List<Enrollment> studentEnrollments = getStudentEnrollments(studentId).stream()
            .filter(Enrollment::hasGrade)
            .filter(e -> e.getGrade() != Grade.W && e.getGrade() != Grade.I)
            .collect(Collectors.toList());
        
        if (studentEnrollments.isEmpty()) {
            return 0.0;
        }
        
        double totalGradePoints = studentEnrollments.stream()
            .mapToDouble(Enrollment::getGradePoints)
            .sum();
        
        int totalCredits = studentEnrollments.stream()
            .mapToInt(e -> e.getCourse().getCredits())
            .sum();
        
        return totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
    }
}

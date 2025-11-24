package edu.ccrm.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Enrollment class representing the relationship between Student and Course
 */
public class Enrollment {
    private Long id;
    private Student student;
    private Course course;
    private Grade grade;
    private LocalDateTime enrollmentDate;
    private LocalDateTime gradeDate;
    private EnrollmentStatus status;
    
    // Static counter for ID generation
    private static Long idCounter = 1L;
    
    public Enrollment(Student student, Course course) {
        this.id = generateId();
        this.student = Objects.requireNonNull(student, "Student cannot be null");
        this.course = Objects.requireNonNull(course, "Course cannot be null");
        this.enrollmentDate = LocalDateTime.now();
        this.status = EnrollmentStatus.ENROLLED;
        
        // Business rule validation
        validateEnrollment();
    }
    
    private static synchronized Long generateId() {
        return idCounter++;
    }
    
    private void validateEnrollment() {
        // Check if student is active
        if (student.getStatus() != StudentStatus.ACTIVE) {
            throw new IllegalArgumentException("Cannot enroll inactive student");
        }
        
        // Check if course is active
        if (!course.isActive()) {
            throw new IllegalArgumentException("Cannot enroll in inactive course");
        }
    }
    
    // Getters
    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public Grade getGrade() { return grade; }
    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public LocalDateTime getGradeDate() { return gradeDate; }
    public EnrollmentStatus getStatus() { return status; }
    
    // Grade assignment with business logic
    public void assignGrade(Grade grade) {
        if (this.status != EnrollmentStatus.ENROLLED) {
            throw new IllegalStateException("Cannot assign grade to non-enrolled student");
        }
        
        this.grade = Objects.requireNonNull(grade, "Grade cannot be null");
        this.gradeDate = LocalDateTime.now();
        
        // Automatically complete enrollment when grade is assigned
        if (grade != Grade.I && grade != Grade.W) { // I=Incomplete, W=Withdrawn
            this.status = EnrollmentStatus.COMPLETED;
        }
    }
    
    public void updateGrade(Grade newGrade) {
        Objects.requireNonNull(newGrade, "Grade cannot be null");
        this.grade = newGrade;
        this.gradeDate = LocalDateTime.now();
    }
    
    public void withdraw() {
        this.status = EnrollmentStatus.WITHDRAWN;
        this.grade = Grade.W;
        this.gradeDate = LocalDateTime.now();
    }
    
    public boolean isCompleted() {
        return status == EnrollmentStatus.COMPLETED;
    }
    
    public boolean hasGrade() {
        return grade != null;
    }
    
    // Calculate grade points for GPA calculation
    public double getGradePoints() {
        if (grade == null) return 0.0;
        return grade.getGradePoints() * course.getCredits();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Enrollment that = (Enrollment) obj;
        return Objects.equals(student.getId(), that.student.getId()) &&
               Objects.equals(course.getCode(), that.course.getCode());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(student.getId(), course.getCode());
    }
    
    @Override
    public String toString() {
        return String.format("Enrollment{student='%s', course='%s', grade=%s, status=%s, date=%s}",
            student.getRegNo(), course.getCode(), 
            grade != null ? grade : "No Grade",
            status,
            enrollmentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}

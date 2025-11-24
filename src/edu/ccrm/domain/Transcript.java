package edu.ccrm.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Transcript class demonstrating polymorphism and advanced calculations
 */
public class Transcript {
    private final Student student;
    private final List<Enrollment> enrollments;
    private final LocalDateTime generatedAt;
    private final double gpa;
    private final int totalCredits;
    private final int completedCredits;
    
    public Transcript(Student student, List<Enrollment> enrollments) {
        this.student = Objects.requireNonNull(student, "Student cannot be null");
        this.enrollments = new ArrayList<>(Objects.requireNonNull(enrollments, "Enrollments cannot be null"));
        this.generatedAt = LocalDateTime.now();
        
        // Calculate metrics
        this.completedCredits = calculateCompletedCredits();
        this.totalCredits = calculateTotalCredits();
        this.gpa = calculateGPA();
    }
    
    private int calculateCompletedCredits() {
        return enrollments.stream()
            .filter(e -> e.hasGrade() && e.getGrade().isPassingGrade())
            .mapToInt(e -> e.getCourse().getCredits())
            .sum();
    }
    
    private int calculateTotalCredits() {
        return enrollments.stream()
            .mapToInt(e -> e.getCourse().getCredits())
            .sum();
    }
    
    private double calculateGPA() {
        List<Enrollment> gradedEnrollments = enrollments.stream()
            .filter(Enrollment::hasGrade)
            .filter(e -> e.getGrade() != Grade.W && e.getGrade() != Grade.I)
            .collect(Collectors.toList());
        
        if (gradedEnrollments.isEmpty()) {
            return 0.0;
        }
        
        double totalGradePoints = gradedEnrollments.stream()
            .mapToDouble(Enrollment::getGradePoints)
            .sum();
        
        int totalGradedCredits = gradedEnrollments.stream()
            .mapToInt(e -> e.getCourse().getCredits())
            .sum();
        
        return totalGradedCredits > 0 ? totalGradePoints / totalGradedCredits : 0.0;
    }
    
    // Getters
    public Student getStudent() { return student; }
    public List<Enrollment> getEnrollments() { return new ArrayList<>(enrollments); }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public double getGpa() { return gpa; }
    public int getTotalCredits() { return totalCredits; }
    public int getCompletedCredits() { return completedCredits; }
    
    // Get enrollments by semester
    public Map<Semester, List<Enrollment>> getEnrollmentsBySemester() {
        return enrollments.stream()
            .collect(Collectors.groupingBy(
                e -> e.getCourse().getSemester(),
                Collectors.toList()
            ));
    }
    
    // Get grade distribution
    public Map<Grade, Long> getGradeDistribution() {
        return enrollments.stream()
            .filter(Enrollment::hasGrade)
            .collect(Collectors.groupingBy(
                Enrollment::getGrade,
                Collectors.counting()
            ));
    }
    
    // Academic standing calculation
    public AcademicStanding getAcademicStanding() {
        if (gpa >= 3.5) return AcademicStanding.DEAN_LIST;
        else if (gpa >= 3.0) return AcademicStanding.GOOD_STANDING;
        else if (gpa >= 2.0) return AcademicStanding.SATISFACTORY;
        else if (gpa >= 1.0) return AcademicStanding.PROBATION;
        else return AcademicStanding.SUSPENSION;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("OFFICIAL ACADEMIC TRANSCRIPT\n");
        sb.append("==========================\n\n");
        
        sb.append("Student Information:\n");
        sb.append("Name: ").append(student.getFullName()).append("\n");
        sb.append("Registration No: ").append(student.getRegNo()).append("\n");
        sb.append("Student ID: ").append(student.getId()).append("\n");
        sb.append("Email: ").append(student.getEmail()).append("\n\n");
        
        sb.append("Academic Summary:\n");
        sb.append("Total Credits Attempted: ").append(totalCredits).append("\n");
        sb.append("Credits Completed: ").append(completedCredits).append("\n");
        sb.append("Cumulative GPA: ").append(String.format("%.3f", gpa)).append("\n");
        sb.append("Academic Standing: ").append(getAcademicStanding()).append("\n\n");
        
        // Group by semester
        Map<Semester, List<Enrollment>> semesterEnrollments = getEnrollmentsBySemester();
        
        sb.append("Course History:\n");
        sb.append("==============\n");
        
        for (Semester semester : Semester.values()) {
            List<Enrollment> semEnrollments = semesterEnrollments.get(semester);
            if (semEnrollments != null && !semEnrollments.isEmpty()) {
                sb.append("\n").append(semester).append(" Semester:\n");
                sb.append(String.format("%-10s %-30s %-8s %-6s%n", "Course", "Title", "Credits", "Grade"));
                sb.append("-".repeat(55)).append("\n");
                
                for (Enrollment enrollment : semEnrollments) {
                    Course course = enrollment.getCourse();
                    sb.append(String.format("%-10s %-30s %-8d %-6s%n",
                        course.getCode(),
                        course.getTitle().length() > 30 ? 
                            course.getTitle().substring(0, 27) + "..." : course.getTitle(),
                        course.getCredits(),
                        enrollment.hasGrade() ? enrollment.getGrade() : "N/A"));
                }
            }
        }
        
        sb.append("\n\nTranscript generated on: ")
            .append(generatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            .append("\n");
        
        return sb.toString();
    }
}

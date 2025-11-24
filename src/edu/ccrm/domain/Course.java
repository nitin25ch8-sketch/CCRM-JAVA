package edu.ccrm.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Course class demonstrating builder pattern, nested classes, and immutable fields
 */
public class Course {
    private final String code; // Immutable course code
    private String title;
    private int credits;
    private String instructor;
    private Semester semester;
    private String department;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    
    // Static nested class for course validation
    public static class CourseValidator {
        public static boolean isValidCredits(int credits) {
            return credits >= 1 && credits <= 6;
        }
        
        public static boolean isValidCourseCode(String code) {
            return code != null && code.matches("^[A-Z]{2,4}\\d{3}$");
        }
        
        public static boolean isValidTitle(String title) {
            return title != null && title.trim().length() >= 3;
        }
    }
    
    // Inner class for course statistics (demonstrates inner class access to outer class)
    public class Statistics {
        public String getCourseSummary() {
            return String.format("Course: %s (%s) - %d credits, taught by %s in %s %s", 
                title, code, credits, instructor, semester, 
                department != null ? "from " + department : "");
        }
        
        public boolean isHighCreditCourse() {
            return Course.this.credits >= 4; // Access to outer class field
        }
    }
    
    // Private constructor for builder
    private Course(Builder builder) {
        this.code = builder.code;
        this.title = builder.title;
        this.credits = builder.credits;
        this.instructor = builder.instructor;
        this.semester = builder.semester;
        this.department = builder.department;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }
    
    // Getters
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public String getInstructor() { return instructor; }
    public Semester getSemester() { return semester; }
    public String getDepartment() { return department; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public boolean isActive() { return active; }
    
    // Setters with validation and timestamp update
    public void setTitle(String title) {
        if (!CourseValidator.isValidTitle(title)) {
            throw new IllegalArgumentException("Invalid course title");
        }
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setCredits(int credits) {
        if (!CourseValidator.isValidCredits(credits)) {
            throw new IllegalArgumentException("Credits must be between 1 and 6");
        }
        this.credits = credits;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setInstructor(String instructor) {
        this.instructor = instructor;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setSemester(Semester semester) {
        this.semester = semester;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setDepartment(String department) {
        this.department = department;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Method to get inner class instance
    public Statistics getStatistics() {
        return new Statistics();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return Objects.equals(code, course.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
    
    @Override
    public String toString() {
        return String.format("Course{code='%s', title='%s', credits=%d, instructor='%s', semester=%s, dept='%s'}",
            code, title, credits, instructor, semester, department);
    }
    
    // Builder Pattern Implementation
    public static class Builder {
        private String code;
        private String title;
        private int credits;
        private String instructor;
        private Semester semester;
        private String department;
        
        public Builder code(String code) {
            this.code = code != null ? code.toUpperCase() : null;
            return this;
        }
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder credits(int credits) {
            this.credits = credits;
            return this;
        }
        
        public Builder instructor(String instructor) {
            this.instructor = instructor;
            return this;
        }
        
        public Builder semester(Semester semester) {
            this.semester = semester;
            return this;
        }
        
        public Builder department(String department) {
            this.department = department;
            return this;
        }
        
        public Course build() {
            // Validation using static nested class
            Objects.requireNonNull(code, "Course code cannot be null");
            Objects.requireNonNull(title, "Course title cannot be null");
            Objects.requireNonNull(instructor, "Instructor cannot be null");
            Objects.requireNonNull(semester, "Semester cannot be null");
            
            if (!CourseValidator.isValidCourseCode(code)) {
                throw new IllegalArgumentException("Invalid course code format: " + code);
            }
            
            if (!CourseValidator.isValidTitle(title)) {
                throw new IllegalArgumentException("Invalid course title: " + title);
            }
            
            if (!CourseValidator.isValidCredits(credits)) {
                throw new IllegalArgumentException("Credits must be between 1 and 6");
            }
            
            return new Course(this);
        }
    }
}

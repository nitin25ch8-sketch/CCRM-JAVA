package edu.ccrm.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Instructor class demonstrating inheritance and polymorphism
 */
public class Instructor extends Person {
    private String employeeId;
    private String department;
    private String designation;
    private final Set<String> coursesTaught;
    private LocalDateTime hireDate;
    
    public Instructor() {
        super();
        this.coursesTaught = new HashSet<>();
        this.hireDate = LocalDateTime.now();
    }
    
    public Instructor(String fullName, String email, String employeeId, String department) {
        super(fullName, email);
        this.employeeId = employeeId;
        this.department = department;
        this.coursesTaught = new HashSet<>();
        this.hireDate = LocalDateTime.now();
    }
    
    @Override
    public String getDisplayType() {
        return "INSTRUCTOR";
    }
    
    @Override
    public String getDetailedProfile() {
        StringBuilder sb = new StringBuilder();
        sb.append("Instructor Profile\n");
        sb.append("=================\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Employee ID: ").append(employeeId).append("\n");
        sb.append("Full Name: ").append(fullName).append("\n");
        sb.append("Email: ").append(email).append("\n");
        sb.append("Department: ").append(department).append("\n");
        sb.append("Designation: ").append(designation != null ? designation : "N/A").append("\n");
        sb.append("Hire Date: ").append(hireDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).append("\n");
        sb.append("Courses Teaching: ").append(coursesTaught.size()).append("\n");
        
        return sb.toString();
    }
    
    // Getters and setters
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    
    public LocalDateTime getHireDate() { return hireDate; }
    public void setHireDate(LocalDateTime hireDate) { this.hireDate = hireDate; }
    
    public Set<String> getCoursesTaught() { return new HashSet<>(coursesTaught); }
    
    public void addCourse(String courseCode) {
        coursesTaught.add(courseCode);
    }
    
    public void removeCourse(String courseCode) {
        coursesTaught.remove(courseCode);
    }
    
    @Override
    public String toString() {
        return String.format("Instructor{id=%d, empId='%s', name='%s', dept='%s', courses=%d}",
            id, employeeId, fullName, department, coursesTaught.size());
    }
}

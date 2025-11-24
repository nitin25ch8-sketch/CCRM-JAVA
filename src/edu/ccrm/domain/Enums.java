package edu.ccrm.domain;

/**
 * Enums demonstrating enum with fields, constructors, and methods
 */

public enum StudentStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    GRADUATED("Graduated"),
    SUSPENDED("Suspended"),
    TRANSFERRED("Transferred");
    
    private final String displayName;
    
    StudentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}

public enum Semester {
    SPRING("Spring", 1),
    SUMMER("Summer", 2),
    FALL("Fall", 3);
    
    private final String displayName;
    private final int order;
    
    Semester(String displayName, int order) {
        this.displayName = displayName;
        this.order = order;
    }
    
    public String getDisplayName() { return displayName; }
    public int getOrder() { return order; }
    
    // Method to get next semester
    public Semester getNext() {
        switch (this) {
            case SPRING: return SUMMER;
            case SUMMER: return FALL;
            case FALL: return SPRING;
            default: throw new IllegalStateException("Unknown semester: " + this);
        }
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}

public enum Grade {
    S("S+", 4.0, true, "Outstanding"),
    A("A", 4.0, true, "Excellent"),
    B("B", 3.0, true, "Good"),
    C("C", 2.0, true, "Satisfactory"),
    D("D", 1.0, true, "Below Average"),
    F("F", 0.0, false, "Fail"),
    I("I", 0.0, false, "Incomplete"),
    W("W", 0.0, false, "Withdrawn");
    
    private final String displayGrade;
    private final double gradePoints;
    private final boolean passing;
    private final String description;
    
    Grade(String displayGrade, double gradePoints, boolean passing, String description) {
        this.displayGrade = displayGrade;
        this.gradePoints = gradePoints;
        this.passing = passing;
        this.description = description;
    }
    
    public String getDisplayGrade() { return displayGrade; }
    public double getGradePoints() { return gradePoints; }
    public boolean isPassingGrade() { return passing; }
    public String getDescription() { return description; }
    
    // Static method to get grade from points
    public static Grade fromGradePoints(double points) {
        if (points >= 4.0) return A; // Treat 4.0 as A (S+ is special case)
        else if (points >= 3.0) return B;
        else if (points >= 2.0) return C;
        else if (points >= 1.0) return D;
        else return F;
    }
    
    @Override
    public String toString() {
        return displayGrade;
    }
}

public enum EnrollmentStatus {
    ENROLLED("Enrolled"),
    COMPLETED("Completed"),
    WITHDRAWN("Withdrawn"),
    DROPPED("Dropped");
    
    private final String displayName;
    
    EnrollmentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() { return displayName; }
    
    @Override
    public String toString() {
        return displayName;
    }
}

public enum AcademicStanding {
    DEAN_LIST("Dean's List", "GPA 3.5 or above"),
    GOOD_STANDING("Good Standing", "GPA 3.0 - 3.49"),
    SATISFACTORY("Satisfactory", "GPA 2.0 - 2.99"),
    PROBATION("Academic Probation", "GPA 1.0 - 1.99"),
    SUSPENSION("Academic Suspension", "GPA below 1.0");
    
    private final String displayName;
    private final String description;
    
    AcademicStanding(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    
    @Override
    public String toString() {
        return displayName;
    }
}

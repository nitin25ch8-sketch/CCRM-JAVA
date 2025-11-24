package edu.ccrm.exception;

/**
 * Checked exception for duplicate student scenarios
 */
public class DuplicateStudentException extends CCRMException {
    public DuplicateStudentException(String message) {
        super(message, "DUPLICATE_STUDENT");
    }
    
    public DuplicateStudentException(String message, Throwable cause) {
        super(message, "DUPLICATE_STUDENT", cause);
    }
}

// File: src/edu/ccrm/exception/StudentNotFoundException.java
package edu.ccrm.exception;

public class StudentNotFoundException extends CCRMException {
    public StudentNotFoundException(String message) {
        super(message, "STUDENT_NOT_FOUND");
    }
}

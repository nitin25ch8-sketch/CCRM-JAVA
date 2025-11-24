package edu.ccrm.exception;

public class DuplicateCourseException extends CCRMException {
    public DuplicateCourseException(String message) {
        super(message, "DUPLICATE_COURSE");
    }
}

// File: src/edu/ccrm/exception/CourseNotFoundException.java
package edu.ccrm.exception;

public class CourseNotFoundException extends CCRMException {
    public CourseNotFoundException(String message) {
        super(message, "COURSE_NOT_FOUND");
    }
}

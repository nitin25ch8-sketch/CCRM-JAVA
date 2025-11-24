package edu.ccrm.exception;

/**
 * Base exception class for CCRM application
 * Demonstrates exception hierarchy and checked exceptions
 */
public class CCRMException extends Exception {
    private final String errorCode;
    
    public CCRMException(String message) {
        super(message);
        this.errorCode = "CCRM_ERROR";
    }
    
    public CCRMException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public CCRMException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "CCRM_ERROR";
    }
    
    public CCRMException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s", errorCode, getMessage());
    }
}

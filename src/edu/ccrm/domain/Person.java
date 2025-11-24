// Domain Classes
// File: src/edu/ccrm/domain/Person.java
package edu.ccrm.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class demonstrating inheritance and abstraction
 */
public abstract class Person {
    protected Long id;
    protected String fullName;
    protected String email;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    
    // Static counter for ID generation
    private static Long idCounter = 1L;
    
    public Person() {
        this.id = generateId();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Person(String fullName, String email) {
        this();
        this.fullName = fullName;
        this.email = email;
    }
    
    // Synchronized method for thread-safe ID generation
    private static synchronized Long generateId() {
        return idCounter++;
    }
    
    // Abstract method - must be implemented by subclasses
    public abstract String getDisplayType();
    
    // Abstract method for detailed profile
    public abstract String getDetailedProfile();
    
    // Getters and setters with encapsulation
    public Long getId() { return id; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { 
        this.fullName = fullName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { 
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return Objects.equals(id, person.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("%s{id=%d, name='%s', email='%s'}", 
            getClass().getSimpleName(), id, fullName, email);
    }
}

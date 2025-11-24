// Service Interfaces and Implementations
// File: src/edu/ccrm/service/Persistable.java
package edu.ccrm.service;

import java.util.List;
import java.util.Optional;

/**
 * Generic interface demonstrating generics and functional interfaces
 */
public interface Persistable<T, ID> {
    void save(T entity);
    void update(T entity);
    void delete(ID id);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean exists(ID id);
    long count();
}

// File: src/edu/ccrm/service/Searchable.java
package edu.ccrm.service;

import java.util.List;
import java.util.function.Predicate;

/**
 * Interface for searchable entities with functional programming support
 */
public interface Searchable<T> {
    List<T> search(String query);
    List<T> filter(Predicate<T> predicate);
    List<T> findByField(String fieldName, Object value);
    
    // Default method demonstrating interface default methods
    default List<T> searchIgnoreCase(String query) {
        return search(query.toLowerCase());
    }
}

// File: src/edu/ccrm/service/StudentService.java
package edu.ccrm.service;

import edu.ccrm.domain.Student;
import edu.ccrm.domain.StudentStatus;
import edu.ccrm.exception.DuplicateStudentException;
import edu.ccrm.exception.StudentNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Student service interface demonstrating service layer abstraction
 */
public interface StudentService extends Persistable<Student, Long>, Searchable<Student> {
    void addStudent(Student student) throws DuplicateStudentException;
    void updateStudent(Student student) throws StudentNotFoundException;
    void deactivateStudent(Long studentId) throws StudentNotFoundException;
    Optional<Student> findStudentById(Long id);
    Optional<Student> findStudentByRegNo(String regNo);
    List<Student> getAllStudents();
    List<Student> getStudentsByStatus(StudentStatus status);
    List<Student> getActiveStudents();
    
    // Business logic methods
    boolean canEnrollInCourse(Long studentId, String courseCode);
    int getTotalCreditsEnrolled(Long studentId);
}

// File: src/edu/ccrm/service/StudentServiceImpl.java
package edu.ccrm.service;

import edu.ccrm.domain.Student;
import edu.ccrm.domain.StudentStatus;
import edu.ccrm.exception.DuplicateStudentException;
import edu.ccrm.exception.StudentNotFoundException;
import edu.ccrm.util.DataStore;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Student service implementation demonstrating service layer pattern
 */
public class StudentServiceImpl implements StudentService {
    private final DataStore<Student, Long> studentStore;
    private final Map<String, Long> regNoIndex; // Secondary index for registration numbers
    
    public StudentServiceImpl() {
        this.studentStore = new DataStore<>();
        this.regNoIndex = new HashMap<>();
    }
    
    @Override
    public void addStudent(Student student) throws DuplicateStudentException {
        Objects.requireNonNull(student, "Student cannot be null");
        
        // Check for duplicate registration number
        if (regNoIndex.containsKey(student.getRegNo())) {
            throw new DuplicateStudentException("Student with registration number " + 
                student.getRegNo() + " already exists");
        }
        
        // Check for duplicate ID (shouldn't happen with proper ID generation)
        if (exists(student.getId())) {
            throw new DuplicateStudentException("Student with ID " + student.getId() + " already exists");
        }
        
        save(student);
        regNoIndex.put(student.getRegNo(), student.getId());
    }
    
    @Override
    public void updateStudent(Student student) throws StudentNotFoundException {
        Objects.requireNonNull(student, "Student cannot be null");
        
        if (!exists(student.getId())) {
            throw new StudentNotFoundException("Student with ID " + student.getId() + " not found");
        }
        
        update(student);
    }
    
    @Override
    public void deactivateStudent(Long studentId) throws StudentNotFoundException {
        Optional<Student> optStudent = findById(studentId);
        if (optStudent.isEmpty()) {
            throw new StudentNotFoundException("Student with ID " + studentId + " not found");
        }
        
        Student student = optStudent.get();
        student.setStatus(StudentStatus.INACTIVE);
        update(student);
    }
    
    @Override
    public Optional<Student> findStudentById(Long id) {
        return findById(id);
    }
    
    @Override
    public Optional<Student> findStudentByRegNo(String regNo) {
        Long studentId = regNoIndex.get(regNo);
        return studentId != null ? findById(studentId) : Optional.empty();
    }
    
    @Override
    public List<Student> getAllStudents() {
        return findAll();
    }
    
    @Override
    public List<Student> getStudentsByStatus(StudentStatus status) {
        return findAll().stream()
            .filter(student -> student.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Student> getActiveStudents() {
        return getStudentsByStatus(StudentStatus.ACTIVE);
    }
    
    @Override
    public boolean canEnrollInCourse(Long studentId, String courseCode) {
        Optional<Student> optStudent = findById(studentId);
        if (optStudent.isEmpty()) return false;
        
        Student student = optStudent.get();
        return student.getStatus() == StudentStatus.ACTIVE && 
               !student.isEnrolledIn(courseCode);
    }
    
    @Override
    public int getTotalCreditsEnrolled(Long studentId) {
        Optional<Student> optStudent = findById(studentId);
        if (optStudent.isEmpty()) return 0;
        
        // This would typically calculate from enrollments
        // For simplicity, returning enrolled courses count * average credits
        return optStudent.get().getEnrolledCourses().size() * 3; // Assuming average 3 credits
    }
    
    // Persistable interface implementation
    @Override
    public void save(Student student) {
        studentStore.save(student);
    }
    
    @Override
    public void update(Student student) {
        studentStore.update(student);
    }
    
    @Override
    public void delete(Long id) {
        Optional<Student> optStudent = findById(id);
        if (optStudent.isPresent()) {
            regNoIndex.remove(optStudent.get().getRegNo());
        }
        studentStore.delete(id);
    }
    
    @Override
    public Optional<Student> findById(Long id) {
        return studentStore.findById(id);
    }
    
    @Override
    public List<Student> findAll() {
        return studentStore.findAll();
    }
    
    @Override
    public boolean exists(Long id) {
        return studentStore.exists(id);
    }
    
    @Override
    public long count() {
        return studentStore.count();
    }
    
    // Searchable interface implementation
    @Override
    public List<Student> search(String query) {
        String lowerQuery = query.toLowerCase();
        return findAll().stream()
            .filter(student -> student.getFullName().toLowerCase().contains(lowerQuery) ||
                              student.getEmail().toLowerCase().contains(lowerQuery) ||
                              student.getRegNo().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Student> filter(Predicate<Student> predicate) {
        return findAll().stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Student> findByField(String fieldName, Object value) {
        // Simple field matching - in real app would use reflection or more sophisticated mapping
        switch (fieldName.toLowerCase()) {
            case "status":
                if (value instanceof StudentStatus) {
                    return getStudentsByStatus((StudentStatus) value);
                }
                break;
            case "regno":
                if (value instanceof String) {
                    return findStudentByRegNo((String) value)
                        .map(List::of)
                        .orElse(List.of());
                }
                break;
        }
        return List.of();
    }
}

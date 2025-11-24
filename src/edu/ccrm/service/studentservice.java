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

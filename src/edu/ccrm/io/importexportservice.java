package edu.ccrm.io;

import edu.ccrm.domain.Student;
import edu.ccrm.domain.Course;
import edu.ccrm.domain.Enrollment;

import java.nio.file.Path;
import java.util.List;
import java.io.IOException;

/**
 * Interface for import/export operations
 */
public interface ImportExportService {
    // Import operations
    List<Student> importStudentsFromCSV(Path filePath) throws IOException;
    List<Course> importCoursesFromCSV(Path filePath) throws IOException;
    
    // Export operations
    void exportStudentsToCSV(List<Student> students, Path filePath) throws IOException;
    void exportCoursesToCSV(List<Course> courses, Path filePath) throws IOException;
    void exportEnrollmentsToCSV(List<Enrollment> enrollments, Path filePath) throws IOException;
    
    // Bulk operations
    void exportAllData(Path exportFolder) throws IOException;
    void importAllData(Path importFolder) throws IOException;
}

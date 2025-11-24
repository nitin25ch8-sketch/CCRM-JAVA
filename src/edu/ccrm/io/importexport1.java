
package edu.ccrm.io;

import edu.ccrm.domain.*;
import edu.ccrm.util.FileUtility;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/**
 * Implementation demonstrating NIO.2, Streams, and CSV processing
 */
public class ImportExportServiceImpl implements ImportExportService {
    
    private static final String CSV_DELIMITER = ",";
    private static final String CSV_QUOTE = "\"";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public List<Student> importStudentsFromCSV(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Student CSV file not found: " + filePath);
        }
        
        List<Student> students = new ArrayList<>();
        
        // Using try-with-resources for automatic resource management
        try (Stream<String> lines = Files.lines(filePath)) {
            List<String> lineList = lines.collect(java.util.stream.Collectors.toList());
            
            if (lineList.isEmpty()) {
                return students;
            }
            
            // Skip header line
            for (int i = 1; i < lineList.size(); i++) {
                String line = lineList.get(i).trim();
                if (line.isEmpty()) continue;
                
                try {
                    Student student = parseStudentFromCSV(line);
                    students.add(student);
                } catch (Exception e) {
                    System.err.println("Error parsing student line " + (i + 1) + ": " + e.getMessage());
                }
            }
        }
        
        return students;
    }
    
    @Override
    public List<Course> importCoursesFromCSV(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Course CSV file not found: " + filePath);
        }
        
        List<Course> courses = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            boolean isFirstLine = true;
            
            // Demonstrate while loop with labeled break
            readLoop: while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip header
                if (isFirstLine) {
                    isFirstLine = false;
                    continue readLoop;
                }
                
                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }
                
                try {
                    Course course = parseCourseFromCSV(line);
                    courses.add(course);
                } catch (Exception e) {
                    System.err.println("Error parsing course line: " + e.getMessage());
                    // Continue processing other lines instead of breaking
                    continue readLoop;
                }
            }
        }
        
        return courses;
    }
    
    @Override
    public void exportStudentsToCSV(List<Student> students, Path filePath) throws IOException {
        FileUtility.ensureDirectoryExists(filePath.getParent());
        
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, 
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            
            // Write CSV header
            writer.write("ID,RegNo,FullName,Email,Status,EnrollmentDate,CreatedAt");
            writer.newLine();
            
            // Write student data using enhanced for loop
            for (Student student : students) {
                String csvLine = formatStudentAsCSV(student);
                writer.write(csvLine);
                writer.newLine();
            }
        }
    }
    
    @Override
    public void exportCoursesToCSV(List<Course> courses, Path filePath) throws IOException {
        FileUtility.ensureDirectoryExists(filePath.getParent());
        
        // Demonstrate try-with-resources with multiple resources
        try (FileWriter fileWriter = new FileWriter(filePath.toFile());
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            
            // Write header
            printWriter.println("Code,Title,Credits,Instructor,Semester,Department,Active,CreatedAt");
            
            // Use Stream API for processing
            courses.stream()
                .filter(Objects::nonNull)
                .map(this::formatCourseAsCSV)
                .forEach(printWriter::println);
        }
    }
    
    @Override
    public void exportEnrollmentsToCSV(List<Enrollment> enrollments, Path filePath) throws IOException {
        FileUtility.ensureDirectoryExists(filePath.getParent());
        
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("EnrollmentID,StudentID,StudentRegNo,CourseCode,Grade,EnrollmentDate,GradeDate,Status");
            writer.newLine();
            
            for (Enrollment enrollment : enrollments) {
                String csvLine = formatEnrollmentAsCSV(enrollment);
                writer.write(csvLine);
                writer.newLine();
            }
        }
    }
    
    @Override
    public void exportAllData(Path exportFolder) throws IOException {
        FileUtility.ensureDirectoryExists(exportFolder);
        
        // This would typically get data from services
        // For demo purposes, creating empty files with headers
        
        // Create students.csv
        Path studentsFile = exportFolder.resolve("students.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(studentsFile)) {
            writer.write("ID,RegNo,FullName,Email,Status,EnrollmentDate,CreatedAt");
            writer.newLine();
        }
        
        // Create courses.csv
        Path coursesFile = exportFolder.resolve("courses.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(coursesFile)) {
            writer.write("Code,Title,Credits,Instructor,Semester,Department,Active,CreatedAt");
            writer.newLine();
        }
        
        // Create enrollments.csv
        Path enrollmentsFile = exportFolder.resolve("enrollments.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(enrollmentsFile)) {
            writer.write("EnrollmentID,StudentID,StudentRegNo,CourseCode,Grade,EnrollmentDate,GradeDate,Status");
            writer.newLine();
        }
        
        // Create metadata file
        Path metadataFile = exportFolder.resolve("export_metadata.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(metadataFile)) {
            writer.write("CCRM Data Export");
            writer.newLine();
            writer.write("Export Date: " + LocalDateTime.now().format(DATE_FORMATTER));
            writer.newLine();
            writer.write("Export Folder: " + exportFolder.toAbsolutePath());
            writer.newLine();
        }
    }
    
    @Override
    public void importAllData(Path importFolder) throws IOException {
        if (!Files.exists(importFolder) || !Files.isDirectory(importFolder)) {
            throw new IllegalArgumentException("Import folder does not exist: " + importFolder);
        }
        
        // Import students if file exists
        Path studentsFile = importFolder.resolve("students.csv");
        if (Files.exists(studentsFile)) {
            List<Student> students = importStudentsFromCSV(studentsFile);
            System.out.println("Imported " + students.size() + " students");
        }
        
        // Import courses if file exists
        Path coursesFile = importFolder.resolve("courses.csv");
        if (Files.exists(coursesFile)) {
            List<Course> courses = importCoursesFromCSV(coursesFile);
            System.out.println("Imported " + courses.size() + " courses");
        }
    }
    
    // Private helper methods for CSV parsing
    private Student parseStudentFromCSV(String csvLine) {
        String[] fields = parseCSVLine(csvLine);
        
        if (fields.length < 4) {
            throw new IllegalArgumentException("Invalid student CSV format: insufficient fields");
        }
        
        // Expected format: ID,RegNo,FullName,Email,Status,EnrollmentDate,CreatedAt
        String regNo = fields[1].trim();
        String fullName = fields[2].trim();
        String email = fields[3].trim();
        
        StudentStatus status = StudentStatus.ACTIVE;
        if (fields.length > 4 && !fields[4].trim().isEmpty()) {
            try {
                status = StudentStatus.valueOf(fields[4].trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Use default status
            }
        }
        
        return new Student.Builder()
            .regNo(regNo)
            .fullName(fullName)
            .email(email)
            .status(status)
            .build();
    }
    
    private Course parseCourseFromCSV(String csvLine) {
        String[] fields = parseCSVLine(csvLine);
        
        if (fields.length < 6) {
            throw new IllegalArgumentException("Invalid course CSV format: insufficient fields");
        }
        
        // Expected format: Code,Title,Credits,Instructor,Semester,Department,Active,CreatedAt
        String code = fields[0].trim();
        String title = fields[1].trim();
        int credits = Integer.parseInt(fields[2].trim());
        String instructor = fields[3].trim();
        Semester semester = Semester.valueOf(fields[4].trim().toUpperCase());
        String department = fields[5].trim();
        
        return new Course.Builder()
            .code(code)
            .title(title)
            .credits(credits)
            .instructor(instructor)
            .semester(semester)
            .department(department)
            .build();
    }
    
    private String formatStudentAsCSV(Student student) {
        return String.join(CSV_DELIMITER,
            escapeCsvField(student.getId().toString()),
            escapeCsvField(student.getRegNo()),
            escapeCsvField(student.getFullName()),
            escapeCsvField(student.getEmail()),
            escapeCsvField(student.getStatus().toString()),
            escapeCsvField(student.getEnrollmentDate().format(DATE_FORMATTER)),
            escapeCsvField(student.getCreatedAt().format(DATE_FORMATTER))
        );
    }
    
    private String formatCourseAsCSV(Course course) {
        return String.join(CSV_DELIMITER,
            escapeCsvField(course.getCode()),
            escapeCsvField(course.getTitle()),
            escapeCsvField(String.valueOf(course.getCredits())),
            escapeCsvField(course.getInstructor()),
            escapeCsvField(course.getSemester().toString()),
            escapeCsvField(course.getDepartment() != null ? course.getDepartment() : ""),
            escapeCsvField(String.valueOf(course.isActive())),
            escapeCsvField(course.getCreatedAt().format(DATE_FORMATTER))
        );
    }
    
    private String formatEnrollmentAsCSV(Enrollment enrollment) {
        return String.join(CSV_DELIMITER,
            escapeCsvField(enrollment.getId().toString()),
            escapeCsvField(enrollment.getStudent().getId().toString()),
            escapeCsvField(enrollment.getStudent().getRegNo()),
            escapeCsvField(enrollment.getCourse().getCode()),
            escapeCsvField(enrollment.getGrade() != null ? enrollment.getGrade().toString() : ""),
            escapeCsvField(enrollment.getEnrollmentDate().format(DATE_FORMATTER)),
            escapeCsvField(enrollment.getGradeDate() != null ? enrollment.getGradeDate().format(DATE_FORMATTER) : ""),
            escapeCsvField(enrollment.getStatus().toString())
        );
    }
    
    private String[] parseCSVLine(String csvLine) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();
        
        // Simple CSV parser (for production, use a proper CSV library)
        for (int i = 0; i < csvLine.length(); i++) {
            char ch = csvLine.charAt(i);
            
            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(ch);
            }
        }
        
        fields.add(currentField.toString()); // Add the last field
        return fields.toArray(new String[0]);
    }
    
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        
        // If field contains comma, quote, or newline, wrap in quotes
        if (field.contains(CSV_DELIMITER) || field.contains(CSV_QUOTE) || field.contains("\n")) {
            return CSV_QUOTE + field.replace(CSV_QUOTE, CSV_QUOTE + CSV_QUOTE) + CSV_QUOTE;
        }
        
        return field;
    }
}

// File: src/edu/ccrm/io/BackupService.java
package edu.ccrm.io;

import java.util.List;
import java.io.IOException;

/**
 * Interface for backup operations
 */
public interface BackupService {
    String createBackup() throws IOException;
    void restoreFromBackup(String backupName) throws IOException;
    List<String> listBackupFolders() throws IOException;
    boolean deleteBackup(String backupName) throws IOException;
    long getBackupSize(String backupName) throws IOException;
}

// File: src/edu/ccrm/io/BackupServiceImpl.java
package edu.ccrm.io;

import edu.ccrm.config.AppConfig;
import edu.ccrm.util.FileUtility;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Backup service implementation demonstrating NIO.2 file operations
 */
public class BackupServiceImpl implements BackupService {
    
    private final AppConfig config;
    private final Path backupRootPath;
    private final ImportExportService importExportService;
    
    public BackupServiceImpl() {
        this.config = AppConfig.getInstance();
        this.backupRootPath = Paths.get(config.getBackupFolderPath());
        this.importExportService = new ImportExportServiceImpl();
        
        try {
            FileUtility.ensureDirectoryExists(backupRootPath);
        } catch (IOException e) {
            System.err.println("Error creating backup directory: " + e.getMessage());
        }
    }
    
    @Override
    public String createBackup() throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFolderName = "backup_" + timestamp;
        Path backupFolder = backupRootPath.resolve(backupFolderName);
        
        // Create backup directory
        Files.createDirectories(backupFolder);
        
        try {
            // Export all data to backup folder
            importExportService.exportAllData(backupFolder);
            
            // Create backup metadata
            createBackupMetadata(backupFolder, timestamp);
            
            // Create backup verification file
            createBackupVerification(backupFolder);
            
            return backupFolder.toString();
            
        } catch (IOException e) {
            // Cleanup on failure
            try {
                FileUtility.deleteDirectory(backupFolder);
            } catch (IOException deleteError) {
                System.err.println("Error cleaning up failed backup: " + deleteError.getMessage());
            }
            throw e;
        }
    }
    
    @Override
    public void restoreFromBackup(String backupName) throws IOException {
        Path backupPath = backupRootPath.resolve(backupName);
        
        if (!Files.exists(backupPath) || !Files.isDirectory(backupPath)) {
            throw new IllegalArgumentException("Backup not found: " + backupName);
        }
        
        // Verify backup integrity
        if (!verifyBackupIntegrity(backupPath)) {
            throw new IOException("Backup integrity check failed for: " + backupName);
        }
        
        try {
            // Import data from backup
            importExportService.importAllData(backupPath);
            
            System.out.println("Successfully restored from backup: " + backupName);
            
        } catch (IOException e) {
            throw new IOException("Error restoring from backup: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<String> listBackupFolders() throws IOException {
        if (!Files.exists(backupRootPath)) {
            return new ArrayList<>();
        }
        
        // Use Stream API with NIO.2
        try (Stream<Path> paths = Files.list(backupRootPath)) {
            return paths
                .filter(Files::isDirectory)
                .filter(path -> path.getFileName().toString().startsWith("backup_"))
                .map(path -> path.getFileName().toString())
                .sorted(Collections.reverseOrder()) // Most recent first
                .collect(Collectors.toList());
        }
    }
    
    @Override
    public boolean deleteBackup(String backupName) throws IOException {
        Path backupPath = backupRootPath.resolve(backupName);
        
        if (!Files.exists(backupPath)) {
            return false;
        }
        
        try {
            FileUtility.deleteDirectory(backupPath);
            return true;
        } catch (IOException e) {
            System.err.println("Error deleting backup: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public long getBackupSize(String backupName) throws IOException {
        Path backupPath = backupRootPath.resolve(backupName);
        
        if (!Files.exists(backupPath)) {
            return 0L;
        }
        
        return FileUtility.calculateDirectorySize(backupPath);
    }
    
    private void createBackupMetadata(Path backupFolder, String timestamp) throws IOException {
        Path metadataFile = backupFolder.resolve("backup_info.txt");
        
        try (var writer = Files.newBufferedWriter(metadataFile)) {
            writer.write("CCRM Backup Information\n");
            writer.write("======================\n");
            writer.write("Backup Created: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
            writer.write("Backup ID: " + timestamp + "\n");
            writer.write("Application Version: 1.0.0\n");
            writer.write("Backup Type: Full\n");
            writer.write("Data Folder: " + config.getDataFolderPath() + "\n");
            
            // List files in backup
            writer.write("\nBackup Contents:\n");
            try (Stream<Path> files = Files.list(backupFolder)) {
                files.filter(Files::isRegularFile)
                     .forEach(file -> {
                         try {
                             long size = Files.size(file);
                             writer.write("- " + file.getFileName() + " (" + FileUtility.formatFileSize(size) + ")\n");
                         } catch (IOException e) {
                             writer.write("- " + file.getFileName() + " (size unknown)\n");
                         }
                     });
            }
        }
    }
    
    private void createBackupVerification(Path backupFolder) throws IOException {
        Path verificationFile = backupFolder.resolve(".backup_checksum");
        
        try (var writer = Files.newBufferedWriter(verificationFile)) {
            // Simple verification - list files with their sizes
            try (Stream<Path> files = Files.walk(backupFolder)) {
                files.filter(Files::isRegularFile)
                     .filter(path -> !path.getFileName().toString().equals(".backup_checksum"))
                     .sorted()
                     .forEach(file -> {
                         try {
                             long size = Files.size(file);
                             String relativePath = backupFolder.relativize(file).toString();
                             writer.write(relativePath + ":" + size + "\n");
                         } catch (IOException e) {
                             // Skip files that can't be read
                         }
                     });
            }
        }
    }
    
    private boolean verifyBackupIntegrity(Path backupPath) throws IOException {
        Path verificationFile = backupPath.resolve(".backup_checksum");
        
        if (!Files.exists(verificationFile)) {
            System.out.println("Warning: No verification file found, skipping integrity check");
            return true;
        }
        
        // Read expected checksums
        Map<String, Long> expectedSizes = new HashMap<>();
        try (Stream<String> lines = Files.lines(verificationFile)) {
            lines.forEach(line -> {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    expectedSizes.put(parts[0], Long.parseLong(parts[1]));
                }
            });
        }
        
        // Verify actual files
        for (Map.Entry<String, Long> entry : expectedSizes.entrySet()) {
            Path filePath = backupPath.resolve(entry.getKey());
            if (!Files.exists(filePath)) {
                System.err.println("Missing file in backup: " + entry.getKey());
                return false;
            }
            
            long actualSize = Files.size(filePath);
            if (actualSize != entry.getValue()) {
                System.err.println("Size mismatch for file " + entry.getKey() + 
                    ": expected " + entry.getValue() + ", actual " + actualSize);
                return false;
            }
        }
        
        return true;
    }
}

// Sample Data Files for Testing
// File: test-data/students.csv
// ID,RegNo,FullName,Email,Status
// 1,2021CS001,John Doe,john.doe@university.edu,ACTIVE
// 2,2021CS002,Jane Smith,jane.smith@university.edu,ACTIVE  
// 3,2021ME001,Bob Johnson,bob.johnson@university.edu,ACTIVE
// 4,2021EE001,Alice Wilson,alice.wilson@university.edu,INACTIVE

// File: test-data/courses.csv
// Code,Title,Credits,Instructor,Semester,Department
// CS101,Programming Fundamentals,3,Dr. Johnson,FALL,Computer Science
// CS102,Data Structures,4,Prof. Davis,SPRING,Computer Science
// MATH201,Calculus II,4,Dr. Wilson,FALL,Mathematics
// PHY101,Physics I,3,Prof. Brown,SPRING,Physics
// ENG101,Technical Writing,2,Dr. Miller,FALL,English

// File: src/edu/ccrm/io/CSVParser.java
package edu.ccrm.io;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for robust CSV parsing
 * Demonstrates regular expressions and string processing
 */
public final class CSVParser {
    private CSVParser() {
        throw new AssertionError("Utility class");
    }
    
    // Pattern for CSV field parsing (handles quoted fields with embedded commas)
    private static final Pattern CSV_PATTERN = Pattern.compile(
        "\"([^\"]*(?:\"\"[^\"]*)*)\"|([^,]*)"
    );
    
    /**
     * Parse a CSV line into fields, handling quoted fields properly
     * @param csvLine The CSV line to parse
     * @return Array of fields
     */
    public static String[] parseLine(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            return new String[0];
        }
        
        List<String> fields = new ArrayList<>();
        Matcher matcher = CSV_PATTERN.matcher(csvLine);
        
        while (matcher.find()) {
            String quotedField = matcher.group(1);
            String unquotedField = matcher.group(2);
            
            if (quotedField != null) {
                // Handle escaped quotes in quoted fields
                fields.add(quotedField.replace("\"\"", "\""));
            } else {
                fields.add(unquotedField != null ? unquotedField : "");
            }
        }
        
        return fields.toArray(new String[0]);
    }
    
    /**
     * Format a field for CSV output, adding quotes if necessary
     */
    public static String formatField(String field) {
        if (field == null) {
            return "";
        }
        
        // Add quotes if field contains comma, quote, or newline
        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            // Escape existing quotes by doubling them
            String escaped = field.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
        
        return field;
    }
    
    /**
     * Join fields into a CSV line
     */
    public static String formatLine(String... fields) {
        return formatLine(Arrays.asList(fields));
    }
    
    /**
     * Join fields into a CSV line
     */
    public static String formatLine(List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return "";
        }
        
        return fields.stream()
            .map(CSVParser::formatField)
            .reduce((a, b) -> a + "," + b)
            .orElse("");
    }
    
    /**
     * Parse multiple CSV lines
     */
    public static List<String[]> parseLines(List<String> csvLines) {
        return csvLines.stream()
            .filter(line -> line != null && !line.trim().isEmpty())
            .map(CSVParser::parseLine)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    /**
     * Convert CSV data to map with headers as keys
     */
    public static List<Map<String, String>> parseToMaps(List<String> csvLines) {
        if (csvLines == null || csvLines.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String[]> parsedLines = parseLines(csvLines);
        if (parsedLines.isEmpty()) {
            return new ArrayList<>();
        }
        
        String[] headers = parsedLines.get(0);
        List<Map<String, String>> result = new ArrayList<>();
        
        // Process data rows (skip header)
        for (int i = 1; i < parsedLines.size(); i++) {
            String[] row = parsedLines.get(i);
            Map<String, String> rowMap = new LinkedHashMap<>();
            
            // Map each field to its header
            for (int j = 0; j < headers.length; j++) {
                String header = headers[j].trim();
                String value = j < row.length ? row[j].trim() : "";
                rowMap.put(header, value);
            }
            
            result.add(rowMap);
        }
        
        return result;
    }
    
    /**
     * Validate CSV structure
     */
    public static boolean validateCSV(List<String> csvLines, String[] expectedHeaders) {
        if (csvLines == null || csvLines.isEmpty()) {
            return false;
        }
        
        String[] actualHeaders = parseLine(csvLines.get(0));
        
        if (actualHeaders.length != expectedHeaders.length) {
            return false;
        }
        
        for (int i = 0; i < expectedHeaders.length; i++) {
            if (!actualHeaders[i].trim().equalsIgnoreCase(expectedHeaders[i].trim())) {
                return false;
            }
        }
        
        return true;
    }
}

// File: src/edu/ccrm/io/FileOperations.java
package edu.ccrm.io;

import edu.ccrm.util.FileUtility;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

/**
 * Advanced file operations demonstrating NIO.2 features
 */
public final class FileOperations {
    private FileOperations() {
        throw new AssertionError("Utility class");
    }
    
    /**
     * Copy files with filtering using NIO.2
     */
    public static void copyWithFilter(Path source, Path target, String fileExtension) throws IOException {
        if (!Files.exists(source)) {
            throw new NoSuchFileException("Source path does not exist: " + source);
        }
        
        FileUtility.ensureDirectoryExists(target);
        
        if (Files.isRegularFile(source)) {
            // Single file copy
            if (fileExtension == null || source.toString().endsWith(fileExtension)) {
                Files.copy(source, target.resolve(source.getFileName()), 
                          StandardCopyOption.REPLACE_EXISTING);
            }
        } else if (Files.isDirectory(source)) {
            // Directory copy with filtering
            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir = target.resolve(source.relativize(dir));
                    Files.createDirectories(targetDir);
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (fileExtension == null || file.toString().endsWith(fileExtension)) {
                        Path targetFile = target.resolve(source.relativize(file));
                        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
    
    /**
     * Move files with conflict resolution
     */
    public static void moveWithConflictResolution(Path source, Path target) throws IOException {
        if (!Files.exists(source)) {
            throw new NoSuchFileException("Source does not exist: " + source);
        }
        
        FileUtility.ensureDirectoryExists(target.getParent());
        
        if (Files.exists(target)) {
            // Handle conflict by creating backup
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = target.getFileName().toString();
            String backupName = fileName + ".backup." + timestamp;
            Path backupPath = target.getParent().resolve(backupName);
            
            Files.move(target, backupPath);
            System.out.println("Existing file backed up as: " + backupName);
        }
        
        Files.move(source, target);
    }
    
    /**
     * Find files by criteria using Stream API
     */
    public static List<Path> findFiles(Path searchPath, String namePattern, 
                                     long minSize, long maxSize) throws IOException {
        if (!Files.exists(searchPath)) {
            return new ArrayList<>();
        }
        
        try (Stream<Path> paths = Files.walk(searchPath)) {
            return paths
                .filter(Files::isRegularFile)
                .filter(path -> {
                    if (namePattern != null) {
                        return path.getFileName().toString().matches(namePattern);
                    }
                    return true;
                })
                .filter(path -> {
                    try {
                        long size = Files.size(path);
                        return size >= minSize && (maxSize == 0 || size <= maxSize);
                    } catch (IOException e) {
                        return false;
                    }
                })
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
    }
    
    /**
     * Archive old files (move files older than specified days)
     */
    public static int archiveOldFiles(Path sourceDir, Path archiveDir, int daysOld) throws IOException {
        if (!Files.exists(sourceDir)) {
            return 0;
        }
        
        FileUtility.ensureDirectoryExists(archiveDir);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        int archivedCount = 0;
        
        try (Stream<Path> paths = Files.list(sourceDir)) {
            for (Path file : paths.filter(Files::isRegularFile).toArray(Path[]::new)) {
                BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                LocalDateTime lastModified = LocalDateTime.ofInstant(
                    attrs.lastModifiedTime().toInstant(), 
                    java.time.ZoneId.systemDefault()
                );
                
                if (lastModified.isBefore(cutoffDate)) {
                    Path archiveFile = archiveDir.resolve(file.getFileName());
                    Files.move(file, archiveFile, StandardCopyOption.REPLACE_EXISTING);
                    archivedCount++;
                }
            }
        }
        
        return archivedCount;
    }
    
    /**
     * Create symbolic links for file organization
     */
    public static void createSymbolicLinks(Map<Path, Path> linkMappings) throws IOException {
        for (Map.Entry<Path, Path> entry : linkMappings.entrySet()) {
            Path link = entry.getKey();
            Path target = entry.getValue();
            
            if (!Files.exists(target)) {
                System.err.println("Warning: Target does not exist: " + target);
                continue;
            }
            
            FileUtility.ensureDirectoryExists(link.getParent());
            
            if (Files.exists(link)) {
                Files.delete(link);
            }
            
            try {
                Files.createSymbolicLink(link, target);
            } catch (UnsupportedOperationException e) {
                // Fall back to hard link if symbolic links are not supported
                try {
                    Files.createLink(link, target);
                } catch (UnsupportedOperationException e2) {
                    // Fall back to copying
                    Files.copy(target, link, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
    
    /**
     * Watch directory for changes (demonstration of WatchService)
     */
    public static void watchDirectory(Path directory, Runnable onChange) throws IOException, InterruptedException {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            directory.register(watchService, 
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
            
            System.out.println("Watching directory: " + directory);
            
            // Poll for events (in real application, this would run in a separate thread)
            WatchKey key;
            int eventCount = 0;
            while ((key = watchService.take()) != null && eventCount < 10) { // Limit for demo
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println("Event: " + event.kind() + " - " + event.context());
                    onChange.run();
                    eventCount++;
                }
                key.reset();
            }
        }
    }
}

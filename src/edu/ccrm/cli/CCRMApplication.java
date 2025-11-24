// Main Application Class
// File: src/edu/ccrm/cli/CCRMApplication.java
package edu.ccrm.cli;

import edu.ccrm.config.AppConfig;
import edu.ccrm.domain.*;
import edu.ccrm.service.*;
import edu.ccrm.io.*;
import edu.ccrm.util.*;
import edu.ccrm.exception.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.Paths;

/**
 * Campus Course & Records Manager - Main Application
 * Demonstrates comprehensive Java SE concepts including OOP, design patterns,
 * file I/O with NIO.2, Stream API, and advanced language features.
 */
public class CCRMApplication {
    private final Scanner scanner;
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final TranscriptService transcriptService;
    private final ImportExportService importExportService;
    private final BackupService backupService;
    
    // Demonstrate anonymous inner class for menu action
    private final Map<String, Runnable> menuActions;
    
    public CCRMApplication() {
        this.scanner = new Scanner(System.in);
        
        // Initialize services (demonstrating dependency injection pattern)
        this.studentService = new StudentServiceImpl();
        this.courseService = new CourseServiceImpl();
        this.enrollmentService = new EnrollmentServiceImpl(studentService, courseService);
        this.transcriptService = new TranscriptServiceImpl(enrollmentService);
        this.importExportService = new ImportExportServiceImpl();
        this.backupService = new BackupServiceImpl();
        
        // Initialize menu actions using anonymous inner classes and lambdas
        this.menuActions = new HashMap<>();
        initializeMenuActions();
    }
    
    public static void main(String[] args) {
        // Display platform information
        displayPlatformInfo();
        
        // Initialize singleton configuration
        AppConfig config = AppConfig.getInstance();
        System.out.println("CCRM initialized with data path: " + config.getDataFolderPath());
        
        CCRMApplication app = new CCRMApplication();
        app.run();
    }
    
    private static void displayPlatformInfo() {
        System.out.println("=".repeat(60));
        System.out.println("Campus Course & Records Manager (CCRM)");
        System.out.println("Java SE Application - Comprehensive Demo");
        System.out.println("=".repeat(60));
        System.out.println("Platform Note:");
        System.out.println("• Java ME: Mobile/embedded platforms with limited APIs");
        System.out.println("• Java SE: Standard platform for desktop applications (this app)");
        System.out.println("• Java EE: Enterprise platform for web applications");
        System.out.println("=".repeat(60));
    }
    
    public void run() {
        boolean running = true;
        
        while (running) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();
            
            // Enhanced switch expression (Java 14+)
            switch (choice) {
                case "1" -> manageStudents();
                case "2" -> manageCourses();
                case "3" -> manageEnrollments();
                case "4" -> manageGrades();
                case "5" -> importExportData();
                case "6" -> backupAndReports();
                case "7" -> generateReports();
                case "8" -> {
                    System.out.println("Thank you for using CCRM!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        
        scanner.close();
    }
    
    private void displayMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("CAMPUS COURSE & RECORDS MANAGER");
        System.out.println("=".repeat(50));
        System.out.println("1. Manage Students");
        System.out.println("2. Manage Courses");
        System.out.println("3. Manage Enrollments");
        System.out.println("4. Manage Grades");
        System.out.println("5. Import/Export Data");
        System.out.println("6. Backup & Archive");
        System.out.println("7. Generate Reports");
        System.out.println("8. Exit");
        System.out.print("Enter your choice: ");
    }
    
    private void initializeMenuActions() {
        // Demonstrate lambda expressions and anonymous inner classes
        menuActions.put("add_student", () -> addStudent());
        menuActions.put("list_students", () -> listStudents());
        
        // Anonymous inner class example
        menuActions.put("search_students", new Runnable() {
            @Override
            public void run() {
                searchStudents();
            }
        });
    }
    
    private void manageStudents() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- Student Management ---");
            System.out.println("1. Add Student");
            System.out.println("2. List All Students");
            System.out.println("3. Update Student");
            System.out.println("4. Deactivate Student");
            System.out.println("5. View Student Profile");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> addStudent();
                case "2" -> listStudents();
                case "3" -> updateStudent();
                case "4" -> deactivateStudent();
                case "5" -> viewStudentProfile();
                case "6" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }
    
    private void addStudent() {
        try {
            System.out.print("Enter Registration Number: ");
            String regNo = scanner.nextLine().trim();
            
            System.out.print("Enter Full Name: ");
            String fullName = scanner.nextLine().trim();
            
            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();
            
            // Using builder pattern
            Student student = new Student.Builder()
                .regNo(regNo)
                .fullName(fullName)
                .email(email)
                .status(StudentStatus.ACTIVE)
                .build();
            
            studentService.addStudent(student);
            System.out.println("Student added successfully with ID: " + student.getId());
            
        } catch (DuplicateStudentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
    
    private void listStudents() {
        List<Student> students = studentService.getAllStudents();
        
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        
        System.out.println("\n--- All Students ---");
        System.out.printf("%-10s %-15s %-25s %-30s %-10s%n", 
            "ID", "Reg No", "Name", "Email", "Status");
        System.out.println("-".repeat(90));
        
        // Demonstrate enhanced for loop
        for (Student student : students) {
            System.out.printf("%-10s %-15s %-25s %-30s %-10s%n",
                student.getId(), student.getRegNo(), student.getFullName(),
                student.getEmail(), student.getStatus());
        }
    }
    
    private void updateStudent() {
        System.out.print("Enter Student ID to update: ");
        String idStr = scanner.nextLine().trim();
        
        try {
            Long id = Long.parseLong(idStr);
            Optional<Student> optStudent = studentService.findStudentById(id);
            
            if (optStudent.isEmpty()) {
                System.out.println("Student not found with ID: " + id);
                return;
            }
            
            Student student = optStudent.get();
            System.out.println("Current details: " + student);
            
            System.out.print("Enter new email (current: " + student.getEmail() + "): ");
            String newEmail = scanner.nextLine().trim();
            
            if (!newEmail.isEmpty()) {
                student.setEmail(newEmail);
                studentService.updateStudent(student);
                System.out.println("Student updated successfully.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (Exception e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }
    
    private void deactivateStudent() {
        System.out.print("Enter Student ID to deactivate: ");
        String idStr = scanner.nextLine().trim();
        
        try {
            Long id = Long.parseLong(idStr);
            studentService.deactivateStudent(id);
            System.out.println("Student deactivated successfully.");
        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }
    
    private void viewStudentProfile() {
        System.out.print("Enter Student ID: ");
        String idStr = scanner.nextLine().trim();
        
        try {
            Long id = Long.parseLong(idStr);
            Optional<Student> optStudent = studentService.findStudentById(id);
            
            if (optStudent.isEmpty()) {
                System.out.println("Student not found with ID: " + id);
                return;
            }
            
            Student student = optStudent.get();
            System.out.println("\n--- Student Profile ---");
            System.out.println(student.getDetailedProfile());
            
            // Show transcript using polymorphism
            Transcript transcript = transcriptService.generateTranscript(student);
            System.out.println("\n--- Academic Transcript ---");
            System.out.println(transcript.toString());
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        } catch (Exception e) {
            System.out.println("Error viewing profile: " + e.getMessage());
        }
    }
    
    private void searchStudents() {
        System.out.print("Enter search term (name or email): ");
        String searchTerm = scanner.nextLine().trim();
        
        // Demonstrate Stream API with lambda expressions
        List<Student> results = studentService.getAllStudents().stream()
            .filter(student -> student.getFullName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                              student.getEmail().toLowerCase().contains(searchTerm.toLowerCase()))
            .sorted((s1, s2) -> s1.getFullName().compareToIgnoreCase(s2.getFullName()))
            .collect(Collectors.toList());
        
        if (results.isEmpty()) {
            System.out.println("No students found matching: " + searchTerm);
        } else {
            System.out.println("\n--- Search Results ---");
            results.forEach(System.out::println); // Method reference
        }
    }
    
    private void manageCourses() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- Course Management ---");
            System.out.println("1. Add Course");
            System.out.println("2. List All Courses");
            System.out.println("3. Search Courses");
            System.out.println("4. Update Course");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> addCourse();
                case "2" -> listCourses();
                case "3" -> searchCourses();
                case "4" -> updateCourse();
                case "5" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }
    
    private void addCourse() {
        try {
            System.out.print("Enter Course Code: ");
            String code = scanner.nextLine().trim().toUpperCase();
            
            System.out.print("Enter Course Title: ");
            String title = scanner.nextLine().trim();
            
            System.out.print("Enter Credits (1-6): ");
            int credits = Integer.parseInt(scanner.nextLine().trim());
            
            // Assertion for invariant checking
            assert credits > 0 && credits <= 6 : "Credits must be between 1 and 6";
            
            System.out.print("Enter Instructor Name: ");
            String instructor = scanner.nextLine().trim();
            
            System.out.print("Enter Semester (SPRING/SUMMER/FALL): ");
            String semesterStr = scanner.nextLine().trim().toUpperCase();
            Semester semester = Semester.valueOf(semesterStr);
            
            System.out.print("Enter Department: ");
            String department = scanner.nextLine().trim();
            
            // Using builder pattern
            Course course = new Course.Builder()
                .code(code)
                .title(title)
                .credits(credits)
                .instructor(instructor)
                .semester(semester)
                .department(department)
                .build();
            
            courseService.addCourse(course);
            System.out.println("Course added successfully: " + course.getCode());
            
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid semester. Use SPRING, SUMMER, or FALL.");
        } catch (DuplicateCourseException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid credits format.");
        } catch (AssertionError e) {
            System.out.println("Assertion failed: " + e.getMessage());
        }
    }
    
    private void listCourses() {
        List<Course> courses = courseService.getAllCourses();
        
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
            return;
        }
        
        System.out.println("\n--- All Courses ---");
        System.out.printf("%-10s %-30s %-8s %-20s %-10s %-15s%n",
            "Code", "Title", "Credits", "Instructor", "Semester", "Department");
        System.out.println("-".repeat(100));
        
        for (Course course : courses) {
            System.out.printf("%-10s %-30s %-8d %-20s %-10s %-15s%n",
                course.getCode(), course.getTitle(), course.getCredits(),
                course.getInstructor(), course.getSemester(), course.getDepartment());
        }
    }
    
    private void searchCourses() {
        System.out.println("\nSearch by:");
        System.out.println("1. Department");
        System.out.println("2. Instructor");
        System.out.println("3. Semester");
        System.out.print("Choose search type: ");
        
        String choice = scanner.nextLine().trim();
        System.out.print("Enter search value: ");
        String searchValue = scanner.nextLine().trim();
        
        List<Course> results = new ArrayList<>();
        
        switch (choice) {
            case "1" -> results = courseService.findCoursesByDepartment(searchValue);
            case "2" -> results = courseService.findCoursesByInstructor(searchValue);
            case "3" -> {
                try {
                    Semester semester = Semester.valueOf(searchValue.toUpperCase());
                    results = courseService.findCoursesBySemester(semester);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid semester format.");
                    return;
                }
            }
            default -> System.out.println("Invalid search type.");
        }
        
        if (results.isEmpty()) {
            System.out.println("No courses found.");
        } else {
            System.out.println("\n--- Search Results ---");
            results.forEach(System.out::println);
        }
    }
    
    private void updateCourse() {
        System.out.print("Enter Course Code to update: ");
        String code = scanner.nextLine().trim().toUpperCase();
        
        try {
            Optional<Course> optCourse = courseService.findCourseByCode(code);
            
            if (optCourse.isEmpty()) {
                System.out.println("Course not found with code: " + code);
                return;
            }
            
            Course course = optCourse.get();
            System.out.println("Current details: " + course);
            
            System.out.print("Enter new instructor (current: " + course.getInstructor() + "): ");
            String newInstructor = scanner.nextLine().trim();
            
            if (!newInstructor.isEmpty()) {
                course.setInstructor(newInstructor);
                courseService.updateCourse(course);
                System.out.println("Course updated successfully.");
            }
            
        } catch (Exception e) {
            System.out.println("Error updating course: " + e.getMessage());
        }
    }
    
    private void manageEnrollments() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- Enrollment Management ---");
            System.out.println("1. Enroll Student in Course");
            System.out.println("2. Unenroll Student from Course");
            System.out.println("3. View Student Enrollments");
            System.out.println("4. View Course Enrollments");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> enrollStudent();
                case "2" -> unenrollStudent();
                case "3" -> viewStudentEnrollments();
                case "4" -> viewCourseEnrollments();
                case "5" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }
    
    private void enrollStudent() {
        try {
            System.out.print("Enter Student ID: ");
            Long studentId = Long.parseLong(scanner.nextLine().trim());
            
            System.out.print("Enter Course Code: ");
            String courseCode = scanner.nextLine().trim().toUpperCase();
            
            enrollmentService.enrollStudent(studentId, courseCode);
            System.out.println("Student enrolled successfully.");
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid student ID format.");
        } catch (StudentNotFoundException | CourseNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (MaxCreditLimitExceededException | DuplicateEnrollmentException e) {
            System.out.println("Enrollment error: " + e.getMessage());
        }
    }
    
    private void unenrollStudent() {
        try {
            System.out.print("Enter Student ID: ");
            Long studentId = Long.parseLong(scanner.nextLine().trim());
            
            System.out.print("Enter Course Code: ");
            String courseCode = scanner.nextLine().trim().toUpperCase();
            
            enrollmentService.unenrollStudent(studentId, courseCode);
            System.out.println("Student unenrolled successfully.");
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid student ID format.");
        } catch (EnrollmentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void viewStudentEnrollments() {
        try {
            System.out.print("Enter Student ID: ");
            Long studentId = Long.parseLong(scanner.nextLine().trim());
            
            List<Enrollment> enrollments = enrollmentService.getStudentEnrollments(studentId);
            
            if (enrollments.isEmpty()) {
                System.out.println("No enrollments found for student ID: " + studentId);
                return;
            }
            
            System.out.println("\n--- Student Enrollments ---");
            enrollments.forEach(System.out::println);
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid student ID format.");
        }
    }
    
    private void viewCourseEnrollments() {
        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine().trim().toUpperCase();
        
        List<Enrollment> enrollments = enrollmentService.getCourseEnrollments(courseCode);
        
        if (enrollments.isEmpty()) {
            System.out.println("No enrollments found for course: " + courseCode);
            return;
        }
        
        System.out.println("\n--- Course Enrollments ---");
        enrollments.forEach(System.out::println);
    }
    
    private void manageGrades() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- Grade Management ---");
            System.out.println("1. Record Grade");
            System.out.println("2. Update Grade");
            System.out.println("3. View Student Grades");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> recordGrade();
                case "2" -> updateGrade();
                case "3" -> viewStudentGrades();
                case "4" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }
    
    private void recordGrade() {
        try {
            System.out.print("Enter Student ID: ");
            Long studentId = Long.parseLong(scanner.nextLine().trim());
            
            System.out.print("Enter Course Code: ");
            String courseCode = scanner.nextLine().trim().toUpperCase();
            
            System.out.print("Enter Grade (S/A/B/C/D/F): ");
            String gradeStr = scanner.nextLine().trim().toUpperCase();
            Grade grade = Grade.valueOf(gradeStr);
            
            enrollmentService.recordGrade(studentId, courseCode, grade);
            System.out.println("Grade recorded successfully.");
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid student ID format.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid grade format. Use S, A, B, C, D, or F.");
        } catch (EnrollmentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void updateGrade() {
        try {
            System.out.print("Enter Student ID: ");
            Long studentId = Long.parseLong(scanner.nextLine().trim());
            
            System.out.print("Enter Course Code: ");
            String courseCode = scanner.nextLine().trim().toUpperCase();
            
            System.out.print("Enter New Grade (S/A/B/C/D/F): ");
            String gradeStr = scanner.nextLine().trim().toUpperCase();
            Grade grade = Grade.valueOf(gradeStr);
            
            enrollmentService.updateGrade(studentId, courseCode, grade);
            System.out.println("Grade updated successfully.");
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid student ID format.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid grade format.");
        } catch (EnrollmentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void viewStudentGrades() {
        try {
            System.out.print("Enter Student ID: ");
            Long studentId = Long.parseLong(scanner.nextLine().trim());
            
            Optional<Student> optStudent = studentService.findStudentById(studentId);
            if (optStudent.isEmpty()) {
                System.out.println("Student not found.");
                return;
            }
            
            Student student = optStudent.get();
            Transcript transcript = transcriptService.generateTranscript(student);
            
            System.out.println("\n--- Student Grades ---");
            System.out.println("Student: " + student.getFullName());
            System.out.println("GPA: " + String.format("%.2f", transcript.getGpa()));
            System.out.println("\nCourse Grades:");
            transcript.getEnrollments().forEach(enrollment -> {
                System.out.printf("%-10s %-30s %s%n",
                    enrollment.getCourse().getCode(),
                    enrollment.getCourse().getTitle(),
                    enrollment.getGrade() != null ? enrollment.getGrade() : "No Grade");
            });
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid student ID format.");
        }
    }
    
    private void importExportData() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- Import/Export Data ---");
            System.out.println("1. Import Students from CSV");
            System.out.println("2. Import Courses from CSV");
            System.out.println("3. Export Students to CSV");
            System.out.println("4. Export Courses to CSV");
            System.out.println("5. Export All Data");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> importStudents();
                case "2" -> importCourses();
                case "3" -> exportStudents();
                case "4" -> exportCourses();
                case "5" -> exportAllData();
                case "6" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }
    
    private void importStudents() {
        try {
            System.out.print("Enter CSV file path: ");
            String filePath = scanner.nextLine().trim();
            
            List<Student> students = importExportService.importStudentsFromCSV(Paths.get(filePath));
            
            // Demonstrate try-with-resources and multi-catch
            int successCount = 0;
            int errorCount = 0;
            
            for (Student student : students) {
                try {
                    studentService.addStudent(student);
                    successCount++;
                } catch (DuplicateStudentException | IllegalArgumentException e) {
                    System.out.println("Error importing student " + student.getRegNo() + ": " + e.getMessage());
                    errorCount++;
                }
            }
            
            System.out.printf("Import completed. Success: %d, Errors: %d%n", successCount, errorCount);
            
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    
    private void importCourses() {
        try {
            System.out.print("Enter CSV file path: ");
            String filePath = scanner.nextLine().trim();
            
            List<Course> courses = importExportService.importCoursesFromCSV(Paths.get(filePath));
            
            int successCount = 0;
            int errorCount = 0;
            
            for (Course course : courses) {
                try {
                    courseService.addCourse(course);
                    successCount++;
                } catch (DuplicateCourseException | IllegalArgumentException e) {
                    System.out.println("Error importing course " + course.getCode() + ": " + e.getMessage());
                    errorCount++;
                }
            }
            
            System.out.printf("Import completed. Success: %d, Errors: %d%n", successCount, errorCount);
            
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
    
    private void exportStudents() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "students_" + timestamp + ".csv";
            
            List<Student> students = studentService.getAllStudents();
            importExportService.exportStudentsToCSV(students, Paths.get(fileName));
            
            System.out.println("Students exported to: " + fileName);
            
        } catch (Exception e) {
            System.out.println("Error exporting students: " + e.getMessage());
        }
    }
    
    private void exportCourses() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "courses_" + timestamp + ".csv";
            
            List<Course> courses = courseService.getAllCourses();
            importExportService.exportCoursesToCSV(courses, Paths.get(fileName));
            
            System.out.println("Courses exported to: " + fileName);
            
        } catch (Exception e) {
            System.out.println("Error exporting courses: " + e.getMessage());
        }
    }
    
    private void exportAllData() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String folderName = "export_" + timestamp;
            
            importExportService.exportAllData(Paths.get(folderName));
            System.out.println("All data exported to folder: " + folderName);
            
        } catch (Exception e) {
            System.out.println("Error exporting data: " + e.getMessage());
        }
    }
    
    private void backupAndReports() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- Backup & Archive ---");
            System.out.println("1. Create Backup");
            System.out.println("2. List Backup Folders");
            System.out.println("3. Calculate Backup Directory Size (Recursive)");
            System.out.println("4. Restore from Backup");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> createBackup();
                case "2" -> listBackupFolders();
                case "3" -> calculateBackupSize();
                case "4" -> restoreFromBackup();
                case "5" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }
    
    private void createBackup() {
        try {
            String backupPath = backupService.createBackup();
            System.out.println("Backup created successfully at: " + backupPath);
            
            // Show timestamped folder structure
            System.out.println("Backup contains:");
            System.out.println("├── students.csv");
            System.out.println("├── courses.csv");
            System.out.println("└── enrollments.csv");
            
        } catch (Exception e) {
            System.out.println("Error creating backup: " + e.getMessage());
        }
    }
    
    private void listBackupFolders() {
        try {
            List<String> backupFolders = backupService.listBackupFolders();
            
            if (backupFolders.isEmpty()) {
                System.out.println("No backup folders found.");
                return;
            }
            
            System.out.println("\n--- Available Backups ---");
            for (int i = 0; i < backupFolders.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, backupFolders.get(i));
            }
            
        } catch (Exception e) {
            System.out.println("Error listing backups: " + e.getMessage());
        }
    }
    
    private void calculateBackupSize() {
        try {
            // Demonstrate recursion
            long totalSize = FileUtility.calculateDirectorySize(Paths.get("backups"));
            String formattedSize = FileUtility.formatFileSize(totalSize);
            
            System.out.println("Total backup directory size: " + formattedSize);
            
            // Show recursive directory listing
            System.out.println("\nDirectory structure:");
            FileUtility.printDirectoryTree(Paths.get("backups"), 0);
            
        } catch (Exception e) {
            System.out.println("Error calculating size: " + e.getMessage());
        }
    }
    
    private void restoreFromBackup() {
        try {
            List<String> backupFolders = backupService.listBackupFolders();
            
            if (backupFolders.isEmpty()) {
                System.out.println("No backup folders available.");
                return;
            }
            
            System.out.println("\nAvailable backups:");
            for (int i = 0; i < backupFolders.size(); i++) {
                System.out.printf("%d. %s%n", i + 1, backupFolders.get(i));
            }
            
            System.out.print("Select backup to restore (number): ");
            int choice = Integer.parseInt(scanner.nextLine().trim()) - 1;
            
            if (choice >= 0 && choice < backupFolders.size()) {
                String selectedBackup = backupFolders.get(choice);
                backupService.restoreFromBackup(selectedBackup);
                System.out.println("Data restored successfully from: " + selectedBackup);
            } else {
                System.out.println("Invalid selection.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (Exception e) {
            System.out.println("Error restoring backup: " + e.getMessage());
        }
    }
    
    private void generateReports() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n--- Reports ---");
            System.out.println("1. Student GPA Distribution");
            System.out.println("2. Top Performing Students");
            System.out.println("3. Course Enrollment Statistics");
            System.out.println("4. Department-wise Course Count");
            System.out.println("5. Grade Distribution Report");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> showGPADistribution();
                case "2" -> showTopStudents();
                case "3" -> showEnrollmentStats();
                case "4" -> showDepartmentStats();
                case "5" -> showGradeDistribution();
                case "6" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }
    
    private void showGPADistribution() {
        List<Student> students = studentService.getAllStudents();
        
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        
        // Demonstrate Stream API with complex pipeline
        Map<String, Long> gpaRanges = students.stream()
            .map(student -> transcriptService.generateTranscript(student))
            .filter(transcript -> transcript.getGpa() > 0)
            .collect(Collectors.groupingBy(
                transcript -> {
                    double gpa = transcript.getGpa();
                    if (gpa >= 3.5) return "Excellent (3.5-4.0)";
                    else if (gpa >= 3.0) return "Good (3.0-3.49)";
                    else if (gpa >= 2.5) return "Average (2.5-2.99)";
                    else return "Below Average (<2.5)";
                },
                Collectors.counting()
            ));
        
        System.out.println("\n--- GPA Distribution ---");
        gpaRanges.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> {
                System.out.printf("%-20s: %d students%n", entry.getKey(), entry.getValue());
            });
    }
    
    private void showTopStudents() {
        System.out.print("Enter number of top students to show (default 10): ");
        String input = scanner.nextLine().trim();
        int limit = input.isEmpty() ? 10 : Integer.parseInt(input);
        
        List<Student> students = studentService.getAllStudents();
        
        // Complex Stream pipeline with sorting and limiting
        List<Student> topStudents = students.stream()
            .map(student -> new Object() {
                final Student student = student;
                final double gpa = transcriptService.generateTranscript(student).getGpa();
            })
            .filter(wrapper -> wrapper.gpa > 0)
            .sorted((w1, w2) -> Double.compare(w2.gpa, w1.gpa))
            .limit(limit)
            .map(wrapper -> wrapper.student)
            .collect(Collectors.toList());
        
        if (topStudents.isEmpty()) {
            System.out.println("No students with grades found.");
            return;
        }
        
        System.out.printf("%n--- Top %d Students ---%n", Math.min(limit, topStudents.size()));
        System.out.printf("%-5s %-15s %-25s %-10s%n", "Rank", "Reg No", "Name", "GPA");
        System.out.println("-".repeat(60));
        
        for (int i = 0; i < topStudents.size(); i++) {
            Student student = topStudents.get(i);
            double gpa = transcriptService.generateTranscript(student).getGpa();
            System.out.printf("%-5d %-15s %-25s %-10.2f%n",
                i + 1, student.getRegNo(), student.getFullName(), gpa);
        }
    }
    
    private void showEnrollmentStats() {
        List<Course> courses = courseService.getAllCourses();
        
        System.out.println("\n--- Course Enrollment Statistics ---");
        System.out.printf("%-10s %-30s %-12s%n", "Code", "Title", "Enrolled");
        System.out.println("-".repeat(55));
        
        for (Course course : courses) {
            List<Enrollment> enrollments = enrollmentService.getCourseEnrollments(course.getCode());
            System.out.printf("%-10s %-30s %-12d%n",
                course.getCode(),
                course.getTitle().length() > 30 ? course.getTitle().substring(0, 27) + "..." : course.getTitle(),
                enrollments.size());
        }
        
        // Calculate totals using Stream API
        int totalEnrollments = courses.stream()
            .mapToInt(course -> enrollmentService.getCourseEnrollments(course.getCode()).size())
            .sum();
        
        double avgEnrollment = courses.isEmpty() ? 0 : (double) totalEnrollments / courses.size();
        
        System.out.println("-".repeat(55));
        System.out.printf("Total Courses: %d%n", courses.size());
        System.out.printf("Total Enrollments: %d%n", totalEnrollments);
        System.out.printf("Average Enrollment per Course: %.2f%n", avgEnrollment);
    }
    
    private void showDepartmentStats() {
        List<Course> courses = courseService.getAllCourses();
        
        // Demonstrate advanced Stream operations
        Map<String, Long> departmentCount = courses.stream()
            .collect(Collectors.groupingBy(
                Course::getDepartment,
                Collectors.counting()
            ));
        
        System.out.println("\n--- Department-wise Course Statistics ---");
        System.out.printf("%-25s %-10s%n", "Department", "Courses");
        System.out.println("-".repeat(40));
        
        departmentCount.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> {
                System.out.printf("%-25s %-10d%n", entry.getKey(), entry.getValue());
            });
    }
    
    private void showGradeDistribution() {
        // Get all enrollments with grades
        List<Enrollment> gradedEnrollments = studentService.getAllStudents().stream()
            .flatMap(student -> enrollmentService.getStudentEnrollments(student.getId()).stream())
            .filter(enrollment -> enrollment.getGrade() != null)
            .collect(Collectors.toList());
        
        if (gradedEnrollments.isEmpty()) {
            System.out.println("No graded enrollments found.");
            return;
        }
        
        Map<Grade, Long> gradeCount = gradedEnrollments.stream()
            .collect(Collectors.groupingBy(
                Enrollment::getGrade,
                Collectors.counting()
            ));
        
        System.out.println("\n--- Grade Distribution ---");
        System.out.printf("%-6s %-10s %-10s%n", "Grade", "Count", "Percentage");
        System.out.println("-".repeat(30));
        
        int total = gradedEnrollments.size();
        
        // Show in grade order
        for (Grade grade : Grade.values()) {
            long count = gradeCount.getOrDefault(grade, 0L);
            double percentage = (count * 100.0) / total;
            System.out.printf("%-6s %-10d %-10.1f%%%n", grade, count, percentage);
        }
        
        System.out.println("-".repeat(30));
        System.out.printf("Total: %d enrollments%n", total);
    }
}

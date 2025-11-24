

# CCRM Usage Guide

## Quick Start

### Prerequisites
- Java 11 or higher installed
- Eclipse IDE (optional but recommended)

### Running the Application

#### Method 1: Command Line
```bash
# Compile all Java files
javac -d bin -cp src src/edu/ccrm/**/*.java

# Run with assertions enabled
java -ea -cp bin edu.ccrm.cli.CCRMApplication
```

#### Method 2: Eclipse IDE
1. Import the project into Eclipse
2. Right-click on `CCRMApplication.java` → Run As → Java Application
3. Add VM argument `-ea` in Run Configuration for assertions

## Application Features

### 1. Student Management
- **Add Student**: Create new student records with validation
- **List Students**: View all students with filtering options  
- **Update Student**: Modify student information
- **Search Students**: Find students by name, email, or registration number
- **View Profile**: Display detailed student information and transcript

### 2. Course Management
- **Add Course**: Create courses with instructor assignment
- **List Courses**: View all available courses
- **Search Courses**: Filter by department, instructor, or semester
- **Update Course**: Modify course details

### 3. Enrollment Management
- **Enroll Student**: Register students for courses with credit limit validation
- **Unenroll Student**: Remove student from courses
- **View Enrollments**: List student or course enrollments

### 4. Grade Management
- **Record Grades**: Assign grades to student enrollments
- **Update Grades**: Modify existing grades
- **View Transcripts**: Generate official transcripts with GPA calculation

### 5. Import/Export Operations
- **Import Students**: Load student data from CSV files
- **Import Courses**: Load course data from CSV files
- **Export Data**: Save current data to CSV format
- **Bulk Operations**: Import/export all data at once

### 6. Backup & Archive
- **Create Backup**: Generate timestamped backup with verification
- **List Backups**: View available backup folders
- **Restore Data**: Restore from selected backup
- **Calculate Size**: Show backup directory size recursively

### 7. Reports & Analytics
- **GPA Distribution**: Show student performance statistics
- **Top Students**: List highest performing students
- **Enrollment Stats**: Course enrollment analysis
- **Department Stats**: Course distribution by department
- **Grade Distribution**: Overall grade statistics

## Sample Data

### Creating Test Data

#### Students CSV Format (students.csv):
```csv
ID,RegNo,FullName,Email,Status
1,2021CS001,John Doe,john.doe@university.edu,ACTIVE
2,2021CS002,Jane Smith,jane.smith@university.edu,ACTIVE
3,2021ME001,Bob Johnson,bob.johnson@university.edu,ACTIVE
4,2021EE001,Alice Wilson,alice.wilson@university.edu,INACTIVE
```

#### Courses CSV Format (courses.csv):
```csv
Code,Title,Credits,Instructor,Semester,Department
CS101,Programming Fundamentals,3,Dr. Johnson,FALL,Computer Science
CS102,Data Structures,4,Prof. Davis,SPRING,Computer Science
MATH201,Calculus II,4,Dr. Wilson,FALL,Mathematics
PHY101,Physics I,3,Prof. Brown,SPRING,Physics
ENG101,Technical Writing,2,Dr. Miller,FALL,English
```

## Common Workflows

### Workflow 1: Setting Up Initial Data
1. Start the application
2. Go to Import/Export → Import Students from CSV
3. Go to Import/Export → Import Courses from CSV
4. Verify data by listing students and courses

### Workflow 2: Student Enrollment
1. Go to Manage Enrollments → Enroll Student in Course
2. Enter Student ID and Course Code
3. System validates credit limits and prerequisites
4. Confirm enrollment

### Workflow 3: Grade Recording
1. Go to Manage Grades → Record Grade
2. Enter Student ID, Course Code, and Grade (S/A/B/C/D/F)
3. System updates transcript and GPA automatically

### Workflow 4: Generating Reports
1. Go to Generate Reports
2. Select desired report type
3. View analytics and export if needed

### Workflow 5: Data Backup
1. Go to Backup & Archive → Create Backup
2. System creates timestamped backup folder
3. Use "Calculate Backup Size" to verify backup
4. Optionally restore from backup later

## File Structure

```
CCRM/
├── src/
│   └── edu/
│       └── ccrm/
│           ├── cli/              # Command-line interface
│           │   └── CCRMApplication.java
│           ├── domain/           # Domain models and enums
│           │   ├── Person.java
│           │   ├── Student.java
│           │   ├── Instructor.java
│           │   ├── Course.java
│           │   ├── Enrollment.java
│           │   └── Transcript.java
│           ├── service/          # Business logic layer
│           │   ├── StudentService.java
│           │   ├── CourseService.java
│           │   ├── EnrollmentService.java
│           │   └── TranscriptService.java
│           ├── io/               # File I/O operations
│           │   ├── ImportExportService.java
│           │   ├── BackupService.java
│           │   └── FileOperations.java
│           ├── util/             # Utility classes
│           │   ├── DataStore.java
│           │   ├── Validators.java
│           │   ├── Comparators.java
│           │   └── FileUtility.java
│           ├── config/           # Configuration
│           │   └── AppConfig.java
│           └── exception/        # Custom exceptions
│               ├── CCRMException.java
│               └── *Exception.java
├── test-data/                    # Sample CSV files
│   ├── students.csv
│   └── courses.csv
├── exports/                      # Generated export files
├── backups/                      # Backup folders
└── README.md
```

## Configuration

The application uses a singleton configuration class (`AppConfig`) with the following default settings:

- **Data Folder**: `data/`
- **Backup Folder**: `backups/`
- **Export Folder**: `exports/`
- **Max Credit Limit**: 18 credits per semester
- **Assertions**: Enabled

## Business Rules

### Student Enrollment Rules
- Students must be in ACTIVE status to enroll
- Cannot enroll in inactive courses
- Maximum 18 credits per semester
- Cannot enroll in the same course twice
- Cannot unenroll if grades are assigned (will mark as withdrawn)

### Grade Assignment Rules
- Grades: S (Outstanding), A (Excellent), B (Good), C (Satisfactory), D (Below Average), F (Fail)
- Special grades: I (Incomplete), W (Withdrawn)
- GPA calculation excludes I and W grades
- S and A grades both count as 4.0 points

### Academic Standing Calculation
- **Dean's List**: GPA ≥ 3.5
- **Good Standing**: GPA 3.0 - 3.49
- **Satisfactory**: GPA 2.0 - 2.99
- **Probation**: GPA 1.0 - 1.99
- **Suspension**: GPA < 1.0

## Error Handling

The application implements comprehensive error handling:

### Checked Exceptions
- `DuplicateStudentException`: Student already exists
- `StudentNotFoundException`: Student not found
- `CCRMException`: Base checked exception

### Unchecked Exceptions
- `DuplicateEnrollmentException`: Already enrolled in course
- `MaxCreditLimitExceededException`: Credit limit exceeded
- `IllegalArgumentException`: Invalid input parameters

### File I/O Exceptions
- Automatic cleanup on failed operations
- Backup verification before restoration
- Graceful handling of missing files

## Performance Features

### Memory Management
- In-memory data storage with concurrent collections
- Lazy loading of reports and analytics
- Automatic garbage collection of unused objects

### Stream API Usage
- Efficient filtering and sorting operations
- Parallel processing for large datasets
- Functional programming patterns

### File I/O Optimization
- Buffered readers/writers for large files
- Stream processing for CSV operations
- NIO.2 for efficient file operations

## Troubleshooting

### Common Issues

1. **"Assertion failed" errors**
   - Enable assertions with `-ea` VM argument
   - Check input validation rules

2. **File not found errors**
   - Verify CSV file paths
   - Ensure proper file permissions
   - Check file format and encoding

3. **Memory issues with large datasets**
   - Increase JVM heap size: `-Xmx2g`
   - Process data in smaller batches

4. **CSV import errors**
   - Verify CSV header format matches expected columns
   - Check for special characters and proper escaping
   - Ensure consistent date formats

### Debug Mode
Enable debug output by modifying log levels in the code or adding system properties:
```bash
java -ea -Ddebug=true -cp bin edu.ccrm.cli.CCRMApplication
```

## Extension Points

The application is designed for extensibility:

### Adding New Entity Types
1. Extend `Person` class or implement `Persistable` interface
2. Create corresponding service interface and implementation
3. Add to CLI menu system

### Custom Report Types
1. Implement new methods in existing services
2. Add Stream API operations for data processing
3. Integrate into reports menu

### Additional File Formats
1. Extend `ImportExportService` interface
2. Implement parsers for new formats (JSON, XML)
3. Add format detection logic

### Database Integration
1. Replace `DataStore` implementations
2. Add JPA annotations to domain classes
3. Implement repository pattern

---

*For additional support, refer to the comprehensive README.md file and inline code documentation.*

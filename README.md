# Campus Course & Records Manager (CCRM)

A comprehensive Java SE console application for managing students, courses, enrollments, and grades in an educational institution.

## Project Overview

CCRM is a menu-driven console application that demonstrates core Java concepts including OOP principles, advanced language features, file I/O with NIO.2, Stream API, Date/Time API, and design patterns.

### Key Features
- Student Management (CRUD operations)
- Course Management with search and filtering
- Enrollment and Grade Management
- File Import/Export (CSV format)
- Backup and Archive functionality
- Transcript generation with GPA calculation

## How to Run

### Prerequisites
- JDK 11 or higher
- Eclipse IDE (recommended)

### Running the Application
1. Clone the repository
2. Import into Eclipse as existing Java project
3. Run the main class: `edu.ccrm.cli.CCRMApplication`
4. Enable assertions with VM argument: `-ea`

```bash
# Command line execution
javac -d bin src/edu/ccrm/**/*.java
java -ea -cp bin edu.ccrm.cli.CCRMApplication
```

## Java Evolution Timeline

- **1995**: Java 1.0 - Initial release by Sun Microsystems
- **1997**: Java 1.1 - Inner classes, reflection, JDBC
- **1998**: Java 1.2 (J2SE) - Collections framework, Swing
- **2000**: Java 1.3 - HotSpot JVM, JNDI
- **2002**: Java 1.4 - Assertions, NIO, logging
- **2004**: Java 5.0 - Generics, enums, annotations, autoboxing
- **2006**: Java 6 - Performance improvements
- **2011**: Java 7 - Diamond operator, try-with-resources, NIO.2
- **2014**: Java 8 - Lambda expressions, Stream API, Optional
- **2017**: Java 9 - Module system, JShell
- **2018**: Java 10 - Local variable type inference (var)
- **2018**: Java 11 - LTS release, HTTP client
- **2019**: Java 12-13 - Switch expressions (preview)
- **2020**: Java 14-15 - Records (preview), text blocks
- **2021**: Java 16-17 - LTS release, sealed classes
- **2022-2024**: Java 18-21 - Pattern matching, virtual threads

## Java Platform Comparison

| Feature | Java ME | Java SE | Java EE |
|---------|---------|---------|---------|
| **Target** | Mobile devices, embedded systems | Desktop applications, standalone apps | Enterprise web applications |
| **Size** | Lightweight, minimal | Standard library | Extended with enterprise features |
| **APIs** | Limited core APIs | Full core APIs, Swing, AWT | Web services, EJB, JSF, JPA |
| **Deployment** | Mobile apps, IoT devices | Desktop applications, CLI tools | Web applications, application servers |
| **Memory** | Low memory footprint | Standard memory requirements | High memory, scalable |

## Java Architecture

### JDK (Java Development Kit)
- Complete development environment
- Includes compiler (javac), debugger, documentation tools
- Contains JRE + development tools

### JRE (Java Runtime Environment)  
- Runtime environment for executing Java applications
- Includes JVM + core libraries
- Required to run Java programs

### JVM (Java Virtual Machine)
- Abstract computing machine
- Executes Java bytecode
- Provides platform independence
- Memory management and garbage collection

**Interaction Flow**: Source Code → JDK (javac) → Bytecode → JRE → JVM → Machine Code

## Windows Java Installation Steps

1. **Download JDK**
   - Visit Oracle JDK or OpenJDK website
   - Download JDK 11+ for Windows x64

2. **Install JDK**
   - Run the installer executable
   - Follow installation wizard
   - Note installation path (e.g., `C:\Program Files\Java\jdk-17`)

3. **Set Environment Variables**
   - Open System Properties → Advanced → Environment Variables
   - Add JAVA_HOME: `C:\Program Files\Java\jdk-17`
   - Edit PATH: Add `%JAVA_HOME%\bin`

4. **Verify Installation**
   ```cmd
   java -version
   javac -version
   ```

*[Screenshot: java -version output showing JDK installation]*

## Eclipse IDE Setup

1. **Download Eclipse IDE for Java Developers**
   - Visit eclipse.org/downloads
   - Download Eclipse IDE for Java Developers

2. **Create New Java Project**
   - File → New → Java Project
   - Project name: "CCRM"
   - Use default JRE
   - Create module-info.java: No

3. **Configure Build Path**
   - Right-click project → Properties
   - Java Build Path → Add source folders

*[Screenshot: Eclipse project structure with CCRM packages]*

## Technical Implementation Mapping

| Syllabus Topic | Implementation Location |
|---------------|------------------------|
| **OOP Principles** | |
| Encapsulation | `domain/*.java` - private fields, getters/setters |
| Inheritance | `domain/Person.java` → `Student.java`, `Instructor.java` |
| Abstraction | `domain/Person.java` (abstract class) |
| Polymorphism | `service/*Service.java` interfaces |
| **Language Features** | |
| Enums | `domain/Grade.java`, `domain/Semester.java` |
| Interfaces | `service/Persistable.java`, `service/Searchable.java` |
| Lambda Expressions | `util/Comparators.java`, Stream operations |
| Anonymous Classes | `cli/CCRMApplication.java` menu handlers |
| **Advanced Concepts** | |
| Nested Classes | `domain/Course.java` (Builder pattern) |
| Generic Classes | `util/DataStore.java<T>` |
| Exception Handling | `exception/*.java`, service layer |
| **File I/O & NIO.2** | |
| Path API | `io/FileOperations.java` |
| Files API | `io/BackupService.java` |
| Stream API | `io/ImportExportService.java` |
| **Design Patterns** | |
| Singleton | `config/AppConfig.java` |
| Builder | `domain/Course.Builder` |
| **Date/Time API** | `domain/Enrollment.java`, backup timestamps |
| **Recursion** | `util/FileUtility.java` directory size calculation |

## Enabling Assertions

Assertions are used for invariant checking throughout the application.

### Enable in Eclipse
- Run Configuration → Arguments → VM arguments: `-ea`

### Command Line
```bash
java -ea -cp bin edu.ccrm.cli.CCRMApplication
```

### Sample Assertions in Code
```java
// In Student.java
assert studentId != null : "Student ID cannot be null";
assert credits >= 0 : "Credits cannot be negative";
```

## Sample Data Files

### students.csv
```csv
ID,RegNo,FullName,Email,Status
1,2021CS001,John Doe,john.doe@university.edu,ACTIVE
2,2021CS002,Jane Smith,jane.smith@university.edu,ACTIVE
```

### courses.csv
```csv
Code,Title,Credits,Instructor,Semester,Department
CS101,Programming Fundamentals,3,Dr. Johnson,FALL,Computer Science
MATH201,Calculus II,4,Prof. Wilson,SPRING,Mathematics
```

## Project Structure
```
src/
└── edu/ccrm/
    ├── cli/              # Command-line interface
    ├── config/           # Configuration and builders
    ├── domain/           # Domain models
    ├── exception/        # Custom exceptions
    ├── io/              # File operations
    ├── service/         # Business logic
    └── util/            # Utility classes
```

## Notes on Implementation

### Design Decisions
- **Interface vs Class Inheritance**: Interfaces used for contracts (Persistable, Searchable) while class inheritance used for IS-A relationships (Person → Student)
- **Immutable Classes**: CourseCode and StudentId are immutable value objects
- **Exception Strategy**: Checked exceptions for recoverable errors, unchecked for programming errors

### Key Features Demonstrated
- Stream API for filtering and reporting
- NIO.2 for file operations and backup
- Lambda expressions for comparators and predicates  
- Builder pattern for complex object construction
- Singleton pattern for application configuration

## Acknowledgements
- Java documentation and tutorials from Oracle
- Design pattern examples from Gang of Four
- NIO.2 examples from Java documentation

---
*This project demonstrates comprehensive Java SE programming concepts in a practical campus management system.*

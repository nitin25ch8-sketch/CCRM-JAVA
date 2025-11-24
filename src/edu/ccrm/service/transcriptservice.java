package edu.ccrm.service;

import edu.ccrm.domain.Student;
import edu.ccrm.domain.Transcript;

public interface TranscriptService {
    Transcript generateTranscript(Student student);
    String generateTranscriptReport(Student student);
    void printTranscript(Student student);
}

// File: src/edu/ccrm/service/TranscriptServiceImpl.java
package edu.ccrm.service;

import edu.ccrm.domain.*;

import java.util.List;
import java.util.Objects;

public class TranscriptServiceImpl implements TranscriptService {
    private final EnrollmentService enrollmentService;
    
    public TranscriptServiceImpl(EnrollmentService enrollmentService) {
        this.enrollmentService = Objects.requireNonNull(enrollmentService);
    }
    
    @Override
    public Transcript generateTranscript(Student student) {
        Objects.requireNonNull(student, "Student cannot be null");
        
        List<Enrollment> enrollments = enrollmentService.getStudentEnrollments(student.getId());
        return new Transcript(student, enrollments);
    }
    
    @Override
    public String generateTranscriptReport(Student student) {
        Transcript transcript = generateTranscript(student);
        return transcript.toString();
    }
    
    @Override
    public void printTranscript(Student student) {
        String transcriptReport = generateTranscriptReport(student);
        System.out.println(transcriptReport);
    }
}

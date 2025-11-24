package edu.ccrm.service;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Semester;
import edu.ccrm.exception.DuplicateCourseException;
import edu.ccrm.exception.CourseNotFoundException;
import edu.ccrm.util.DataStore;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CourseServiceImpl implements CourseService {
    private final DataStore<Course, String> courseStore;
    
    public CourseServiceImpl() {
        this.courseStore = new DataStore<>();
    }
    
    @Override
    public void addCourse(Course course) throws DuplicateCourseException {
        Objects.requireNonNull(course, "Course cannot be null");
        
        if (exists(course.getCode())) {
            throw new DuplicateCourseException("Course with code " + course.getCode() + " already exists");
        }
        
        save(course);
    }
    
    @Override
    public void updateCourse(Course course) throws CourseNotFoundException {
        Objects.requireNonNull(course, "Course cannot be null");
        
        if (!exists(course.getCode())) {
            throw new CourseNotFoundException("Course with code " + course.getCode() + " not found");
        }
        
        update(course);
    }
    
    @Override
    public void deactivateCourse(String courseCode) throws CourseNotFoundException {
        Optional<Course> optCourse = findById(courseCode);
        if (optCourse.isEmpty()) {
            throw new CourseNotFoundException("Course with code " + courseCode + " not found");
        }
        
        Course course = optCourse.get();
        course.setActive(false);
        update(course);
    }
    
    @Override
    public Optional<Course> findCourseByCode(String code) {
        return findById(code);
    }
    
    @Override
    public List<Course> getAllCourses() {
        return findAll();
    }
    
    @Override
    public List<Course> getActiveCourses() {
        return findAll().stream()
            .filter(Course::isActive)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> findCoursesByDepartment(String department) {
        return findAll().stream()
            .filter(course -> course.getDepartment() != null && 
                             course.getDepartment().toLowerCase().contains(department.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> findCoursesByInstructor(String instructor) {
        return findAll().stream()
            .filter(course -> course.getInstructor() != null && 
                             course.getInstructor().toLowerCase().contains(instructor.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> findCoursesBySemester(Semester semester) {
        return findAll().stream()
            .filter(course -> course.getSemester() == semester)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> findCoursesByCredits(int credits) {
        return findAll().stream()
            .filter(course -> course.getCredits() == credits)
            .collect(Collectors.toList());
    }
    
    // Persistable interface implementation
    @Override
    public void save(Course course) {
        courseStore.save(course);
    }
    
    @Override
    public void update(Course course) {
        courseStore.update(course);
    }
    
    @Override
    public void delete(String code) {
        courseStore.delete(code);
    }
    
    @Override
    public Optional<Course> findById(String code) {
        return courseStore.findById(code);
    }
    
    @Override
    public List<Course> findAll() {
        return courseStore.findAll();
    }
    
    @Override
    public boolean exists(String code) {
        return courseStore.exists(code);
    }
    
    @Override
    public long count() {
        return courseStore.count();
    }
    
    // Searchable interface implementation
    @Override
    public List<Course> search(String query) {
        String lowerQuery = query.toLowerCase();
        return findAll().stream()
            .filter(course -> course.getCode().toLowerCase().contains(lowerQuery) ||
                             course.getTitle().toLowerCase().contains(lowerQuery) ||
                             (course.getInstructor() != null && course.getInstructor().toLowerCase().contains(lowerQuery)) ||
                             (course.getDepartment() != null && course.getDepartment().toLowerCase().contains(lowerQuery)))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> filter(Predicate<Course> predicate) {
        return findAll().stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> findByField(String fieldName, Object value) {
        switch (fieldName.toLowerCase()) {
            case "department":
                if (value instanceof String) {
                    return findCoursesByDepartment((String) value);
                }
                break;
            case "instructor":
                if (value instanceof String) {
                    return findCoursesByInstructor((String) value);
                }
                break;
            case "semester":
                if (value instanceof Semester) {
                    return findCoursesBySemester((Semester) value);
                }
                break;
            case "credits":
                if (value instanceof Integer) {
                    return findCoursesByCredits((Integer) value);
                }
                break;
        }
        return List.of();
    }
}

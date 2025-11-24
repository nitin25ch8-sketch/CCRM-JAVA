package edu.ccrm.service;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Semester;
import edu.ccrm.exception.DuplicateCourseException;
import edu.ccrm.exception.CourseNotFoundException;

import java.util.List;
import java.util.Optional;

public interface CourseService extends Persistable<Course, String>, Searchable<Course> {
    void addCourse(Course course) throws DuplicateCourseException;
    void updateCourse(Course course) throws CourseNotFoundException;
    void deactivateCourse(String courseCode) throws CourseNotFoundException;
    Optional<Course> findCourseByCode(String code);
    List<Course> getAllCourses();
    List<Course> getActiveCourses();
    
    // Search methods
    List<Course> findCoursesByDepartment(String department);
    List<Course> findCoursesByInstructor(String instructor);
    List<Course> findCoursesBySemester(Semester semester);
    List<Course> findCoursesByCredits(int credits);
}

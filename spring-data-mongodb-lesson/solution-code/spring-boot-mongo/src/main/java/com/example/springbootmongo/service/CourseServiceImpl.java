package com.example.springbootmongo.service;

import com.example.springbootmongo.model.Course;
import com.example.springbootmongo.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService{

    @Autowired
    CourseRepository courseRepository;

    @Override
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public List<Course> listCourses() {
        return courseRepository.findAll();
    }
}

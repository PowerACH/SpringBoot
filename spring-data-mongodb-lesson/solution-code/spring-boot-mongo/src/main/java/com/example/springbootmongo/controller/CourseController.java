package com.example.springbootmongo.controller;

import com.example.springbootmongo.model.Course;
import com.example.springbootmongo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    CourseService courseService;

    @GetMapping
    public List<Course> listCourses(){
        return courseService.listCourses();
    }

    @PostMapping
    public Course saveCourse(@RequestBody Course course){
        return courseService.saveCourse(course);
    }
}
package com.coursitory.app.Controllers;

import com.coursitory.app.Entities.Course;
import com.coursitory.app.Entities.User;
import com.coursitory.app.Services.CourseService;
import com.coursitory.app.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    CourseService courseService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user){
        return userService.verify(user);
    }
    @GetMapping("/testing")
    public String testing(){
        return "Hello !";
    }


}

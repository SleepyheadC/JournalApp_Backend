package com.application.journalApp.controllers;

import com.application.journalApp.entity.User;
import com.application.journalApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    private UserService userService;
    @GetMapping("/health-check")
    public String healthCheck(){
        return "OK";
    }
    @PostMapping("/create-user")
    public void createUser(@RequestBody User user){
        userService.saveNewUser(user);
    }
}

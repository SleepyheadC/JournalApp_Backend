package com.application.journalApp.controllers;
import com.application.journalApp.entity.JournalEntry;
import com.application.journalApp.entity.User;
import com.application.journalApp.repository.UserRepository;
import com.application.journalApp.services.JournalEntryService;
import com.application.journalApp.services.UserService;
import org.apache.coyote.Response;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAll();
    }
    @PostMapping
    public void createUser(@RequestBody User user){
        userService.saveNewUser(user);
    }

    @PutMapping("/{userName}")
//    jis username aur password se ye /user waali api hit krege to apne aap username and passowrd aa jaayega
//    hmhe yaha explicitly mention krne ki zaroorat nahi pdegee

    public ResponseEntity<?> updateUser(@RequestBody User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User userInDb = userService.findByUserName(userName);
        if(userInDb !=null){
            userInDb.setUserName(user.getUserName());
            userInDb.setPassword(user.getPassword());
            userService.saveNewUser(userInDb);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUserById(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userRepository.deleteByUserName(authentication.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

package com.house.demo.controllers;

import com.house.demo.classes.*;
import com.house.demo.repositories.ApplicationRepository;
import com.house.demo.repositories.SettingsRepository;
import com.house.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
public class ControllerRest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @GetMapping("/api/users/{id}")
    public User user( @PathVariable("id") long id) {
        User user = userRepository.getOne(id);
        return user;
    }

    @GetMapping("/api/users_by_name/{name}")
    public User user( @PathVariable("name") String username) {
        User user = userRepository.findByUsername(username);
        return user;
    }




    private User get_current_user() {
        MyUserDetails principal = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User current_user = principal.getUser();

        return current_user;
    }


    @GetMapping("/api/me")
    public User me() {
        long id = get_current_user().getId();
        User user_ = userRepository.getOne(id);

        return user_;
    }


    @GetMapping("/api/settings")
    public Settings settings() {
        Settings s = settingsRepository.getSettings();
        return s;
    }


    @PostMapping("/api/{id}/create_application")
    public Application createApp(@PathVariable("id") long id, @RequestBody Application application) {
        User user = userRepository.getOne(id);
        application.setUser(user);
        applicationRepository.save(application);

        return application;
    }

    @PostMapping("/api/users/{id}")
    public User updateUser( @PathVariable("id") long id,  @RequestBody User user) {

        User user_ = userRepository.getOne(id);
        user_.setEmail(user.getEmail());

        userRepository.save(user_);
        return user_;
    }

    @RequestMapping(value="/api/katataxi", method = RequestMethod.GET)
    public List <User> katataxi(Model model) {
        List<User> users = userRepository.findBySpecificRole("student");

        List <User> valid_users = new ArrayList<User>();

        // add valid users
        for (User user : users) {
            if (user.getApplication()!=null) {
                if (user.getApplication().getScore()>=0) {
                    valid_users.add(user);
                }
            }
        }

        Collections.sort(valid_users, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return -o1.getApplication().getScore().compareTo(o2.getApplication().getScore());
            }
        });

        return valid_users;
    }


}
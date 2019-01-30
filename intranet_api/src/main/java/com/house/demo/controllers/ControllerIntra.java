package com.house.demo.controllers;

import com.house.demo.classes.*;
import com.house.demo.repositories.*;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ControllerIntra {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SettingsRepository settingsRepository;


    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private RoleRepository roleRepository;


    private Role get_current_user_role() {
        MyUserDetails principal = (MyUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User current_user = principal.getUser();
        Role current_user_role = new Role();

        for (Role r : current_user.getRoles()) {
            current_user_role = r;
        }

        return current_user_role;
    }

    @RequestMapping(value="/intranet/users", method = RequestMethod.GET)
    public String users(Model model) {
        Role current_user_role = get_current_user_role();
        int users_to_return = -1;
        for (Privilege privilege : current_user_role.getPrivileges()) {
            if (privilege.getName().equals("see_all_users")) {
                users_to_return = 0;
            } else if (privilege.getName().equals("see_all_students")) {
                users_to_return = 1;
            }
        }

        if (users_to_return == 0) {
            model.addAttribute("users", userRepository.findAll());
        }
        else if (users_to_return == 1) {
            model.addAttribute("users", userRepository.findBySpecificRole("student"));
        }

        return "users";
    }

    @RequestMapping(value="/intranet/katataxi", method = RequestMethod.GET)
    public String katataxi(Model model) {
        List <User> users = userRepository.findBySpecificRole("student");

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

        model.addAttribute("users", valid_users);

        return "katataxi";
    }


    @GetMapping("/intranet/create_user")
    public String create_user(Model model) {
        Role current_user_role = get_current_user_role();
        int allowed = 0;
        for (Privilege privilege : current_user_role.getPrivileges()) {
            if (privilege.getName().equals("create_update_user")) {
                allowed = 1;
            }
        }



        if (allowed == 0) {
            return "redirect:/intranet/not_allowed";
        }


        model.addAttribute("user", new User());
        model.addAttribute("departments", departmentRepository.findAll());
        return "create_user";
    }

    @GetMapping("/intranet/not_allowed")
    public String not_allowed() {
        return "not_allowed";
    }

    @GetMapping("/intranet/update_settings")
    public String get_update_settings(Model model) {
        Role current_user_role = get_current_user_role();
        int allowed = 0;
        for (Privilege privilege : current_user_role.getPrivileges()) {
            if (privilege.getName().equals("update_house_limit")) {
                allowed = 1;
            }
        }

        if (allowed == 0) {
            return "redirect:/intranet/not_allowed";
        }


        model.addAttribute("settings", settingsRepository.getSettings());
        return "update_settings";
    }

    @PostMapping("/intranet/update_settings")
    public String submit_user(@ModelAttribute Settings settings, Model model) {
        Settings s = settingsRepository.getSettings();
        s.setHouse_limit(settings.getHouse_limit());
        settingsRepository.save(s);

        model.addAttribute("settings", s);
        model.addAttribute("success", true);
        return "update_settings";
    }


    @GetMapping("/intranet/users/{id}")
    public String user( @PathVariable("id") long id, Model model) {
        User user = userRepository.getOne(id);
        model.addAttribute("user", user);
        model.addAttribute("app", applicationRepository.getOne(user.getApplication().getId()));
        return "show_user";
    }

    @GetMapping(value="/intranet/users/{id}", produces = "application/json")
    @ResponseBody
    public User user_json( @PathVariable("id") long id) {
        User user = userRepository.getOne(id);
        return user;
    }


    @GetMapping(value="/intranet/users_by_name/{username}", produces = "application/json")
    @ResponseBody
    public User user_json( @PathVariable("username") String username) {
        User user = userRepository.findByUsername(username);
        return user;
    }


    @PostMapping("/intranet/create_user")
    public String submit_user(@ModelAttribute User user) {
        Role r = roleRepository.findByName("student");

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRoles(Arrays.asList(r));
        userRepository.save(user);
        return "result";
    }

    @PostMapping("/intranet/change_application_status/{id}")
    public String change_application_status(@PathVariable("id") long id, @ModelAttribute Application application, Model model) {

        Role current_user_role = get_current_user_role();

        int allowed = 0;
        for (Privilege privilege : current_user_role.getPrivileges()) {
            if (privilege.getName().equals("update_student_application_status")) {
                allowed = 1;
            }
        }

        if (allowed == 0) {
            return "redirect:/intranet/not_allowed";
        }


        Application app = applicationRepository.getOne(id);
        System.out.println(application.getStatus());
        app.setStatus(application.getStatus());
        applicationRepository.save(app);

        model.addAttribute("user", app.getUser());

        return "redirect:/intranet/users/" + app.getUser().getId();
    }
}
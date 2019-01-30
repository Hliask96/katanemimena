package com.example.front.controllers;

import com.example.front.classes.Application;
import com.example.front.classes.Settings;
import com.example.front.classes.User;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.security.Principal;
import java.util.*;

@Controller
public class ControllerExtra {


    HttpHeaders createHeaders(String email, String password) {
        HttpHeaders acceptHeaders = new HttpHeaders() {
            {
                set(com.google.common.net.HttpHeaders.ACCEPT,
                        MediaType.APPLICATION_JSON.toString());
            }
        };
        String authorization = email + ":" + password;
        String basic = new String(Base64.encodeBase64
                (authorization.getBytes(Charset.forName("US-ASCII"))));
        acceptHeaders.set("Authorization", "Basic " + basic);

        return acceptHeaders;
    }

    @GetMapping("/")
    public String user(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        RestTemplate restTemplate = new RestTemplate();


        HttpEntity<Principal> entity = new HttpEntity<Principal>(createHeaders(name, password));

        ResponseEntity<User> a = restTemplate.exchange("http://localhost:8080/api/users_by_name/"+name, HttpMethod.GET, entity, User.class);

        User user = a.getBody();


        ResponseEntity<Settings> b = restTemplate.exchange("http://localhost:8080/api/settings", HttpMethod.GET, entity, Settings.class);

        Settings s = b.getBody();
        model.addAttribute("s", s.getHouse_limit());

        boolean newApp = false;

        if (user.getApplication() == null) {
            System.out.println("No app");
            newApp = true;
            model.addAttribute("application", new Application());

        }
        else {
            ResponseEntity<User[]> katataxi = restTemplate.exchange("http://localhost:8080/api/katataxi/", HttpMethod.GET, entity, User[].class);

            User[] valid_users = katataxi.getBody();

            int k = 1;
            boolean found = false;

            for (User userf : valid_users) {
                if (userf.getId() == user.getId()) {
                    found = true;
                    break;
                }
                k+=1;
            }

            int lefta = 0, stegasi = 0, tipota = 0;
            if (k < s.getHouse_limit() && found) {
                stegasi = 1;
            }
            else if (k < s.getHouse_limit()+100 && found) {
                lefta = 1;
            }
            else if (found) {
                tipota = 1;
            }

            model.addAttribute("stegasi", stegasi);
            model.addAttribute("lefta", lefta);
            model.addAttribute("tipota", tipota);

            model.addAttribute("spot", k);
        }
        model.addAttribute("user", user);
        model.addAttribute("newApp", newApp);

        return "show_user";

    }

    @PostMapping("/users/{id}")
    public String updateUser( @PathVariable("id") long id, Model model, @RequestParam("email") String email) {

        RestTemplate restTemplate = new RestTemplate();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        HttpEntity<Principal> entity = new HttpEntity<Principal>(createHeaders(name, password));

        ResponseEntity<User> a = restTemplate.exchange("http://localhost:8080/api/users_by_name/"+name, HttpMethod.GET, entity, User.class);

        User user = a.getBody();


        user.setEmail(email);

        ResponseEntity<User> b = restTemplate.exchange("http://localhost:8080/api/users/"+id, HttpMethod.POST, new HttpEntity<>(user, createHeaders(name, password)), User.class);
        user = b.getBody();

    //        user = restTemplate.getForObject("http://localhost:8080/api/users/"+id, User.class);

        model.addAttribute("user", user);
        return "redirect:/";
    }


    @PostMapping("/create_application")
    public String createApp( Model model, @ModelAttribute Application application) {

        RestTemplate restTemplate = new RestTemplate();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        HttpEntity<Principal> entity = new HttpEntity<Principal>(createHeaders(name, password));

        ResponseEntity<User> a = restTemplate.exchange("http://localhost:8080/api/users_by_name/"+name, HttpMethod.GET, entity, User.class);

        User user = a.getBody();

        application.setUser(user);

        System.out.println(application.getYear());
        application.setStatus(0);

        ResponseEntity<Application> b = restTemplate.exchange("http://localhost:8080/api/"+user.getId()+"/create_application", HttpMethod.POST, new HttpEntity<>(application, createHeaders(name, password)), Application.class);

        a = restTemplate.exchange("http://localhost:8080/api/users_by_name/"+name, HttpMethod.GET, entity, User.class);
        user = a.getBody();

        model.addAttribute("user", user);
        return "redirect:/";
    }

}
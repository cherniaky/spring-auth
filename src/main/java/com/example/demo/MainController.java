package com.example.demo;

import java.security.Principal;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/secured")
public class MainController {

    @GetMapping("/user")
    public String userAccess() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        if (user == null) {
            return "You are not logged in!";
        }
        return "You are logged in as " + user;
    }
}

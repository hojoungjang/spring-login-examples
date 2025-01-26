package com.github.hojoungjang.spring_login_examples.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserViewController {
    
    private final Logger log = LoggerFactory.getLogger(getClass());

    @GetMapping("/login")
    public String loginPage(Principal principal) {
        if (principal != null) {
            log.info("Already logged in as " + principal.getName());
            return "redirect:/";
        }
        return "oauthlogin";
    }
    
    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }
    
}

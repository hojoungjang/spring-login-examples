package com.github.hojoungjang.spring_login_examples.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.github.hojoungjang.spring_login_examples.domain.User;
import com.github.hojoungjang.spring_login_examples.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class HomeController {
    private final UserService userService;

    @GetMapping("/")
    public String home(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("name", user.getNickname());
        return "index";
    }
    
}

package com.github.hojoungjang.spring_login_examples.controller;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.hojoungjang.spring_login_examples.dto.AddUserRequest;
import com.github.hojoungjang.spring_login_examples.service.UserService;


@RequiredArgsConstructor
@Controller
public class UserApiController {
    private final UserService userService;

    @PostMapping("/user")
    public String signUp(@RequestBody AddUserRequest request) {
        userService.save(request);
        return "redirect:/login";
    }
    
}

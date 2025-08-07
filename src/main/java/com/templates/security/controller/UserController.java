package com.templates.security.controller;

import com.templates.security.dto.ApiResponse;
import com.templates.security.dto.LoginDto;
import com.templates.security.dto.UserDto;
import com.templates.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<UserDto> createUser(@RequestBody UserDto dto){
        return userService.createUser(dto);
    }

    @PostMapping("/login")
    public ApiResponse<String> createUser(@RequestBody LoginDto dto){
        return userService.getToken(dto);
    }

}

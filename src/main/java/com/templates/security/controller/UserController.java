package com.templates.security.controller;

import com.templates.security.dto.ApiResponse;
import com.templates.security.dto.LoginDto;
import com.templates.security.dto.UserDto;
import com.templates.security.dto.UserResponseDto;
import com.templates.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    @PreAuthorize(value = "authentication.principal.users.id == #id")
    public ApiResponse<UserResponseDto> getUserById(@RequestParam("id") Integer id){
        return userService.getUserById(id);
    }

}

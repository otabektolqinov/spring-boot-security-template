package com.templates.security.controller;

import com.templates.security.dto.*;
import com.templates.security.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
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
    public ApiResponse<TokenResponseDto> getToke(@RequestBody LoginDto dto, HttpServletResponse response){
        return userService.getToken(dto, response);
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponseDto> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ){
        return userService.refreshToken(refreshToken);
    }

    @GetMapping
    @PreAuthorize(value = "authentication.principal.id == #id")
    public ApiResponse<UserResponseDto> getUserById(@RequestParam("id") Integer id){
        return userService.getUserById(id);
    }

    @DeleteMapping
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ApiResponse<UserResponseDto> deleteUserById(@RequestParam("id") Integer id){
        return userService.deleteUserById(id);
    }

}

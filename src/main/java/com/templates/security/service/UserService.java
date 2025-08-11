package com.templates.security.service;

import com.templates.security.config.JwtUtil;
import com.templates.security.domain.Users;
import com.templates.security.dto.ApiResponse;
import com.templates.security.dto.LoginDto;
import com.templates.security.dto.UserDto;
import com.templates.security.dto.UserResponseDto;
import com.templates.security.repository.UserRepository;
import com.templates.security.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public ApiResponse<UserDto> createUser(UserDto dto){
        Users entity = userMapper.toEntity(dto);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        Users users = userRepository.save(entity);

        return ApiResponse.<UserDto>builder()
                .content(userMapper.toDto(users))
                .message("Successfully saved User")
                .success(true)
                .build();
    }


    public ApiResponse<String> getToken(LoginDto dto) {
        // todo: We need to check whether exists in our system or not.
        // todo: Next, We need to check password is correct or not
        // todo: Authentication handles all these checks, and it integrates easily with other security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Optional<Users> byUsername = userRepository.findByUsername(userDetails.getUsername());

        // todo: When you create a token, you can add extra info as claim. Example userId
        String jwt = jwtUtil.createAccessToken(userDetails.getUsername(), byUsername.get().getId());

        return ApiResponse.<String>builder()
                .success(true)
                .message("OK")
                .content(jwt)
                .build();
    }

    public ApiResponse<UserResponseDto> getUserById(Integer id) {
        Optional<Users> optional = userRepository.findById(id);
        if (optional.isEmpty())
            return ApiResponse.<UserResponseDto>builder()
                    .message("User not found")
                    .success(false)
                    .build();

        return ApiResponse.<UserResponseDto>builder()
                .success(true)
                .message("OK")
                .content(userMapper.toResponseDto(optional.get()))
                .build();
    }
}

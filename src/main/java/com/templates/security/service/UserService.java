package com.templates.security.service;

import com.templates.security.config.JwtUtil;
import com.templates.security.domain.Users;
import com.templates.security.dto.*;
import com.templates.security.repository.UserRepository;
import com.templates.security.service.mapper.UserMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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


    public ApiResponse<TokenResponseDto> getToken(LoginDto dto, HttpServletResponse response) {
        // todo: We need to check whether exists in our system or not.
        // todo: Next, We need to check password is correct or not
        // todo: Authentication handles all these checks, and it integrates easily with other security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Optional<Users> byUsername = userRepository.findByUsername(userDetails.getUsername());

        // todo: When you create a token, you can add extra info as claim. Example userId
        // todo: For mobile and desktop apps, you better send both access and refresh tokens

        // todo: For web apps, you have to send only the access token
        // todo: And set the refresh token to the cookie, and mark it as http only
        // todo: This prevents XSS attacks

        String accessToken = jwtUtil.createAccessToken(userDetails.getUsername(), byUsername.get().getId());
        String refreshToken = jwtUtil.createRefreshToken(userDetails.getUsername(), byUsername.get().getId());

        // todo: For mobile and desktop
        /*return ApiResponse.<TokenResponseDto>builder()
                .success(true)
                .message("OK")
                .content(
                        TokenResponseDto.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build())
                .build();*/

        // todo: Why we need it, when request comes to the path, it automatically retrieves the token
        // todo: And it can validate and return new token
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/users/refresh")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ApiResponse.<TokenResponseDto>builder()
                .message("OK")
                .success(true)
                .content(TokenResponseDto.builder()
                        .accessToken(accessToken)
                        .build())
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

    public ApiResponse<UserResponseDto> deleteUserById(Integer id) {
        Optional<Users> optional = userRepository.findById(id);
        if (optional.isEmpty())
            return ApiResponse.<UserResponseDto>builder()
                    .message("User not found")
                    .success(false)
                    .build();

        userRepository.deleteById(id);

        return ApiResponse.<UserResponseDto>builder()
                .message("User successfully deleted")
                .success(true)
                .build();
    }

    public ApiResponse<TokenResponseDto> refreshToken(String refreshToken){
        if (refreshToken == null || !jwtUtil.validateRefreshToken(refreshToken)) {
            return ApiResponse.<TokenResponseDto>builder()
                    .message("Invalid Refresh token")
                    .build();
        }

        String username = jwtUtil.getClaim("sub", refreshToken, String.class);
        Optional<Users> byUsername = userRepository.findByUsername(username);

        // todo: You can update the logics' implementations
        if (byUsername.isEmpty())
            return ApiResponse.<TokenResponseDto>builder()
                    .message("User does not exist")
                    .build();

        String accessToken = jwtUtil.createAccessToken(username, byUsername.get().getId());
        return ApiResponse.<TokenResponseDto>builder()
                .success(true)
                .content(TokenResponseDto.builder()
                        .accessToken(accessToken)
                        .build())
                .message("ok")
                .build();
    }
}

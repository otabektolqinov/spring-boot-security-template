package com.templates.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.templates.security.dto.ApiResponse;
import com.templates.security.dto.ErrorDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // todo: We should check AuthHeader is Bearer or not
        // todo: If yes we extract token and username
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtUtil.isTokenExpired(token)){
            response.setStatus (HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            var errorDto = new ErrorDto(
                    request.getRequestURI (),
                    "Token expired. Please log in again.",
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            response.getWriter ().write (objectMapper.writeValueAsString (errorDto));
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(token, username)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                response.setStatus (HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                var errorDto = new ErrorDto (
                        request.getRequestURI (),
                        "Token is invalid. Please check your JWT token.",
                        HttpServletResponse.SC_UNAUTHORIZED
                );
                response.getWriter ().write (objectMapper.writeValueAsString (errorDto));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

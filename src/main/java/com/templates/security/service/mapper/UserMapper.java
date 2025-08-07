package com.templates.security.service.mapper;

import com.templates.security.domain.Users;
import com.templates.security.dto.UserDto;
import com.templates.security.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public Users toEntity(UserDto dto){
        return Users.builder()
                .username(dto.getUsername())
                .age(dto.getAge())
                .role(Role.valueOf(dto.getRole()))
                .build();
    }

    public UserDto toDto(Users users){
        return UserDto.builder()
                .age(users.getAge())
                .id(users.getId())
                .username(users.getUsername())
                .build();
    }
}

package com.templates.security.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Integer id;
    private String username;
    private String password;
    private String role;
    private Integer age;

}

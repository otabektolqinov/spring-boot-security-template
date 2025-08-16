package com.templates.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
public class ErrorDto {
    private final String errorPath;
    private final String errorMessage;
    private final int errorCode;
    private final LocalDateTime timestamp;

    public ErrorDto(String errorPath, String errorMessage, int errorCode) {
        this.errorPath = errorPath;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now(Clock.system(ZoneId.of("Asia/Tashkent")));
    }
}

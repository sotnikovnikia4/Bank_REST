package com.example.bankcards.exception;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ExceptionMessage {
    private int status;
    private String message;
    private Date timestamp;
}

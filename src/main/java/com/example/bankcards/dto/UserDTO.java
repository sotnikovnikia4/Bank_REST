package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDTO {
    private UUID id;

    private String role;

    private String login;

    private String name;
}

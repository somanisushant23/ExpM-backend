package com.userservice.expmbff.dto;

import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoginProfileDto {
    @Email(message = "Email must be a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    private String password;
}

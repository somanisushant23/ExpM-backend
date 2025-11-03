package com.userservice.expmbff.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserProfileDto {

    @Size(min = 3, max = 30, message = "Name should be more than 3 & less than 30 characters")
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email must be a valid email address")
    @NotBlank(message = "Email is required")
    @Size(max = 150, message = "Email must be less than or equal to 150 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be 8-20 characters long")
    @Pattern(
            regexp = ".*[!@#$%^&*()_+\\-\\[\\]{}|;:'\",.<>/?].*",
            message = "Password must contain at least one special character"
    )
    private String password;
}

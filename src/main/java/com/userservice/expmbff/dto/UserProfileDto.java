package com.userservice.expmbff.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserProfileDto {

    @Size(min = 3, max = 30, message = "Name should be more than 3 & less than 10 characters")
    @NotBlank(message = "Name is required")
    private String name;

    private String email;

    private String password;
}

package com.userservice.expmbff.dto;

import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoginProfileDto {
    private String email;

    private String password;
}

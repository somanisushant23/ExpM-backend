package com.userservice.expmbff.dto;

import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserProfileResponse {
    private String name;

    private String email;

    private String token;
}

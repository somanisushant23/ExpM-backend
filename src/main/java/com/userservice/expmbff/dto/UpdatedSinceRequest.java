package com.userservice.expmbff.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UpdatedSinceRequest {

    @NotNull(message = "updatedTime is required")
    @Min(value = 0, message = "updatedTime must be non-negative")
    private Long updatedTime; // epoch millis
}

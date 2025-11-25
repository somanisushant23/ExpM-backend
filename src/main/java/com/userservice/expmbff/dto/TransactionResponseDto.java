package com.userservice.expmbff.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TransactionResponseDto {

    private Long id;
    private String title, description, category, transactionType;
    private Integer amount;
    private LocalDate transactionDate;
    private Long createdOn, updatedOn;
    private String clientId;
}

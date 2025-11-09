package com.userservice.expmbff.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TransactionResponseDto {

    private Long id;
    private String title, description, category, transactionType;
    private Integer amount;
    private Date transactionDate;
    private Long createdOn, updatedOn;
}

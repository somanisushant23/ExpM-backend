package com.userservice.expmbff.dto;

import com.userservice.expmbff.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TransactionDto {

    @NotBlank(message = "Title is required")
    @Size(max = 30, message = "Email must be less than or equal to 30 characters")
    private String title;

    @Size(max = 100, message = "Email must be less than or equal to 100 characters")
    private String description;

    @NotNull(message = "Amount is required")
    private Integer amount;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @NotNull(message = "Transaction date is required")
    private Date transactionDate;

}

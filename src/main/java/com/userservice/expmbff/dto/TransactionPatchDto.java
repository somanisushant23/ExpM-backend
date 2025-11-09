package com.userservice.expmbff.dto;

import com.userservice.expmbff.entity.TransactionType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TransactionPatchDto {
    // All fields optional for patching
    @Size(max = 30, message = "Title must be <= 30 characters")
    private String title;

    @Size(max = 100, message = "Description must be <= 100 characters")
    private String description;

    @Size(max = 20, message = "Category must be <= 20 characters")
    private String category;

    private Integer amount;
    private TransactionType transactionType;
    private Date transactionDate;
}

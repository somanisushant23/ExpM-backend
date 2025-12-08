package com.userservice.expmbff.dto;

import com.userservice.expmbff.entity.enums.InvestmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class InvestmentDto {

    @NotNull(message = "Investment Type is required")
    private InvestmentType investmentType;//FD, RD, Shares, MF, Gold, etc.

    @Size(max = 20, message = "ID Number must be less than or equal to 20 characters")
    private String idNumber;//FD number, RD number, etc.

    @NotNull(message = "Amount is required")
    private Integer amount;//principal amount

    private Float expectedReturnRate;//in percentage

    @NotNull(message = "Transaction date is required")
    @JsonFormat(pattern = "dd-MM-yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate creationDate;

    @JsonFormat(pattern = "dd-MM-yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate maturityDate;

    @Size(max = 100, message = "Description must be less than or equal to 100 characters")
    private String description;

    private Long createdOn;

    @NotBlank(message = "ClientId is required")
    private String clientId;
}

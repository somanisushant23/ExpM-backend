package com.userservice.expmbff.dto;
import com.userservice.expmbff.entity.enums.InvestmentType;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class InvestmentResponseDto {
    private Long id;
    private InvestmentType investmentType;
    private String description, clientId, amount, investmentAccountNumber;
    private Float expectedReturnRate;
    @JsonFormat(pattern = "dd-MM-yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate creationDate;
    @JsonFormat(pattern = "dd-MM-yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate maturityDate;
    private Long createdOn, updatedOn;
    private String institutionName;
}

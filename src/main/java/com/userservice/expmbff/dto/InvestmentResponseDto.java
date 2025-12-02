package com.userservice.expmbff.dto;
import com.userservice.expmbff.entity.enums.InvestmentType;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class InvestmentResponseDto {
    private Long id;
    private InvestmentType investmentType;
    private String idNumber, description, clientId;
    private Integer amount;
    private Float expectedReturnRate;
    private LocalDate creationDate,maturityDate;
    private Long createdOn, updatedOn;
}

package com.userservice.expmbff.entity;

import com.userservice.expmbff.entity.enums.InvestmentType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InvestmentHistoryKey implements Serializable {
    @Column(nullable = false)
    private Long timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestmentType investmentType;
}


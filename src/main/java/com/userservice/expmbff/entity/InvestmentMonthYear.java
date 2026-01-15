package com.userservice.expmbff.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InvestmentMonthYear implements Serializable {
    private String month;
    private String year;
}


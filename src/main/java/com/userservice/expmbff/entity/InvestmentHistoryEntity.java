package com.userservice.expmbff.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.EmbeddedId;
import lombok.*;

@Entity
@Table(name = "investment_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentHistoryEntity {
    @EmbeddedId
    private InvestmentHistoryKey investmentHistoryKey;

    @Column(nullable = false)
    private String amount;//principal amount

}

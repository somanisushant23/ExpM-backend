package com.userservice.expmbff.entity;

import com.userservice.expmbff.entity.enums.InvestmentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "investment_info")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentEntity extends BaseModelEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestmentType investmentType;//FD, RD, Shares, MF, Gold, etc.

    @Column(nullable = true)
    private String idNumber;//FD number, RD number, etc.

    @Column(nullable = false)
    private Integer amount;//principal amount

    @Column(nullable = true)
    private Float expectedReturnRate;//in percentage

    @Column(nullable = false)
    private LocalDate creationDate;

    @Column(nullable = true)
    private LocalDate maturityDate;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private UUID clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserEntity user;

}

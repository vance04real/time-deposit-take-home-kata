package org.ikigaidigital.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "time_deposits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeDepositEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "plan_type", nullable = false)
    private String planType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private Integer days;

    @OneToMany(mappedBy = "timeDeposit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WithdrawalEntity> withdrawals = new ArrayList<>();
}
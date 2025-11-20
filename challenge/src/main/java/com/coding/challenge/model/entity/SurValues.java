package com.coding.challenge.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "SurValues")
@Data
@NoArgsConstructor
public class SurValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Company", length = 1, nullable = false)
    private String company;

    @Column(name = "Chdrnum", length = 8, nullable = false)
    private String chdrnum;

    @Column(name = "SurrenderValue", precision = 15, scale = 2, nullable = false)
    private BigDecimal surrenderValue;

    @Column(name = "Currency", length = 3)
    private String currency;

    @Column(name = "ValidDate", length = 10)
    private String validDate;
}

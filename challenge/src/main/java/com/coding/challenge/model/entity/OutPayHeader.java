package com.coding.challenge.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "OutPay_Header")
@Data
@NoArgsConstructor
public class OutPayHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Clntnum", length = 8, nullable = false)
    private String clntnum;

    @Column(name = "Chdrnum", length = 8, nullable = false)
    private String chdrnum;

    @Column(name = "LetterType", length = 12, nullable = false)
    private String letterType;

    @Column(name = "PrintDate", nullable = false)
    private LocalDate printDate;

    @Column(name = "DataID", length = 6)
    private String dataID;

    @Column(name = "ClntName", length = 80)
    private String clntName;

    @Column(name = "ClntAddress", length = 80)
    private String clntAddress;

    @Column(name = "RegDate")
    private LocalDate regDate;

    @Column(name = "BenPercent", precision = 6, scale = 2)
    private BigDecimal benPercent;

    @Column(name = "Role1", length = 2)
    private String role1;

    @Column(name = "Role2", length = 2)
    private String role2;

    @Column(name = "CownNum", length = 8)
    private String cownNum;

    @Column(name = "cownName", length = 80)
    private String cownName;

    @Column(name = "Notice01", length = 80)
    private String notice01;

    @Column(name = "Notice02", length = 80)
    private String notice02;

    @Column(name = "Notice03", length = 80)
    private String notice03;

    @Column(name = "Notice04", length = 80)
    private String notice04;

    @Column(name = "Notice05", length = 80)
    private String notice05;

    @Column(name = "Notice06", length = 80)
    private String notice06;

    @Column(name = "Claim_ID", length = 9)
    private String claim_ID;

    @Column(name = "TP2ProcessDate")
    private LocalDate tP2ProcessDate;
}

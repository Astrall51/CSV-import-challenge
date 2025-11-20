package com.coding.challenge.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Policy")
@Data
@NoArgsConstructor
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Chdrnum", length = 8, nullable = false)
    private String chdrnum;

    @Column(name = "Cownnum", length = 8, nullable = false)
    private String cownnum;

    @Column(name = "OwnerName", length = 50)
    private String ownerName;

    @Column(name = "LifcNum", length = 8)
    private String lifcNum;

    @Column(name = "LifcName", length = 50)
    private String lifcName;

    @Column(name = "Aracde", length = 3)
    private String aracde;

    @Column(name = "Agntnum", length = 5)
    private String agntnum;

    @Column(name = "MailAddress", length = 200)
    private String MailAddress;
}

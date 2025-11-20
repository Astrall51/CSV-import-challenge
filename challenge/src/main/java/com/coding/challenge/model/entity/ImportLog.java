package com.coding.challenge.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Import_Log")
@Data
@NoArgsConstructor
public class ImportLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String fileName;

    private LocalDateTime processedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImportStatusEnum status;

    public ImportLog(String fileName, ImportStatusEnum status) {
        this.fileName = fileName;
        this.status = status;
        this.processedAt = LocalDateTime.now();
    }
}

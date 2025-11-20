package com.coding.challenge.repository;

import com.coding.challenge.model.entity.ImportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportLogRepository extends JpaRepository<ImportLog, Long> {
    boolean existsByFileName(String fileName);
}

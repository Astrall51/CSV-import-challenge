package com.coding.challenge.repository;

import com.coding.challenge.model.entity.OutPayHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutPayHeaderRepository extends JpaRepository<OutPayHeader, Long> {
}

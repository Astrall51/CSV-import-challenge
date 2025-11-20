package com.coding.challenge.repository;

import com.coding.challenge.model.entity.SurValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurValuesRepository extends JpaRepository<SurValues, Long> {
}

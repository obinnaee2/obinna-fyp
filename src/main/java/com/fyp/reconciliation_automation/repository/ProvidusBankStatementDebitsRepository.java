package com.fyp.reconciliation_automation.repository;

import com.fyp.reconciliation_automation.entity.ProvidusBankStatementDebits;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProvidusBankStatementDebitsRepository extends JpaRepository<ProvidusBankStatementDebits, Long> {
    List<ProvidusBankStatementDebits> findAll();
}

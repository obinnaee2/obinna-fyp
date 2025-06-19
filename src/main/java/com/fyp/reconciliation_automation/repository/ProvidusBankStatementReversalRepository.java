package com.fyp.reconciliation_automation.repository;

import com.fyp.reconciliation_automation.entity.ProvidusBankStatementReversals;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProvidusBankStatementReversalRepository extends JpaRepository<ProvidusBankStatementReversals, Long> {
    List<ProvidusBankStatementReversals> findAll();
}

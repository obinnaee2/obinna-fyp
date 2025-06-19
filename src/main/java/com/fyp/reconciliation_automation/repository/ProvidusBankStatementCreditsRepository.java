package com.fyp.reconciliation_automation.repository;

import com.fyp.reconciliation_automation.entity.ProvidusBankStatementCredits;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProvidusBankStatementCreditsRepository extends JpaRepository<ProvidusBankStatementCredits, Long> {
    List<ProvidusBankStatementCredits> findAll();
}

package com.fyp.reconciliation_automation.repository;

import com.fyp.reconciliation_automation.entity.PremiumTrustBankStatementDebits;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PremiumTrustDebitsRepository extends JpaRepository<PremiumTrustBankStatementDebits, Long> {
    List<PremiumTrustBankStatementDebits> findAll();
}

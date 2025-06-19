package com.fyp.reconciliation_automation.repository;

import com.fyp.reconciliation_automation.entity.PremiumTrustBankStatementCredits;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PremiumTrustCreditsRepository extends JpaRepository<PremiumTrustBankStatementCredits, Long> {
    List<PremiumTrustBankStatementCredits> findAll();
}

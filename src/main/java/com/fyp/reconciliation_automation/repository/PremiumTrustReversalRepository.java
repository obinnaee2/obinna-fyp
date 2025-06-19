package com.fyp.reconciliation_automation.repository;

import com.fyp.reconciliation_automation.entity.PremiumTrustReversals;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PremiumTrustReversalRepository extends JpaRepository<PremiumTrustReversals, Long> {
    List<PremiumTrustReversals> findAll();
}

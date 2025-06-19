package com.fyp.reconciliation_automation.repository;

import com.fyp.reconciliation_automation.entity.CreditFailed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditFailedRepository extends JpaRepository<  CreditFailed, String> {
}

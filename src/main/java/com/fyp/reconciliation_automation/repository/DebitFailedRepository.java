package com.fyp.reconciliation_automation.repository;

import com.fyp.reconciliation_automation.entity.DebitFailed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebitFailedRepository extends JpaRepository<DebitFailed, String> {
}

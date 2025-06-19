package com.fyp.reconciliation_automation.repository;

import com.fyp.reconciliation_automation.entity.NibbsAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface NibbsAuditLogRepository extends JpaRepository<NibbsAuditLog, Long> {
    Optional<NibbsAuditLog> findByUploadDateAndWindow(LocalDate uploadDate, String window);
}
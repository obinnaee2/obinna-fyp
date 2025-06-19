package com.fyp.reconciliation_automation.repository;

import com.fyp.reconciliation_automation.entity.OtherDocumentsAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface OtherDocumentsAuditLogRepository extends JpaRepository<OtherDocumentsAuditLog, Long> {
    OtherDocumentsAuditLog findByUploadDate(LocalDate uploadDate);
}
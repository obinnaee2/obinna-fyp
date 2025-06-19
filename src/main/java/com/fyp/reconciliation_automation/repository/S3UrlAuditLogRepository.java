package com.fyp.reconciliation_automation.repository;

import com.fyp.reconciliation_automation.entity.S3UrlAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface S3UrlAuditLogRepository extends JpaRepository<S3UrlAuditLog, Long> {
    List<S3UrlAuditLog> findByUploadDateAndFileType(LocalDate uploadDate, String fileType);
    List<S3UrlAuditLog> findByUploadDateAndTimeWindow(LocalDate uploadDate, String timeWindow);
    List<S3UrlAuditLog> findByUploadDate(LocalDate uploadDate);
    List<S3UrlAuditLog> findByFileType(String fileType);
}
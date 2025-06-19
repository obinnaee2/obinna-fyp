package com.fyp.reconciliation_automation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "s3_url_audit_log", schema = "recon")
@Data
public class S3UrlAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "upload_date", nullable = false)
    private LocalDate uploadDate;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "time_window")
    private String timeWindow;

    @Column(name = "s3_url", nullable = true)
    private String s3Url;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
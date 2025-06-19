package com.fyp.reconciliation_automation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "recon",name = "nibbs_audit_log")
public class NibbsAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "upload_date", nullable = false)
    private LocalDate uploadDate;

    @Column(name = "time_window", nullable = false)
    private String window;

    @Column(name = "debit_successful", nullable = false)
    private String debitSuccessful;

    @Column(name = "credit_failed", nullable = false)
    private String creditFailed;

    @Column(name = "credit_successful", nullable = false)
    private String creditSuccessful;

    @Column(name = "debit_advice_failed", nullable = false)
    private String debitAdviceFailed;

    @Column(name = "debit_advice_successful", nullable = false)
    private String debitAdviceSuccessful;

    @Column(name = "debit_failed", nullable = false)
    private String debitFailed;


}

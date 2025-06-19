package com.fyp.reconciliation_automation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nibbs_central_recon", schema = "recon")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NibbsCentralRecon {

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "transaction_reference")
    private String transactionReference;

    @Id
    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "debit_successful")
    private Boolean debitSuccessful;

    @Column(name = "credit_successful")
    private Boolean creditSuccessful;

    @Column(name = "debit_failed")
    private Boolean debitFailed;

    @Column(name = "credit_failed")
    private Boolean creditFailed;

    @Column(name = "debit_advise_successful")
    private Boolean debitAdviseSuccessful;

    @Column(name = "debit_advise_failed")
    private Boolean debitAdviseFailed;

    @Column(name = "expected_status")
    private String expectedStatus;

    @Column(name = "originator_account_number")
    private String originatorAccountNumber;

    @Column(name = "amount")
    private String amount;

    @Column(name = "bank_match")
    private String bankMatch;

    @Column(name = "narration")
    private String narration;

    @Column(name = "meta_match")
    private String metaMatch;

    @Column(name = "final_status")
    private String finalStatus;

    @Column(name = "upload_date")
    private LocalDate uploadDate;

}

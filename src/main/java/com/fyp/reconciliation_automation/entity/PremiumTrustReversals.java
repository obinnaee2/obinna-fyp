package com.fyp.reconciliation_automation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "premiumtrust_bank_statement_failed_reversals", schema = "recon"
        // uniqueConstraints = {
        //     @UniqueConstraint(columnNames = "session_id", name = "unique_session_id")
        // }
)
public class PremiumTrustReversals {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "premium_trust_bank_statement_seq")
    @SequenceGenerator(name = "premium_trust_bank_statement_seq", sequenceName = "premium_trust_bank_statement_SEQ", allocationSize = 2000)
    @Column(name = "id")
    private Long id;

    @Column(name = "trans_date")
    private String transactionDate;

    @Column(name = "value_date")
    private String valueDate;

    @Column(name = "transaction_details")
    private String transactionDetails;

    @Column(name = "inst_no")
    private String institutionNumber;

    @Column(name = "dr_amount")
    private String debitAmount;

    @Column(name = "cr_amount")
    private String creditAmount;

    @Column(name = "balance")
    private String balance;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "match")
    private String match;

    @Column(name = "status")
    private String status;
}

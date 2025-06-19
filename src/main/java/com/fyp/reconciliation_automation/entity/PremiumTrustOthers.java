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
        name = "premiumtrust_others",
        schema = "recon"
)
public class PremiumTrustOthers {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "premium_trust_seq")
    @SequenceGenerator(name = "premium_trust_seq", sequenceName = "premium_trust_SEQ", allocationSize = 2000)
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

    @Column(name = "ref_no")
    private String referenceNumber;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "match")
    private String match;

    @Column(name = "status")
    private String status;
}
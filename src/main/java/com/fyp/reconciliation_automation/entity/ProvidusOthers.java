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
        name = "providus_others", schema = "recon"
        // uniqueConstraints = {
        //     @UniqueConstraint(columnNames = "session_id", name = "unique_session_id")
        // }
)
public class ProvidusOthers {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "providus_others_seq")
    @SequenceGenerator(name = "providus_others_seq", sequenceName = "providus_others_SEQ", allocationSize = 2000)
    @Column(name = "id")
    private Long id;

    @Column(name = "match")
    private String match;

    @Column(name = "status")
    private String status;

    @Column(name = "tra_date")
    private String transactionDate;

    @Column(name = "val_date")
    private String valueDate;

    @Column(name = "acct_no")
    private String accountNumber;

    @Column(name = "acct_name")
    private String accountName;

    @Column(name = "debit")
    private String debit;

    @Column(name = "credit")
    private String credit;

    @Column(name = "crnt_bal")
    private String currentBalance;

    @Column(name = "tra_type")
    private String transactionType;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "reference")
    private String reference;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "beneficiary_name")
    private String beneficiaryName;

    @Column(name = "act_tra_date")
    private String actualTransactionDate;

    @Column(name = "upd_time")
    private String updateTime;

    @Column(name = "tra_seq1")
    private String transactionSequence1;

    @Column(name = "tra_seq2")
    private String transactionSequence2;
}
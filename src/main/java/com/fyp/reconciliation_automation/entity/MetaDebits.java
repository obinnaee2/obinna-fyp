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
@Table(schema = "recon", name = "meta_debits")
public class MetaDebits {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "transaction_reference")
    private String transactionReference;

    @Column(name = "transaction_amount")
    private String transactionAmount;

    @Column(name = "transaction_fee")
    private String transactionFee;

    @Column(name = "debit_amount")
    private String debitAmount;

    @Column(name = "credit_amount")
    private String creditAmount;

    @Column(name = "balance_before")
    private String balanceBefore;

    @Column(name = "balance_after")
    private String balanceAfter;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "transaction_narration")
    private String transactionNarration;

    @Column(name = "transaction_status")
    private String transactionStatus;

    @Column(name = "response_code")
    private String responseCode;

    @Column(name = "response_message")
    private String responseMessage;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "to_account")
    private String toAccount;

    @Column(name = "payout_processor")
    private String payoutProcessor;

    @Column(name = "currency")
    private String currency;

    @Column(name = "banks_name")
    private String banksName;

    @Column(name = "banks_currency_code")
    private String banksCurrencyCode;

    @Column(name = "transaction_date")
    private String transactionDate;

    @Column(name = "bank_chargesy")
    private String bankChargesy;

    @Column(name = "sessionid")
    private String sessionId;

    @Column(name = "match_statement")
    private String matchStatement;

    @Column(name = "nibbs_match")
    private String nibbsMatch;

    @Column(name = "nibbs_status")
    private String nibbsStatus;

    @Column(name = "status")
    private String status;

    @Column(name = "matched_bank")
    private String matchedBank;


}

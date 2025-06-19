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
@Table(schema = "recon",name = "metabase")
public class TempMetabase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "temp_metabase_seq")
    @SequenceGenerator(name = "temp_metabase_seq", sequenceName = "temp_metabase_SEQ", allocationSize = 2000)
    private Long id;

    private String accountName;
    private String transactionReference;
    private String transactionAmount;
    private String transactionFee;
    private String debitAmount;
    private String creditAmount;
    private String balanceBefore;
    private String balanceAfter;
    private String bankChargesY;
    private String transactionType;
    private String transactionNarration;
    private String transactionStatus;
    private String responseCode;
    private String responseMessage;
    private String sessionID;
    private String bankName;
    private String toAccount;
    private String payoutProcessor;
    private String currency;
    private String banksName;
    private String banksCurrencyCode;
    private String transactionDate;

}

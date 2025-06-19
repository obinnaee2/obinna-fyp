package com.fyp.reconciliation_automation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetabaseTransactionDTO {

    private Long id;
    private Long approvedBy;
    private Long branchFk;
    private Long businessFk;
    private String fromAccount;
    private Long merchantFk;
    private String payazaAccountTransactionReference;
    private String payazaTransactionNarration;
    private Long settlementLedgerFk;
    private String statusDescription;
    private String toAccount;
    private BigDecimal transactionAmount;
    private Timestamp transactionDate;
    private String transactionNarration;
    private String transactionReference;
    private String transactionStatus;
    private String transactionType;
    private Long trialBalanceFk;
    private Timestamp updatedAt;
    private Long payazaAccountId;
    private String responseCode;
    private String responseMessage;
    private String sessionId;
    private String batchId;
    private String tranchId;
    private String bankCode;
    private String bankName;
    private String transactionFee;
    private BigDecimal balanceAfter;
    private BigDecimal balanceBefore;
    private Long requeryCount;
    private Long retryCount;
    private Integer groupId;
    private String retryResponseCode;
    private String retryResponseMessage;
    private Boolean isReversed;
    private Long serialId;
    private BigDecimal debitAmount;
    private BigDecimal reversed_Amount;
    private Timestamp processingDate;
    private BigDecimal escrowAfter;
    private BigDecimal escrowBefore;
    private BigDecimal creditAmount;
    private String creditName;
    private String debitName;
    private String amount;
    private Boolean isProcessing;
    private Boolean isQueryProcessing;
    private Boolean isEscrowProcessing;
    private Boolean isFundingProcessing;
    private Boolean notificationStatus;
    private String senderAddress;
    private String senderId;
    private String senderPhoneNumber;
    private String senderName;
    private String tenant;
    private Timestamp nipRequestDate;
    private Timestamp nipResponseDate;
    private BigDecimal reversedAmount;
    private Long balanceOrder;
    private String currencyCode;
    private String countryCode;
    private String payoutProcessor;
    private Boolean fundingMovementStatus;
    private Timestamp createdOn;
    private Timestamp modifiedAt;
    private String payazaAccountNumber;
    private String transactionCategory;
    private Boolean pendingFraudCheck;
    private Boolean sentToFraudnet;
    private String fraudnetStatus;
    private String ipAddress;
}

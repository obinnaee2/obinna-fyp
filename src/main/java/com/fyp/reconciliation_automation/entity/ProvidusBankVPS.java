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
        name = "providus_bank_vps",
        schema = "recon"
)
public class ProvidusBankVPS {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "providus_bank_vps_seq")
    @SequenceGenerator(name = "providus_bank_vps_seq", sequenceName = "providus_bank_vps_SEQ", allocationSize = 2000)
    @Column(name = "id")
    private Long id;

    @Column(name = "session_id", unique = true)
    private String sessionId;

    @Column(name = "settlement_ref")
    private String settlementRef;

    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "transaction_amount_minor")
    private String transactionAmountMinor;

    @Column(name = "settled_amount_minor")
    private String settledAmountMinor;

    @Column(name = "charge_amount_minor")
    private String chargeAmountMinor;

    @Column(name = "vat_amount_minor")
    private String vatAmountMinor;

    @Column(name = "currency")
    private String currency;

    @Column(name = "notification_acknowledgement")
    private String notificationAcknowledgement;

    @Column(name = "num_retries")
    private String numRetries;

    @Column(name = "retry_batch_id")
    private String retryBatchId;

    @Column(name = "failed_count")
    private String failedCount;

    @Column(name = "source_acct_name")
    private String sourceAcctName;

    @Column(name = "source_acct_no")
    private String sourceAcctNo;

    @Column(name = "source_bank_code")
    private String sourceBankCode;

    @Column(name = "virtual_acct_no")
    private String virtualAcctNo;

    @Column(name = "account_ref_code")
    private String accountRefCode;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @Column(name = "narration")
    private String narration;

    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "post_flg")
    private String postFlg;

    @Column(name = "stamp_duty_flg")
    private String stampDutyFlg;

    @Column(name = "cba_tran_time")
    private String cbaTranTime;

    @Column(name = "reason")
    private String reason;

    @Column(name = "reversal_session_id")
    private String reversalSessionId;

    @Column(name = "settlement_notification_retry_batch_id")
    private String settlementNotificationRetryBatchId;

    @Column(name = "match_meta")
    private String matchMeta;

    @Column(name = "match_statement")
    private String matchStatement;

    @Column(name = "status")
    private String status;
}
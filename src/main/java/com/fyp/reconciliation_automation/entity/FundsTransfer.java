package com.fyp.reconciliation_automation.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "fund_transfer", schema = "recon")
public class FundsTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fund_trans_seq")
    @SequenceGenerator(name = "fund_trans_seq", sequenceName = "fund_trans_SEQ", allocationSize = 2000)
    @Column(name = "id")
    private Long id;

    @Column(name = "fund_transfer_id")
    private String fundTransferId;

    @Column(name = "created_on")
    private String createdOn;

    @Column(name = "modified_on")
    private String modifiedOn;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "currency")
    private String currency;

    @Column(name = "dest_account")
    private String destAccount;

    @Column(name = "dest_account_name")
    private String destAccountName;

    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "name_enquiry_reference")
    private String nameEnquiryReference;

    @Column(name = "narration")
    private String narration;

    @Column(name = "processor")
    private String processor;

    @Column(name = "request_uuid")
    private String requestUuid;

    @Column(name = "response_code")
    private String responseCode;

    @Column(name = "response_message")
    private String responseMessage;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "source_account")
    private String sourceAccount;

    @Column(name = "transaction_amount")
    private String transactionAmount;

    @Column(name = "transaction_reference")
    private String transactionReference;

    @Column(name = "transaction_status")
    private String transactionStatus;

    @Column(name = "sender_sender_id")
    private String senderSenderId;

    @Column(name = "requery_count")
    private Integer requeryCount;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "requery_response_code")
    private String requeryResponseCode;

    @Column(name = "requery_response_message")
    private String requeryResponseMessage;

    @Column(name = "source_account_name")
    private String sourceAccountName;

    @Column(name = "destination_bank_name")
    private String destinationBankName;

    @Column(name = "processor_reference")
    private String processorReference;

    @Column(name = "retry_processor")
    private String retryProcessor;

    @Column(name = "retry_processor_reference")
    private String retryProcessorReference;
}
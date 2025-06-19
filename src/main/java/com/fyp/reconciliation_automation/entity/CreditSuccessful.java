package com.fyp.reconciliation_automation.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "nibss_credit_successful",
        schema = "recon",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "session_id", name = "credit_failed_session_id"),
                @UniqueConstraint(columnNames = "transaction_id", name = "credit_failed_transaction_id")
        }
)
public class CreditSuccessful {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credit_successful_seq")
    @SequenceGenerator(name = "credit_successful_seq", sequenceName = "credit_successful_id_seq", allocationSize = 2000)
    private Long id;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "status")
    private String status;

    @Column(name = "message")
    private String message;

    @Column(name = "nip_response_code")
    private String nipResponseCode;

    @Column(name = "name_enquiry_ref")
    private String nameEnquiryRef;

    @Column(name = "destination_institution_code")
    private String destinationInstitutionCode;

    @Column(name = "channel_code")
    private String channelCode;

    @Column(name = "beneficiary_account_name")
    private String beneficiaryAccountName;

    @Column(name = "beneficiary_account_number")
    private String beneficiaryAccountNumber;

    @Column(name = "beneficiary_bvn")
    private String beneficiaryBvn;

    @Column(name = "beneficiary_kyc_level")
    private String beneficiaryKycLevel;

    @Column(name = "originator_account_name")
    private String originatorAccountName;

    @Column(name = "originator_account_number")
    private String originatorAccountNumber;

    @Column(name = "originator_bvn")
    private String originatorBvn;

    @Column(name = "originator_kyc_level")
    private String originatorKycLevel;

    @Column(name = "narration")
    private String narration;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "amount")
    private String amount;

    @Column(name = "transaction_location")
    private String transactionLocation;

    @Column(name = "initiatior_account_name")
    private String initiatorAccountName;

    @Column(name = "initiatior_account_number")
    private String initiatorAccountNumber;

    @Column(name = "payaza_reference")
    private String payazaReference;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @JsonProperty("transactionDate")
    public String getFormattedTransactionDate() {
        return transactionDate != null ? transactionDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }

}

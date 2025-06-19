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
        name = "nibss_debit_advice_failed",
        schema = "recon",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "session_id", name = "credit_failed_session_id"),
                @UniqueConstraint(columnNames = "transaction_id", name = "credit_failed_transaction_id")
        }
)
public class DebitAdviceFailed extends AbstractPersistableEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "debit_advice_failed_seq")
    @SequenceGenerator(name = "debit_advice_failed_seq", sequenceName = "debit_advice_failed_id_seq", allocationSize = 2000)
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

}

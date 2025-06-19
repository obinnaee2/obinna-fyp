package com.fyp.reconciliation_automation.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "meta_collections", schema = "recon")
public class MetaCollections {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meta_collections_seq")
    @SequenceGenerator(name = "recon.meta_collections_SEQ", sequenceName = "recon.meta_collections_SEQ", allocationSize = 2000)
    @Column(name = "id")
    private Long id;

    @Column(name = "transaction_value_amount")
    private String transactionValueAmount;

    @Column(name = "fee_amount")
    private String feeAmount;

    @Column(name = "partner_transaction_amount")
    private String partnerTransactionAmount;

    @Column(name = "partner_fee_amount")
    private String partnerFeeAmount;

    @Column(name = "currency_fk")
    private String currencyFk;

    @Column(name = "business_fk_name")
    private String businessFkName;

    @Column(name = "business_branch_fk_name")
    private String businessBranchFkName;

    @Column(name = "partner")
    private String partner;

    @Column(name = "partner_transaction_reference")
    private String partnerTransactionReference;

    @Column(name = "ended_at")
    private String endedAt;

    @Column(name = "match")
    private String match;

    @Column(name = "match_status")
    private String matchStatus;

}

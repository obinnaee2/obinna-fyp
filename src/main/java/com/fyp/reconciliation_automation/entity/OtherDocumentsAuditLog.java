package com.fyp.reconciliation_automation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "recon", name = "other_docs_audit_log")
public class OtherDocumentsAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "upload_date", nullable = false)
    private LocalDate uploadDate;

    @Column(name = "providus_bank_statement", nullable = false)
    private String providusBankStatement;

    @Column(name = "providus_bank_vps", nullable = false)
    private String providusBankVps;

    @Column(name = "premium_trust_bank_vps", nullable = false)
    private String premiumTrustBankVps;

    @Column(name = "premium_trust_bank_statement", nullable = false)
    private String premiumTrustBankStatement;

    @Column(name = "funds_transfer", nullable = false)
    private String fundsTransfer;

    @Column(name = "temp_metabase", nullable = false)
    private String tempMetabase;

    @Column(name = "meta_collections", nullable = false)
    private String metaCollections;
}
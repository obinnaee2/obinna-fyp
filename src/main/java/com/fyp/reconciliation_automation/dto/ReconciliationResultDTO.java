package com.fyp.reconciliation_automation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class ReconciliationResultDTO {
    @JsonProperty("total_reconciled_amount")
    private BigDecimal reconciledAmount;
    @JsonProperty("total_unreconciled_amount")
    private BigDecimal unreconciledAmount;
    @JsonProperty("premium_trust_bank_statement")
    private BigDecimal premiumTrustBankStatement;
    @JsonProperty("providus_bank_statement")
    private BigDecimal providusBankStatement;
    @JsonProperty("reconciled_count")
    private Long reconciledCount;
    @JsonProperty("unreconciled_count")
    private Long unreconciledCount;

    public ReconciliationResultDTO(BigDecimal reconciledAmount, BigDecimal unreconciledAmount,
                                   BigDecimal premiumTrustBankStatement, BigDecimal providusBankStatement,
                                   Long reconciledCount, Long unreconciledCount) {
        this.reconciledAmount = reconciledAmount;
        this.unreconciledAmount = unreconciledAmount;
        this.premiumTrustBankStatement = premiumTrustBankStatement;
        this.providusBankStatement = providusBankStatement;
        this.reconciledCount = reconciledCount;
        this.unreconciledCount = unreconciledCount;
    }

    public BigDecimal getReconciledAmount() { return reconciledAmount; }
    public void setReconciledAmount(BigDecimal reconciledAmount) { this.reconciledAmount = reconciledAmount; }
    public BigDecimal getUnreconciledAmount() { return unreconciledAmount; }
    public void setUnreconciledAmount(BigDecimal unreconciledAmount) { this.unreconciledAmount = unreconciledAmount; }
    public BigDecimal getPremiumTrustBankStatement() { return premiumTrustBankStatement; }
    public void setPremiumTrustBankStatement(BigDecimal premiumTrustBankStatement) { this.premiumTrustBankStatement = premiumTrustBankStatement; }
    public BigDecimal getProvidusBankStatement() { return providusBankStatement; }
    public void setProvidusBankStatement(BigDecimal providusBankStatement) { this.providusBankStatement = providusBankStatement; }
    public Long getReconciledCount() { return reconciledCount; }
    public void setReconciledCount(Long reconciledCount) { this.reconciledCount = reconciledCount; }
    public Long getUnreconciledCount() { return unreconciledCount; }
    public void setUnreconciledCount(Long unreconciledCount) { this.unreconciledCount = unreconciledCount; }
}
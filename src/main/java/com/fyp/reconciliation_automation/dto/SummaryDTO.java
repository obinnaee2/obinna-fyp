package com.fyp.reconciliation_automation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class SummaryDTO {
    @JsonProperty("total_credit")
    private BigDecimal totalCredit;
    @JsonProperty("total_debit")
    private BigDecimal totalDebit;
    @JsonProperty("total_balance")
    private BigDecimal totalBalance;

    public SummaryDTO(BigDecimal totalCredit, BigDecimal totalDebit, BigDecimal totalBalance) {
        this.setTotalCredit(totalCredit);
        this.setTotalDebit(totalDebit);
        this.setTotalBalance(totalBalance);
    }

    public BigDecimal getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(BigDecimal totalCredit) {
        this.totalCredit = totalCredit;
    }

    public BigDecimal getTotalDebit() {
        return totalDebit;
    }

    public void setTotalDebit(BigDecimal totalDebit) {
        this.totalDebit = totalDebit;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }
}
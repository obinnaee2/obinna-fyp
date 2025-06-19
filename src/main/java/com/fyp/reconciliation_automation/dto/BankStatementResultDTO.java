package com.fyp.reconciliation_automation.dto;

public class BankStatementResultDTO {
    private SummaryDTO premiumtrust;
    private SummaryDTO providus;

    public BankStatementResultDTO(SummaryDTO premiumtrust, SummaryDTO providus) {
        this.setPremiumtrust(premiumtrust);
        this.setProvidus(providus);
    }

    public SummaryDTO getPremiumtrust() {
        return premiumtrust;
    }

    public void setPremiumtrust(SummaryDTO premiumtrust) {
        this.premiumtrust = premiumtrust;
    }

    public SummaryDTO getProvidus() {
        return providus;
    }

    public void setProvidus(SummaryDTO providus) {
        this.providus = providus;
    }
}

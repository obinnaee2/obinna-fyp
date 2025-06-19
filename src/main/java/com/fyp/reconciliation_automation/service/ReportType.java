package com.fyp.reconciliation_automation.service;

public enum ReportType {
    PROVIDUS_NIBSS_PREMIUM_TRUST("Providus, NIBSS, and Premium Trust"),
    PREMIUM_TRUST_NIBSS("Premium Trust and NIBSS"),
    PROVIDUS_NIBSS("Providus and NIBSS");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

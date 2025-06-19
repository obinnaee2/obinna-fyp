package com.fyp.reconciliation_automation.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FontConfiguration {
    @PostConstruct
    public void setProperty() {
        System.setProperty("org.apache.poi.ss.ignoreMissingFontSystem", "true");
    }
}
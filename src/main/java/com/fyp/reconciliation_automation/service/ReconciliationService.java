package com.fyp.reconciliation_automation.service;

import com.fyp.reconciliation_automation.dto.BankStatementResultDTO;
import com.fyp.reconciliation_automation.dto.ReconciliationResultDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Map;

public interface ReconciliationService {
    ReconciliationResultDTO getReconciliationStatusAmount(LocalDate uploadDate);
    BankStatementResultDTO getBankStatementSummary(LocalDate uploadDate);
    Page<Map<String, Object>> getAllReconciledTransactions(
            int page, int size, LocalDate uploadDate, LocalDate startDate, LocalDate endDate,
            String transactionReference, String sessionId,
            String transactionDate, String expectedStatus, String narration, String finalStatus,
            String searchPhrase);
    Page<Map<String, Object>> getAllUnreconciledTransactions(
            int page, int size, LocalDate uploadDate, LocalDate startDate, LocalDate endDate,
            String transactionReference, String sessionId,
            String transactionDate, String expectedStatus, String narration, String finalStatus,
            String searchPhrase);
    <T> Map<String, Object> buildPaginationResponse(Page<T> page);
    Map<String, Double> getReconciliationPercentage(LocalDate uploadDate);
}
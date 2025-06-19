package com.fyp.reconciliation_automation.service;

import com.fyp.reconciliation_automation.Pagination.PaginationResponse;
import com.fyp.reconciliation_automation.dto.BankStatementResultDTO;
import com.fyp.reconciliation_automation.dto.ReconciliationResultDTO;
import com.fyp.reconciliation_automation.dto.SummaryDTO;
import com.fyp.reconciliation_automation.repository.NibbsCentralReconRepository;
import com.fyp.reconciliation_automation.repository.PremiumTrustRepository;
import com.fyp.reconciliation_automation.repository.ProvidusBankRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReconciliationServiceImpl implements ReconciliationService {
    private final NibbsCentralReconRepository nibbsCentralReconRepository;
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final PremiumTrustRepository premiumTrustRepository;
    private final ProvidusBankRepository providusBankRepository;

    public ReconciliationServiceImpl(NibbsCentralReconRepository nibbsCentralReconRepository, JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                     PremiumTrustRepository premiumTrustRepository,
                                     ProvidusBankRepository providusBankRepository) {
        this.nibbsCentralReconRepository = nibbsCentralReconRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.premiumTrustRepository = premiumTrustRepository;
        this.providusBankRepository = providusBankRepository;
    }

    @Override
    public ReconciliationResultDTO getReconciliationStatusAmount(LocalDate uploadDate) {
        String premiumSql;
        if (uploadDate == null) {
            premiumSql = "SELECT COALESCE(SUM(REPLACE(COALESCE(dr_amount, '0')::TEXT, ',', '')::DECIMAL), 0) " +
                    "FROM recon.premiumtrust_bank_statement";
        } else {
            premiumSql = "SELECT COALESCE(SUM(REPLACE(COALESCE(dr_amount, '0')::TEXT, ',', '')::DECIMAL), 0) " +
                    "FROM recon.premiumtrust_bank_statement " +
                    "WHERE CAST(upload_date AS DATE) = ?";
        }

        BigDecimal premiumTrustStatement = jdbcTemplate.queryForObject(
                premiumSql, BigDecimal.class, uploadDate != null ? java.sql.Date.valueOf(uploadDate) : new Object[]{});

        String providusSql;
        if (uploadDate == null) {
            providusSql = "SELECT COALESCE(SUM(REPLACE(COALESCE(debit, '0')::TEXT, ',', '')::DECIMAL), 0) " +
                    "FROM recon.providus_bank_statement";
        } else {
            providusSql = "SELECT COALESCE(SUM(REPLACE(COALESCE(debit, '0')::TEXT, ',', '')::DECIMAL), 0) " +
                    "FROM recon.providus_bank_statement " +
                    "WHERE CAST(upload_date AS DATE) = ?";
        }

        BigDecimal providusBankStatement = jdbcTemplate.queryForObject(
                providusSql, BigDecimal.class, uploadDate != null ? java.sql.Date.valueOf(uploadDate) : new Object[]{});

        premiumTrustStatement = (premiumTrustStatement != null) ? premiumTrustStatement : BigDecimal.ZERO;
        providusBankStatement = (providusBankStatement != null) ? providusBankStatement : BigDecimal.ZERO;

        String reconSql;
        Object[] reconParams;
        if (uploadDate == null) {
            reconSql = "SELECT " +
                    "SUM(CASE WHEN final_status IN ('TRANSACTION REVERSED', 'TRANSACTION SUCCESSFUL', 'CONFIRM REVERSAL', 'REVERSED -CONFIRM REVERSAL', 'TRANSACTION SUCCESSFUL -CONFIRM FINAL STATUS') THEN CAST(amount AS DECIMAL) ELSE 0 END) AS reconciledAmount, " +
                    "COUNT(CASE WHEN final_status IN ('TRANSACTION REVERSED', 'TRANSACTION SUCCESSFUL', 'CONFIRM REVERSAL', 'REVERSED -CONFIRM REVERSAL', 'TRANSACTION SUCCESSFUL -CONFIRM FINAL STATUS') THEN 1 ELSE NULL END) AS reconciledCount, " +
                    "SUM(CASE WHEN final_status IN ('AWAITING REVERSAL', 'NO IMPACT IN BANK', 'ESCALATE TO BANK', 'ESCALATE TO ENGINEERING', 'ESCALATE TO BANK/ENGINEERING', 'CONFIRM FINAL STATUS', 'CONFIRM BANK') OR final_status IS NULL THEN CAST(amount AS DECIMAL) ELSE 0 END) AS unreconciledAmount, " +
                    "COUNT(CASE WHEN final_status IN ('AWAITING REVERSAL', 'NO IMPACT IN BANK', 'ESCALATE TO BANK', 'ESCALATE TO ENGINEERING', 'ESCALATE TO BANK/ENGINEERING', 'CONFIRM FINAL STATUS', 'CONFIRM BANK') OR final_status IS NULL THEN 1 ELSE NULL END) AS unreconciledCount " +
                    "FROM recon.nibbs_central_recon";
            reconParams = new Object[]{};
        } else {
            reconSql = "SELECT " +
                    "SUM(CASE WHEN final_status IN ('TRANSACTION REVERSED', 'TRANSACTION SUCCESSFUL', 'CONFIRM REVERSAL', 'REVERSED -CONFIRM REVERSAL', 'TRANSACTION SUCCESSFUL -CONFIRM FINAL STATUS') THEN CAST(amount AS DECIMAL) ELSE 0 END) AS reconciledAmount, " +
                    "COUNT(CASE WHEN final_status IN ('TRANSACTION REVERSED', 'TRANSACTION SUCCESSFUL', 'CONFIRM REVERSAL', 'REVERSED -CONFIRM REVERSAL', 'TRANSACTION SUCCESSFUL -CONFIRM FINAL STATUS') THEN 1 ELSE NULL END) AS reconciledCount, " +
                    "SUM(CASE WHEN final_status IN ('AWAITING REVERSAL', 'NO IMPACT IN BANK', 'ESCALATE TO BANK', 'ESCALATE TO ENGINEERING', 'ESCALATE TO BANK/ENGINEERING', 'CONFIRM FINAL STATUS', 'CONFIRM BANK') OR final_status IS NULL THEN CAST(amount AS DECIMAL) ELSE 0 END) AS unreconciledAmount, " +
                    "COUNT(CASE WHEN final_status IN ('AWAITING REVERSAL', 'NO IMPACT IN BANK', 'ESCALATE TO BANK', 'ESCALATE TO ENGINEERING', 'ESCALATE TO BANK/ENGINEERING', 'CONFIRM FINAL STATUS', 'CONFIRM BANK') OR final_status IS NULL THEN 1 ELSE NULL END) AS unreconciledCount " +
                    "FROM recon.nibbs_central_recon " +
                    "WHERE upload_date = ?";
            reconParams = new Object[]{java.sql.Date.valueOf(uploadDate)};
        }

        Map<String, Object> reconResult = jdbcTemplate.queryForMap(reconSql, reconParams);

        if (reconResult == null || reconResult.isEmpty()) {
            return null;
        }

        BigDecimal reconciledAmount = reconResult.get("reconciledAmount") != null ? new BigDecimal(reconResult.get("reconciledAmount").toString()) : BigDecimal.ZERO;
        Long reconciledCount = reconResult.get("reconciledCount") != null ? ((Number) reconResult.get("reconciledCount")).longValue() : 0L;
        BigDecimal unreconciledAmount = reconResult.get("unreconciledAmount") != null ? new BigDecimal(reconResult.get("unreconciledAmount").toString()) : BigDecimal.ZERO;
        Long unreconciledCount = reconResult.get("unreconciledCount") != null ? ((Number) reconResult.get("unreconciledCount")).longValue() : 0L;

        return new ReconciliationResultDTO(reconciledAmount, unreconciledAmount, premiumTrustStatement, providusBankStatement, reconciledCount, unreconciledCount);
    }

    @Override
    public BankStatementResultDTO getBankStatementSummary(LocalDate uploadDate) {

        String premiumSql;
        if (uploadDate == null) {
            premiumSql = "SELECT COALESCE(SUM(REPLACE(COALESCE(cr_amount, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_credit, " +
                    "COALESCE(SUM(REPLACE(COALESCE(dr_amount, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_debit, " +
                    "COALESCE(SUM(REPLACE(COALESCE(balance, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_balance " +
                    "FROM recon.premiumtrust_bank_statement";
        } else {
            premiumSql = "SELECT COALESCE(SUM(REPLACE(COALESCE(cr_amount, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_credit, " +
                    "COALESCE(SUM(REPLACE(COALESCE(dr_amount, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_debit, " +
                    "COALESCE(SUM(REPLACE(COALESCE(balance, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_balance " +
                    "FROM recon.premiumtrust_bank_statement " +
                    "WHERE CAST(upload_date AS DATE) = ?";
        }

        Map<String, Object> premiumResult = jdbcTemplate.queryForMap(
                premiumSql, uploadDate != null ? new Object[]{java.sql.Date.valueOf(uploadDate)} : new Object[]{});

        if (premiumResult == null || premiumResult.isEmpty()) {
            return null;
        }
        BigDecimal credit = premiumResult.get("total_credit") != null ? new BigDecimal(premiumResult.get("total_credit").toString()) : BigDecimal.ZERO;
        BigDecimal debit = premiumResult.get("total_debit") != null ? new BigDecimal(premiumResult.get("total_debit").toString()) : BigDecimal.ZERO;
        BigDecimal balance = premiumResult.get("total_balance") != null ? new BigDecimal(premiumResult.get("total_balance").toString()) : BigDecimal.ZERO;
        SummaryDTO premiumTrustSummary = new SummaryDTO(credit, debit, balance);

        String providusSql;
        if (uploadDate == null) {
            providusSql = "SELECT COALESCE(SUM(REPLACE(COALESCE(credit, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_credit, " +
                    "COALESCE(SUM(REPLACE(COALESCE(debit, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_debit, " +
                    "COALESCE(SUM(REPLACE(COALESCE(crnt_bal, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_balance " +
                    "FROM recon.providus_bank_statement";
        } else {
            providusSql = "SELECT COALESCE(SUM(REPLACE(COALESCE(credit, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_credit, " +
                    "COALESCE(SUM(REPLACE(COALESCE(debit, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_debit, " +
                    "COALESCE(SUM(REPLACE(COALESCE(crnt_bal, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_balance " +
                    "FROM recon.providus_bank_statement " +
                    "WHERE CAST(upload_date AS DATE) = ?";
        }

        Map<String, Object> providusResult = jdbcTemplate.queryForMap(
                providusSql, uploadDate != null ? new Object[]{java.sql.Date.valueOf(uploadDate)} : new Object[]{});

        SummaryDTO providusSummary;
        if (providusResult == null || providusResult.isEmpty()) {
            providusSummary = new SummaryDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        } else {
            BigDecimal providusCredit = providusResult.get("total_credit") != null ? new BigDecimal(providusResult.get("total_credit").toString()) : BigDecimal.ZERO;
            BigDecimal providusDebit = providusResult.get("total_debit") != null ? new BigDecimal(providusResult.get("total_debit").toString()) : BigDecimal.ZERO;
            BigDecimal providusBalance = providusResult.get("total_balance") != null ? new BigDecimal(providusResult.get("total_balance").toString()) : BigDecimal.ZERO;
            providusSummary = new SummaryDTO(providusCredit, providusDebit, providusBalance);
        }
        return new BankStatementResultDTO(premiumTrustSummary, providusSummary);
    }

    @Override
    public Page<Map<String, Object>> getAllReconciledTransactions(
            int page, int size, LocalDate uploadDate, LocalDate startDate, LocalDate endDate,
            String transactionReference, String sessionId,
            String transactionDate, String expectedStatus, String narration, String finalStatus,
            String searchPhrase) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            StringBuilder sql = new StringBuilder(RECONCILED_TRANSACTIONS);
            List<Object> params = new ArrayList<>();
            appendSearchAndFilterConditions(sql, params, uploadDate, startDate, endDate,
                    transactionReference, sessionId, transactionDate, expectedStatus,
                    narration, finalStatus, searchPhrase);

            return queryForPaginatedList(sql.toString(), pageable, params.toArray());
        } catch (Exception e) {
            log.error("Error fetching reconciled transactions: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch reconciled transactions", e);
        }
    }





    private static final String RECONCILED_TRANSACTIONS =
            "SELECT transaction_date, transaction_reference, session_id, expected_status, " +
                    "amount, narration, final_status, upload_date " +
                    "FROM recon.nibbs_central_recon " +
                    "WHERE final_status IN ('TRANSACTION REVERSED', 'TRANSACTION SUCCESSFUL', 'CONFIRM REVERSAL', 'REVERSED -CONFIRM REVERSAL', 'TRANSACTION SUCCESSFUL -CONFIRM FINAL STATUS')";





    private void appendSearchAndFilterConditions(StringBuilder sql, List<Object> params, LocalDate uploadDate,
                                                 LocalDate startDate, LocalDate endDate,
                                                 String transactionReference, String sessionId,
                                                 String transactionDate, String expectedStatus,
                                                 String narration, String finalStatus, String searchPhrase) {
        List<String> conditions = new ArrayList<>();

        if (startDate != null && endDate != null) {
            conditions.add("upload_date BETWEEN ? AND ?");
            params.add(java.sql.Date.valueOf(startDate));
            params.add(java.sql.Date.valueOf(endDate));
        } else if (uploadDate != null) {
            conditions.add("upload_date = ?");
            params.add(java.sql.Date.valueOf(uploadDate));
        }

        if (transactionReference != null && !transactionReference.trim().isEmpty()) {
            conditions.add("transaction_reference LIKE ?");
            params.add("%" + transactionReference + "%");
        }
        if (sessionId != null && !sessionId.trim().isEmpty()) {
            conditions.add("session_id LIKE ?");
            params.add("%" + sessionId + "%");
        }
        if (transactionDate != null && !transactionDate.trim().isEmpty()) {
            conditions.add("CAST(transaction_date AS DATE) = ?");
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate parsedTransactionDate = LocalDate.parse(transactionDate, formatter);
                params.add(java.sql.Date.valueOf(parsedTransactionDate));
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Invalid transactionDate: " + transactionDate + ". Expected format: YYYY-MM-DD", e);
            }
        }
        if (expectedStatus != null && !expectedStatus.trim().isEmpty()) {
            conditions.add("expected_status = ?");
            params.add(expectedStatus);
        }
        if (narration != null && !narration.trim().isEmpty()) {
            conditions.add("narration LIKE ?");
            params.add("%" + narration + "%");
        }
        if (finalStatus != null && !finalStatus.trim().isEmpty()) {
            conditions.add("final_status = ?");
            params.add(finalStatus);
        }
        if (searchPhrase != null && !searchPhrase.trim().isEmpty()) {
            String searchPattern = "%" + searchPhrase.trim() + "%";
            conditions.add("(" +
                    "transaction_reference LIKE ? OR " +
                    "session_id LIKE ? OR " +
                    "expected_status LIKE ? OR " +
                    "CAST(amount AS TEXT) LIKE ? OR " +
                    "narration LIKE ? OR " +
                    "final_status LIKE ?" +
                    ")");
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (!conditions.isEmpty()) {
            sql.append(" AND ").append(String.join(" AND ", conditions));
        }
    }

    private Page<Map<String, Object>> queryForPaginatedList(String sql, Pageable pageable, Object... params) {
        String countSql = "SELECT COUNT(*) FROM (" + sql + ") AS count_query";
        Long total = jdbcTemplate.queryForObject(countSql, Long.class, params);

        String paginatedSql = sql + " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
        List<Map<String, Object>> content = jdbcTemplate.queryForList(paginatedSql, params);

        return new PageImpl<>(content, pageable, total);
    }
    @Override
    public <T> Map<String, Object> buildPaginationResponse(Page<T> page) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", page.getContent());
        response.put("pagination", PaginationResponse.builder()
                .pageSize(page.getSize())
                .pageNumber(page.getNumber() + 1)
                .totalNumberOfItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build());
        return response;
    }

    @Override
    public Map<String, Double> getReconciliationPercentage(LocalDate uploadDate) {
        String sql;
        Object[] params;

        if (uploadDate == null) {
            sql = "SELECT " +
                    "COUNT(CASE WHEN final_status IN ('TRANSACTION REVERSED', 'TRANSACTION SUCCESSFUL', 'CONFIRM REVERSAL', 'REVERSED -CONFIRM REVERSAL', 'TRANSACTION SUCCESSFUL -CONFIRM FINAL STATUS') THEN 1 ELSE NULL END) AS reconciledCount, " +
                    "COUNT(CASE WHEN final_status IN ('AWAITING REVERSAL', 'NO IMPACT IN BANK', 'ESCALATE TO BANK', 'ESCALATE TO ENGINEERING', 'ESCALATE TO BANK/ENGINEERING', 'CONFIRM FINAL STATUS', 'CONFIRM BANK') OR final_status IS NULL THEN 1 ELSE NULL END) AS unreconciledCount " +
                    "FROM recon.nibbs_central_recon";
            params = new Object[]{};
        } else {
            sql = "SELECT " +
                    "COUNT(CASE WHEN final_status IN ('TRANSACTION REVERSED', 'TRANSACTION SUCCESSFUL', 'CONFIRM REVERSAL', 'REVERSED -CONFIRM REVERSAL', 'TRANSACTION SUCCESSFUL -CONFIRM FINAL STATUS') THEN 1 ELSE NULL END) AS reconciledCount, " +
                    "COUNT(CASE WHEN final_status IN ('AWAITING REVERSAL', 'NO IMPACT IN BANK', 'ESCALATE TO BANK', 'ESCALATE TO ENGINEERING', 'ESCALATE TO BANK/ENGINEERING', 'CONFIRM FINAL STATUS', 'CONFIRM BANK') OR final_status IS NULL THEN 1 ELSE NULL END) AS unreconciledCount " +
                    "FROM recon.nibbs_central_recon " +
                    "WHERE upload_date = ?";
            params = new Object[]{java.sql.Date.valueOf(uploadDate)};
        }

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, params);

        long reconciledCount = result.get("reconciledCount") != null ? ((Number) result.get("reconciledCount")).longValue() : 0L;
        long unreconciledCount = result.get("unreconciledCount") != null ? ((Number) result.get("unreconciledCount")).longValue() : 0L;

        long totalCount = reconciledCount + unreconciledCount;
        double reconciledPercentage = totalCount > 0 ? (reconciledCount * 100.0) / totalCount : 0;
        double unreconciledPercentage = totalCount > 0 ? (unreconciledCount * 100.0) / totalCount : 0;

        Map<String, Double> percentages = new HashMap<>();
        percentages.put("reconciledPercentage", Math.round(reconciledPercentage * 100.0) / 100.0);

        return percentages;
    }
    private static final String UNRECONCILED_TRANSACTIONS =
            "SELECT transaction_date, transaction_reference, session_id, expected_status, " +
                    "amount, narration, final_status, upload_date " +
                    "FROM recon.nibbs_central_recon " +
                    "WHERE 1=1";

    @Override
    public Page<Map<String, Object>> getAllUnreconciledTransactions(
            int page, int size, LocalDate uploadDate, LocalDate startDate, LocalDate endDate,
            String transactionReference, String sessionId,
            String transactionDate, String expectedStatus, String narration, String finalStatus,
            String searchPhrase) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            StringBuilder sql = new StringBuilder(UNRECONCILED_TRANSACTIONS);
            List<Object> params = new ArrayList<>();

            appendUnreconciledFilterConditions(sql, params, uploadDate, startDate, endDate,
                    transactionReference, sessionId, transactionDate, expectedStatus,
                    narration, finalStatus, searchPhrase);

            return queryForPaginatedList(sql.toString(), pageable, params.toArray());
        } catch (Exception e) {
            log.error("Error fetching unreconciled transactions: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch unreconciled transactions", e);
        }
    }

    private void appendUnreconciledFilterConditions(StringBuilder sql, List<Object> params, LocalDate uploadDate,
                                                    LocalDate startDate, LocalDate endDate,
                                                    String transactionReference, String sessionId,
                                                    String transactionDate, String expectedStatus,
                                                    String narration, String finalStatus, String searchPhrase) {
        List<String> conditions = new ArrayList<>();

        if (startDate != null && endDate != null) {
            conditions.add("CAST(upload_date AS DATE) BETWEEN ? AND ?");
            params.add(java.sql.Date.valueOf(startDate));
            params.add(java.sql.Date.valueOf(endDate));
        } else if (uploadDate != null) {
            conditions.add("CAST(upload_date AS DATE) = ?");
            params.add(java.sql.Date.valueOf(uploadDate));
        }

        if (transactionReference != null && !transactionReference.trim().isEmpty()) {
            conditions.add("transaction_reference = ?");
            params.add(transactionReference.trim());
        }

        if (sessionId != null && !sessionId.trim().isEmpty()) {
            conditions.add("session_id = ?");
            params.add(sessionId.trim());
        }

        if (transactionDate != null && !transactionDate.trim().isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate parsedTransactionDate = LocalDate.parse(transactionDate, formatter);
                conditions.add("CAST(transaction_date AS DATE) = ?");
                params.add(java.sql.Date.valueOf(parsedTransactionDate));
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Invalid transactionDate format", e);
            }
        }

        if (expectedStatus != null && !expectedStatus.trim().isEmpty()) {
            conditions.add("expected_status ILIKE ?");
            params.add("%" + expectedStatus.trim() + "%");
        }

        if (narration != null && !narration.trim().isEmpty()) {
            conditions.add("narration ILIKE ?");
            params.add("%" + narration.trim() + "%");
        }


        if (finalStatus != null && !finalStatus.trim().isEmpty()) {
            if ("null".equalsIgnoreCase(finalStatus.trim())) {
                conditions.add("final_status IS NULL");
            } else {
                conditions.add("final_status = ?");
                params.add(finalStatus.trim());
            }
        } else {
            conditions.add("(final_status IS NULL OR final_status IN ('PENDING', 'AWAITING REVERSAL', 'NO IMPACT IN BANK', " +
                    "'ESCALATE TO BANK', 'ESCALATE TO ENGINEERING', 'ESCALATE TO BANK/ENGINEERING', " +
                    "'CONFIRM FINAL STATUS', 'CONFIRM BANK'))");
        }


        if (searchPhrase != null && !searchPhrase.trim().isEmpty()) {
            String searchPattern = "%" + searchPhrase.trim() + "%";
            conditions.add("(" +
                    "transaction_reference ILIKE ? OR " +
                    "session_id ILIKE ? OR " +
                    "expected_status ILIKE ? OR " +
                    "narration ILIKE ? OR " +
                    "final_status ILIKE ? OR " +
                    "CAST(amount AS TEXT) ILIKE ?)");
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (!conditions.isEmpty()) {
            sql.append(" AND ").append(String.join(" AND ", conditions));
        }

        sql.append(" ORDER BY transaction_date DESC");
    }
}
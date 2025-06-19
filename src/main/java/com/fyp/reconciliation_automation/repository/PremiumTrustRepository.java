package com.fyp.reconciliation_automation.repository;

import com.fyp.reconciliation_automation.entity.PremiumTrustBankStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PremiumTrustRepository extends JpaRepository<PremiumTrustBankStatement, Long> {
    @Query(value = "SELECT COALESCE(SUM(REPLACE(COALESCE(dr_amount, '0')::TEXT, ',', '')::DECIMAL), 0) " +
            "FROM recon.premiumtrust_bank_statement " +
            "WHERE (CAST(:uploadDate AS DATE) IS NULL OR upload_date = CAST(:uploadDate AS DATE))", nativeQuery = true)
    BigDecimal getBankStatementSum(@Param("uploadDate") LocalDate uploadDate);

    @Query(value = "SELECT COALESCE(SUM(REPLACE(COALESCE(cr_amount, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_credit, " +
            "COALESCE(SUM(REPLACE(COALESCE(dr_amount, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_debit, " +
            "COALESCE(SUM(REPLACE(COALESCE(balance, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_balance " +
            "FROM recon.premiumtrust_bank_statement " +
            "WHERE (CAST(:uploadDate AS DATE) IS NULL OR upload_date = CAST(:uploadDate AS DATE))", nativeQuery = true)
    Object[] getSummary(@Param("uploadDate") LocalDate uploadDate);
}
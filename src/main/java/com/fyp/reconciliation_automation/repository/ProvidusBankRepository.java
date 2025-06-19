package com.fyp.reconciliation_automation.repository;

import com.fyp.reconciliation_automation.entity.ProvidusBankStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ProvidusBankRepository extends JpaRepository<ProvidusBankStatement, String> {
    @Query(value = "SELECT COALESCE(SUM(REPLACE(COALESCE(debit, '0')::TEXT, ',', '')::DECIMAL), 0) " +
            "FROM recon.providus_bank_statement " +
            "WHERE (CAST(:uploadDate AS DATE) IS NULL OR upload_date = CAST(:uploadDate AS DATE))", nativeQuery = true)
    BigDecimal getBankStatementSum(@Param("uploadDate") LocalDate uploadDate);

    @Query(value = "SELECT COALESCE(SUM(REPLACE(COALESCE(credit, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_credit, " +
            "COALESCE(SUM(REPLACE(COALESCE(debit, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_debit, " +
            "COALESCE(SUM(REPLACE(COALESCE(crnt_bal, '0')::TEXT, ',', '')::DECIMAL), 0) AS total_balance " +
            "FROM recon.providus_bank_statement " +
            "WHERE (CAST(:uploadDate AS DATE) IS NULL OR upload_date = CAST(:uploadDate AS DATE))", nativeQuery = true)
    Object[] getSummary(@Param("uploadDate") LocalDate uploadDate);
}
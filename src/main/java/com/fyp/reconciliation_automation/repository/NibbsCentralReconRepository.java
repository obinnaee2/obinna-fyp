package com.fyp.reconciliation_automation.repository;

import com.fyp.reconciliation_automation.entity.NibbsCentralRecon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface NibbsCentralReconRepository extends JpaRepository<NibbsCentralRecon, Long> {
    @Query(value = "SELECT" +
            "    SUM(CASE WHEN final_status IN ('TRANSACTION REVERSED', 'TRANSACTION SUCCESSFUL') THEN CAST(amount AS DECIMAL) ELSE 0 END) AS reconciledAmount," +
            "    COUNT(CASE WHEN final_status IN ('TRANSACTION REVERSED', 'TRANSACTION SUCCESSFUL') THEN 1 ELSE NULL END) AS reconciledCount," +
            "    SUM(CASE WHEN final_status IN ('AWAITING REVERSAL', 'NO IMPACT IN BANK', 'ESCALATE TO BANK/ENGINEERING', 'Unreconciled') OR final_status IS NULL THEN CAST(amount AS DECIMAL) ELSE 0 END) AS unreconciledAmount," +
            "    COUNT(CASE WHEN final_status IN ('AWAITING REVERSAL', 'NO IMPACT IN BANK', 'ESCALATE TO BANK/ENGINEERING', 'Unreconciled') OR final_status IS NULL THEN 1 ELSE NULL END) AS unreconciledCount" +
            " FROM recon.nibbs_central_recon" +
            " WHERE (CAST(:uploadDate AS DATE) IS NULL OR upload_date = CAST(:uploadDate AS DATE))", nativeQuery = true)
    Object[] getReconciliationStatusAmount(@Param("uploadDate") LocalDate uploadDate);
}
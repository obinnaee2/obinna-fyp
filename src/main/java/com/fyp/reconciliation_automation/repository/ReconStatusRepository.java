package com.fyp.reconciliation_automation.repository;

import com.fyp.reconciliation_automation.entity.ReconStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface ReconStatusRepository extends JpaRepository<ReconStatus, String> {
    Optional<ReconStatus> findByUploadDate(LocalDate uploadDate);
    Page<ReconStatus> findByStatus(String status, Pageable pageable);
    Page<ReconStatus> findByUploadDateAndStatus(LocalDate uploadDate, String status, Pageable pageable);
    Page<ReconStatus> findByReconId(String reconId, Pageable pageable);
    Page<ReconStatus> findByUploadDateAndReconId(LocalDate uploadDate, String reconId, Pageable pageable);
    Page<ReconStatus> findByStatusAndReconId(String status, String reconId, Pageable pageable);
    Page<ReconStatus> findByUploadDateAndStatusAndReconId(LocalDate uploadDate, String status, String reconId, Pageable pageable);
    Page<ReconStatus> findByUploadDate(LocalDate uploadDate, Pageable pageable);
    Page<ReconStatus> findByUploadDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<ReconStatus> findByUploadDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, String status, Pageable pageable);
    Page<ReconStatus> findByUploadDateBetweenAndReconId(LocalDate startDate, LocalDate endDate, String reconId, Pageable pageable);
    Page<ReconStatus> findByUploadDateBetweenAndStatusAndReconId(LocalDate startDate, LocalDate endDate, String status, String reconId, Pageable pageable);
}
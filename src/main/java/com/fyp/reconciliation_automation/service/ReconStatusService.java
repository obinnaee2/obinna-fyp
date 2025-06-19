package com.fyp.reconciliation_automation.service;

import com.fyp.reconciliation_automation.dto.ReconStatusDto;
import com.fyp.reconciliation_automation.entity.ReconStatus;
import com.fyp.reconciliation_automation.repository.ReconStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
public class ReconStatusService {
    private final ReconStatusRepository reconStatusRepository;

    public ReconStatusService(ReconStatusRepository reconStatusRepository) {
        this.reconStatusRepository = reconStatusRepository;
    }

    @Transactional
    public void updateReconStatusToStarted(String uploadDateStr) {
        try {
            LocalDate uploadDate = LocalDate.parse(uploadDateStr);
            Optional<ReconStatus> existingStatus = reconStatusRepository.findByUploadDate(uploadDate);

            if (existingStatus.isPresent()) {
                ReconStatus reconStatus = existingStatus.get();
                reconStatus.setStatus("In_progress");
                reconStatusRepository.save(reconStatus);
                log.info("Updated recon_status to In_progress for upload_date: {}", uploadDateStr);
            } else {
                String reconId = generateReconId(uploadDate);
                ReconStatus reconStatus = new ReconStatus();
                reconStatus.setReconId(reconId);
                reconStatus.setStatus("In_progress");
                reconStatus.setUploadDate(uploadDate);
                reconStatusRepository.save(reconStatus);
                log.info("Created new recon_status for upload_date: {}", uploadDateStr);
            }
        } catch (Exception e) {
            log.error("Error ensuring recon_status for upload_date: {}", uploadDateStr, e);
            throw new RuntimeException("Failed to ensure recon_status: " + e.getMessage());
        }
    }

    private String generateReconId(LocalDate uploadDate) {
        LocalDateTime now = LocalDateTime.now();
        String datePart = uploadDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String timePart = now.format(DateTimeFormatter.ofPattern("HHmmssSSS"));
        return "recon" + datePart + timePart;
    }

    @Transactional
    public void updateReconStatusToCompleted(String uploadDateStr) {
        try {
            LocalDate uploadDate = LocalDate.parse(uploadDateStr);
            Optional<ReconStatus> existingStatus = reconStatusRepository.findByUploadDate(uploadDate);

            if (existingStatus.isPresent()) {
                ReconStatus reconStatus = existingStatus.get();
                reconStatus.setStatus("Completed");
                reconStatusRepository.save(reconStatus);
                log.info("Updated recon_status to Completed for upload_date: {}", uploadDate);
            } else {
                log.warn("No recon_status record found for upload_date: {}", uploadDate);
            }
        } catch (Exception e) {
            log.error("Error updating recon_status to Completed for upload_date: {}", uploadDateStr, e);
            throw new RuntimeException("Failed to update recon_status: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<ReconStatusDto> getReconStatus(LocalDate uploadDate, LocalDate startDate, LocalDate endDate,
                                               String status, String reconId, Pageable pageable) {
        try {
            Page<ReconStatus> reconStatuses;

            if (startDate != null && endDate != null) {
                if (status != null && !status.trim().isEmpty()) {
                    if (reconId != null && !reconId.trim().isEmpty()) {
                        reconStatuses = reconStatusRepository.findByUploadDateBetweenAndStatusAndReconId(
                                startDate, endDate, status, reconId, pageable);
                    } else {
                        reconStatuses = reconStatusRepository.findByUploadDateBetweenAndStatus(
                                startDate, endDate, status, pageable);
                    }
                } else if (reconId != null && !reconId.trim().isEmpty()) {
                    reconStatuses = reconStatusRepository.findByUploadDateBetweenAndReconId(
                            startDate, endDate, reconId, pageable);
                } else {
                    reconStatuses = reconStatusRepository.findByUploadDateBetween(startDate, endDate, pageable);
                }
            } else if (uploadDate != null) {

                if (status != null && !status.trim().isEmpty()) {
                    if (reconId != null && !reconId.trim().isEmpty()) {
                        reconStatuses = reconStatusRepository.findByUploadDateAndStatusAndReconId(
                                uploadDate, status, reconId, pageable);
                    } else {
                        reconStatuses = reconStatusRepository.findByUploadDateAndStatus(
                                uploadDate, status, pageable);
                    }
                } else if (reconId != null && !reconId.trim().isEmpty()) {
                    reconStatuses = reconStatusRepository.findByUploadDateAndReconId(
                            uploadDate, reconId, pageable);
                } else {
                    reconStatuses = reconStatusRepository.findByUploadDate(uploadDate, pageable);
                }
            } else if (status != null && !status.trim().isEmpty()) {
                if (reconId != null && !reconId.trim().isEmpty()) {
                    reconStatuses = reconStatusRepository.findByStatusAndReconId(status, reconId, pageable);
                } else {
                    reconStatuses = reconStatusRepository.findByStatus(status, pageable);
                }
            } else if (reconId != null && !reconId.trim().isEmpty()) {
                reconStatuses = reconStatusRepository.findByReconId(reconId, pageable);
            } else {
                reconStatuses = reconStatusRepository.findAll(pageable);
            }

            return reconStatuses.map(rs -> new ReconStatusDto(rs.getReconId(), rs.getStatus(), rs.getUploadDate()));
        } catch (Exception e) {
            log.error("Error retrieving recon_status data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve recon_status data: " + e.getMessage());
        }
    }
}
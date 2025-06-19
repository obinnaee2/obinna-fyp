package com.fyp.reconciliation_automation.controller;

import com.fyp.reconciliation_automation.dto.ApiResponse;
import com.fyp.reconciliation_automation.dto.BankStatementResultDTO;
import com.fyp.reconciliation_automation.dto.ReconciliationResultDTO;
import com.fyp.reconciliation_automation.service.FileOperationsService;
import com.fyp.reconciliation_automation.service.ReconciliationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/reconciliation")
@Slf4j
public class ReconciliationController {
    private final ReconciliationService reconService;
    private final FileOperationsService fileOperationsService;

    @Autowired
    public ReconciliationController(ReconciliationService reconService, FileOperationsService fileOperationsService) {
        this.reconService = reconService;
        this.fileOperationsService = fileOperationsService;
    }

    @GetMapping("/dashboard-metrics")
    public ResponseEntity<ApiResponse<ReconciliationResultDTO>> getDashboardMetrics(
            @RequestParam(required = false) String uploadDate) {
        try {
            LocalDate parsedUploadDate = validateUploadDate(uploadDate);
            ReconciliationResultDTO response = reconService.getReconciliationStatusAmount(parsedUploadDate);
            if (response == null) {
                return ResponseEntity.ok(new ApiResponse<>(
                        "No reconciliation data found for upload date " + uploadDate,
                        String.valueOf(HttpStatus.OK.value())
                ));
            }
            return ResponseEntity.ok(new ApiResponse<>(
                    "Dashboard metrics retrieved successfully",
                    String.valueOf(HttpStatus.OK.value()),
                    response
            ));
        } catch (Exception e) {
            log.error("Error fetching dashboard metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    "Could not fetch reconciliation status amounts. Reason: " + e.getMessage(),
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
            ));
        }
    }

    @GetMapping("/bank-statement")
    public ResponseEntity<ApiResponse<BankStatementResultDTO>> getBankStatementMetrics(
            @RequestParam(required = false) String uploadDate) {
        try {
            LocalDate parsedUploadDate = validateUploadDate(uploadDate);
            BankStatementResultDTO response = reconService.getBankStatementSummary(parsedUploadDate);
            if (response == null) {
                return ResponseEntity.ok(new ApiResponse<>(
                        "No bank statement data found for upload date " + uploadDate,
                        String.valueOf(HttpStatus.OK.value())
                ));
            }
            return ResponseEntity.ok(new ApiResponse<>(
                    "Bank statement metrics retrieved successfully",
                    String.valueOf(HttpStatus.OK.value()),
                    response
            ));
        } catch (Exception e) {
            log.error("Error fetching bank statement metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    "Failed to get bank statement summary. Details: " + e.getMessage(),
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
            ));
        }
    }

    @GetMapping("/reconciled")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReconciledTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String uploadDate,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String transactionReference,
            @RequestParam(required = false) String sessionId,
            @RequestParam(required = false) String transactionDate,
            @RequestParam(required = false) String expectedStatus,
            @RequestParam(required = false) String narration,
            @RequestParam(required = false) String finalStatus,
            @RequestParam(required = false) String searchPhrase) {
        try {
            LocalDate parsedUploadDate = null;
            LocalDate parsedStartDate = null;
            LocalDate parsedEndDate = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            if (page < 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                        "Page number must be greater than 0",
                        String.valueOf(HttpStatus.BAD_REQUEST.value())
                ));
            }
            if (size < 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                        "Page size must be greater than 0",
                        String.valueOf(HttpStatus.BAD_REQUEST.value())
                ));
            }

            if (uploadDate != null && !uploadDate.trim().isEmpty()) {
                try {
                    parsedUploadDate = LocalDate.parse(uploadDate, formatter);
                    if (!fileOperationsService.isValidDate(uploadDate)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                                String.format("Invalid uploadDate '%s'. Expected format: YYYY-MM-DD (e.g., 2025-05-25)", uploadDate),
                                String.valueOf(HttpStatus.BAD_REQUEST.value())
                        ));
                    }
                } catch (DateTimeParseException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                            String.format("Invalid uploadDate '%s'. Expected format: YYYY-MM-DD (e.g., 2025-05-25)", uploadDate),
                            String.valueOf(HttpStatus.BAD_REQUEST.value())
                    ));
                }
            }

            if (startDate != null && !startDate.trim().isEmpty()) {
                try {
                    parsedStartDate = LocalDate.parse(startDate, formatter);
                    if (!fileOperationsService.isValidDate(startDate)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                                "Invalid start date format. Use YYYY-MM-DD",
                                String.valueOf(HttpStatus.BAD_REQUEST.value())
                        ));
                    }
                } catch (DateTimeParseException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                            "Invalid start date format. Use YYYY-MM-DD",
                            String.valueOf(HttpStatus.BAD_REQUEST.value())
                    ));
                }
            }

            if (endDate != null && !endDate.trim().isEmpty()) {
                try {
                    parsedEndDate = LocalDate.parse(endDate, formatter);
                    if (!fileOperationsService.isValidDate(endDate)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                                "Invalid end date format. Use YYYY-MM-DD",
                                String.valueOf(HttpStatus.BAD_REQUEST.value())
                        ));
                    }
                } catch (DateTimeParseException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                            "Invalid end date format. Use YYYY-MM-DD",
                            String.valueOf(HttpStatus.BAD_REQUEST.value())
                    ));
                }
            }

            if (parsedStartDate != null && parsedEndDate != null) {
                if (parsedStartDate.isAfter(parsedEndDate)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                            "startDate cannot be after endDate",
                            String.valueOf(HttpStatus.BAD_REQUEST.value())
                    ));
                }
                parsedUploadDate = null;
            } else if (parsedStartDate != null || parsedEndDate != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                        "Both startDate and endDate must be provided for date range",
                        String.valueOf(HttpStatus.BAD_REQUEST.value())
                ));
            }

            var transactions = reconService.getAllReconciledTransactions(
                    page, size, parsedUploadDate, parsedStartDate, parsedEndDate,
                    transactionReference, sessionId, transactionDate, expectedStatus,
                    narration, finalStatus, searchPhrase);
            return ResponseEntity.ok(new ApiResponse<>(
                    "Reconciled transactions retrieved successfully",
                    String.valueOf(HttpStatus.OK.value()),
                    reconService.buildPaginationResponse(transactions)
            ));
        } catch (Exception e) {
            log.error("Error retrieving reconciled transactions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    "Failed to retrieve reconciled transactions: " + e.getMessage(),
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
            ));
        }
    }

    @GetMapping("/unreconciled")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUnreconciledTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String uploadDate,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String transactionReference,
            @RequestParam(required = false) String sessionId,
            @RequestParam(required = false) String transactionDate,
            @RequestParam(required = false) String expectedStatus,
            @RequestParam(required = false) String narration,
            @RequestParam(required = false) String finalStatus,
            @RequestParam(required = false) String searchPhrase) {
        try {
            LocalDate parsedUploadDate = null;
            LocalDate parsedStartDate = null;
            LocalDate parsedEndDate = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            if (page < 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                        "Page number must be greater than 0",
                        String.valueOf(HttpStatus.BAD_REQUEST.value())
                ));
            }
            if (size < 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                        "Page size must be greater than 0",
                        String.valueOf(HttpStatus.BAD_REQUEST.value())
                ));
            }

            if (uploadDate != null && !uploadDate.trim().isEmpty()) {
                try {
                    parsedUploadDate = LocalDate.parse(uploadDate, formatter);
                    if (!fileOperationsService.isValidDate(uploadDate)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                                String.format("Invalid uploadDate '%s'. Expected format: YYYY-MM-DD (e.g., 2025-05-25)", uploadDate),
                                String.valueOf(HttpStatus.BAD_REQUEST.value())
                        ));
                    }
                } catch (DateTimeParseException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                            String.format("Invalid uploadDate '%s'. Expected format: YYYY-MM-DD (e.g., 2025-05-25)", uploadDate),
                            String.valueOf(HttpStatus.BAD_REQUEST.value())
                    ));
                }
            }

            if (startDate != null && !startDate.trim().isEmpty()) {
                try {
                    parsedStartDate = LocalDate.parse(startDate, formatter);
                    if (!fileOperationsService.isValidDate(startDate)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                                "Invalid start date format. Use YYYY-MM-DD",
                                String.valueOf(HttpStatus.BAD_REQUEST.value())
                        ));
                    }
                } catch (DateTimeParseException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                            "Invalid start date format. Use YYYY-MM-DD",
                            String.valueOf(HttpStatus.BAD_REQUEST.value())
                    ));
                }
            }

            if (endDate != null && !endDate.trim().isEmpty()) {
                try {
                    parsedEndDate = LocalDate.parse(endDate, formatter);
                    if (!fileOperationsService.isValidDate(endDate)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                                "Invalid end date format. Use YYYY-MM-DD",
                                String.valueOf(HttpStatus.BAD_REQUEST.value())
                        ));
                    }
                } catch (DateTimeParseException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                            "Invalid end date format. Use YYYY-MM-DD",
                            String.valueOf(HttpStatus.BAD_REQUEST.value())
                    ));
                }
            }

            if (parsedStartDate != null && parsedEndDate != null) {
                if (parsedStartDate.isAfter(parsedEndDate)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                            "startDate cannot be after endDate",
                            String.valueOf(HttpStatus.BAD_REQUEST.value())
                    ));
                }
                parsedUploadDate = null;
            } else if (parsedStartDate != null || parsedEndDate != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                        "Both startDate and endDate must be provided for date range",
                        String.valueOf(HttpStatus.BAD_REQUEST.value())
                ));
            }

            var transactions = reconService.getAllUnreconciledTransactions(
                    page, size, parsedUploadDate, parsedStartDate, parsedEndDate,
                    transactionReference, sessionId, transactionDate, expectedStatus,
                    narration, finalStatus, searchPhrase);
            return ResponseEntity.ok(new ApiResponse<>(
                    "Unreconciled transactions retrieved successfully",
                    String.valueOf(HttpStatus.OK.value()),
                    reconService.buildPaginationResponse(transactions)
            ));
        } catch (Exception e) {
            log.error("Error retrieving unreconciled transactions: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    "Failed to retrieve unreconciled transactions: " + e.getMessage(),
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
            ));
        }
    }

    @GetMapping("/percentage")
    public ResponseEntity<ApiResponse<Map<String, Double>>> getReconciliationPercentage(
            @RequestParam(required = false) String uploadDate) {
        try {
            LocalDate parsedUploadDate = validateUploadDate(uploadDate);
            Map<String, Double> percentages = reconService.getReconciliationPercentage(parsedUploadDate);
            return ResponseEntity.ok(new ApiResponse<>(
                    "Reconciliation percentages retrieved successfully",
                    String.valueOf(HttpStatus.OK.value()),
                    percentages
            ));
        } catch (Exception e) {
            log.error("An Error occurred calculating reconciliation percentages: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    "Failed to calculate reconciliation percentages: " + e.getMessage(),
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
            ));
        }
    }

    @GetMapping("/graph")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getReconciliationAmountsGraph(
            @RequestParam(required = false) String uploadDate) {
        try {
            LocalDate parsedUploadDate = validateUploadDate(uploadDate);
            ReconciliationResultDTO result = reconService.getReconciliationStatusAmount(parsedUploadDate);
            if (result == null) {
                return ResponseEntity.ok(new ApiResponse<>(
                        "No reconciliation data found for upload date " + uploadDate,
                        String.valueOf(HttpStatus.OK.value())
                ));
            }
            Map<String, BigDecimal> graphData = new HashMap<>();
            graphData.put("reconciledAmount", result.getReconciledAmount());
            graphData.put("unreconciledAmount", result.getUnreconciledAmount());
            graphData.put("premiumTrustBankStatement", result.getPremiumTrustBankStatement());
            graphData.put("providusBankStatement", result.getProvidusBankStatement());

            return ResponseEntity.ok(new ApiResponse<>(
                    "Reconciliation amounts graph data retrieved successfully",
                    String.valueOf(HttpStatus.OK.value()),
                    graphData
            ));
        } catch (Exception e) {
            log.error("Error fetching reconciliation amounts graph data: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    "Failed to fetch reconciliation amounts graph data: " + e.getMessage(),
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
            ));
        }
    }

    private LocalDate validateUploadDate(String uploadDate) {
        if (uploadDate == null || uploadDate.trim().isEmpty()) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate parsedDate = LocalDate.parse(uploadDate, formatter);
            if (!fileOperationsService.isValidDate(uploadDate)) {
                throw new DateTimeParseException("Invalid date format", uploadDate, 0);
            }
            return parsedDate;
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid uploadDate: " + uploadDate + ". Expected format: YYYY-MM-DD", e);
        }
    }


}
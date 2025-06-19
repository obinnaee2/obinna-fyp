package com.fyp.reconciliation_automation.controller;

import com.fyp.reconciliation_automation.AwsBucket.AwsOperations;
import com.fyp.reconciliation_automation.dto.ApiResponse;
import com.fyp.reconciliation_automation.dto.ReconStatusDto;
import com.fyp.reconciliation_automation.repository.ReconStatusRepository;
import com.fyp.reconciliation_automation.service.EmailService;
import com.fyp.reconciliation_automation.service.FileContentValidator;
import com.fyp.reconciliation_automation.service.FileOperationsService;
import com.fyp.reconciliation_automation.service.FileType;
import com.fyp.reconciliation_automation.service.ReconStatusService;
import com.fyp.reconciliation_automation.service.ReconciliationService;
import com.fyp.reconciliation_automation.service.ReportType;
import com.fyp.reconciliation_automation.service.TimeWindow;
import com.fyp.reconciliation_automation.service.WorksheetExportService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
@Slf4j
public class FileController {

    private final EmailService emailService;
    private final FileOperationsService fileOperationsService;
    private final ExecutorService executorService;
    private final WorksheetExportService worksheetExportService;
    private final AwsOperations aws;
    private final ReconStatusRepository reconStatusRepository;
    private final ReconStatusService reconStatusService;
    private final JdbcTemplate jdbcTemplate;
    private final ReconciliationService reconciliationService;

    public FileController(EmailService emailService,
                          FileOperationsService fileOperationsService,
                          WorksheetExportService worksheetExportService,
                          AwsOperations aws, ReconStatusRepository reconStatusRepository,
                          ReconStatusService reconStatusService, JdbcTemplate jdbcTemplate,
                          ReconciliationService reconciliationService) {
        this.emailService = emailService;
        this.fileOperationsService = fileOperationsService;
        this.worksheetExportService = worksheetExportService;
        this.aws = aws;
        this.reconStatusRepository = reconStatusRepository;
        this.reconStatusService = reconStatusService;
        this.jdbcTemplate = jdbcTemplate;
        this.reconciliationService = reconciliationService;
        this.executorService = Executors.newWorkStealingPool();
    }

    @GetMapping("/index")
    public ResponseEntity<ApiResponse<String>> index() {
        log.info("Welcome to reconciliation automation service");
        return ResponseEntity.ok(new ApiResponse<>(
                "Welcome to the reconciliation service...",
                String.valueOf(HttpStatus.OK.value())
        ));
    }
//rebuild
    @GetMapping("/get_file_types")
    public ResponseEntity<ApiResponse<List<String>>> getAllFileTypes() {
        List<String> fileTypes = Arrays.stream(FileType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(
                "File types retrieved successfully",
                String.valueOf(HttpStatus.OK.value()),
                fileTypes
        ));
    }

    @Transactional
    @PostMapping("/upload_other_documents")
    public ResponseEntity<ApiResponse<String>> uploadOtherDocuments(
            @RequestParam("fileType") FileType fileType,
            @RequestParam("email") String recipientsEmail,
            @RequestParam("uploadDate") String uploadDate,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "fileUrl", required = false) String fileUrl) {

        if (!fileOperationsService.isValidEmail(recipientsEmail)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Invalid email format. Please provide a valid email address.",
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        if (!fileOperationsService.isValidFileType(fileType)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Invalid file type: " + fileType,
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        if (uploadDate == null || uploadDate.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Upload date is required and cannot be empty.",
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        if (!fileOperationsService.isValidDate(uploadDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Invalid upload date format. Please use YYYY-MM-DD.",
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        if ((file == null || file.isEmpty()) && (fileUrl == null || fileUrl.trim().isEmpty())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Either a file or a file URL must be provided.",
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        if (fileUrl != null && !fileUrl.trim().isEmpty() && !aws.isValidS3OrCloudFrontUrl(fileUrl)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Invalid or inaccessible S3/CloudFront URL: " + fileUrl,
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        executorService.submit(() -> {
            try {
                MultipartFile fileToProcess = file;
                if (fileUrl != null && !fileUrl.trim().isEmpty()) {
                    fileToProcess = aws.downloadFromS3(fileUrl);
                }

                try (InputStream inputStream = fileToProcess.getInputStream()) {
                    FileContentValidator.validateFileContent(inputStream, fileType);
                } catch (ResponseStatusException ex) {
                    String errorMessage = ex.getReason();
                    log.error("File validation failed: {}", errorMessage);
                    emailService.sendEmail("File Upload Failed", "File validation failed: " + errorMessage, recipientsEmail);
                    return;
                }
                reconStatusService.updateReconStatusToStarted(uploadDate);

                uploadFile(fileToProcess, fileType, recipientsEmail, uploadDate, fileUrl);
                log.info("Completed upload for file type: {} on date: {}", fileType, uploadDate);
            } catch (IOException e) {
                log.error("Error downloading file from S3 or processing file: {}", e.getMessage(), e);
                emailService.sendEmail("File Upload Failed", "Error downloading file from S3 or processing file: " + e.getMessage(), recipientsEmail);
            } catch (Exception e) {
                log.error("Unexpected error during file processing: {}", e.getMessage(), e);
                emailService.sendEmail("File Upload Failed", "Unexpected error during file processing: " + e.getMessage(), recipientsEmail);
            }
        });

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse<>(
                "File processing queued.",
                String.valueOf(HttpStatus.ACCEPTED.value())
        ));
    }

    @Async
    public void uploadFile(MultipartFile file, FileType fileType, String recipientsEmail, String uploadDate, String fileUrl) {
        try {
            String startSubject = "File Upload Started";
            String startEmailMessage = "Your document has started uploading and you will be notified upon completion.";
            emailService.sendEmail(startSubject, startEmailMessage, recipientsEmail);
            fileOperationsService.uploadFile(file, fileType, recipientsEmail, uploadDate);
            fileOperationsService.saveS3Url(uploadDate, fileType.name(), fileUrl, null);
            String subject = "File Upload Successful";
            String emailMessage = "Your file has been uploaded successfully.";
            emailService.sendEmail(subject, emailMessage, recipientsEmail);
            log.info("File uploaded successfully for email: {}", recipientsEmail);
            fileOperationsService.logUpload(file.getOriginalFilename(), fileType);
        } catch (Exception ex) {
            String subject = "File Upload Failed";
            String emailMessage = "File upload failed due to: " + ex.getMessage();
            emailService.sendEmail(subject, emailMessage, recipientsEmail);
            log.error("File upload failed for email: {}", recipientsEmail, ex);
        }
    }

    @PostMapping("/upload/{window}")
    public ResponseEntity<ApiResponse<String>> uploadFilesByWindow(
            @PathVariable("window") String window,
            @RequestParam("uploadDate") String uploadDate,
            @RequestParam(name = "creditFailedFileUrl", required = false) String creditFailedFileUrl,
            @RequestParam(name = "creditSuccessfulFileUrl", required = false) String creditSuccessfulFileUrl,
            @RequestParam(name = "debitAdviceFailedFileUrl", required = false) String debitAdviceFailedFileUrl,
            @RequestParam(name = "debitFailedFileUrl", required = false) String debitFailedFileUrl,
            @RequestParam(name = "debitSuccessfulFileUrl", required = false) String debitSuccessfulFileUrl,
            @RequestParam(name = "debitAdviceSuccessfulFileUrl", required = false) String debitAdviceSuccessfulFileUrl,
            @RequestParam(name = "creditFailedFile", required = false) MultipartFile creditFailedFile,
            @RequestParam(name = "creditSuccessfulFile", required = false) MultipartFile creditSuccessfulFile,
            @RequestParam(name = "debitAdviceFailedFile", required = false) MultipartFile debitAdviceFailedFile,
            @RequestParam(name = "debitFailedFile", required = false) MultipartFile debitFailedFile,
            @RequestParam(name = "debitSuccessfulFile", required = false) MultipartFile debitSuccessfulFile,
            @RequestParam(name = "debitAdviceSuccessfulFile", required = false) MultipartFile debitAdviceSuccessfulFile,
            @RequestParam("email") String recipientsEmail) {

        List<String> allowedWindows = Arrays.stream(TimeWindow.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        if (uploadDate == null || uploadDate.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Upload date is required and cannot be empty.",
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        if (!fileOperationsService.isValidDate(uploadDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Invalid upload date format. Please use YYYY-MM-DD.",
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        if (!allowedWindows.contains(window)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Invalid window type: " + window + ". Allowed values are: " + allowedWindows,
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        if (fileOperationsService.isAlreadyUploaded(uploadDate, TimeWindow.valueOf(window))) {
            String errorMessage = "Upload not allowed: All files are already complete for window " + window + " on " + uploadDate;
            emailService.sendEmail("Upload Not Allowed", errorMessage, recipientsEmail);
            log.error(errorMessage);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(
                    errorMessage,
                    String.valueOf(HttpStatus.OK.value())
            ));
        }

        if (!fileOperationsService.isValidEmail(recipientsEmail)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Invalid email format. Please provide a valid email address.",
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        Map<String, String> urlToFileType = new LinkedHashMap<>();
        urlToFileType.put(creditFailedFileUrl, "credit_failed");
        urlToFileType.put(creditSuccessfulFileUrl, "credit_successful");
        urlToFileType.put(debitAdviceFailedFileUrl, "debit_advice_failed");
        urlToFileType.put(debitFailedFileUrl, "debit_failed");
        urlToFileType.put(debitSuccessfulFileUrl, "debit_successful");
        urlToFileType.put(debitAdviceSuccessfulFileUrl, "debit_advice_successful");

        List<String> invalidFileTypes = urlToFileType.entrySet().stream()
                .filter(entry -> entry.getKey() != null && !entry.getKey().trim().isEmpty() && !aws.isValidS3OrCloudFrontUrl(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (!invalidFileTypes.isEmpty()) {
            String errorMessage = "Invalid S3/CloudFront URL formats for file types: " + String.join(", ", invalidFileTypes);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    errorMessage,
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        if (Stream.of(creditFailedFileUrl, creditSuccessfulFileUrl, debitAdviceFailedFileUrl,
                        debitFailedFileUrl, debitSuccessfulFileUrl, debitAdviceSuccessfulFileUrl,
                        creditFailedFile, creditSuccessfulFile, debitAdviceFailedFile,
                        debitFailedFile, debitSuccessfulFile, debitAdviceSuccessfulFile)
                .allMatch(obj -> obj == null || (obj instanceof String && ((String) obj).trim().isEmpty()) || (obj instanceof MultipartFile && ((MultipartFile) obj).isEmpty()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "At least one file or file URL must be provided.",
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        executorService.submit(() -> {
            try {
                MultipartFile creditFailedFileToProcess = creditFailedFile != null && !creditFailedFile.isEmpty() ? creditFailedFile : downloadFileIfProvided(creditFailedFileUrl);
                MultipartFile creditSuccessfulFileToProcess = creditSuccessfulFile != null && !creditSuccessfulFile.isEmpty() ? creditSuccessfulFile : downloadFileIfProvided(creditSuccessfulFileUrl);
                MultipartFile debitAdviceFailedFileToProcess = debitAdviceFailedFile != null && !debitAdviceFailedFile.isEmpty() ? debitAdviceFailedFile : downloadFileIfProvided(debitAdviceFailedFileUrl);
                MultipartFile debitFailedFileToProcess = debitFailedFile != null && !debitFailedFile.isEmpty() ? debitFailedFile : downloadFileIfProvided(debitFailedFileUrl);
                MultipartFile debitSuccessfulFileToProcess = debitSuccessfulFile != null && !debitSuccessfulFile.isEmpty() ? debitSuccessfulFile : downloadFileIfProvided(debitSuccessfulFileUrl);
                MultipartFile debitAdviceSuccessfulFileToProcess = debitAdviceSuccessfulFile != null && !debitAdviceSuccessfulFile.isEmpty() ? debitAdviceSuccessfulFile : downloadFileIfProvided(debitAdviceSuccessfulFileUrl);

                processFiles(uploadDate, TimeWindow.valueOf(window),
                        creditFailedFileToProcess, creditSuccessfulFileToProcess,
                        debitAdviceFailedFileToProcess, debitFailedFileToProcess,
                        debitSuccessfulFileToProcess, debitAdviceSuccessfulFileToProcess,
                        recipientsEmail,
                        creditFailedFileUrl, creditSuccessfulFileUrl,
                        debitAdviceFailedFileUrl, debitFailedFileUrl,
                        debitSuccessfulFileUrl, debitAdviceSuccessfulFileUrl);
                reconStatusService.updateReconStatusToStarted(uploadDate);
                log.info("Completed upload for window: {} on date: {}", window, uploadDate);
                emailService.sendEmail("Window Upload Completed", "Upload for window " + window + " completed successfully.", recipientsEmail);
            } catch (Exception e) {
                log.error("Error processing files for window {}: {}", window, e.getMessage(), e);
                emailService.sendEmail("File Processing Error",
                        "Error processing files for window " + window + ": " + e.getMessage(),
                        recipientsEmail);
            }
        });

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse<>(
                "File upload queued for " + window + " and date: " + uploadDate,
                String.valueOf(HttpStatus.ACCEPTED.value())
        ));
    }

    private MultipartFile downloadFileIfProvided(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return null;
        }
        return aws.downloadFromS3(fileUrl);
    }

    private void processFiles(String uploadDate, TimeWindow window,
                              MultipartFile creditFailedFile, MultipartFile creditSuccessfulFile,
                              MultipartFile debitAdviceFailedFile, MultipartFile debitFailedFile,
                              MultipartFile debitSuccessfulFile, MultipartFile debitAdviceSuccessfulFile,
                              String recipientsEmail,
                              String creditFailedFileUrl, String creditSuccessfulFileUrl,
                              String debitAdviceFailedFileUrl, String debitFailedFileUrl,
                              String debitSuccessfulFileUrl, String debitAdviceSuccessfulFileUrl) {

        Map<String, String> fileUploadStatus = new LinkedHashMap<>();
        for (FileType fileType : FileType.values()) {
            fileUploadStatus.put(fileType.name(), "No");
        }

        fileOperationsService.processFile(uploadDate, window, creditFailedFile, FileType.credit_failed, recipientsEmail, fileUploadStatus);
        if (creditFailedFileUrl != null && !creditFailedFileUrl.trim().isEmpty()) {
            fileOperationsService.saveS3Url(uploadDate, FileType.credit_failed.name(), creditFailedFileUrl, window.name());
        }

        fileOperationsService.processFile(uploadDate, window, creditSuccessfulFile, FileType.credit_successful, recipientsEmail, fileUploadStatus);
        if (creditSuccessfulFileUrl != null && !creditSuccessfulFileUrl.trim().isEmpty()) {
            fileOperationsService.saveS3Url(uploadDate, FileType.credit_successful.name(), creditSuccessfulFileUrl, window.name());
        }

        fileOperationsService.processFile(uploadDate, window, debitAdviceFailedFile, FileType.debit_advice_failed, recipientsEmail, fileUploadStatus);
        if (debitAdviceFailedFileUrl != null && !debitAdviceFailedFileUrl.trim().isEmpty()) {
            fileOperationsService.saveS3Url(uploadDate, FileType.debit_advice_failed.name(), debitAdviceFailedFileUrl, window.name());
        }

        fileOperationsService.processFile(uploadDate, window, debitFailedFile, FileType.debit_failed, recipientsEmail, fileUploadStatus);
        if (debitFailedFileUrl != null && !debitFailedFileUrl.trim().isEmpty()) {
            fileOperationsService.saveS3Url(uploadDate, FileType.debit_failed.name(), debitFailedFileUrl, window.name());
        }

        fileOperationsService.processFile(uploadDate, window, debitSuccessfulFile, FileType.debit_successful, recipientsEmail, fileUploadStatus);
        if (debitSuccessfulFileUrl != null && !debitSuccessfulFileUrl.trim().isEmpty()) {
            fileOperationsService.saveS3Url(uploadDate, FileType.debit_successful.name(), debitSuccessfulFileUrl, window.name());
        }

        fileOperationsService.processFile(uploadDate, window, debitAdviceSuccessfulFile, FileType.debit_advice_successful, recipientsEmail, fileUploadStatus);
        if (debitAdviceSuccessfulFileUrl != null && !debitAdviceSuccessfulFileUrl.trim().isEmpty()) {
            fileOperationsService.saveS3Url(uploadDate, FileType.debit_advice_successful.name(), debitAdviceSuccessfulFileUrl, window.name());
        }
    }

    @GetMapping("/get/{fileType}")
    public ResponseEntity<ApiResponse<Object>> getFileByType(@PathVariable("fileType") FileType fileType) {
        if (!fileOperationsService.isValidFileType(fileType)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Invalid file type: " + fileType,
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }
        Object data = fileOperationsService.getFileByType(fileType).getBody();
        return ResponseEntity.ok(new ApiResponse<>(
                "File data retrieved successfully",
                String.valueOf(HttpStatus.OK.value()),
                data
        ));
    }

    @GetMapping("/download/reconciliationWorksheet")
    public ResponseEntity<ApiResponse<Object>> downloadNibbsCentralRecon(
            @RequestParam String recipientEmail,
            @RequestParam(required = false) LocalDate MetabaseStartDate,
            @RequestParam(required = false) LocalDate MetabaseEndDate,
            @RequestParam(required = true) String uploadDate,
            @RequestParam ReportType reportType) {

        if (uploadDate == null || uploadDate.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Required request parameter 'uploadDate' is missing.",
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        if (!fileOperationsService.isValidDate(uploadDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Invalid upload date format. Please use YYYY-MM-DD.",
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Please provide an email address.",
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        if (!fileOperationsService.isValidEmail(recipientEmail)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Invalid email format. Please provide a valid email address.",
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        List<String> missingFiles = fileOperationsService.checkUploadedFilesForReportType(reportType);
        if (!fileOperationsService.areAllWindowsUploaded(uploadDate)) {
            List<Map<String, Object>> allWindowsData = fileOperationsService.getReviewDataForAllWindows(uploadDate);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "You cannot call the download API yet; you still have some NIBSS files to upload.",
                    String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    Map.of("Upload Status", allWindowsData)
            ));
        }

        Map<String, Object> documentsStatus = fileOperationsService.checkOtherDocumentsUploadStatus(uploadDate, reportType);
        if (!(Boolean) documentsStatus.get("allUploaded")) {
            List<String> missingDocs = (List<String>) documentsStatus.get("missingFiles");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "You cannot call the download API yet; the following required documents are missing: " + missingDocs,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    Map.of("Missing Documents", missingDocs, "Upload Date", uploadDate, "Report Type", reportType.name())
            ));
        }

        executorService.submit(() -> {
            try {
                jdbcTemplate.execute("SELECT * FROM recon.delete_duplicate_rows()");
                String initiationSubject = "Reconciliation Worksheet Generation Initiated";
                String initiationEmailMessage = "This is to inform you that the reconciliation worksheet generation process has been initiated. You will receive another email with the download link once it's complete.";
                emailService.sendEmail(initiationSubject, initiationEmailMessage, recipientEmail);
                jdbcTemplate.execute("SELECT * FROM recon.reconciliation_records_matching()");
                generateAndUploadWorksheet(recipientEmail, reportType);
                reconStatusService.updateReconStatusToCompleted(uploadDate);
            } catch (Exception e) {
                log.error("An error occurred during the download process: {}", e.getMessage(), e);
                String errorSubject = "Error in Reconciliation Worksheet Download";
                String errorMessage = "Dear User,\n\nAn error occurred during the reconciliation worksheet generation process. Please check the system for more details.\n\nError Details: " + e.getMessage();
                emailService.sendEmail(errorSubject, errorMessage, recipientEmail);
            }
        });

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse<>(
                "Worksheet generation and upload queued for processing.",
                String.valueOf(HttpStatus.ACCEPTED.value())
        ));
    }

    private void generateAndUploadWorksheet(String recipientEmail, ReportType reportType) {
        Runnable worksheetTask = () -> {
            try {
                String folder = String.valueOf(System.currentTimeMillis());
                String filePath = "export/doc/" + folder;
                String fileName = "Reconciliation-Worksheet.xlsx";

                File directory = new File(filePath);
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                File toUpload = new File(directory, fileName);

                worksheetExportService.exportWorksheetForReportType(toUpload, reportType);
                String urlEndpoint = aws.uploadFile(toUpload, fileName);

                String subject = "Worksheet Download Link";
                String emailMessage = "You can download the worksheet using the following link: " + urlEndpoint;
                emailService.sendEmail(subject, emailMessage, recipientEmail);

                log.info("Worksheet generated and uploaded successfully. Email sent to: {}", recipientEmail);
            } catch (IOException e) {
                log.error("Error generating or uploading worksheet: ", e);
                emailService.sendEmail("Worksheet Generation Error",
                        "Error generating or uploading worksheet: " + e.getMessage(),
                        recipientEmail);
            }
        };
        executorService.execute(worksheetTask);
    }

    @GetMapping("/retrieve-nibbs-upload-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> reviewUploadByWindow(
            @RequestParam(required = false) String window,
            @RequestParam("uploadDate") String uploadDate) {

        Map<String, Object> reviewResponse = new HashMap<>();
        try {
            if (window != null) {
                if (!Arrays.asList(TimeWindow.values()).stream().map(TimeWindow::name).collect(Collectors.toList()).contains(window)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                            "Invalid window type: " + window + ". Allowed values are: " + Arrays.toString(TimeWindow.values()),
                            String.valueOf(HttpStatus.BAD_REQUEST.value())
                    ));
                }

                Map<String, Object> reviewData = fileOperationsService.getReviewDataByWindow(window, uploadDate);
                if (reviewData.isEmpty()) {
                    reviewResponse.put("Window", window);
                    reviewResponse.put("Upload Date", uploadDate);
                    reviewResponse.put("File upload status", "No files uploaded for this window and date");
                    reviewResponse.put("Status", "NOT_UPLOADED");
                    return ResponseEntity.ok(new ApiResponse<>(
                            "No files uploaded for window " + window + " on " + uploadDate,
                            String.valueOf(HttpStatus.OK.value()),
                            reviewResponse
                    ));
                } else {
                    reviewResponse.put("Window", window);
                    reviewResponse.put("Upload Date", uploadDate);
                    reviewResponse.put("File upload status", reviewData);
                    return ResponseEntity.ok(new ApiResponse<>(
                            "Upload status retrieved successfully",
                            String.valueOf(HttpStatus.OK.value()),
                            reviewResponse
                    ));
                }
            } else {
                List<Map<String, Object>> allWindowsData = fileOperationsService.getReviewDataForAllWindows(uploadDate);
                if (allWindowsData.isEmpty()) {
                    reviewResponse.put("Upload Date", uploadDate);
                    reviewResponse.put("All Windows Status", "No data available for any window on this date");
                    reviewResponse.put("Status", "NOT_UPLOADED");
                    return ResponseEntity.ok(new ApiResponse<>(
                            "No data available for any window on " + uploadDate,
                            String.valueOf(HttpStatus.OK.value()),
                            reviewResponse
                    ));
                } else {
                    reviewResponse.put("Upload Date", uploadDate);
                    reviewResponse.put("All Windows Status", allWindowsData);
                    return ResponseEntity.ok(new ApiResponse<>(
                            "Upload status for all windows retrieved successfully",
                            String.valueOf(HttpStatus.OK.value()),
                            reviewResponse
                    ));
                }
            }
        } catch (Exception e) {
            reviewResponse.put("Error", "Failed to retrieve review data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    "Failed to retrieve review data: " + e.getMessage(),
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    reviewResponse
            ));
        }
    }

    @GetMapping("/retrieve-other-documents-upload-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOtherDocumentsUploadStatus(
            @RequestParam("uploadDate") String uploadDate) {

        if (uploadDate == null || uploadDate.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Upload date is required",
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        if (!fileOperationsService.isValidDate(uploadDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    "Invalid date format. Use YYYY-MM-DD",
                    String.valueOf(HttpStatus.BAD_REQUEST.value())
            ));
        }

        Map<String, Object> status = fileOperationsService.getOtherDocumentsUploadStatus(uploadDate);
        return ResponseEntity.ok(new ApiResponse<>(
                "Other documents upload status retrieved successfully",
                String.valueOf(HttpStatus.OK.value()),
                status
        ));
    }
        @GetMapping("/retrieve-records")
        public ResponseEntity<ApiResponse<Object>> retrieveUploadedData(
                @RequestParam("window") String window,
                @RequestParam("fileType") String fileType,
                @RequestParam("date") String date,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "transaction_id", required = false) String transactionId,
        @RequestParam(value = "session_id", required = false) String sessionId,
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(value = "message", required = false) String message,
        @RequestParam(value = "nip_response_code", required = false) String nipResponseCode,
        @RequestParam(value = "name_enquiry_ref", required = false) String nameEnquiryRef,
        @RequestParam(value = "destination_institution_code", required = false) String destinationInstitutionCode,
        @RequestParam(value = "beneficiary_account_number", required = false) String beneficiaryAccountNumber,
        @RequestParam(value = "beneficiary_bvn", required = false) String beneficiaryBvn,
        @RequestParam(value = "originator_account_name", required = false) String originatorAccountName,
        @RequestParam(value = "originator_account_number", required = false) String originatorAccountNumber,
        @RequestParam(value = "originator_bvn", required = false) String originatorBvn,
        @RequestParam(value = "payment_reference", required = false) String paymentReference,
        @RequestParam(value = "transaction_location", required = false) String transactionLocation,
        @RequestParam(value = "payaza_reference", required = false) String payazaReference,
        @RequestParam(value = "transaction_date", required = false) String transactionDate) {

            if (!Arrays.stream(TimeWindow.values()).map(Enum::name).collect(Collectors.toList()).contains(window)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                        "Invalid window type: " + window + ". Allowed values are: " + Arrays.toString(TimeWindow.values()),
                        String.valueOf(HttpStatus.BAD_REQUEST.value())
                ));
            }

            if (!Arrays.stream(FileType.values()).map(Enum::name).collect(Collectors.toList()).contains(fileType)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                        "Invalid file type: " + fileType + ". Allowed values are: " + Arrays.toString(FileType.values()),
                        String.valueOf(HttpStatus.BAD_REQUEST.value())
                ));
            }

            if (!fileOperationsService.isValidDate(date)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                        "Invalid date format. Use YYYY-MM-DD",
                        String.valueOf(HttpStatus.BAD_REQUEST.value())
                ));
            }

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

            try {
                Pageable pageable = PageRequest.of(page - 1, size);
                Map<String, String> filters = new HashMap<>();
                filters.put("transaction_id", transactionId);
                filters.put("session_id", sessionId);
                filters.put("status", status);
                filters.put("message", message);
                filters.put("nip_response_code", nipResponseCode);
                filters.put("name_enquiry_ref", nameEnquiryRef);
                filters.put("destination_institution_code", destinationInstitutionCode);
                filters.put("beneficiary_account_number", beneficiaryAccountNumber);
                filters.put("beneficiary_bvn", beneficiaryBvn);
                filters.put("originator_account_name", originatorAccountName);
                filters.put("originator_account_number", originatorAccountNumber);
                filters.put("originator_bvn", originatorBvn);
                filters.put("payment_reference", paymentReference);
                filters.put("transaction_location", transactionLocation);
                filters.put("payaza_reference", payazaReference);
                filters.put("transaction_date", transactionDate);

                Page<Map<String, Object>> results = fileOperationsService.getUploadedDataPaginated(
                        window, fileType, date, pageable, filters);
                if (results.isEmpty()) {
                    return ResponseEntity.ok(new ApiResponse<>(
                            "No records found for " + window + " and " + date,
                            String.valueOf(HttpStatus.OK.value())
                    ));
                } else {
                    return ResponseEntity.ok(new ApiResponse<>(
                            "Records retrieved successfully",
                            String.valueOf(HttpStatus.OK.value()),
                            reconciliationService.buildPaginationResponse(results)
                    ));
                }
            } catch (Exception e) {
                log.error("Error retrieving uploaded data: ", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                        "Error retrieving data: " + e.getMessage(),
                        String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
                ));
            }
        }

    @PostMapping("/upload-to-s3")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFileToS3(
            @RequestParam(name = "file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                        "File is empty",
                        String.valueOf(HttpStatus.BAD_REQUEST.value())
                ));
            }

            File tempFile = File.createTempFile("upload-", "-" + file.getOriginalFilename());
            file.transferTo(tempFile);
            String fileUrl = aws.uploadFile(tempFile, file.getOriginalFilename());
            tempFile.delete();

            Map<String, String> data = new HashMap<>();
            data.put("url", fileUrl);
            return ResponseEntity.ok(new ApiResponse<>(
                    "File uploaded successfully",
                    String.valueOf(HttpStatus.OK.value()),
                    data
            ));
        } catch (IOException e) {
            log.error("Failed to upload file to S3", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    "Failed to upload file: " + e.getMessage(),
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
            ));
        }
    }

    @GetMapping("/get-recon-status")
    public ResponseEntity<ApiResponse<Object>> getReconStatus(
            @RequestParam(required = false) String uploadDate,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String reconId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {

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

            LocalDate parsedUploadDate = null;
            LocalDate parsedStartDate = null;
            LocalDate parsedEndDate = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            if (uploadDate != null && !uploadDate.trim().isEmpty()) {
                try {
                    parsedUploadDate = LocalDate.parse(uploadDate, formatter);
                    if (!fileOperationsService.isValidDate(uploadDate)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                                "Invalid upload date format. Use YYYY-MM-DD",
                                String.valueOf(HttpStatus.BAD_REQUEST.value())
                        ));
                    }
                } catch (DateTimeParseException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                            "Invalid upload date format. Use YYYY-MM-DD",
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


            if (status != null && !status.trim().isEmpty()) {
                if (!List.of("In_progress", "Completed").contains(status)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                            "Invalid status. Allowed values are: In_progress, Completed",
                            String.valueOf(HttpStatus.BAD_REQUEST.value())
                    ));
                }
            }

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<ReconStatusDto> reconStatuses = reconStatusService.getReconStatus(
                    parsedUploadDate, parsedStartDate, parsedEndDate, status, reconId, pageable);

            if (reconStatuses.isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(
                        "No reconciliation statuses found",
                        String.valueOf(HttpStatus.OK.value())
                ));
            }

            return ResponseEntity.ok(new ApiResponse<>(
                    "Reconciliation statuses retrieved successfully",
                    String.valueOf(HttpStatus.OK.value()),
                    reconciliationService.buildPaginationResponse(reconStatuses)
            ));
        } catch (Exception e) {
            log.error("Error retrieving recon_status data: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    "Failed to retrieve reconciliation statuses: " + e.getMessage(),
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
            ));
        }
    }

    @GetMapping("/retrieve-s3-urls")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getS3Urls(
            @RequestParam("uploadDate") String uploadDate,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) String timeWindow) {

        try {
            Map<String, Object> response = fileOperationsService.getStructuredS3Urls(uploadDate, fileType, timeWindow);
            String message = response.get("timeWindows") == null || ((Map<?, ?>) response.get("timeWindows")).isEmpty()
                    ? "No S3 URLs found for the specified criteria."
                    : "S3 URLs retrieved successfully.";
            return ResponseEntity.ok(new ApiResponse<>(message, String.valueOf(HttpStatus.OK.value()), response));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse<>(
                    e.getReason(), String.valueOf(e.getStatusCode().value())));
        } catch (Exception e) {
            log.error("Unexpected error retrieving S3 URLs: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    "Failed to retrieve S3 URLs: " + e.getMessage(),
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
        }
    }
    @PreDestroy
    public void shutdownExecutor() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(100, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
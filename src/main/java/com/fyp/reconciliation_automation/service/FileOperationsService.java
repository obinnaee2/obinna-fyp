package com.fyp.reconciliation_automation.service;

import com.fyp.reconciliation_automation.entity.CreditFailed;
import com.fyp.reconciliation_automation.entity.CreditSuccessful;
import com.fyp.reconciliation_automation.entity.DebitAdviceFailed;
import com.fyp.reconciliation_automation.entity.DebitAdviceSuccessful;
import com.fyp.reconciliation_automation.entity.DebitFailed;
import com.fyp.reconciliation_automation.entity.DebitSuccessful;
import com.fyp.reconciliation_automation.entity.FundsTransfer;
import com.fyp.reconciliation_automation.entity.MetaCollections;
import com.fyp.reconciliation_automation.entity.NibbsAuditLog;
import com.fyp.reconciliation_automation.entity.PremiumTrustBankStatement;
import com.fyp.reconciliation_automation.entity.PremiumTrustVPS;
import com.fyp.reconciliation_automation.entity.ProvidusBankStatement;
import com.fyp.reconciliation_automation.entity.ProvidusBankVPS;
import com.fyp.reconciliation_automation.entity.S3UrlAuditLog;
import com.fyp.reconciliation_automation.entity.TempMetabase;
import com.fyp.reconciliation_automation.repository.CreditFailedRepository;
import com.fyp.reconciliation_automation.repository.CreditSuccessfulRepository;
import com.fyp.reconciliation_automation.repository.DebitAdviceFailedRepository;
import com.fyp.reconciliation_automation.repository.DebitAdviceSuccessfulRepository;
import com.fyp.reconciliation_automation.repository.DebitFailedRepository;
import com.fyp.reconciliation_automation.repository.DebitSuccessfulRepository;
import com.fyp.reconciliation_automation.repository.FundsTransferRepository;
import com.fyp.reconciliation_automation.repository.MetaCollectionsRepository;
import com.fyp.reconciliation_automation.repository.NibbsAuditLogRepository;
import com.fyp.reconciliation_automation.repository.OtherDocumentsAuditLogRepository;
import com.fyp.reconciliation_automation.repository.PremiumTrustRepository;
import com.fyp.reconciliation_automation.repository.PremiumTrustVpsRepository;
import com.fyp.reconciliation_automation.repository.ProvidusBankVpsRepository;
import com.fyp.reconciliation_automation.repository.S3UrlAuditLogRepository;
import com.fyp.reconciliation_automation.repository.TempMetabaseRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.fyp.reconciliation_automation.service.ExcelUploadService.getPremiumTrustBankStatementDataFromExcel;


@Slf4j
@Service
public class FileOperationsService {
    private final JdbcTemplate jdbcTemplate;
    private final OtherDocumentsAuditLogRepository otherDocumentsAuditLogRepository;
    private final DataSource dataSource;
    private final com.fyp.reconciliation_automation.repository.ProvidusBankRepository ProvidusBankRepository;
    private final PremiumTrustRepository premiumTrustRepository;
    private final CreditFailedRepository CreditFailedRepository;
    private final CreditSuccessfulRepository CreditSuccessfulRepository;
    private final DebitAdviceFailedRepository DebitAdviceFailedRepository;
    private final DebitAdviceSuccessfulRepository DebitAdviceSuccessfulRepository;
    private final DebitFailedRepository DebitFailedRepository;
    private final DebitSuccessfulRepository DebitSuccessfulRepository;
    private final EntityManager entityManager;
    private final TempMetabaseRepository tempMetabaseRepository;
    private final ProvidusBankVpsRepository providusBankVpsRepository;
    private final PremiumTrustVpsRepository premiumTrustVpsRepository;
    private final MetaCollectionsRepository metaCollectionsRepository;
    private final FundsTransferRepository fundsTransferRepository;
    private final EmailService emailService;
    private final S3UrlAuditLogRepository S3UrlAuditLogRepository;
    private final S3UrlAuditLogRepository s3UrlAuditLogRepository;
    private final NibbsAuditLogRepository nibbsAuditLogRepository;


    public FileOperationsService (JdbcTemplate jdbcTemplate, OtherDocumentsAuditLogRepository otherDocumentsAuditLogRepository, DataSource dataSource, com.fyp.reconciliation_automation.repository.ProvidusBankRepository providusBankRepository, PremiumTrustRepository premiumTrustRepository, com.fyp.reconciliation_automation.repository.CreditFailedRepository creditFailedRepository, com.fyp.reconciliation_automation.repository.CreditSuccessfulRepository creditSuccessfulRepository, com.fyp.reconciliation_automation.repository.DebitAdviceFailedRepository debitAdviceFailedRepository, com.fyp.reconciliation_automation.repository.DebitAdviceSuccessfulRepository debitAdviceSuccessfulRepository, com.fyp.reconciliation_automation.repository.DebitFailedRepository debitFailedRepository, com.fyp.reconciliation_automation.repository.DebitSuccessfulRepository debitSuccessfulRepository, EntityManager entityManager, TempMetabaseRepository tempMetabaseRepository, ProvidusBankVpsRepository providusBankVpsRepository, PremiumTrustVpsRepository premiumTrustVpsRepository, MetaCollectionsRepository metaCollectionsRepository, FundsTransferRepository fundsTransferRepository, EmailService emailService, S3UrlAuditLogRepository s3UrlAuditLogRepository, NibbsAuditLogRepository nibbsAuditLogRepository){
        this.jdbcTemplate = jdbcTemplate;
        this.otherDocumentsAuditLogRepository = otherDocumentsAuditLogRepository;
        this.dataSource = dataSource;
        ProvidusBankRepository = providusBankRepository;
        this.premiumTrustRepository = premiumTrustRepository;
        CreditFailedRepository = creditFailedRepository;
        CreditSuccessfulRepository = creditSuccessfulRepository;
        DebitAdviceFailedRepository = debitAdviceFailedRepository;
        DebitAdviceSuccessfulRepository = debitAdviceSuccessfulRepository;
        DebitFailedRepository = debitFailedRepository;
        DebitSuccessfulRepository = debitSuccessfulRepository;
        this.entityManager = entityManager;
        this.tempMetabaseRepository = tempMetabaseRepository;
        this.providusBankVpsRepository = providusBankVpsRepository;
        this.premiumTrustVpsRepository = premiumTrustVpsRepository;
        this.metaCollectionsRepository = metaCollectionsRepository;
        this.fundsTransferRepository = fundsTransferRepository;
        this.emailService = emailService;
        S3UrlAuditLogRepository = s3UrlAuditLogRepository;
        this.s3UrlAuditLogRepository = s3UrlAuditLogRepository;
        this.nibbsAuditLogRepository = nibbsAuditLogRepository;
    }


    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    public boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean areAllWindowsUploaded(String uploadDate) {
        String sql = "SELECT * FROM recon.nibbs_audit_log WHERE upload_date = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date utilDate = dateFormat.parse(uploadDate);


            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

            pstmt.setDate(1, sqlDate);
            ResultSet rs = pstmt.executeQuery();

            boolean[] windowsUploaded = new boolean[4];
            while (rs.next()) {
                String window = rs.getString("time_window");
                String debitSuccessful = rs.getString("debit_successful");
                String creditFailed = rs.getString("credit_failed");
                String creditSuccessful = rs.getString("credit_successful");
                String debitAdviceFailed = rs.getString("debit_advice_failed");
                String debitAdviceSuccessful = rs.getString("debit_advice_successful");
                String debitFailed = rs.getString("debit_failed");

                if (debitSuccessful.equals("Yes") && creditFailed.equals("Yes") &&
                        creditSuccessful.equals("Yes") && debitFailed.equals("Yes")) {

                    switch (TimeWindow.valueOf(window)) {
                        case WINDOW_6AM:
                            windowsUploaded[0] = true;
                            break;
                        case WINDOW_12PM:
                            windowsUploaded[1] = true;
                            break;
                        case WINDOW_6PM:
                            windowsUploaded[2] = true;
                            break;
                        case WINDOW_12AM:
                            windowsUploaded[3] = true;
                            break;
                    }
                }
            }

            for (boolean uploaded : windowsUploaded) {
                if (!uploaded) {
                    return false;
                }
            }
            return true;
        } catch (SQLException | ParseException e) {
            log.error("Error checking window uploads: ", e);
            return false;
        }
    }


    public List<String> checkUploadedFilesForReportType(ReportType reportType) {
        List<String> missingFiles = new ArrayList<>();
        List<String> tablesToCheck = getTablesToCheckForReportType(reportType);

        try (Connection conn = dataSource.getConnection()) {
            for (String table : tablesToCheck) {
                if (!existsInTable(conn, table)) {
                    missingFiles.add(table);
                }
            }
        } catch (SQLException e) {
            log.error("Error checking for uploaded files: ", e);
        }

        return missingFiles;
    }

    private List<String> getTablesToCheckForReportType(ReportType reportType) {
        switch (reportType) {
            case PROVIDUS_NIBSS_PREMIUM_TRUST:
                return Arrays.asList(
                        "recon.premium_trust_vps",
                        "recon.premiumtrust_bank_statement",
                        "recon.providus_bank_statement",
                        "recon.providus_bank_vps",
                        "recon.meta_collections",
                        "recon.fund_transfer",
                        "recon.metabase"

                ); case PREMIUM_TRUST_NIBSS:
                return Arrays.asList(
                        "recon.premium_trust_vps",
                        "recon.premiumtrust_bank_statement",
                        "recon.meta_collections",
                        "recon.fund_transfer",
                        "recon.metabase"
                );
            case PROVIDUS_NIBSS:
                return Arrays.asList(
                        "recon.providus_bank_statement",
                        "recon.providus_bank_vps",
                        "recon.meta_collections",
                        "recon.fund_transfer",
                        "recon.metabase"
                );
            default:
                throw new UnsupportedOperationException("Unsupported ReportType");
        }
    }

    private boolean existsInTable(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM " + tableName + ")";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getBoolean(1);
            }
        }
        return false;
    }

    public void validateTimeWindow(TimeWindow window) {
        if (window == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time window: null");
        }

        boolean isValid = Arrays.stream(TimeWindow.values())
                .anyMatch(validWindow -> validWindow == window);

        if (!isValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time window: " + window);
        }
    }

    @Transactional
    public void uploadFile(MultipartFile file, FileType fileType, String recipientsEmail, String uploadDate) {
        try {
            FileContentValidator.validateFileContent(file.getInputStream(), fileType);
            switch (fileType) {
                case providus_bank_statement:
                    saveProvidusBankStatementToDatabase(file, recipientsEmail, uploadDate);
                    break;
                case providus_bank_vps:
                    saveProvidusBankVpsToDatabase(file, recipientsEmail, uploadDate);
                    break;
                case premium_trust_bank_vps:
                    savePremiumTrustVpsToDatabase(file, recipientsEmail, uploadDate);
                    break;
                case premium_trust_bank_statement:
                    savePremiumTrustBankStatementToDatabase(file, recipientsEmail, uploadDate);
                    break;
                case credit_failed:
                    saveCreditFailedToDatabase(file, recipientsEmail, uploadDate);
                    break;
                case credit_successful:
                    saveCreditSuccessfulToDatabase(file, recipientsEmail, uploadDate);
                    break;
                case debit_advice_failed:
                    saveDebitAdviceFailedToDatabase(file, recipientsEmail, uploadDate);
                    break;
                case debit_advice_successful:
                    saveDebitAdviceSuccessfulToDatabase(file, recipientsEmail, uploadDate);
                    break;
                case debit_failed:
                    saveDebitFailedToDatabase(file, recipientsEmail, uploadDate);
                    break;
                case debit_successful:
                    saveDebitSuccessfulToDatabase(file, recipientsEmail, uploadDate);
                    break;
                case temporary_metabase:
                    saveTempMetabaseToDatabase(file, recipientsEmail, uploadDate);
                    break;
                case metabase_collections:
                    saveMetaCollectionsToDatabase(file, recipientsEmail, uploadDate);
                    break;
                case funds_transfer:
                    saveFundsTransferToDatabase(file, recipientsEmail, uploadDate);
                    break;
                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file type: " + fileType);
            }
        } catch (IOException e) {
            log.error("There was an error validating file content: ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid file content for type " + fileType + ": " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (!ExcelUploadService.isValidExcelFile(file) && !CSVUploadService.isValidCsvFile(file)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file type: " + file.getOriginalFilename());
        }
    }

    public boolean isValidFileType(FileType fileType) {
        return fileType == FileType.providus_bank_statement ||
                fileType == FileType.funds_transfer ||
                fileType == FileType.premium_trust_bank_vps ||
                fileType == FileType.providus_bank_vps ||
                fileType == FileType.premium_trust_bank_statement ||
                fileType == FileType.credit_failed ||
                fileType == FileType.credit_successful ||
                fileType == FileType.debit_advice_failed ||
                fileType == FileType.debit_failed ||
                fileType == FileType.debit_successful ||
                fileType == FileType.debit_advice_successful ||
                fileType == FileType.temporary_metabase ||
                fileType == FileType.metabase_collections;
    }

    public void updateAuditLog(String uploadDate, TimeWindow window, Map<String, String> fileUploadStatus) {
        try {

            String sql = "SELECT * FROM recon.nibbs_audit_log WHERE upload_date =? AND time_window =?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date utilDate = dateFormat.parse(uploadDate);
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

                pstmt.setDate(1, sqlDate);
                pstmt.setString(2, window.name());
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {

                    Map<String, String> existingStatus = retrieveExistingStatus(rs);


                    fileUploadStatus = mergeMaps(existingStatus, fileUploadStatus);


                    String updateSql = "UPDATE recon.nibbs_audit_log SET " +
                            "debit_successful =?, credit_failed =?, credit_successful =?, " +
                            "debit_advice_failed =?, debit_advice_successful =?, debit_failed =? " +
                            "WHERE upload_date =? AND time_window =?";

                    try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                        updatePstmt.setString(1, fileUploadStatus.get("debit_successful"));
                        updatePstmt.setString(2, fileUploadStatus.get("credit_failed"));
                        updatePstmt.setString(3, fileUploadStatus.get("credit_successful"));
                        updatePstmt.setString(4, fileUploadStatus.get("debit_advice_failed"));
                        updatePstmt.setString(5, fileUploadStatus.get("debit_advice_successful"));
                        updatePstmt.setString(6, fileUploadStatus.get("debit_failed"));
                        updatePstmt.setDate(7, sqlDate);
                        updatePstmt.setString(8, window.name());
                        updatePstmt.executeUpdate();
                    }
                } else {

                    String insertSql = "INSERT INTO recon.nibbs_audit_log (upload_date, time_window, debit_successful, credit_failed, " +
                            "credit_successful, debit_advice_failed, debit_advice_successful, debit_failed) VALUES (?,?,?,?,?,?,?,?)";

                    try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {
                        insertPstmt.setDate(1, sqlDate);
                        insertPstmt.setString(2, window.name());
                        insertPstmt.setString(3, fileUploadStatus.get("debit_successful"));
                        insertPstmt.setString(4, fileUploadStatus.get("credit_failed"));
                        insertPstmt.setString(5, fileUploadStatus.get("credit_successful"));
                        insertPstmt.setString(6, fileUploadStatus.get("debit_advice_failed"));
                        insertPstmt.setString(7, fileUploadStatus.get("debit_advice_successful"));
                        insertPstmt.setString(8, fileUploadStatus.get("debit_failed"));
                        insertPstmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException | ParseException e) {
            log.error("Error updating audit log: ", e);
        }
    }

    private Map<String, String> retrieveExistingStatus(ResultSet rs) throws SQLException {
        Map<String, String> existingStatus = new HashMap<>();
        existingStatus.put("debit_successful", rs.getString("debit_successful"));
        existingStatus.put("credit_failed", rs.getString("credit_failed"));
        existingStatus.put("credit_successful", rs.getString("credit_successful"));
        existingStatus.put("debit_advice_failed", rs.getString("debit_advice_failed"));
        existingStatus.put("debit_advice_successful", rs.getString("debit_advice_successful"));
        existingStatus.put("debit_failed", rs.getString("debit_failed"));
        return existingStatus;
    }

    private Map<String, String> mergeMaps(Map<String, String> existing, Map<String, String> newcomers) {
        Map<String, String> merged = new HashMap<>(existing);
        for (Map.Entry<String, String> entry : newcomers.entrySet()) {
            String key = entry.getKey();
            String newValue = entry.getValue();
            if (newValue.equalsIgnoreCase("Yes")) {
                merged.put(key, newValue);
            }

        }
        return merged;
    }

    public boolean isAlreadyUploaded(String uploadDate, TimeWindow window) {
        try {
            String sql = "SELECT * FROM recon.nibbs_audit_log WHERE upload_date =? AND time_window =?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date utilDate = dateFormat.parse(uploadDate);
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

                pstmt.setDate(1, sqlDate);
                pstmt.setString(2, window.name());
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {

                    return rs.getString("debit_successful").equals("Yes")
                            && rs.getString("credit_failed").equals("Yes")
                            && rs.getString("credit_successful").equals("Yes")
                            && rs.getString("debit_failed").equals("Yes");
                }
            }
        } catch (SQLException | ParseException e) {
            log.error("Error checking if already uploaded: ", e);
        }
        return false;
    }

    public Map<String, Object> getReviewDataByWindow(String window, String uploadDate) {
        Map<String, Object> reviewData = new HashMap<>();
        try {
            LocalDate date = LocalDate.parse(uploadDate);
            List<S3UrlAuditLog> s3Urls = s3UrlAuditLogRepository.findByUploadDateAndTimeWindow(date, window);
            Map<String, String> s3UrlMap = s3Urls.stream()
                    .collect(Collectors.toMap(S3UrlAuditLog::getFileType, S3UrlAuditLog::getS3Url, (v1, v2) -> v1));

            String sql = "SELECT * FROM recon.nibbs_audit_log WHERE upload_date = ? AND time_window = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setDate(1, java.sql.Date.valueOf(date));
                pstmt.setString(2, window);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    reviewData.put("Debit Successful", createStatusMap(
                            "Yes".equals(rs.getString("debit_successful")),
                            s3UrlMap.get("debit_successful")));
                    reviewData.put("Credit Failed", createStatusMap(
                            "Yes".equals(rs.getString("credit_failed")),
                            s3UrlMap.get("credit_failed")));
                    reviewData.put("Credit Successful", createStatusMap(
                            "Yes".equals(rs.getString("credit_successful")),
                            s3UrlMap.get("credit_successful")));
                    reviewData.put("Debit Advice Failed", createStatusMap(
                            "Yes".equals(rs.getString("debit_advice_failed")),
                            s3UrlMap.get("debit_advice_failed")));
                    reviewData.put("Debit Advice Successful", createStatusMap(
                            "Yes".equals(rs.getString("debit_advice_successful")),
                            s3UrlMap.get("debit_advice_successful")));
                    reviewData.put("Debit Failed", createStatusMap(
                            "Yes".equals(rs.getString("debit_failed")),
                            s3UrlMap.get("debit_failed")));
                } else {

                    reviewData.put("Debit Successful", createStatusMap(false, null));
                    reviewData.put("Credit Failed", createStatusMap(false, null));
                    reviewData.put("Credit Successful", createStatusMap(false, null));
                    reviewData.put("Debit Advice Failed", createStatusMap(false, null));
                    reviewData.put("Debit Advice Successful", createStatusMap(false, null));
                    reviewData.put("Debit Failed", createStatusMap(false, null));
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving review data: ", e);
        }
        return reviewData;
    }

    public List<Map<String, Object>> getReviewDataForAllWindows(String uploadDate) {
        List<Map<String, Object>> allWindowsData = new ArrayList<>();
        List<String> allWindows = Arrays.asList("WINDOW_6AM", "WINDOW_12PM", "WINDOW_6PM", "WINDOW_12AM");

        try {
            LocalDate date = LocalDate.parse(uploadDate);
            List<S3UrlAuditLog> s3Urls = s3UrlAuditLogRepository.findByUploadDate(date);
            Map<String, Map<String, String>> windowS3UrlMap = s3Urls.stream()
                    .filter(log -> log.getTimeWindow() != null)
                    .collect(Collectors.groupingBy(
                            S3UrlAuditLog::getTimeWindow,
                            Collectors.toMap(S3UrlAuditLog::getFileType, S3UrlAuditLog::getS3Url, (v1, v2) -> v1)));

            String sql = "SELECT time_window, debit_successful, credit_failed, credit_successful, debit_advice_failed, debit_advice_successful, debit_failed " +
                    "FROM recon.nibbs_audit_log WHERE upload_date = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setDate(1, java.sql.Date.valueOf(date));
                ResultSet rs = pstmt.executeQuery();

                Map<String, Map<String, Object>> windowStatusMap = new HashMap<>();
                while (rs.next()) {
                    String window = rs.getString("time_window");
                    Map<String, String> s3UrlMap = windowS3UrlMap.getOrDefault(window, new HashMap<>());
                    Map<String, Object> processedReviewData = new HashMap<>();
                    processedReviewData.put("debit_advice_failed", createStatusMap(
                            "Yes".equals(rs.getString("debit_advice_failed")),
                            s3UrlMap.get("debit_advice_failed")));
                    processedReviewData.put("debit_failed", createStatusMap(
                            "Yes".equals(rs.getString("debit_failed")),
                            s3UrlMap.get("debit_failed")));
                    processedReviewData.put("credit_failed", createStatusMap(
                            "Yes".equals(rs.getString("credit_failed")),
                            s3UrlMap.get("credit_failed")));
                    processedReviewData.put("credit_successful", createStatusMap(
                            "Yes".equals(rs.getString("credit_successful")),
                            s3UrlMap.get("credit_successful")));
                    processedReviewData.put("debit_advice_successful", createStatusMap(
                            "Yes".equals(rs.getString("debit_advice_successful")),
                            s3UrlMap.get("debit_advice_successful")));
                    processedReviewData.put("debit_successful", createStatusMap(
                            "Yes".equals(rs.getString("debit_successful")),
                            s3UrlMap.get("debit_successful")));
                    windowStatusMap.put(window, processedReviewData);
                }

                for (String window : allWindows) {
                    Map<String, Object> windowData = new HashMap<>();
                    windowData.put("Window", window);
                    windowData.put("Upload Date", uploadDate);

                    if (windowStatusMap.containsKey(window)) {
                        windowData.put("File upload status", windowStatusMap.get(window));
                    } else {
                        Map<String, Object> defaultStatus = new HashMap<>();
                        defaultStatus.put("debit_successful", createStatusMap(false, null));
                        defaultStatus.put("credit_failed", createStatusMap(false, null));
                        defaultStatus.put("credit_successful", createStatusMap(false, null));
                        defaultStatus.put("debit_advice_failed", createStatusMap(false, null));
                        defaultStatus.put("debit_advice_successful", createStatusMap(false, null));
                        defaultStatus.put("debit_failed", createStatusMap(false, null));
                        windowData.put("File upload status", defaultStatus);
                    }

                    allWindowsData.add(windowData);
                }
                allWindowsData.sort(Comparator.comparing(m -> m.get("Window").toString()));
            }
        } catch (SQLException e) {
            log.error("Error retrieving review data for all windows: ", e);
        } catch (DateTimeParseException e) {
            log.error("Invalid date format for uploadDate: {}", uploadDate, e);
        }

        return allWindowsData;
    }
//    private Map<String, Boolean> processReviewDataAllWindows(ResultSet rs) throws SQLException {
//        Map<String, Boolean> processedReviewData = new HashMap<>();
//        processedReviewData.put("debit_advice_failed", rs.getString("debit_advice_failed").equals("Yes"));
//        processedReviewData.put("debit_failed", rs.getString("debit_failed").equals("Yes"));
//        processedReviewData.put("credit_failed", rs.getString("credit_failed").equals("Yes"));
//        processedReviewData.put("credit_successful", rs.getString("credit_successful").equals("Yes"));
//        processedReviewData.put("debit_advice_successful", rs.getString("debit_advice_successful").equals("Yes"));
//        processedReviewData.put("debit_successful", rs.getString("debit_successful").equals("Yes"));
//        return processedReviewData;
//    }

    public Map<String, Boolean> processReviewData(Map<String, String> reviewData) {
        Map<String, Boolean> processedReviewData = new HashMap<>();

        for (Map.Entry<String, String> entry : reviewData.entrySet()) {
            processedReviewData.put(entry.getKey(), entry.getValue().equals("Yes"));
        }

        return processedReviewData;
    }

    public void processFile(String uploadDate, TimeWindow window,
                            MultipartFile file, FileType fileType,
                            String recipientsEmail, Map<String, String> fileUploadStatus) {
        boolean skipUpload = checkIfFileTypeUploaded(uploadDate, window, fileType);
        if (skipUpload) {
            log.info("Skipping upload for {} as it is already marked as 'Yes' for upload_date {} and window {}",
                    fileType.name(), uploadDate, window.name());
            fileUploadStatus.put(fileType.name(), "Yes");
            return;
        }

        if (file == null || file.isEmpty()) {
            fileUploadStatus.put(fileType.name(), "No");
            log.warn("No file provided for {}", fileType.name());
            updateAuditLog(uploadDate, window, new HashMap<>(fileUploadStatus));
            return;
        }
        try {
            validateFile(file);
            uploadFile(file, fileType, recipientsEmail, uploadDate);
            fileUploadStatus.put(fileType.name(), "Yes");
            log.info("{} uploaded successfully.", fileType.name());
        } catch (Exception ex) {
            fileUploadStatus.put(fileType.name(), "No");
            log.error("{} upload failed: {}", fileType.name(), ex.getMessage(), ex);
            emailService.sendEmail(
                    "Reconciliation Document Upload - " + fileType.name() + " File Upload Error",
                    "Failed to upload " + fileType.name() + ": " + ex.getMessage(),
                    recipientsEmail
            );
        } finally {
            updateAuditLog(uploadDate, window, new HashMap<>(fileUploadStatus));
        }
    }

    private boolean checkIfFileTypeUploaded(String uploadDate, TimeWindow window, FileType fileType) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            LocalDate localDate = dateFormat.parse(uploadDate).toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();

            Optional<NibbsAuditLog> auditLog = nibbsAuditLogRepository.findByUploadDateAndWindow(localDate, window.name());
            if (auditLog.isPresent()) {
                NibbsAuditLog log = auditLog.get();
                switch (fileType) {
                    case debit_successful:
                        return "Yes".equals(log.getDebitSuccessful());
                    case credit_failed:
                        return "Yes".equals(log.getCreditFailed());
                    case credit_successful:
                        return "Yes".equals(log.getCreditSuccessful());
                    case debit_advice_failed:
                        return "Yes".equals(log.getDebitAdviceFailed());
                    case debit_advice_successful:
                        return "Yes".equals(log.getDebitAdviceSuccessful());
                    case debit_failed:
                        return "Yes".equals(log.getDebitFailed());
                    default:
                        return false;
                }
            }
            return false;
        } catch (ParseException e) {
            log.error("Error parsing upload date {}: {}", uploadDate, e.getMessage(), e);
            return false;
        }
    }


    public List<Map<String, Object>> getUploadedData(String window, String fileType, String date) throws SQLException {
        Time timeRangeStart;
        Time timeRangeEnd;

        switch (window) {
            case "WINDOW_6AM":
                timeRangeStart = Time.valueOf("06:00:00");
                timeRangeEnd = Time.valueOf("11:59:59");
                break;
            case "WINDOW_12PM":
                timeRangeStart = Time.valueOf("12:00:00");
                timeRangeEnd = Time.valueOf("17:59:59");
                break;
            case "WINDOW_6PM":
                timeRangeStart = Time.valueOf("18:00:00");
                timeRangeEnd = Time.valueOf("23:59:59");
                break;
            case "WINDOW_12AM":
                timeRangeStart = Time.valueOf("00:00:00");
                timeRangeEnd = Time.valueOf("05:59:59");
                break;
            default:
                throw new IllegalArgumentException("Invalid window type");
        }

        String tableName = getTableName(fileType);
        if (tableName == null) {
            throw new IllegalArgumentException("Invalid file type");
        }

        String sql = String.format("SELECT * FROM recon.%s WHERE transaction_date::DATE = ? AND transaction_date::TIME BETWEEN ? AND ?", tableName);

        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, java.sql.Date.valueOf(date));
            pstmt.setTime(2, timeRangeStart);
            pstmt.setTime(3, timeRangeEnd);

            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    results.add(row);
                }
            }
        }

        return results;
    }

    private String getTableName(String fileType) {
        switch (fileType) {
            case "credit_successful":
                return "nibss_credit_successful";
            case "credit_failed":
                return "nibss_credit_failed";
            case "debit_successful":
                return "nibss_debit_successful";
            case "debit_failed":
                return "nibss_debit_failed";
            case "debit_advice_successful":
                return "nibss_debit_advice_successful";
            case "debit_advice_failed":
                return "nibss_debit_advice_failed";
            default:
                return null;
        }
    }

    @Transactional
    public void updateOtherDocumentsAuditLog(String uploadDate, FileType fileType) {
        try {
            LocalDate date = LocalDate.parse(uploadDate);
            String sql = "SELECT * FROM recon.other_docs_audit_log WHERE upload_date = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setDate(1, java.sql.Date.valueOf(date));
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String updateSql = "UPDATE recon.other_docs_audit_log SET " +
                            getColumnName(fileType) + " = 'Yes' WHERE upload_date = ?";
                    try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                        updatePstmt.setDate(1, java.sql.Date.valueOf(date));
                        updatePstmt.executeUpdate();
                    }
                } else {
                    String insertSql = "INSERT INTO recon.other_docs_audit_log " +
                            "(upload_date, " + getColumnName(fileType) + ") VALUES (?, 'Yes')";
                    try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {
                        insertPstmt.setDate(1, java.sql.Date.valueOf(date));
                        insertPstmt.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error updating other documents audit log: ", e);
        }
    }

    private String getColumnName(FileType fileType) {
        switch (fileType) {
            case providus_bank_statement: return "providus_bank_statement";
            case providus_bank_vps: return "providus_bank_vps";
            case premium_trust_bank_vps: return "premium_trust_bank_vps";
            case premium_trust_bank_statement: return "premium_trust_bank_statement";
            case funds_transfer: return "funds_transfer";
            case temporary_metabase: return "temp_metabase";
            case metabase_collections: return "meta_collections";
            default: throw new IllegalArgumentException("Invalid file type for other documents audit log");
        }
    }
    public Map<String, Object> checkOtherDocumentsUploadStatus(String uploadDate, ReportType reportType) {
        Map<String, Object> result = new HashMap<>();
        List<String> missingFiles = new ArrayList<>();
        boolean allUploaded = true;

        try {
            LocalDate date = LocalDate.parse(uploadDate);
            String sql = "SELECT * FROM recon.other_docs_audit_log WHERE upload_date = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setDate(1, java.sql.Date.valueOf(date));
                ResultSet rs = pstmt.executeQuery();

                if (!rs.next()) {
                    allUploaded = false;
                    missingFiles.addAll(getRequiredDocumentsForReportType(reportType));
                } else {
                    for (FileType fileType : getRequiredFileTypes(reportType)) {
                        String columnName = getColumnName(fileType);
                        if (!"Yes".equals(rs.getString(columnName))) {
                            allUploaded = false;
                            missingFiles.add(fileType.name());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error checking other documents upload status: ", e);
            allUploaded = false;
            missingFiles.add("Error checking upload status");
        }

        result.put("allUploaded", allUploaded);
        result.put("missingFiles", missingFiles);
        return result;
    }

    private List<FileType> getRequiredFileTypes(ReportType reportType) {
        switch (reportType) {
            case PROVIDUS_NIBSS_PREMIUM_TRUST:
                return Arrays.asList(
                        FileType.providus_bank_statement,
                        FileType.providus_bank_vps,
                        FileType.premium_trust_bank_vps,
                        FileType.premium_trust_bank_statement,
                        FileType.funds_transfer,
                        FileType.temporary_metabase,
                        FileType.metabase_collections
                );
            case PREMIUM_TRUST_NIBSS:
                return Arrays.asList(
                        FileType.premium_trust_bank_vps,
                        FileType.premium_trust_bank_statement,
                        FileType.funds_transfer,
                        FileType.temporary_metabase,
                        FileType.metabase_collections
                );
            case PROVIDUS_NIBSS:
                return Arrays.asList(
                        FileType.providus_bank_statement,
                        FileType.providus_bank_vps,
                        FileType.funds_transfer,
                        FileType.temporary_metabase,
                        FileType.metabase_collections
                );
            default:
                return Collections.emptyList();
        }
    }

    private List<String> getRequiredDocumentsForReportType(ReportType reportType) {
        return getRequiredFileTypes(reportType).stream()
                .map(FileType::name)
                .collect(Collectors.toList());
    }

    public void logUpload(String fileName, FileType fileType) throws SQLException {
        System.out.println("Starting logUpload for file: " + fileName + ", fileType: " + fileType);

        List<LocalDate> dates = new ArrayList<>();

        switch (fileType) {
            case providus_bank_statement:
                System.out.println("Fetching dates from providus_bank_statement");
                dates = fetchDatesFromTable("providus_bank_statement", "tra_date");
                break;
            case providus_bank_vps:
                System.out.println("Fetching dates from providus_bank_vps");
                dates = fetchDatesFromTable("providus_bank_vps", "created_at");
                break;
            case premium_trust_bank_statement:
                System.out.println("Fetching dates from premiumtrust_bank_statement");
                dates = fetchDatesFromTable("premiumtrust_bank_statement", "trans_date");
                break;
            case premium_trust_bank_vps:
                System.out.println("Fetching dates from premium_trust_vps");
                dates = fetchDatesFromTable("premium_trust_vps", "trans_date");
                break;
        }

        if (!dates.isEmpty()) {
            LocalDate minDate = Collections.min(dates);
            LocalDate maxDate = Collections.max(dates);
            String dateRange = minDate + "-" + maxDate;

            System.out.println("Date range found: " + dateRange);
            insertIntoAuditLog(fileName, fileType.name(), dateRange);
        } else {
            System.out.println("No dates found for fileType: " + fileType);
        }
    }

    private List<LocalDate> fetchDatesFromTable(String tableName, String dateColumn) throws SQLException {
        List<LocalDate> dates = new ArrayList<>();
        String sql = "SELECT " + dateColumn + " FROM recon." + tableName;

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String dateValue = rs.getString(dateColumn);
                if (dateValue != null && !dateValue.isEmpty()) {

                    String[] dateArray = dateValue.split(",");

                    for (String dateStr : dateArray) {
                        LocalDate parsedDate = parseDate(dateStr.trim());
                        if (parsedDate != null) {
                            dates.add(parsedDate);
                        } else {
                            System.out.println("Failed to parse date: " + dateStr.trim());
                        }
                    }
                }
            }
        }

        return dates;
    }

    private LocalDate parseDate(String dateStr) {
        List<DateTimeFormatter> dateFormats = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ISO_DATE_TIME,
                DateTimeFormatter.ofPattern("d/M/yy"),
                DateTimeFormatter.ofPattern("d/M/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("M/d/yy"),
                DateTimeFormatter.ofPattern("M/d/yyyy"),
                DateTimeFormatter.ofPattern("MM/d/yy"),
                DateTimeFormatter.ofPattern("MM/d/yyyy"),
                DateTimeFormatter.ofPattern("d/MM/yy"),
                DateTimeFormatter.ofPattern("d/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("dd-MMM-yyyy"),
                new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("dd-MMM-yyyy")
                        .toFormatter(Locale.ENGLISH)
        );

        for (DateTimeFormatter formatter : dateFormats) {
            try {
                if (formatter == DateTimeFormatter.ISO_DATE_TIME) {
                    OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateStr, formatter);
                    return offsetDateTime.toLocalDate();
                } else {
                    return LocalDate.parse(dateStr, formatter);
                }
            } catch (DateTimeParseException e) {

            }
        }
        return null;
    }
    private void insertIntoAuditLog(String fileName, String fileType, String dateRange) {
        String sql = "INSERT INTO recon.bankstatement_and_vps_audit_log (file_name, file_type, date_range) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fileName);
            pstmt.setString(2, fileType);
            pstmt.setString(3, dateRange);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ResponseEntity<?> getFileByType(FileType fileType) {
        switch (fileType) {
            case providus_bank_statement:
                return new ResponseEntity<>(getProvidusBankStatementData(), HttpStatus.FOUND);
            case providus_bank_vps:
                return new ResponseEntity<>(getProvidusBankVpsData(), HttpStatus.FOUND);
            case premium_trust_bank_vps:
                return new ResponseEntity<>(getPremiumtrustBankVpsData(), HttpStatus.FOUND);
            case premium_trust_bank_statement:
                return new ResponseEntity<>(getPremiumTrustBankStatementData(), HttpStatus.FOUND);
            case credit_failed:
                return new ResponseEntity<>(getCreditFailedCredits(), HttpStatus.FOUND);
            case credit_successful:
                return new ResponseEntity<>(getCreditSuccessfulCredits(), HttpStatus.FOUND);
            case debit_advice_failed:
                return new ResponseEntity<>(getDebitAdviceFailDebits(), HttpStatus.FOUND);
            case debit_advice_successful:
                return new ResponseEntity<>(getDebitAdviceSuccessfulDebits(), HttpStatus.FOUND);
            case debit_failed:
                return new ResponseEntity<>(getDebitFailedDebits(), HttpStatus.FOUND);
            case debit_successful:
                return new ResponseEntity<>(getDebitSuccessfulDebits(), HttpStatus.FOUND);
            default:
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @jakarta.transaction.Transactional
    public void saveProvidusBankStatementToDatabase(MultipartFile file, String recipientsEmail,String uploadDate) {
        long startTime = System.currentTimeMillis();
        System.out.println("Uploaded file content type: " + file.getContentType());

        if (ExcelUploadService.isValidExcelFile(file)) {
            emailService.sendEmail("Reconciliation Document Upload Started - Providus Bank Statement", "Your Providus Bank Statement file upload has started and will be completed soon.", recipientsEmail);

            try {
                List<ProvidusBankStatement> bankStatementList = ExcelUploadService.getProvidusBankStatementDataFromExcel(file.getInputStream(),uploadDate);
                System.out.println("File upload has started");

                int batchSize = 2000;
                for (int i = 0; i < bankStatementList.size(); i++) {
                    ProvidusBankRepository.save(bankStatementList.get(i));
                    if (i % batchSize == 0 && i > 0) {
                        ProvidusBankRepository.flush();
                        entityManager.clear();
                    }
                }
                ProvidusBankRepository.flush();
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                jdbcTemplate.execute("SELECT * FROM recon.transform_providus_bank_statement()");
                jdbcTemplate.execute("SELECT * FROM recon.move_providus_bank_statement()");
                jdbcTemplate.execute("SELECT * FROM recon.insert_providus_reversal_entries()");
                jdbcTemplate.execute("SELECT recon.move_providus_rows()");



                System.out.println("File upload has been completed in " + duration + " milliseconds.");
                updateOtherDocumentsAuditLog(uploadDate, FileType.providus_bank_statement);
                emailService.sendCompletedEmails("Reconciliation Document Upload - Bank Statement Completed", "Your Bank Statement file upload has been completed successfully.", recipientsEmail);
            } catch (ResponseStatusException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getReason();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Bank Statement File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            } catch (DataIntegrityViolationException e) {
                String sessionId = extractSessionId(e.getMessage());
                String emailMessage = "Detail: Key (session_id)=(" + sessionId + ") already exists. " +
                        "Your file contains duplicate values. Please check the session IDs and ensure all entries are unique.";
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Bank Statement File Upload Error", emailMessage, recipientsEmail);
            } catch (IOException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Bank Statement File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            } catch (Exception e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Bank Statement File Upload Error", errorMessage, recipientsEmail);
            }
        } else {
            String errorMessage = "Invalid file format. Please upload an XLSX file.";
            System.out.println(errorMessage);
            emailService.sendEmail("Reconciliation Document Upload - Bank Statement File Upload Error", errorMessage, recipientsEmail);
        }
    }
    @jakarta.transaction.Transactional
    public void saveProvidusBankVpsToDatabase(MultipartFile file, String recipientsEmail, String uploadDate) {
        long startTime = System.currentTimeMillis();
        System.out.println("Uploaded file content type: " + file.getContentType());

        if (ExcelUploadService.isValidExcelFile(file)) {
            emailService.sendEmail("Reconciliation Document Upload Started - Providus Bank VPS", "Your Providus Bank VPS file upload has started and will be completed soon.", recipientsEmail);

            try {
                List<ProvidusBankVPS> bankVPSList = ExcelUploadService.getProvidusBankVPSDataFromExcel(file.getInputStream());
                System.out.println("File upload has started");

                int batchSize = 2000;
                for (int i = 0; i < bankVPSList.size(); i++) {
                    providusBankVpsRepository.save(bankVPSList.get(i));
                    if (i % batchSize == 0 && i > 0) {
                        providusBankVpsRepository.flush();
                        entityManager.clear();
                    }
                }
                providusBankVpsRepository.flush();
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                System.out.println("File upload has been completed in " + duration + " milliseconds.");
                updateOtherDocumentsAuditLog(uploadDate, FileType.providus_bank_vps);
                emailService.sendCompletedEmails("Reconciliation Document Upload - Bank VPS Completed", "Your Bank VPS file upload has been completed successfully.", recipientsEmail);
            } catch (ResponseStatusException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getReason();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Bank VPS File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            } catch (DataIntegrityViolationException e) {
                String sessionId = extractSessionId(e.getMessage());
                String emailMessage = "Detail: Key (session_id)=(" + sessionId + ") already exists. " +
                        "Your file contains duplicate values. Please check the session IDs and ensure all entries are unique.";
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Bank VPS File Upload Error", emailMessage, recipientsEmail);
            } catch (IOException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Bank VPS File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            } catch (Exception e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Bank VPS File Upload Error", errorMessage, recipientsEmail);
            }
        } else {
            String errorMessage = "Invalid file format. Please upload an XLSX file.";
            System.out.println(errorMessage);
            emailService.sendEmail("Reconciliation Document Upload - Bank VPS File Upload Error", errorMessage, recipientsEmail );
        }
    }

    @jakarta.transaction.Transactional
    public void savePremiumTrustVpsToDatabase(MultipartFile file, String recipientsEmail,String uploadDate) {
        long startTime = System.currentTimeMillis();
        System.out.println("Uploaded file content type: " + file.getContentType());

        if (ExcelUploadService.isValidExcelFile(file)) {
            emailService.sendEmail("Reconciliation Document Upload Started - Premium Trust VPS", "Your Premium Trust VPS file upload has started and will be completed soon.", recipientsEmail);

            try {
                List<PremiumTrustVPS> premiumTrustVPSList = ExcelUploadService.getPremiumTrustVPSDataFromExcel(file.getInputStream());
                System.out.println("File upload has started");

                int batchSize = 2000;
                for (int i = 0; i < premiumTrustVPSList.size(); i++) {
                    premiumTrustVpsRepository.save(premiumTrustVPSList.get(i));
                    if (i % batchSize == 0 && i > 0) {
                        premiumTrustVpsRepository.flush();
                        entityManager.clear();
                    }
                }
                premiumTrustVpsRepository.flush();
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;

                System.out.println("File upload has been completed in " + duration + " milliseconds.");
                updateOtherDocumentsAuditLog(uploadDate, FileType.premium_trust_bank_vps);
                emailService.sendCompletedEmails("Reconciliation Document Upload - Premium Trust VPS Completed", "Your Premium Trust VPS file upload has been completed successfully.", recipientsEmail);
            } catch (ResponseStatusException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getReason();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Premium Trust VPS File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
//            }
//            catch (DataIntegrityViolationException e) {
//                String refNo = extractRefNo(e.getMessage());
//                String emailMessage = "Detail: Key (ref_no)=(" + refNo + ") already exists. " +
//                        "Your file contains duplicate values. Please check the reference numbers and ensure all entries are unique.";
//                e.printStackTrace();
//                emailService.sendEmail("Reconciliation Document Upload - Premium Trust VPS File Upload Error", emailMessage, recipientsEmail);
            } catch (IOException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Premium Trust VPS File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            } catch (Exception e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Premium Trust VPS File Upload Error", errorMessage, recipientsEmail);
            }
        } else {
            String errorMessage = "Invalid file format. Please upload an XLSX file.";
            System.out.println(errorMessage);
            emailService.sendEmail("Reconciliation Document Upload - Premium Trust VPS File Upload Error", errorMessage, recipientsEmail );
        }
    }

    @jakarta.transaction.Transactional
    public void savePremiumTrustBankStatementToDatabase(MultipartFile file, String recipientsEmail, String uploadDate) {
        long startTime = System.currentTimeMillis();
        System.out.println("Uploaded file content type: " + file.getContentType());

        if (ExcelUploadService.isValidExcelFile(file)) {
            emailService.sendEmail("Reconciliation Document Upload Started - Premium Trust Bank Statement", "Your Premium Trust Bank Statement file upload has started and will be completed soon.", recipientsEmail);

            try {
                List<PremiumTrustBankStatement> premiumTrustBankStatementList = getPremiumTrustBankStatementDataFromExcel(file.getInputStream(),uploadDate);
                System.out.println("File upload has started");

                int batchSize = 2000;
                for (int i = 0; i < premiumTrustBankStatementList.size(); i++) {
                    premiumTrustRepository.save(premiumTrustBankStatementList.get(i));
                    if (i % batchSize == 0 && i > 0) {
                        premiumTrustRepository.flush();
                        entityManager.clear();
                    }
                }
                premiumTrustRepository.flush();
                jdbcTemplate.execute("SELECT * FROM recon.premium_trust_bank_statement_transformation()");
                jdbcTemplate.execute("SELECT * FROM recon.transfer_premiumtrust_bank_statement()");
                jdbcTemplate.execute("SELECT * FROM recon.move_premiumtrust_reversals()");


                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;

                System.out.println("File upload has been completed in " + duration + " milliseconds.");
                updateOtherDocumentsAuditLog(uploadDate, FileType.premium_trust_bank_statement);
                emailService.sendCompletedEmails("Reconciliation Document Upload - Premium Trust Bank Statement Completed", "Your  Premium Trust Bank Statement file upload has been completed successfully.", recipientsEmail);
            } catch (ResponseStatusException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getReason();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Premium Trust Bank Statement File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            } catch (DataIntegrityViolationException e) {
                String sessionId = extractSessionId(e.getMessage());
                String emailMessage = "Detail: Key (session_id)=(" + sessionId + ") already exists. " +
                        "Your file contains duplicate values. Please check the session IDs and ensure all entries are unique.";
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Premium Trust Bank Statement File Upload Error", emailMessage, recipientsEmail);
            } catch (IOException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Premium Trust Bank Statement File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            } catch (Exception e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Premium Trust Bank Statement File Upload Error", errorMessage, recipientsEmail);
            }
        } else {
            String errorMessage = "Invalid file format. Please upload an XLSX file.";
            System.out.println(errorMessage);
            emailService.sendEmail("Reconciliation Document Upload - Premium Trust Bank Statement File Upload Error", errorMessage, recipientsEmail);
        }
    }

    @Transactional
    public void saveCreditFailedToDatabase(MultipartFile file, String recipientsEmail, String uploadDate) {
        emailService.sendEmail("Reconciliation Document Upload Started - Credit Failed File", "Your credit failed file upload has started and will be completed soon.", recipientsEmail);
        long startTime = System.currentTimeMillis();
        System.out.println("Uploaded file content type: " + file.getContentType());

        if (ExcelUploadService.isValidExcelFile(file) || CSVUploadService.isValidCsvFile(file)) {
            try {
                List<CreditFailed> credits;
                if (CSVUploadService.isValidCsvFile(file)) {
                    credits = CSVUploadService.getCreditFailedDataFromCsv(file.getInputStream());
                } else {
                    credits = ExcelUploadService.getCreditFailedDataFromExcel(file.getInputStream());
                }
                System.out.println("File upload has started");

                int batchSize = 100;
                for (int i = 0; i < credits.size(); i++) {
                    CreditFailedRepository.save(credits.get(i));
                    if (i % batchSize == 0 && i > 0) {
                        CreditFailedRepository.flush();
                        entityManager.clear();
                    }
                }
                CreditFailedRepository.flush();
                jdbcTemplate.execute("SELECT * FROM recon.transform_nibss_credit_failed()");
                jdbcTemplate.execute("SELECT recon.update_credit_failed('" + uploadDate + "')");
                jdbcTemplate.execute("SELECT * FROM recon.update_expected_status()");

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                System.out.println("File upload has been completed in " + duration + " milliseconds.");
                emailService.sendCompletedEmails("Reconciliation Document Upload - Credit Failed Completed", "Your credit failed file upload has been completed successfully.", recipientsEmail);
            } catch (ResponseStatusException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getReason();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Credit Failed File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            } catch (DataIntegrityViolationException e) {
                String sessionId = extractSessionId(e.getMessage());
                String emailMessage = "Detail: Key (session_id)=(" + sessionId + ") already exists. " +
                        "Your file contains duplicate values. Please check the session IDs and ensure all entries are unique.";
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Credit Failed File Upload Error", emailMessage, recipientsEmail);
            } catch (IOException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Credit Failed File Upload File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            } catch (Exception e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Credit Failed File Upload Error", errorMessage, recipientsEmail);
            }
        } else {
            String errorMessage = "Invalid file format. Please upload an XLSX or CSV file.";
            System.out.println(errorMessage);
            emailService.sendEmail("Reconciliation Document Upload - Credit Failed File Upload Error", errorMessage, recipientsEmail);
        }
    }

    @Transactional
    public void saveCreditSuccessfulToDatabase(MultipartFile file, String recipientsEmail, String uploadDate) {
        emailService.sendEmail("Reconciliation Document Upload Started - Credit Successful", "Your credit successful file upload has started and will be completed soon.", recipientsEmail);
        long startTime = System.currentTimeMillis();
        System.out.println("Uploaded file content type: " + file.getContentType());

        if (ExcelUploadService.isValidExcelFile(file) || CSVUploadService.isValidCsvFile(file)) {
            try {
                List<CreditSuccessful> credits;
                if (CSVUploadService.isValidCsvFile(file)) {
                    credits = CSVUploadService.getCreditSuccessfulDataFromCsv(file.getInputStream());
                } else {
                    credits = ExcelUploadService.getCreditSuccessfulDataFromExcel(file.getInputStream());
                }
                System.out.println("File upload has started");

                int batchSize = 100;
                for (int i = 0; i < credits.size(); i++) {
                    CreditSuccessfulRepository.save(credits.get(i));
                    if (i % batchSize == 0 && i > 0) {
                        CreditSuccessfulRepository.flush();
                        entityManager.clear();
                    }
                }
                CreditSuccessfulRepository.flush();

                jdbcTemplate.execute("SELECT * FROM recon.transform_nibss_credit_successful()");
                jdbcTemplate.execute("SELECT recon.update_credit_successful('" + uploadDate + "')");
                jdbcTemplate.execute("SELECT * FROM recon.update_expected_status()");

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                System.out.println("File upload has been completed in " + duration + " milliseconds.");
                emailService.sendCompletedEmails("Reconciliation Document Upload - Credit Successful Completed", "Your credit successful file upload has been completed successfully.", recipientsEmail);
            } catch (ResponseStatusException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getReason();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Credit Successful File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            } catch (DataIntegrityViolationException e) {
                String sessionId = extractSessionId(e.getMessage());
                String emailMessage = "Detail: Key (session_id)=(" + sessionId + ") already exists. " +
                        "Your file contains duplicate values. Please check the session IDs and ensure all entries are unique.";
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Credit Successful File Upload Error", emailMessage, recipientsEmail);
            } catch (IOException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Credit Successful File Upload File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            } catch (Exception e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Credit Successful File Upload Error", errorMessage, recipientsEmail);
            }
        } else {
            String errorMessage = "Invalid file format. Please upload an XLSX or CSV file.";
            System.out.println(errorMessage);
            emailService.sendEmail("Reconciliation Document Upload - Credit Successful File Upload Error", errorMessage, recipientsEmail);
        }
    }

    @Transactional
    public void saveDebitFailedToDatabase(MultipartFile file, String recipientsEmail, String uploadDate) {
        emailService.sendEmail("Reconciliation Document Upload  Started - Debit Failed", "Your debit failed file upload has started and will be completed soon.",recipientsEmail);
        long startTime = System.currentTimeMillis();
        System.out.println("Uploaded file content type: " + file.getContentType());

        if (ExcelUploadService.isValidExcelFile(file) || CSVUploadService.isValidCsvFile(file)) {
            try {
                List<DebitFailed> debits;
                if (CSVUploadService.isValidCsvFile(file)) {
                    debits = CSVUploadService.getDebitFailedDataFromCsv(file.getInputStream());
                } else {
                    debits = ExcelUploadService.getDebitFailedDataFromExcel(file.getInputStream());
                }
                System.out.println("File upload has started");

                int batchSize = 100;
                for (int i = 0; i < debits.size(); i++) {
                    DebitFailedRepository.save(debits.get(i));
                    if (i % batchSize == 0 && i > 0) {
                        DebitFailedRepository.flush();
                        entityManager.clear();
                    }
                }
                DebitFailedRepository.flush();
                jdbcTemplate.execute("SELECT * FROM recon.transform_nibss_debit_failed()");
                jdbcTemplate.execute("SELECT recon.update_debit_failed('" + uploadDate + "')");
                jdbcTemplate.execute("SELECT * FROM recon.update_expected_status()");

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                System.out.println("File upload has been completed in " + duration + " milliseconds.");
                emailService.sendCompletedEmails("Reconciliation Document Upload - Debit Failed Completed", "Your debit failed file upload has been completed successfully.",recipientsEmail);
            }
            catch (ResponseStatusException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getReason();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Debit Failed File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            }
            catch (DataIntegrityViolationException e) {
                String sessionId = extractSessionId(e.getMessage());
                String emailMessage = "Detail: Key (session_id)=(" + sessionId + ") already exists. " +
                        "Your file contains duplicate values. Please check the session IDs and ensure all entries are unique.";
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Debit Failed File Upload Error", emailMessage,recipientsEmail);
            } catch (IOException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Debit Failed File Upload File Upload Error", errorMessage,recipientsEmail);
                e.printStackTrace();
            } catch (Exception e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Debit Failed File Upload Error", errorMessage,recipientsEmail);
            }
        } else {
            String errorMessage = "Invalid file format. Please upload an XLSX or CSV file.";
            System.out.println(errorMessage);
            emailService.sendEmail("Reconciliation Document Upload - Debit Failed File Upload Error", errorMessage,recipientsEmail);
        }
    }

    @Transactional
    public void saveDebitSuccessfulToDatabase(MultipartFile file, String recipientsEmail, String uploadDate) {
        emailService.sendEmail("Reconciliation Document Upload Started - Debit Successful", "Your debit successful file upload has started and will be completed soon.",recipientsEmail);
        long startTime = System.currentTimeMillis();
        System.out.println("Uploaded file content type: " + file.getContentType());

        if (ExcelUploadService.isValidExcelFile(file) || CSVUploadService.isValidCsvFile(file)) {
            try {
                List<DebitSuccessful> debits;
                if (CSVUploadService.isValidCsvFile(file)) {
                    debits = CSVUploadService.getDebitSuccessfulDataFromCsv(file.getInputStream());
                } else {
                    debits = ExcelUploadService.getDebitSuccessfulDataFromExcel(file.getInputStream());
                }
                System.out.println("File upload has started");

                int batchSize = 100;
                for (int i = 0; i < debits.size(); i++) {
                    DebitSuccessfulRepository.save(debits.get(i));
                    if (i % batchSize == 0 && i > 0) {
                        DebitSuccessfulRepository.flush();
                        entityManager.clear();
                    }
                }
                DebitSuccessfulRepository.flush();
                jdbcTemplate.execute("SELECT * FROM recon.transform_nibss_debit_successful()");
                jdbcTemplate.execute("SELECT recon.update_debit_successful('" + uploadDate + "')");
                jdbcTemplate.execute("SELECT * FROM recon.update_expected_status()");

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                System.out.println("File upload has been completed in " + duration + " milliseconds.");
                emailService.sendCompletedEmails("Reconciliation Document Upload - Debit Successful Completed", "Your debit successful file upload has been completed successfully.",recipientsEmail);
            }
            catch (ResponseStatusException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getReason();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Debit Successful File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            }
            catch (DataIntegrityViolationException e) {
                String sessionId = extractSessionId(e.getMessage());
                String emailMessage = "Detail: Key (session_id)=(" + sessionId + ") already exists. " +
                        "Your file contains duplicate values. Please check the session IDs and ensure all entries are unique.";
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Debit Successful File Upload Error", emailMessage,recipientsEmail);
            } catch (IOException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Debit Successful File Upload File Upload Error", errorMessage,recipientsEmail);
                e.printStackTrace();
            } catch (Exception e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Debit Successful File Upload Error", errorMessage,recipientsEmail);
            }
        } else {
            String errorMessage = "Invalid file format. Please upload an XLSX or CSV file.";
            System.out.println(errorMessage);
            emailService.sendEmail("Reconciliation Document Upload - Debit Successful File Upload Error", errorMessage,recipientsEmail);
        }
    }

    @Transactional
    public void saveDebitAdviceFailedToDatabase(MultipartFile file, String recipientsEmail, String uploadDate) {
        emailService.sendEmail("Reconciliation Document Upload Started - Debit Advice Failed", "Your Debit Advice Failed file upload has started and will be completed soon.",recipientsEmail);
        long startTime = System.currentTimeMillis();
        System.out.println("Uploaded file content type: " + file.getContentType());

        if (ExcelUploadService.isValidExcelFile(file) || CSVUploadService.isValidCsvFile(file)) {
            try {
                List<DebitAdviceFailed> debits;
                if (CSVUploadService.isValidCsvFile(file)) {
                    debits = CSVUploadService.getDebitAdviceFailedDataFromCsv(file.getInputStream());
                } else {
                    debits = ExcelUploadService.getDebitAdviceFailedDataFromExcel(file.getInputStream());
                }
                System.out.println("File upload has started");

                int batchSize = 100;
                for (int i = 0; i < debits.size(); i++) {
                    DebitAdviceFailedRepository.save(debits.get(i));
                    if (i % batchSize == 0 && i > 0) {
                        DebitAdviceFailedRepository.flush();
                        entityManager.clear();
                    }
                }
                DebitAdviceFailedRepository.flush();
                jdbcTemplate.execute("SELECT * FROM recon.transform_nibss_debit_advice_failed()");
                jdbcTemplate.execute("SELECT recon.update_debit_advice_failed('" + uploadDate + "')");
                jdbcTemplate.execute("SELECT * FROM recon.update_expected_status()");

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                System.out.println("File upload has been completed in " + duration + " milliseconds.");
                emailService.sendCompletedEmails("Reconciliation Document Upload - Debit Advice Failed Completed", "Your Debit Advice Failed file upload has been completed successfully.",recipientsEmail);
            }
            catch (ResponseStatusException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getReason();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Debit Advice Failed File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            }
            catch (DataIntegrityViolationException e) {
                String sessionId = extractSessionId(e.getMessage());
                String emailMessage = "Detail: Key (session_id)=(" + sessionId + ") already exists. " +
                        "Your file contains duplicate values. Please check the session IDs and ensure all entries are unique.";
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Debit Advice Failed File Upload Error", emailMessage,recipientsEmail);
            } catch (IOException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Debit Advice Failed File Upload File Upload Error", errorMessage,recipientsEmail);
                e.printStackTrace();
            } catch (Exception e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Debit Advice Failed File Upload Error", errorMessage,recipientsEmail);
            }
        } else {
            String errorMessage = "Invalid file format. Please upload an XLSX or CSV file.";
            System.out.println(errorMessage);
            emailService.sendEmail("Reconciliation Document Upload - Debit Advice Failed File Upload Error", errorMessage,recipientsEmail);
        }
    }

    @Transactional
    public void saveDebitAdviceSuccessfulToDatabase(MultipartFile file, String recipientsEmail, String uploadDate) {
        emailService.sendEmail("Reconciliation Document Upload Started - Debit Advice Successful", "Your Debit Advice Successful file upload has started and will be completed soon.",recipientsEmail);
        long startTime = System.currentTimeMillis();
        System.out.println("Uploaded file content type: " + file.getContentType());

        if (ExcelUploadService.isValidExcelFile(file) || CSVUploadService.isValidCsvFile(file)) {
            try {
                List<DebitAdviceSuccessful> debits;
                if (CSVUploadService.isValidCsvFile(file)) {
                    debits = CSVUploadService.getDebitAdviceSuccessfulDataFromCsv(file.getInputStream());
                } else {
                    debits = ExcelUploadService.getDebitAdviceSuccessfulDataFromExcel(file.getInputStream());
                }
                System.out.println("File upload has started");

                int batchSize = 100;
                for (int i = 0; i < debits.size(); i++) {
                    DebitAdviceSuccessfulRepository.save(debits.get(i));
                    if (i % batchSize == 0 && i > 0) {
                        DebitAdviceSuccessfulRepository.flush();
                        entityManager.clear();
                    }
                }
                DebitAdviceSuccessfulRepository.flush();
                jdbcTemplate.execute("SELECT * FROM recon.transform_nibss_debit_advice_successful()");
                jdbcTemplate.execute("SELECT recon.update_debit_advice_successful('" + uploadDate + "')");
                jdbcTemplate.execute("SELECT * FROM recon.update_expected_status()");

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                System.out.println("File upload has been completed in " + duration + " milliseconds.");
                emailService.sendCompletedEmails("Reconciliation Document Upload - Debit Advice Successful Completed", "Your Debit Advice Successful file upload has been completed successfully.",recipientsEmail);
            }
            catch (ResponseStatusException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getReason();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Debit Advice Successful File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            }
            catch (DataIntegrityViolationException e) {
                String sessionId = extractSessionId(e.getMessage());
                String emailMessage = "Detail: Key (session_id)=(" + sessionId + ") already exists. " +
                        "Your file contains duplicate values. Please check the session IDs and ensure all entries are unique.";
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Debit Advice Successful File Upload Error", emailMessage,recipientsEmail);
            } catch (IOException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                emailService.sendEmail("Reconciliation Document Upload - Debit Advice Successful File Upload File Upload Error", errorMessage,recipientsEmail);
                e.printStackTrace();
            } catch (Exception e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                e.printStackTrace();
                emailService.sendEmail("Reconciliation Document Upload - Debit Advice Successful File Upload Error", errorMessage,recipientsEmail);
            }
        } else {
            String errorMessage = "Invalid file format. Please upload an XLSX or CSV file.";
            System.out.println(errorMessage);
            emailService.sendEmail("Reconciliation Document Upload - Debit Advice Successful File Upload Error", errorMessage,recipientsEmail);
        }
    }

    @Transactional
    public void saveTempMetabaseToDatabase(MultipartFile file, String recipientsEmail, String uploadDate) {
        long startTime = System.currentTimeMillis();
        System.out.println("Uploaded file content type: " + file.getContentType());

        if (ExcelUploadService.isValidExcelFile(file)) {
            emailService.sendEmail("Temp Metabase File Upload Started", "Your Temp Metabase file upload has started and will be completed soon.", recipientsEmail);

            try {
                List<TempMetabase> tempMetabaseList = ExcelUploadService.getTempMetabaseDataFromExcel(file.getInputStream());
                System.out.println("File upload has started");

                int batchSize = 100;
                for (int i = 0; i < tempMetabaseList.size(); i++) {
                    tempMetabaseRepository.save(tempMetabaseList.get(i));
                    if (i % batchSize == 0 && i > 0) {
                        tempMetabaseRepository.flush();
                        entityManager.clear();
                    }
                }
                tempMetabaseRepository.flush();
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                jdbcTemplate.execute("SELECT * FROM recon.move_meta_debits()");
                jdbcTemplate.execute("SELECT * FROM recon.move_meta_credits_and_reversals()");
                updateOtherDocumentsAuditLog(uploadDate, FileType.temporary_metabase);
                System.out.println("File upload has been completed in " + duration + " milliseconds.");
                emailService.sendCompletedEmails("Temp Metabase File Upload Completed", "Your Temp Metabase file upload has been completed successfully.", recipientsEmail);
            }

            catch (ResponseStatusException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getReason();
                System.out.println(errorMessage);
                emailService.sendEmail("Metabase File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            }
            catch (DataIntegrityViolationException e) {
                String sessionId = extractSessionId(e.getMessage());
                String emailMessage = "Detail: Key (session_id)=(" + sessionId + ") already exists. " +
                        "Your file contains duplicate values. Please check the session IDs and ensure all entries are unique.";
                e.printStackTrace();
                emailService.sendEmail("Metabase File Upload Error", emailMessage, recipientsEmail);
            }
            catch (IOException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                emailService.sendEmail("Metabase File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            }
            catch (Exception e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                e.printStackTrace();
                emailService.sendEmail("Metabase File Upload Error", errorMessage, recipientsEmail);
            }
        } else {
            String errorMessage = "Invalid file format. Please upload an XLSX file.";
            System.out.println(errorMessage);
            emailService.sendEmail("Metabase File Upload Error", errorMessage, recipientsEmail);
        }
    }

    @jakarta.transaction.Transactional
    public void saveFundsTransferToDatabase(MultipartFile file, String recipientsEmail, String uploadDate) {
        long startTime = System.currentTimeMillis();
        System.out.println("Uploaded file content type: " + file.getContentType());

        if (ExcelUploadService.isValidExcelFile(file)) {
            emailService.sendEmail("Funds Transfer File Upload Started", "Your Funds Transfer file upload has started and will be completed soon.", recipientsEmail);

            try {
                List<FundsTransfer> fundTransferList = ExcelUploadService.getFundsTransferDataFromExcel(file.getInputStream());
                System.out.println("File upload has started");

                int batchSize = 100;
                for (int i = 0; i < fundTransferList.size(); i++) {
                    fundsTransferRepository.save(fundTransferList.get(i));
                    if (i % batchSize == 0 && i > 0) {
                        fundsTransferRepository.flush();
                        entityManager.clear();
                    }
                }
                fundsTransferRepository.flush();
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                System.out.println("File upload has been completed in " + duration + " milliseconds.");
                updateOtherDocumentsAuditLog(uploadDate, FileType.funds_transfer);
                emailService.sendCompletedEmails("Funds Transfer File Upload Completed", "Your Funds Transfer file upload has been completed successfully.", recipientsEmail);
            } catch (ResponseStatusException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getReason();
                System.out.println(errorMessage);
                emailService.sendEmail("Funds Transfer File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            } catch (DataIntegrityViolationException e) {
                String sessionId = extractSessionId(e.getMessage());
                String emailMessage = "Detail: Key (session_id)=(" + sessionId + ") already exists. " +
                        "Your file contains duplicate values. Please check the session IDs and ensure all entries are unique.";
                e.printStackTrace();
                emailService.sendEmail("Funds Transfer File Upload Error", emailMessage, recipientsEmail);
            } catch (IOException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                emailService.sendEmail("Funds Transfer File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            } catch (Exception e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                e.printStackTrace();
                emailService.sendEmail("Funds Transfer File Upload Error", errorMessage, recipientsEmail);
            }
        } else {
            String errorMessage = "Invalid file format. Please upload an XLSX file.";
            System.out.println(errorMessage);
            emailService.sendEmail("Funds Transfer File Upload Error", errorMessage, recipientsEmail);
        }
    }

    @jakarta.transaction.Transactional
    public void saveMetaCollectionsToDatabase(MultipartFile file, String recipientsEmail, String uploadDate) {
        long startTime = System.currentTimeMillis();
        System.out.println("Uploaded file content type: " + file.getContentType());

        if (ExcelUploadService.isValidExcelFile(file)) {
            emailService.sendEmail("Meta Collections File Upload Started", "Your Meta Collections file upload has started and will be completed soon.", recipientsEmail);

            try {
                List<MetaCollections> metaCollectionsList = ExcelUploadService.getMetaCollectionsDataFromExcel(file.getInputStream());
                System.out.println("File upload has started");

                int batchSize = 100;
                for (int i = 0; i < metaCollectionsList.size(); i++) {
                    metaCollectionsRepository.save(metaCollectionsList.get(i));
                    if (i % batchSize == 0 && i > 0) {
                        metaCollectionsRepository.flush();
                        entityManager.clear();
                    }
                }
                metaCollectionsRepository.flush();
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;
                updateOtherDocumentsAuditLog(uploadDate, FileType.metabase_collections);
                System.out.println("File upload has been completed in " + duration + " milliseconds.");
                emailService.sendCompletedEmails("Meta Collections File Upload Completed", "Your Meta Collections file upload has been completed successfully.", recipientsEmail);
            } catch (ResponseStatusException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getReason();
                System.out.println(errorMessage);
                emailService.sendEmail("Meta Collections File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            } catch (DataIntegrityViolationException e) {
                String sessionId = extractSessionId(e.getMessage());
                String emailMessage = "Detail: Key (session_id)=(" + sessionId + ") already exists. " +
                        "Your file contains duplicate values. Please check the session IDs and ensure all entries are unique.";
                e.printStackTrace();
                emailService.sendEmail("Meta Collections File Upload Error", emailMessage, recipientsEmail);
            } catch (IOException e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                emailService.sendEmail("Meta Collections File Upload Error", errorMessage, recipientsEmail);
                e.printStackTrace();
            } catch (Exception e) {
                String errorMessage = "An unexpected error occurred during the upload process: " + e.getMessage();
                System.out.println(errorMessage);
                e.printStackTrace();
                emailService.sendEmail("Meta Collections File Upload Error", errorMessage, recipientsEmail);
            }
        } else {
            String errorMessage = "Invalid file format. Please upload an XLSX file.";
            System.out.println(errorMessage);
            emailService.sendEmail("Meta Collections File Upload Error", errorMessage, recipientsEmail);
        }
    }

    public Map<String, Object> getOtherDocumentsUploadStatus(String uploadDate) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> statusList = new ArrayList<>();

        try {
            LocalDate date = LocalDate.parse(uploadDate);
            List<S3UrlAuditLog> s3Urls = s3UrlAuditLogRepository.findByUploadDate(date);
            Map<String, String> s3UrlMap = s3Urls.stream()
                    .collect(Collectors.toMap(S3UrlAuditLog::getFileType, S3UrlAuditLog::getS3Url, (v1, v2) -> v1));

            String sql = "SELECT * FROM recon.other_docs_audit_log WHERE upload_date = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setDate(1, java.sql.Date.valueOf(date));
                ResultSet rs = pstmt.executeQuery();

                Map<String, Object> docStatus = new LinkedHashMap<>();
                if (rs.next()) {

                    docStatus.put("providus_bank_statement", createStatusMap(
                            "Yes".equals(rs.getString("providus_bank_statement")),
                            s3UrlMap.get("providus_bank_statement")));
                    docStatus.put("providus_bank_vps", createStatusMap(
                            "Yes".equals(rs.getString("providus_bank_vps")),
                            s3UrlMap.get("providus_bank_vps")));
                    docStatus.put("premium_trust_bank_vps", createStatusMap(
                            "Yes".equals(rs.getString("premium_trust_bank_vps")),
                            s3UrlMap.get("premium_trust_bank_vps")));
                    docStatus.put("premium_trust_bank_statement", createStatusMap(
                            "Yes".equals(rs.getString("premium_trust_bank_statement")),
                            s3UrlMap.get("premium_trust_bank_statement")));
                    docStatus.put("funds_transfer", createStatusMap(
                            "Yes".equals(rs.getString("funds_transfer")),
                            s3UrlMap.get("funds_transfer")));
                    docStatus.put("temporary_metabase", createStatusMap(
                            "Yes".equals(rs.getString("temp_metabase")),
                            s3UrlMap.get("temporary_metabase")));
                    docStatus.put("metabase_collections", createStatusMap(
                            "Yes".equals(rs.getString("meta_collections")),
                            s3UrlMap.get("metabase_collections")));
                } else {

                    docStatus.put("providus_bank_statement", createStatusMap(false, null));
                    docStatus.put("providus_bank_vps", createStatusMap(false, null));
                    docStatus.put("premium_trust_bank_vps", createStatusMap(false, null));
                    docStatus.put("premium_trust_bank_statement", createStatusMap(false, null));
                    docStatus.put("funds_transfer", createStatusMap(false, null));
                    docStatus.put("temp_metabase", createStatusMap(false, null));
                    docStatus.put("meta_collections", createStatusMap(false, null));
                    response.put("Message", "No upload records found for this date");
                }

                response.put("Upload Date", uploadDate);
                response.put("Documents Upload Status", docStatus);
            }
        } catch (Exception e) {
            log.error("Error retrieving other documents upload status: ", e);
            response.put("Error", "Failed to retrieve upload status: " + e.getMessage());
        }

        return response;
    }

    private Map<String, Object> createStatusMap(boolean uploaded, String s3Url) {
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("uploaded", uploaded);
        statusMap.put("s3Url", s3Url);
        return statusMap;
    }

    public List<ProvidusBankStatement> getProvidusBankStatementData() {
        List<ProvidusBankStatement> records = ProvidusBankRepository.findAll();
        return records;
    }

    public List<CreditFailed> getCreditFailedCredits() {
        List<CreditFailed> records = CreditFailedRepository.findAll();
        return records;
    }

    public List<ProvidusBankVPS> getProvidusBankVpsData() {
        List<ProvidusBankVPS> records = providusBankVpsRepository.findAll();
        return records;
    }

    public List<PremiumTrustVPS> getPremiumtrustBankVpsData() {
        List<PremiumTrustVPS> records = premiumTrustVpsRepository.findAll();
        return records;
    }

    public List<CreditSuccessful> getCreditSuccessfulCredits() {
        List<CreditSuccessful> records = CreditSuccessfulRepository.findAll();
        return records;
    }

    public List<DebitAdviceFailed> getDebitAdviceFailDebits() {
        List<DebitAdviceFailed> records = DebitAdviceFailedRepository.findAll();
        return records;
    }

    public List<DebitAdviceSuccessful> getDebitAdviceSuccessfulDebits() {
        List<DebitAdviceSuccessful> records = DebitAdviceSuccessfulRepository.findAll();
        return records;
    }

    public List<PremiumTrustBankStatement> getPremiumTrustBankStatementData() {
        return premiumTrustRepository.findAll();
    }

    public List<DebitFailed> getDebitFailedDebits() {
        List<DebitFailed> records = DebitFailedRepository.findAll();
        return records;
    }

    public List<DebitSuccessful> getDebitSuccessfulDebits() {
        List<DebitSuccessful> records = DebitSuccessfulRepository.findAll();
        return records;
    }

    private String extractSessionId(String message) {
        String regex = "session_id\\)=\\((\\d+)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return "unknown";
    }

    public void saveS3Url(String uploadDate, String fileType, String s3Url, String timeWindow) {
        if (s3Url == null || s3Url.trim().isEmpty()) {
            log.info("Skipping S3 URL save for fileType: {} - URL is empty", fileType);
            return;
        }
        try {
            S3UrlAuditLog auditLog = new S3UrlAuditLog();
            auditLog.setUploadDate(LocalDate.parse(uploadDate));
            auditLog.setFileType(fileType);
            auditLog.setS3Url(s3Url);
            auditLog.setTimeWindow(timeWindow);
            auditLog.setCreatedAt(LocalDateTime.now());
            s3UrlAuditLogRepository.save(auditLog);
            log.info("Saved S3 URL for fileType: {}, uploadDate: {}, timeWindow: {}", fileType, uploadDate, timeWindow);
        } catch (Exception e) {
            log.error("Error saving S3 URL for fileType: {}, uploadDate: {}, timeWindow: {}", fileType, uploadDate, timeWindow, e);
        }
    }

    public List<S3UrlAuditLog> getS3UrlsByFileTypeAndUploadDate(String fileType, String uploadDate) {
        try {
            LocalDate date = LocalDate.parse(uploadDate);
            return s3UrlAuditLogRepository.findByUploadDateAndFileType(date, fileType);
        } catch (Exception e) {
            log.error("Error retrieving S3 URLs for fileType: {}, uploadDate: {}", fileType, uploadDate, e);
            return Collections.emptyList();
        }
    }

    public List<S3UrlAuditLog> getS3UrlsByUploadDate(String uploadDate) {
        try {
            LocalDate date = LocalDate.parse(uploadDate);
            return s3UrlAuditLogRepository.findByUploadDate(date);
        } catch (Exception e) {
            log.error("Error retrieving S3 URLs for uploadDate: {}", uploadDate, e);
            return Collections.emptyList();
        }
    }

    public List<S3UrlAuditLog> getS3UrlsByFileType(String fileType) {
        try {
            return s3UrlAuditLogRepository.findByFileType(fileType);
        } catch (Exception e) {
            log.error("Error retrieving S3 URLs for fileType: {}", fileType, e);
            return Collections.emptyList();
        }
    }

    public Page<Map<String, Object>> getUploadedDataPaginated(
            String window, String fileType, String date, Pageable pageable, Map<String, String> filters) throws SQLException {
        Time timeRangeStart;
        Time timeRangeEnd;

        switch (window) {
            case "WINDOW_6AM":
                timeRangeStart = Time.valueOf("06:00:00");
                timeRangeEnd = Time.valueOf("11:59:59");
                break;
            case "WINDOW_12PM":
                timeRangeStart = Time.valueOf("12:00:00");
                timeRangeEnd = Time.valueOf("17:59:59");
                break;
            case "WINDOW_6PM":
                timeRangeStart = Time.valueOf("18:00:00");
                timeRangeEnd = Time.valueOf("23:59:59");
                break;
            case "WINDOW_12AM":
                timeRangeStart = Time.valueOf("00:00:00");
                timeRangeEnd = Time.valueOf("05:59:59");
                break;
            default:
                throw new IllegalArgumentException("Invalid window type");
        }

        String tableName = getTableName(fileType);
        if (tableName == null) {
            throw new IllegalArgumentException("Invalid file type");
        }

        StringBuilder sql = new StringBuilder(String.format(
                "SELECT * FROM recon.%s WHERE transaction_date::DATE = ? AND transaction_date::TIME BETWEEN ? AND ?",
                tableName));
        StringBuilder countSql = new StringBuilder(String.format(
                "SELECT COUNT(*) FROM recon.%s WHERE transaction_date::DATE = ? AND transaction_date::TIME BETWEEN ? AND ?",
                tableName));

        List<Object> params = new ArrayList<>();
        params.add(java.sql.Date.valueOf(date));
        params.add(timeRangeStart);
        params.add(timeRangeEnd);

        appendFilterConditions(sql, countSql, params, filters);

        sql.append(" ORDER BY transaction_date LIMIT ? OFFSET ?");
        params.add(pageable.getPageSize());
        params.add(pageable.getOffset());

        List<Map<String, Object>> results = new ArrayList<>();
        long totalCount = 0;

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = pstmt.executeQuery()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    while (rs.next()) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(metaData.getColumnName(i), rs.getObject(i));
                        }
                        results.add(row);
                    }
                }
            }

            try (PreparedStatement countPstmt = conn.prepareStatement(countSql.toString())) {
                for (int i = 0; i < params.size() - 2; i++) {
                    countPstmt.setObject(i + 1, params.get(i));
                }
                try (ResultSet countRs = countPstmt.executeQuery()) {
                    if (countRs.next()) {
                        totalCount = countRs.getLong(1);
                    }
                }
            }
        }

        return new PageImpl<>(results, pageable, totalCount);
    }

    private void appendFilterConditions(StringBuilder sql, StringBuilder countSql, List<Object> params, Map<String, String> filters) {
        List<String> conditions = new ArrayList<>();

        if (filters.get("transaction_id") != null && !filters.get("transaction_id").trim().isEmpty()) {
            conditions.add("transaction_id = ?");
            params.add(filters.get("transaction_id"));
        }
        if (filters.get("session_id") != null && !filters.get("session_id").trim().isEmpty()) {
            conditions.add("session_id = ?");
            params.add(filters.get("session_id"));
        }
        if (filters.get("status") != null && !filters.get("status").trim().isEmpty()) {
            conditions.add("status = ?");
            params.add(filters.get("status"));
        }
        if (filters.get("message") != null && !filters.get("message").trim().isEmpty()) {
            conditions.add("message LIKE ?");
            params.add("%" + filters.get("message") + "%");
        }
        if (filters.get("nip_response_code") != null && !filters.get("nip_response_code").trim().isEmpty()) {
            conditions.add("nip_response_code = ?");
            params.add(filters.get("nip_response_code"));
        }
        if (filters.get("name_enquiry_ref") != null && !filters.get("name_enquiry_ref").trim().isEmpty()) {
            conditions.add("name_enquiry_ref = ?");
            params.add(filters.get("name_enquiry_ref"));
        }
        if (filters.get("destination_institution_code") != null && !filters.get("destination_institution_code").trim().isEmpty()) {
            conditions.add("destination_institution_code = ?");
            params.add(filters.get("destination_institution_code"));
        }
        if (filters.get("beneficiary_account_number") != null && !filters.get("beneficiary_account_number").trim().isEmpty()) {
            conditions.add("beneficiary_account_number = ?");
            params.add(filters.get("beneficiary_account_number"));
        }
        if (filters.get("beneficiary_bvn") != null && !filters.get("beneficiary_bvn").trim().isEmpty()) {
            conditions.add("beneficiary_bvn = ?");
            params.add(filters.get("beneficiary_bvn"));
        }
        if (filters.get("originator_account_name") != null && !filters.get("originator_account_name").trim().isEmpty()) {
            conditions.add("originator_account_name = ?");
            params.add(filters.get("originator_account_name"));
        }
        if (filters.get("originator_account_number") != null && !filters.get("originator_account_number").trim().isEmpty()) {
            conditions.add("originator_account_number = ?");
            params.add(filters.get("originator_account_number"));
        }
        if (filters.get("originator_bvn") != null && !filters.get("originator_bvn").trim().isEmpty()) {
            conditions.add("originator_bvn = ?");
            params.add(filters.get("originator_bvn"));
        }
        if (filters.get("payment_reference") != null && !filters.get("payment_reference").trim().isEmpty()) {
            conditions.add("payment_reference = ?");
            params.add(filters.get("payment_reference"));
        }
        if (filters.get("transaction_location") != null && !filters.get("transaction_location").trim().isEmpty()) {
            conditions.add("transaction_location = ?");
            params.add(filters.get("transaction_location"));
        }
        if (filters.get("payaza_reference") != null && !filters.get("payaza_reference").trim().isEmpty()) {
            conditions.add("payaza_reference = ?");
            params.add(filters.get("payaza_reference"));
        }
        if (filters.get("transaction_date") != null && !filters.get("transaction_date").trim().isEmpty()) {
            try {
                LocalDate parsedTransactionDate = LocalDate.parse(filters.get("transaction_date"), DateTimeFormatter.ISO_LOCAL_DATE);
                conditions.add("CAST(transaction_date AS DATE) = ?");
                params.add(java.sql.Date.valueOf(parsedTransactionDate));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid transaction_date format. Use YYYY-MM-DD", e);
            }
        }

        if (!conditions.isEmpty()) {
            String conditionStr = " AND " + String.join(" AND ", conditions);
            sql.append(conditionStr);
            countSql.append(conditionStr);
        }
    }
    public Map<String, Object> getStructuredS3Urls(String uploadDate, String fileType, String timeWindow) {
        Map<String, Object> response = new HashMap<>();

        if (uploadDate == null || uploadDate.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload date is required and cannot be empty.");
        }

        if (!isValidDate(uploadDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid upload date format. Please use YYYY-MM-DD.");
        }

        if (fileType != null && !fileType.trim().isEmpty()) {
            try {
                FileType.valueOf(fileType);
                if (!isValidFileType(FileType.valueOf(fileType))) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Invalid file type: " + fileType + ". Allowed values are: " +
                                    Arrays.stream(FileType.values()).map(Enum::name).collect(Collectors.joining(", ")));
                }
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid file type: " + fileType + ". Allowed values are: " +
                                Arrays.stream(FileType.values()).map(Enum::name).collect(Collectors.joining(", ")));
            }
        }

        if (timeWindow != null && !timeWindow.trim().isEmpty()) {
            if (!Arrays.stream(TimeWindow.values()).map(Enum::name).collect(Collectors.toList()).contains(timeWindow)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid time window: " + timeWindow + ". Allowed values are: " +
                                Arrays.stream(TimeWindow.values()).map(Enum::name).collect(Collectors.joining(", ")));
            }
        }

        try {
            List<S3UrlAuditLog> s3Urls;
            if (fileType != null && !fileType.trim().isEmpty() && timeWindow != null && !timeWindow.trim().isEmpty()) {
                s3Urls = getS3UrlsByFileTypeAndUploadDate(fileType, uploadDate).stream()
                        .filter(log -> timeWindow.equals(log.getTimeWindow()))
                        .collect(Collectors.toList());
            } else if (fileType != null && !fileType.trim().isEmpty()) {
                s3Urls = getS3UrlsByFileTypeAndUploadDate(fileType, uploadDate);
            } else if (timeWindow != null && !timeWindow.trim().isEmpty()) {
                s3Urls = getS3UrlsByUploadDate(uploadDate).stream()
                        .filter(log -> timeWindow.equals(log.getTimeWindow()))
                        .collect(Collectors.toList());
            } else {
                s3Urls = getS3UrlsByUploadDate(uploadDate);
            }

            if (s3Urls.isEmpty()) {
                response.put("uploadDate", uploadDate);
                response.put("timeWindows", new HashMap<>());
                response.put("otherDocuments", buildEmptyOtherDocuments());
                return response;
            }
            Map<String, Map<String, String>> timeWindows = new HashMap<>();
            Map<String, Object> otherDocuments = new HashMap<>();

            for (TimeWindow window : TimeWindow.values()) {
                Map<String, String> fileTypeUrls = new HashMap<>();
                fileTypeUrls.put("debit_successful", null);
                fileTypeUrls.put("credit_failed", null);
                fileTypeUrls.put("credit_successful", null);
                fileTypeUrls.put("debit_advice_failed", null);
                fileTypeUrls.put("debit_advice_successful", null);
                fileTypeUrls.put("debit_failed", null);
                timeWindows.put(window.name(), fileTypeUrls);
            }

            Map<String, String> providus = new HashMap<>();
            providus.put("bank_statement", null);
            providus.put("vps", null);
            Map<String, String> premiumTrust = new HashMap<>();
            premiumTrust.put("bank_statement", null);
            premiumTrust.put("vps", null);
            otherDocuments.put("providus", providus);
            otherDocuments.put("premium_trust", premiumTrust);
            otherDocuments.put("funds_transfer", null);
            otherDocuments.put("temporary_metabase", null);
            otherDocuments.put("metabase_collections", null);


            for (S3UrlAuditLog log : s3Urls) {
                String fileTypeName = log.getFileType();
                String s3Url = log.getS3Url();
                String window = log.getTimeWindow();


                if (Arrays.asList("debit_successful", "credit_failed", "credit_successful",
                                "debit_advice_failed", "debit_advice_successful", "debit_failed")
                        .contains(fileTypeName) && window != null) {
                    if (timeWindows.containsKey(window)) {
                        timeWindows.get(window).put(fileTypeName, s3Url);
                    }
                } else {

                    if (fileTypeName.equals("providus_bank_statement")) {
                        ((Map<String, String>) otherDocuments.get("providus")).put("bank_statement", s3Url);
                    } else if (fileTypeName.equals("providus_bank_vps")) {
                        ((Map<String, String>) otherDocuments.get("providus")).put("vps", s3Url);
                    } else if (fileTypeName.equals("premium_trust_bank_statement")) {
                        ((Map<String, String>) otherDocuments.get("premium_trust")).put("bank_statement", s3Url);
                    } else if (fileTypeName.equals("premium_trust_bank_vps")) {
                        ((Map<String, String>) otherDocuments.get("premium_trust")).put("vps", s3Url);
                    } else if (fileTypeName.equals("funds_transfer")) {
                        otherDocuments.put("funds_transfer", s3Url);
                    } else if (fileTypeName.equals("temporary_metabase")) {
                        otherDocuments.put("temporary_metabase", s3Url);
                    } else if (fileTypeName.equals("metabase_collections")) {
                        otherDocuments.put("metabase_collections", s3Url);
                    }
                }
            }

            if (fileType != null && !fileType.trim().isEmpty()) {
                Map<String, Object> filteredOtherDocuments = new HashMap<>();
                if (fileType.equals("providus_bank_statement")) {
                    filteredOtherDocuments.put("providus", Collections.singletonMap("bank_statement",
                            ((Map<String, String>) otherDocuments.get("providus")).get("bank_statement")));
                } else if (fileType.equals("providus_bank_vps")) {
                    filteredOtherDocuments.put("providus", Collections.singletonMap("vps",
                            ((Map<String, String>) otherDocuments.get("providus")).get("vps")));
                } else if (fileType.equals("premium_trust_bank_statement")) {
                    filteredOtherDocuments.put("premium_trust", Collections.singletonMap("bank_statement",
                            ((Map<String, String>) otherDocuments.get("premium_trust")).get("bank_statement")));
                } else if (fileType.equals("premium_trust_bank_vps")) {
                    filteredOtherDocuments.put("premium_trust", Collections.singletonMap("vps",
                            ((Map<String, String>) otherDocuments.get("premium_trust")).get("vps")));
                } else if (fileType.equals("funds_transfer") || fileType.equals("temporary_metabase") ||
                        fileType.equals("metabase_collections")) {
                    filteredOtherDocuments.put(fileType, otherDocuments.get(fileType));
                } else {
                    timeWindows.entrySet().forEach(entry -> entry.getValue().keySet().removeIf(k -> !k.equals(fileType)));
                }
                otherDocuments = filteredOtherDocuments;
            }

            if (timeWindow != null && !timeWindow.trim().isEmpty()) {
                Map<String, Map<String, String>> filteredTimeWindows = new HashMap<>();
                filteredTimeWindows.put(timeWindow, timeWindows.get(timeWindow));
                timeWindows = filteredTimeWindows;
            }

            response.put("uploadDate", uploadDate);
            response.put("timeWindows", timeWindows);
            response.put("otherDocuments", otherDocuments);

            return response;
        } catch (Exception e) {
            log.error("Error retrieving S3 URLs: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve S3 URLs: " + e.getMessage());
        }
    }
    private Map<String, Object> buildEmptyOtherDocuments() {
        Map<String, Object> otherDocuments = new HashMap<>();
        Map<String, String> providus = new HashMap<>();
        providus.put("bank_statement", null);
        providus.put("vps", null);
        Map<String, String> premiumTrust = new HashMap<>();
        premiumTrust.put("bank_statement", null);
        premiumTrust.put("vps", null);
        otherDocuments.put("providus", providus);
        otherDocuments.put("premium_trust", premiumTrust);
        otherDocuments.put("funds_transfer", null);
        otherDocuments.put("temporary_metabase", null);
        otherDocuments.put("metabase_collections", null);
        return otherDocuments;
    }
}

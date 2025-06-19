package com.fyp.reconciliation_automation.service;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileContentValidator {

    public static void validateFileContent(InputStream inputStream, FileType fileType) throws IOException {
        switch (fileType) {
            case providus_bank_statement:
                validateProvidusBankStatement(inputStream);
                break;
            case providus_bank_vps:
                validateProvidusBankVps(inputStream);
                break;
            case premium_trust_bank_vps:
                validatePremiumTrustVps(inputStream);
                break;
            case premium_trust_bank_statement:
                validatePremiumTrustBankStatement(inputStream);
                break;
            case funds_transfer:
                validateFundsTransfer(inputStream);
                break;
            case temporary_metabase:
                validateTempMetabase(inputStream);
                break;
            case metabase_collections:
                validateMetaCollections(inputStream);
                break;
        }
    }

    private static void validateProvidusBankStatement(InputStream inputStream) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = null;

            List<String> expectedHeaders = Arrays.asList(
                    "TRA DATE", "VAL DATE", "ACCT NO", "ACCT NAME", "DEBIT"
            );

            for (int i = 0; i <= 3; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell firstCell = row.getCell(0);
                if (firstCell == null || !getCellValueAsString(firstCell).trim().equalsIgnoreCase("TRA DATE")) {
                    continue;
                }

                List<String> actualHeaders = new ArrayList<>();
                for (int j = 0; j < 5; j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) continue;
                    actualHeaders.add(getCellValueAsString(cell).trim().toUpperCase());
                }

                if (actualHeaders.containsAll(expectedHeaders)) {
                    headerRow = row;
                    break;
                }
            }
            if (headerRow == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "File does not appear to be a valid Providus Bank Statement. Required headers are missing.");
            }
        }
    }

    private static void validateProvidusBankVps(InputStream inputStream) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = null;

            List<String> expectedHeaders = Arrays.asList(
                    "id", "session_id", "settlement_ref", "merchant_id", "transaction_amount_minor",
                    "settled_amount_minor", "charge_amount_minor", "vat_amount_minor", "currency",
                    "notification_acknowledgement", "num_retries", "retry_batch_id", "failed_count",
                    "source_acct_name", "source_acct_no", "source_bank_code", "virtual_acct_no",
                    "account_ref_code", "created_at", "updated_at", "narration", "channel_id",
                    "post_flg", "stamp_duty_flg", "cba_tran_time", "reason", "reversal_session_id",
                    "settlement_notification_retry_batch_id"
            );

            boolean headersFound = false;

            for (int i = 0; i <= 3; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell firstCell = row.getCell(0);
                if (firstCell == null || !getCellValueAsString(firstCell).trim().equalsIgnoreCase("id")) {
                    continue;
                }

                List<String> actualHeaders = new ArrayList<>();
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) continue;
                    actualHeaders.add(getCellValueAsString(cell).trim().toLowerCase()); // Use lower case for consistency
                }

                if (actualHeaders.containsAll(expectedHeaders)) {
                    headersFound = true;
                    break;
                }
            }
            if (!headersFound) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "File does not appear to be a valid Providus Bank VPS document. Required headers are missing.");
            }
        }
    }


    private static void validatePremiumTrustVps(InputStream inputStream) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            boolean accountNumberFound = false;
            boolean accountTypeFound = false;
            boolean customerNameFound = false;

            for (int i = 0; i < 20; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                for (Cell cell : row) {
                    String cellValue = cell.getStringCellValue().trim();

                    if (cellValue.contains("0540024492")) {
                        accountNumberFound = true;
                    }
                    if (cellValue.contains("COLLECTION WITH DUTIES")) {
                        accountTypeFound = true;
                    }
                    if (cellValue.contains("PAYAZA Limited VA Collections")) {
                        customerNameFound = true;
                    }
                }

                if (accountNumberFound && accountTypeFound && customerNameFound) {
                    break;
                }
            }

            if (!accountNumberFound || !accountTypeFound || !customerNameFound) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "File does not appear to be a Premium Trust VPS file. Missing required identifiers.");
            }
        }
    }

    private static void validatePremiumTrustBankStatement(InputStream inputStream) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            boolean accountNumberFound = false;
            boolean accountTypeFound = false;
            boolean customerNameFound = false;

            for (int i = 0; i < 20; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                for (Cell cell : row) {
                    String cellValue = cell.getStringCellValue().trim();

                    if (cellValue.contains("0040085630")) {
                        accountNumberFound = true;
                    }
                    if (cellValue.contains("PREMIUM TRUST CORPORATE PLUS ACCOUNT")) {
                        accountTypeFound = true;
                    }
                    if (cellValue.contains("PAYAZA AFRICA LIMITED")) {
                        customerNameFound = true;
                    }
                }

                if (accountNumberFound && accountTypeFound && customerNameFound) {
                    break;
                }
            }

            if (!accountNumberFound || !accountTypeFound || !customerNameFound) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "File does not appear to be a Premium Trust Bank Statement file. Missing required identifiers.");
            }
        }
    }

    private static void validateFundsTransfer(InputStream inputStream) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = null;

            List<String> expectedHeaders = Arrays.asList(
                    "Fund Transfer ID",
                    "Created On",
                    "Modified On",
                    "Bank Code",
                    "Currency",
                    "Dest Account",
                    "Dest Account Name",
                    "Merchant ID",
                    "Name Enquiry Reference",
                    "Narration",
                    "Processor",
                    "Request UUID",
                    "Response Code",
                    "Response Message",
                    "Session ID",
                    "Source Account",
                    "Transaction Amount",
                    "Transaction Reference",
                    "Transaction Status",
                    "Sender Sender ID",
                    "Requery Count",
                    "Retry Count",
                    "Requery Response Code",
                    "Requery Response Message",
                    "Source Account Name",
                    "Destination Bank Name",
                    "Processor Reference",
                    "Retry Processor",
                    "Retry Processor Reference"
            );

            for (int i = 0; i <= 4; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell firstCell = row.getCell(0);
                if (firstCell == null || !getCellValueAsString(firstCell).trim().equalsIgnoreCase("Fund Transfer ID")) {
                    continue;
                }

                List<String> actualHeaders = new ArrayList<>();
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) continue;
                    actualHeaders.add(getCellValueAsString(cell).trim());
                }

                if (actualHeaders.containsAll(expectedHeaders)) {
                    headerRow = row;
                    break;
                }
            }

            if (headerRow == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "File does not appear to be a valid Funds Transfer document. Required headers are missing.");
            }
        }
    }

    private static void validateTempMetabase(InputStream inputStream) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = null;

            List<String> expectedHeaders = Arrays.asList(
                    "Account Name",
                    "Payaza Account Balance Runner → Transaction Reference",
                    "Payaza Account Transaction → Transaction Amount",
                    "Payaza Account Transaction → Transaction Fee",
                    "Payaza Account Balance Runner → Debit Amount",
                    "Payaza Account Balance Runner → Credit Amount",
                    "Payaza Account Balance Runner → Balance Before",
                    "Payaza Account Balance Runner → Balance After",
                    "Bank Charges (Y)",
                    "Payaza Account Transaction → Transaction Type",
                    "Payaza Account Transaction → Transaction Narration",
                    "Payaza Account Transaction → Transaction Status",
                    "Payaza Account Transaction → Response Code",
                    "Payaza Account Transaction → Response Message",
                    "Payaza Account Transaction → Session ID",
                    "Payaza Account Transaction → Bank Name",
                    "Payaza Account Transaction → To Account",
                    "Payaza Account Transaction → Payout Processor",
                    "Payaza Account Balance Runner → Currency",
                    "Banks → Name",
                    "Banks → Currency Code",
                    "Payaza Account Transaction → Transaction Date"
            );

            for (int i = 0; i <=4; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell firstCell = row.getCell(0);
                if (firstCell == null || !getCellValueAsString(firstCell).trim().equalsIgnoreCase("Account Name")) {
                    continue;
                }

                List<String> actualHeaders = new ArrayList<>();
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) continue;
                    actualHeaders.add(getCellValueAsString(cell).trim());
                }

                if (actualHeaders.containsAll(expectedHeaders)) {
                    headerRow = row;
                    break;
                }
            }

            if (headerRow == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "File does not appear to be a valid Metabase document. Required headers are missing.");
            }
        }
    }


    private static void validateMetaCollections(InputStream inputStream) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = null;

            List<String> expectedHeaders = Arrays.asList(
                    "Transaction Value Amount",
                    "Fee Amount",
                    "Partner Transaction Amount",
                    "Partner Fee Amount",
                    "Currency Fk",
                    "Business - Business Fk → Name",
                    "Business Branches - Business Branch Fk → Name",
                    "Partner",
                    "Ended At"
            );

            for (int i = 0; i <= 4; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell firstCell = row.getCell(0);
                if (firstCell == null || !getCellValueAsString(firstCell).trim().equalsIgnoreCase("Transaction Value Amount")) {
                    continue;
                }

                List<String> actualHeaders = new ArrayList<>();
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) continue;
                    actualHeaders.add(getCellValueAsString(cell).trim());
                }

                if (actualHeaders.containsAll(expectedHeaders)) {
                    headerRow = row;
                    break;
                }
            }
            if (headerRow == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "File does not appear to be a valid Meta collections document. Required headers are missing.");
            }
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return "";
        }
    }
}
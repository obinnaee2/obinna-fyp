package com.fyp.reconciliation_automation.service;

import com.fyp.reconciliation_automation.entity.CreditFailed;
import com.fyp.reconciliation_automation.entity.CreditSuccessful;
import com.fyp.reconciliation_automation.entity.DebitAdviceFailed;
import com.fyp.reconciliation_automation.entity.DebitAdviceSuccessful;
import com.fyp.reconciliation_automation.entity.DebitFailed;
import com.fyp.reconciliation_automation.entity.DebitSuccessful;
import com.fyp.reconciliation_automation.entity.FundsTransfer;
import com.fyp.reconciliation_automation.entity.MetaCollections;
import com.fyp.reconciliation_automation.entity.PremiumTrustBankStatement;
import com.fyp.reconciliation_automation.entity.PremiumTrustVPS;
import com.fyp.reconciliation_automation.entity.ProvidusBankStatement;
import com.fyp.reconciliation_automation.entity.ProvidusBankVPS;
import com.fyp.reconciliation_automation.entity.TempMetabase;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Service
public class ExcelUploadService {
    public static boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    //Providus
    public static List<ProvidusBankVPS> getProvidusBankVPSDataFromExcel(InputStream inputStream) {
        List<ProvidusBankVPS> providusBankVPSList = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            int startRowIndex = 1;
            for (int rowIndex = startRowIndex; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                ProvidusBankVPS vps = new ProvidusBankVPS();

                vps.setSessionId(getCellValueForProvidusBankVPS(row, 1, dataFormatter));
                vps.setSettlementRef(getCellValueForProvidusBankVPS(row, 2, dataFormatter));
                vps.setMerchantId(getCellValueForProvidusBankVPS(row, 3, dataFormatter));
                vps.setTransactionAmountMinor(getCellValueForProvidusBankVPS(row, 4, dataFormatter));
                vps.setSettledAmountMinor(getCellValueForProvidusBankVPS(row, 5, dataFormatter));
                vps.setChargeAmountMinor(getCellValueForProvidusBankVPS(row, 6, dataFormatter));
                vps.setVatAmountMinor(getCellValueForProvidusBankVPS(row, 7, dataFormatter));
                vps.setCurrency(getCellValueForProvidusBankVPS(row, 8, dataFormatter));
                vps.setNotificationAcknowledgement(getCellValueForProvidusBankVPS(row, 9, dataFormatter));
                vps.setNumRetries(getCellValueForProvidusBankVPS(row, 10, dataFormatter));
                vps.setRetryBatchId(getCellValueForProvidusBankVPS(row, 11, dataFormatter));
                vps.setFailedCount(getCellValueForProvidusBankVPS(row, 12, dataFormatter));
                vps.setSourceAcctName(getCellValueForProvidusBankVPS(row, 13, dataFormatter));
                vps.setSourceAcctNo(getCellValueForProvidusBankVPS(row, 14, dataFormatter));
                vps.setSourceBankCode(getCellValueForProvidusBankVPS(row, 15, dataFormatter));
                vps.setVirtualAcctNo(getCellValueForProvidusBankVPS(row, 16, dataFormatter));
                vps.setAccountRefCode(getCellValueForProvidusBankVPS(row, 17, dataFormatter));
                vps.setCreatedAt(getCellValueForProvidusBankVPS(row, 18, dataFormatter));
                vps.setUpdatedAt(getCellValueForProvidusBankVPS(row, 19, dataFormatter));
                vps.setNarration(getCellValueForProvidusBankVPS(row, 20, dataFormatter));
                vps.setChannelId(getCellValueForProvidusBankVPS(row, 21, dataFormatter));
                vps.setPostFlg(getCellValueForProvidusBankVPS(row, 22, dataFormatter));
                vps.setStampDutyFlg(getCellValueForProvidusBankVPS(row, 23, dataFormatter));
                vps.setCbaTranTime(getCellValueForProvidusBankVPS(row, 24, dataFormatter));
                vps.setReason(getCellValueForProvidusBankVPS(row, 25, dataFormatter));
                vps.setReversalSessionId(getCellValueForProvidusBankVPS(row, 26, dataFormatter));
                vps.setSettlementNotificationRetryBatchId(getCellValueForProvidusBankVPS(row, 27, dataFormatter));

                providusBankVPSList.add(vps);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return providusBankVPSList;
    }
    private static String getCellValueForProvidusBankVPS(Row row, int cellIndex, DataFormatter dataFormatter) {
        Cell cell = row.getCell(cellIndex);
        return (cell!= null && cell.getCellType()!= CellType.BLANK)? dataFormatter.formatCellValue(cell).trim() : "";
    }
    public static List<ProvidusBankStatement> getProvidusBankStatementDataFromExcel(InputStream inputStream, String uploadDate) {
        List<ProvidusBankStatement> providusBankStatementList = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();


            for (int rowIndex = 3; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                ProvidusBankStatement statement = new ProvidusBankStatement();
                statement.setTransactionDate(getCellValueForProvidus(row, 0, dataFormatter));
                statement.setValueDate(getCellValueForProvidus(row, 1, dataFormatter));
                statement.setAccountNumber(getCellValueForProvidus(row, 2, dataFormatter));
                statement.setAccountName(getCellValueForProvidus(row, 3, dataFormatter));
                statement.setDebitAmount(getCellValueForProvidus(row, 4, dataFormatter));
                statement.setCreditAmount(getCellValueForProvidus(row, 5, dataFormatter));
                statement.setCurrentBalance(getCellValueForProvidus(row, 6, dataFormatter));
                statement.setTransactionType(getCellValueForProvidus(row, 7, dataFormatter));
                statement.setRemarks(getCellValueForProvidus(row, 8, dataFormatter));
                statement.setReference(getCellValueForProvidus(row, 9, dataFormatter));
                statement.setSessionId(getCellValueForProvidus(row, 10, dataFormatter));
                statement.setPaymentReference(getCellValueForProvidus(row, 11, dataFormatter));
                statement.setBeneficiaryName(getCellValueForProvidus(row, 12, dataFormatter));
                statement.setAccountTransactionDate(getCellValueForProvidus(row, 13, dataFormatter));
                statement.setUpdTime(getCellValueForProvidus(row, 14, dataFormatter));
                statement.setTransactionSequence1(getCellValueForProvidus(row, 15, dataFormatter));
                statement.setTransactionSequence2(getCellValueForProvidus(row, 16, dataFormatter));
                statement.setUploadDate(uploadDate);

                providusBankStatementList.add(statement);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return providusBankStatementList;
    }
    private static String getCellValueForProvidus(Row row, int cellIndex, DataFormatter dataFormatter) {
        Cell cell = row.getCell(cellIndex);
        return (cell != null && cell.getCellType() != CellType.BLANK) ? dataFormatter.formatCellValue(cell).trim() : "";
    }

    //Premium trust
    public static List<PremiumTrustVPS> getPremiumTrustVPSDataFromExcel(InputStream inputStream) {
        List<PremiumTrustVPS> premiumTrustVPSList = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);

            int startRowIndex = 0;
            while (startRowIndex < sheet.getLastRowNum()) {
                Row row = sheet.getRow(startRowIndex);
                if (row != null && row.getCell(0) != null && row.getCell(0).getStringCellValue().trim().equals("Trans Date")) {
                    break;
                }
                startRowIndex++;
            }

            int endRowIndex = sheet.getLastRowNum();
            DataFormatter dataFormatter = new DataFormatter();

            for (int rowIndex = startRowIndex + 1; rowIndex <= endRowIndex; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;


                if (isRowEmpty(row, dataFormatter)) {
                    int emptyRowCount = 1;
                    for (int nextRowIndex = rowIndex + 1; nextRowIndex <= endRowIndex; nextRowIndex++) {
                        Row nextRow = sheet.getRow(nextRowIndex);
                        if (nextRow == null || isRowEmpty(nextRow, dataFormatter)) {
                            emptyRowCount++;
                        } else {
                            break;
                        }
                    }
                    if (emptyRowCount >= 3) {
                        break;
                    }
                }

                PremiumTrustVPS vps = new PremiumTrustVPS();

                vps.setTransactionDate(dataFormatter.formatCellValue(row.getCell(0)).trim());
                vps.setValueDate(dataFormatter.formatCellValue(row.getCell(1)).trim());

                StringBuilder transactionDetails = new StringBuilder();
                for (int i = 2; i <= 8; i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        transactionDetails.append(dataFormatter.formatCellValue(cell).trim()).append(" ");
                    }
                }
                vps.setTransactionDetails(transactionDetails.toString().trim());

                vps.setInstitutionNumber(dataFormatter.formatCellValue(row.getCell(9)).trim());

                StringBuilder debitAmount = new StringBuilder();
                for (int i = 10; i <= 12; i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        debitAmount.append(dataFormatter.formatCellValue(cell).trim()).append(" ");
                    }
                }
                vps.setDebitAmount(debitAmount.toString().trim());

                vps.setCreditAmount(dataFormatter.formatCellValue(row.getCell(13)).trim());

                StringBuilder balance = new StringBuilder();
                for (int i = 14; i <= 15; i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        balance.append(dataFormatter.formatCellValue(cell).trim()).append(" ");
                    }
                }
                vps.setBalance(balance.toString().trim());

                premiumTrustVPSList.add(vps);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return premiumTrustVPSList;
    }
    public static List<PremiumTrustBankStatement> getPremiumTrustBankStatementDataFromExcel(InputStream inputStream, String uploadDate) {
        List<PremiumTrustBankStatement> premiumTrustBankStatementList = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);

            int startRowIndex = 0;
            while (startRowIndex < sheet.getLastRowNum()) {
                Row row = sheet.getRow(startRowIndex);
                if (row != null && row.getCell(0) != null && row.getCell(0).getStringCellValue().trim().equals("Trans Date")) {
                    break;
                }
                startRowIndex++;
            }

            int endRowIndex = sheet.getLastRowNum();
            DataFormatter dataFormatter = new DataFormatter();

            for (int rowIndex = startRowIndex + 1; rowIndex <= endRowIndex; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;


                if (isRowEmpty(row, dataFormatter)) {
                    int emptyRowCount = 1;
                    for (int nextRowIndex = rowIndex + 1; nextRowIndex <= endRowIndex; nextRowIndex++) {
                        Row nextRow = sheet.getRow(nextRowIndex);
                        if (nextRow == null || isRowEmpty(nextRow, dataFormatter)) {
                            emptyRowCount++;
                        } else {
                            break;
                        }
                    }
                    if (emptyRowCount >= 3) {
                        break;
                    }
                }

                PremiumTrustBankStatement statement = new PremiumTrustBankStatement();

                statement.setTransactionDate(dataFormatter.formatCellValue(row.getCell(0)).trim());
                statement.setValueDate(dataFormatter.formatCellValue(row.getCell(1)).trim());

                StringBuilder transactionDetails = new StringBuilder();
                for (int i = 2; i <= 8; i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        transactionDetails.append(dataFormatter.formatCellValue(cell).trim()).append(" ");
                    }
                }
                statement.setTransactionDetails(transactionDetails.toString().trim());

                statement.setInstitutionNumber(dataFormatter.formatCellValue(row.getCell(9)).trim());

                StringBuilder debitAmount = new StringBuilder();
                for (int i = 10; i <= 12; i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        debitAmount.append(dataFormatter.formatCellValue(cell).trim()).append(" ");
                    }
                }
                statement.setDebitAmount(debitAmount.toString().trim());

                statement.setCreditAmount(dataFormatter.formatCellValue(row.getCell(13)).trim());

                StringBuilder balance = new StringBuilder();
                for (int i = 14; i <= 15; i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        balance.append(dataFormatter.formatCellValue(cell).trim()).append(" ");
                    }
                }
                statement.setBalance(balance.toString().trim());
                statement.setUploadDate(uploadDate);

                premiumTrustBankStatementList.add(statement);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return premiumTrustBankStatementList;
    }
    private static boolean isRowEmpty(Row row, DataFormatter dataFormatter) {
        for (int cellIndex = 0; cellIndex < row.getPhysicalNumberOfCells(); cellIndex++) {
            String cellValue = dataFormatter.formatCellValue(row.getCell(cellIndex)).trim();
            if (!cellValue.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    //NIBBS
    public static List<CreditFailed> getCreditFailedDataFromExcel(InputStream inputStream) {
        List<CreditFailed> creditFailedList = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            validateCreditFailedHeaders(sheet.getRow(0));

            DataFormatter dataFormatter = new DataFormatter();
            int rowIndex = 0;
            for (Row row : sheet) {
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                CreditFailed credit = new CreditFailed();
                int cellIndex = 0;
                for (Cell cell : row) {
                    String cellValue = dataFormatter.formatCellValue(cell).trim();
                    if (cell == null || cell.getCellType() == CellType.BLANK || "null".equalsIgnoreCase(cellValue)) {
                        cellIndex++;
                        continue;
                    }
                    cell.setCellType(CellType.STRING);
                    switch (cellIndex) {
                        case 0 -> credit.setTransactionId(cellValue);
                        case 1 -> credit.setSessionId(cellValue);
                        case 2 -> credit.setStatus(cellValue);
                        case 3 -> credit.setMessage(cellValue);
                        case 4 -> credit.setNipResponseCode(cellValue);
                        case 5 -> credit.setNameEnquiryRef(cellValue);
                        case 6 -> credit.setDestinationInstitutionCode(cellValue);
                        case 7 -> credit.setChannelCode(cellValue);
                        case 8 -> credit.setBeneficiaryAccountName(cellValue);
                        case 9 -> credit.setBeneficiaryAccountNumber(cellValue);
                        case 10 -> credit.setBeneficiaryBvn(cellValue);
                        case 11 -> credit.setBeneficiaryKycLevel(cellValue);
                        case 12 -> credit.setOriginatorAccountName(cellValue);
                        case 13 -> credit.setOriginatorAccountNumber(cellValue);
                        case 14 -> credit.setOriginatorBvn(cellValue);
                        case 15 -> credit.setOriginatorKycLevel(cellValue);
                        case 16 -> credit.setNarration(cellValue);
                        case 17 -> credit.setPaymentReference(cellValue);
                        case 18 -> credit.setAmount(cellValue);
                        case 19 -> credit.setTransactionLocation(cellValue);
                        case 20 -> {
                            if ("N/A".equalsIgnoreCase(cellValue)) {
                                credit.setInitiatorAccountName(null);
                            } else {
                                credit.setInitiatorAccountName(cellValue);
                            }
                        }
                        case 21 -> {
                            if ("N/A".equalsIgnoreCase(cellValue)) {
                                credit.setInitiatorAccountNumber(null);
                            } else {
                                credit.setInitiatorAccountNumber(cellValue);
                            }
                        }
                        default -> {
                        }
                    }
                    cellIndex++;
                }
                creditFailedList.add(credit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return creditFailedList;
    }
    public static List<CreditSuccessful> getCreditSuccessfulDataFromExcel(InputStream inputStream) {
        List<CreditSuccessful> creditSuccessfulList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            validateCreditSuccessfulHeaders(sheet.getRow(0));

            DataFormatter dataFormatter = new DataFormatter();

            int rowIndex = 0;
            for (Row row : sheet) {

                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                CreditSuccessful credit = new CreditSuccessful();
                int cellIndex = 0;
                for (Cell cell : row) {
                    String cellValue = dataFormatter.formatCellValue(cell).trim();
                    if (cell == null || cell.getCellType() == CellType.BLANK || "null".equalsIgnoreCase(cellValue)) {
                        cellIndex++;
                        continue;
                    }
                    cell.setCellType(CellType.STRING);
                    switch (cellIndex) {
                        case 0 -> credit.setSessionId(cellValue);
                        case 1 -> credit.setTransactionId(cellValue);
                        case 2 -> credit.setStatus(cellValue);
                        case 3 -> credit.setMessage(cellValue);
                        case 4 -> {
                            if ("0".equals(cellValue)) {
                                credit.setNipResponseCode("00");
                            } else {
                                credit.setNipResponseCode(cellValue);
                            }
                        }
                        case 5 -> credit.setNameEnquiryRef(cellValue);
                        case 6 -> credit.setDestinationInstitutionCode(cellValue);
                        case 7 -> credit.setChannelCode(cellValue);
                        case 8 -> credit.setBeneficiaryAccountName(cellValue);
                        case 9 -> credit.setBeneficiaryAccountNumber(cellValue);
                        case 10 -> credit.setBeneficiaryBvn(cellValue);
                        case 11 -> credit.setBeneficiaryKycLevel(cellValue);
                        case 12 -> credit.setOriginatorAccountName(cellValue);
                        case 13 -> credit.setOriginatorAccountNumber(cellValue);
                        case 14 -> credit.setOriginatorBvn(cellValue);
                        case 15 -> credit.setOriginatorKycLevel(cellValue);
                        case 16 -> credit.setNarration(cellValue);
                        case 17 -> credit.setPaymentReference(cellValue);
                        case 18 -> credit.setAmount(cellValue);
                        case 19 -> credit.setTransactionLocation(cellValue);
                        case 20 -> {
                            if ("N/A".equalsIgnoreCase(cellValue)) {
                                credit.setInitiatorAccountName(null);
                            } else {
                                credit.setInitiatorAccountName(cellValue);
                            }
                        }
                        case 21 -> {
                            if ("N/A".equalsIgnoreCase(cellValue)) {
                                credit.setInitiatorAccountNumber(null);
                            } else {
                                credit.setInitiatorAccountNumber(cellValue);
                            }
                        }
                        default -> {
                        }
                    }
                    cellIndex++;
                }
                creditSuccessfulList.add(credit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return creditSuccessfulList;
    }
    public static List<DebitAdviceFailed> getDebitAdviceFailedDataFromExcel(InputStream inputStream) {
        List<DebitAdviceFailed> debitAdviceFailedList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            validateDebitAdviceFailedHeaders(sheet.getRow(0));
            DataFormatter dataFormatter = new DataFormatter();
            int rowIndex = 0;
            for (Row row : sheet) {
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                DebitAdviceFailed debit = new DebitAdviceFailed();
                int cellIndex = 0;
                for (Cell cell : row) {

                    String cellValue = dataFormatter.formatCellValue(cell).trim();
                    if (cell == null || cell.getCellType() == CellType.BLANK || "null".equalsIgnoreCase(cellValue)) {
                        cellIndex++;
                        continue;
                    }
                    cell.setCellType(CellType.STRING);
                    switch (cellIndex) {
                        case 0 -> debit.setSessionId(cellValue);
                        case 1 -> debit.setTransactionId(cellValue);
                        case 2 -> debit.setStatus(cellValue);
                        case 3 -> debit.setMessage(cellValue);
                        case 4 -> debit.setNipResponseCode(cellValue);
                        case 5 -> debit.setNameEnquiryRef(cellValue);
                        case 6 -> debit.setDestinationInstitutionCode(cellValue);
                        case 7 -> debit.setChannelCode(cellValue);
                        case 8 -> debit.setBeneficiaryAccountName(cellValue);
                        case 9 -> debit.setBeneficiaryAccountNumber(cellValue);
                        case 10 -> debit.setBeneficiaryBvn(cellValue);
                        case 11 -> debit.setBeneficiaryKycLevel(cellValue);
                        case 12 -> debit.setOriginatorAccountName(cellValue);
                        case 13 ->  debit.setOriginatorAccountNumber(cellValue);
                        case 14 -> debit.setOriginatorBvn(cellValue);
                        case 15 -> debit.setOriginatorKycLevel(cellValue);
                        case 16 -> debit.setNarration(cellValue);
                        case 17 -> debit.setPaymentReference(cellValue);
                        case 18 -> debit.setAmount(cellValue);
                        case 19 -> debit.setTransactionLocation(cellValue);

                    }
                    cellIndex++;
                }
                debitAdviceFailedList.add(debit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return debitAdviceFailedList;
    }
    public static List<DebitAdviceSuccessful> getDebitAdviceSuccessfulDataFromExcel(InputStream inputStream) {
        List<DebitAdviceSuccessful> debitAdviceSuccessfulList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            validateDebitAdviceSuccessfulHeaders(sheet.getRow(0));
            DataFormatter dataFormatter = new DataFormatter();
            int rowIndex = 0;
            for (Row row : sheet) {
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                DebitAdviceSuccessful debit = new DebitAdviceSuccessful();
                int cellIndex = 0;
                for (Cell cell : row) {

                    String cellValue = dataFormatter.formatCellValue(cell).trim();
                    if (cell == null || cell.getCellType() == CellType.BLANK || "null".equalsIgnoreCase(cellValue)) {
                        cellIndex++;
                        continue;
                    }
                    cell.setCellType(CellType.STRING);
                    switch (cellIndex) {
                        case 0 -> debit.setSessionId(cellValue);
                        case 1 -> debit.setTransactionId(cellValue);
                        case 2 -> debit.setStatus(cellValue);
                        case 3 -> debit.setMessage(cellValue);
                        case 4 -> {
                            if ("0".equals(cellValue)) {
                                debit.setNipResponseCode("00");
                            } else {
                                debit.setNipResponseCode(cellValue);
                            }
                        }
                        case 5 -> debit.setNameEnquiryRef(cellValue);
                        case 6 -> debit.setDestinationInstitutionCode(cellValue);
                        case 7 -> debit.setChannelCode(cellValue);
                        case 8 -> debit.setBeneficiaryAccountName(cellValue);
                        case 9 -> debit.setBeneficiaryAccountNumber(cellValue);
                        case 10 -> debit.setBeneficiaryBvn(cellValue);
                        case 11 -> debit.setBeneficiaryKycLevel(cellValue);
                        case 12 -> debit.setOriginatorAccountName(cellValue);
                        case 13 -> debit.setOriginatorAccountNumber(cellValue);
                        case 14 -> debit.setOriginatorBvn(cellValue);
                        case 15 -> debit.setOriginatorKycLevel(cellValue);
                        case 16 -> debit.setNarration(cellValue);
                        case 17 -> debit.setPaymentReference(cellValue);
                        case 18 -> debit.setAmount(cellValue);
                        case 19 -> debit.setTransactionLocation(cellValue);
                        default -> {
                        }
                    }
                    cellIndex++;
                }
                debitAdviceSuccessfulList.add(debit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return debitAdviceSuccessfulList;
    }
    public static List<DebitFailed> getDebitFailedDataFromExcel(InputStream inputStream) {
        List<DebitFailed> debitFailedList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            validateDebitFailedHeaders(sheet.getRow(0));

            DataFormatter dataFormatter = new DataFormatter();

            int rowIndex = 0;
            for (Row row : sheet) {
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                DebitFailed debit = new DebitFailed();
                int cellIndex = 0;
                for (Cell cell : row) {

                    String cellValue = dataFormatter.formatCellValue(cell).trim();
                    if (cell == null || cell.getCellType() == CellType.BLANK || "null".equalsIgnoreCase(cellValue)) {
                        cellIndex++;
                        continue;
                    }
                    cell.setCellType(CellType.STRING);
                    switch (cellIndex) {
                        case 0 -> debit.setSessionId(cellValue);
                        case 1 -> debit.setTransactionId(cellValue);
                        case 2 -> debit.setStatus(cellValue);
                        case 3 -> debit.setMessage(cellValue);
                        case 4 -> debit.setNipResponseCode(cellValue);
                        case 5 -> debit.setNameEnquiryRef(cellValue);
                        case 6 -> debit.setDestinationInstitutionCode(cellValue);
                        case 7 -> debit.setChannelCode(cellValue);
                        case 8 -> debit.setBeneficiaryAccountName(cellValue);
                        case 9 -> debit.setBeneficiaryAccountNumber(cellValue);
                        case 10 -> debit.setBeneficiaryBvn(cellValue);
                        case 11 -> debit.setBeneficiaryKycLevel(cellValue);
                        case 12 -> debit.setOriginatorAccountName(cellValue);
                        case 13 -> debit.setOriginatorAccountNumber(cellValue);
                        case 14 -> debit.setOriginatorBvn(cellValue);
                        case 15 -> debit.setOriginatorKycLevel(cellValue);
                        case 16 -> debit.setNarration(cellValue);
                        case 17 -> debit.setPaymentReference(cellValue);
                        case 18 -> debit.setAmount(cellValue);
                        case 19 -> debit.setTransactionLocation(cellValue);
                        default -> {
                        }
                    }
                    cellIndex++;
                }
                debitFailedList.add(debit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return debitFailedList;
    }
    public static List<DebitSuccessful> getDebitSuccessfulDataFromExcel(InputStream inputStream) {
        List<DebitSuccessful> debitSuccessfulList = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            validateDebitSuccessfulHeaders(sheet.getRow(0));

            DataFormatter dataFormatter = new DataFormatter();

            int rowIndex = 0;
            for (Row row : sheet) {
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                DebitSuccessful debit = new DebitSuccessful();
                int cellIndex = 0;
                for (Cell cell : row) {

                    String cellValue = dataFormatter.formatCellValue(cell).trim();
                    if (cell == null || cell.getCellType() == CellType.BLANK || "null".equalsIgnoreCase(cellValue)) {
                        cellIndex++;
                        continue;
                    }
                    cell.setCellType(CellType.STRING);
                    try {
                        switch (cellIndex) {
                            case 0 -> debit.setSessionId(cellValue);
                            case 1 -> debit.setTransactionId(cellValue);
                            case 2 -> debit.setStatus(cellValue);
                            case 3 -> debit.setMessage(cellValue);
                            case 4 -> {
                                if ("0".equals(cellValue)) {
                                    debit.setNipResponseCode("00");
                                } else {
                                    debit.setNipResponseCode(cellValue);
                                }
                            }
                            case 5 -> debit.setNameEnquiryRef(cellValue);
                            case 6 -> debit.setDestinationInstitutionCode(cellValue);
                            case 7 -> debit.setChannelCode(cellValue);
                            case 8 -> debit.setBeneficiaryAccountName(cellValue);
                            case 9 -> debit.setBeneficiaryAccountNumber(cellValue);
                            case 10 -> debit.setBeneficiaryBvn(cellValue);
                            case 11 -> debit.setBeneficiaryKycLevel(cellValue);
                            case 12 -> debit.setOriginatorAccountName(cellValue);
                            case 13 -> debit.setOriginatorAccountNumber(cellValue);
                            case 14 -> debit.setOriginatorBvn(cellValue);
                            case 15 -> debit.setOriginatorKycLevel(cellValue);
                            case 16 -> debit.setNarration(cellValue);
                            case 17 -> debit.setPaymentReference(cellValue);
                            case 18 -> debit.setAmount(cellValue);
                            case 19 -> debit.setTransactionLocation(cellValue);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    cellIndex++;
                }
                debitSuccessfulList.add(debit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return debitSuccessfulList;
    }

    public static List<TempMetabase> getTempMetabaseDataFromExcel(InputStream inputStream) {
        List<TempMetabase> tempMetabaseList = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            System.out.println("Total Rows to Process: " + sheet.getLastRowNum());
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                TempMetabase tempMetabase = new TempMetabase();


                tempMetabase.setAccountName(getCellValueForMetabase(row, 0, dataFormatter));
                tempMetabase.setTransactionReference(getCellValueForMetabase(row, 1, dataFormatter));
                tempMetabase.setTransactionAmount(getCellValueForMetabase(row, 2, dataFormatter));
                tempMetabase.setTransactionFee(getCellValueForMetabase(row, 3, dataFormatter));
                tempMetabase.setDebitAmount(getCellValueForMetabase(row, 4, dataFormatter));
                tempMetabase.setCreditAmount(getCellValueForMetabase(row, 5, dataFormatter));
                tempMetabase.setBalanceBefore(getCellValueForMetabase(row, 6, dataFormatter));
                tempMetabase.setBalanceAfter(getCellValueForMetabase(row, 7, dataFormatter));
                tempMetabase.setBankChargesY(getCellValueForMetabase(row, 8, dataFormatter));
                tempMetabase.setTransactionType(getCellValueForMetabase(row, 9, dataFormatter));
                tempMetabase.setTransactionNarration(getCellValueForMetabase(row, 10, dataFormatter));
                tempMetabase.setTransactionStatus(getCellValueForMetabase(row, 11, dataFormatter));
                tempMetabase.setResponseCode(getCellValueForMetabase(row, 12, dataFormatter));
                tempMetabase.setResponseMessage(getCellValueForMetabase(row, 13, dataFormatter));
                tempMetabase.setSessionID(getCellValueForMetabase(row, 14, dataFormatter));
                tempMetabase.setBankName(getCellValueForMetabase(row, 15, dataFormatter));
                tempMetabase.setToAccount(getCellValueForMetabase(row, 16, dataFormatter));
                tempMetabase.setPayoutProcessor(getCellValueForMetabase(row, 17, dataFormatter));
                tempMetabase.setCurrency(getCellValueForMetabase(row, 18, dataFormatter));
                tempMetabase.setBanksName(getCellValueForMetabase(row, 19, dataFormatter));
                tempMetabase.setBanksCurrencyCode(getCellValueForMetabase(row, 20, dataFormatter));
                tempMetabase.setTransactionDate(getCellValueForMetabase(row, 21, dataFormatter));

                tempMetabaseList.add(tempMetabase);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempMetabaseList;
    }

    private static String getCellValueForMetabase(Row row, int cellIndex, DataFormatter dataFormatter) {
        Cell cell = row.getCell(cellIndex);
        return (cell!= null && cell.getCellType()!= CellType.BLANK)? dataFormatter.formatCellValue(cell).trim() : "";
    }


    public static List<FundsTransfer> getFundsTransferDataFromExcel(InputStream inputStream) {
        List<FundsTransfer> fundTransferList = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            System.out.println("Total Rows to Process: " + sheet.getLastRowNum());
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                FundsTransfer fundTransfer = new FundsTransfer();

                fundTransfer.setFundTransferId(getCellValue(row, 0, dataFormatter));
                fundTransfer.setCreatedOn(getCellValue(row, 1, dataFormatter));
                fundTransfer.setModifiedOn(getCellValue(row, 2, dataFormatter));
                fundTransfer.setBankCode(getCellValue(row, 3, dataFormatter));
                fundTransfer.setCurrency(getCellValue(row, 4, dataFormatter));
                fundTransfer.setDestAccount(getCellValue(row, 5, dataFormatter));
                fundTransfer.setDestAccountName(getCellValue(row, 6, dataFormatter));
                fundTransfer.setMerchantId(getCellValue(row, 7, dataFormatter));
                fundTransfer.setNameEnquiryReference(getCellValue(row, 8, dataFormatter));
                fundTransfer.setNarration(getCellValue(row, 9, dataFormatter));
                fundTransfer.setProcessor(getCellValue(row, 10, dataFormatter));
                fundTransfer.setRequestUuid(getCellValue(row, 11, dataFormatter));
                fundTransfer.setResponseCode(getCellValue(row, 12, dataFormatter));
                fundTransfer.setResponseMessage(getCellValue(row, 13, dataFormatter));
                fundTransfer.setSessionId(getCellValue(row, 14, dataFormatter));
                fundTransfer.setSourceAccount(getCellValue(row, 15, dataFormatter));
                fundTransfer.setTransactionAmount(getCellValue(row, 16, dataFormatter));
                fundTransfer.setTransactionReference(getCellValue(row, 17, dataFormatter));
                fundTransfer.setTransactionStatus(getCellValue(row, 18, dataFormatter));
                fundTransfer.setSenderSenderId(getCellValue(row, 19, dataFormatter));
                fundTransfer.setRequeryCount(Integer.parseInt(getCellValue(row, 20, dataFormatter)));
                fundTransfer.setRetryCount(Integer.parseInt(getCellValue(row, 21, dataFormatter)));
                fundTransfer.setRequeryResponseCode(getCellValue(row, 22, dataFormatter));
                fundTransfer.setRequeryResponseMessage(getCellValue(row, 23, dataFormatter));
                fundTransfer.setSourceAccountName(getCellValue(row, 24, dataFormatter));
                fundTransfer.setDestinationBankName(getCellValue(row, 25, dataFormatter));
                fundTransfer.setProcessorReference(getCellValue(row, 26, dataFormatter));
                fundTransfer.setRetryProcessor(getCellValue(row, 27, dataFormatter));
                fundTransfer.setRetryProcessorReference(getCellValue(row, 28, dataFormatter));

                fundTransferList.add(fundTransfer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fundTransferList;
    }

    private static String getCellValue(Row row, int cellIndex, DataFormatter dataFormatter) {
        Cell cell = row.getCell(cellIndex);
        return (cell != null && cell.getCellType() != CellType.BLANK) ? dataFormatter.formatCellValue(cell).trim() : "";
    }

    public static List<MetaCollections> getMetaCollectionsDataFromExcel(InputStream inputStream) {
        List<MetaCollections> metaCollectionsList = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            System.out.println("Total Rows to Process: " + sheet.getLastRowNum());
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                MetaCollections metaCollections = new MetaCollections();

                metaCollections.setTransactionValueAmount(getCellValueForMetaCollections(row, 0, dataFormatter));
                metaCollections.setFeeAmount(getCellValueForMetaCollections(row, 1, dataFormatter));
                metaCollections.setPartnerTransactionAmount(getCellValueForMetaCollections(row, 2, dataFormatter));
                metaCollections.setPartnerFeeAmount(getCellValueForMetaCollections(row, 3, dataFormatter));
                metaCollections.setCurrencyFk(getCellValueForMetaCollections(row, 4, dataFormatter));
                metaCollections.setBusinessFkName(getCellValueForMetaCollections(row, 5, dataFormatter));
                metaCollections.setBusinessBranchFkName(getCellValueForMetaCollections(row, 6, dataFormatter));
                metaCollections.setPartner(getCellValueForMetaCollections(row, 7, dataFormatter));
                metaCollections.setPartnerTransactionReference(getCellValueForMetaCollections(row, 8, dataFormatter));
                metaCollections.setEndedAt(getCellValueForMetaCollections(row, 9, dataFormatter));

                metaCollectionsList.add(metaCollections);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return metaCollectionsList;
    }

    private static String getCellValueForMetaCollections(Row row, int cellIndex, DataFormatter dataFormatter) {
        Cell cell = row.getCell(cellIndex);
        return (cell != null && cell.getCellType() != CellType.BLANK) ? dataFormatter.formatCellValue(cell).trim() : "";
    }

    private static void validateDebitAdviceSuccessfulHeaders(Row headerRow) {
        String[] requiredHeaders = {
                "TRANSACTION_ID", "SESSION_ID", "STATUS", "MESSAGE", "NIP_RESPONSE_CODE", "NAME_ENQUIRY_REF",
                "DESTINATION_INSTITUTION_CODE", "CHANNEL_CODE", "BENEFICIARY_ACCOUNT_NAME", "BENEFICIARY_ACCOUNT_NUMBER",
                "BENEFICIARY_BVN", "BENEFICIARY_KYC_LEVEL", "DEBIT_ACCOUNT_NAME", "DEBIT_ACCOUNT_NUMBER", "DEBIT_BVN",
                "DEBIT_KYC_LEVEL", "NARRATION", "PAYMENT_REFERENCE", "AMOUNT", "TRANSACTION_LOCATION"
        };

        for (String requiredHeader : requiredHeaders) {
            boolean found = false;
            for (Cell cell : headerRow) {
                if (requiredHeader.equalsIgnoreCase(cell.getStringCellValue().trim())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Missing required header: " + requiredHeader +
                                ". Please ensure it is a debit advice successful stacked file you uploaded.");
            }
        }
    }
    private static void validateDebitFailedHeaders(Row headerRow) {
        String[] requiredHeaders = {
                "TRANSACTION_ID", "SESSION_ID", "STATUS", "MESSAGE", "NIP_RESPONSE_CODE", "NAME_ENQUIRY_REF",
                "DESTINATION_INSTITUTION_CODE", "CHANNEL_CODE", "BENEFICIARY_ACCOUNT_NAME", "BENEFICIARY_ACCOUNT_NUMBER",
                "BENEFICIARY_BVN", "BENEFICIARY_KYC_LEVEL", "DEBIT_ACCOUNT_NAME", "DEBIT_ACCOUNT_NUMBER", "DEBIT_BVN",
                "DEBIT_KYC_LEVEL", "NARRATION", "PAYMENT_REFERENCE", "AMOUNT", "TRANSACTION_LOCATION"
        };

        for (String requiredHeader : requiredHeaders) {
            boolean found = false;
            for (Cell cell : headerRow) {
                if (requiredHeader.equalsIgnoreCase(cell.getStringCellValue().trim())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Missing required header: " + requiredHeader +
                                ". Please ensure it is a debit failed stacked file you uploaded.");
            }
        }
    }
    private static void validateDebitSuccessfulHeaders(Row headerRow) {
        String[] requiredHeaders = {
                "TRANSACTION_ID", "SESSION_ID", "STATUS", "MESSAGE", "NIP_RESPONSE_CODE", "NAME_ENQUIRY_REF",
                "DESTINATION_INSTITUTION_CODE", "CHANNEL_CODE", "BENEFICIARY_ACCOUNT_NAME", "BENEFICIARY_ACCOUNT_NUMBER",
                "BENEFICIARY_BVN", "BENEFICIARY_KYC_LEVEL", "DEBIT_ACCOUNT_NAME", "DEBIT_ACCOUNT_NUMBER", "DEBIT_BVN",
                "DEBIT_KYC_LEVEL", "NARRATION", "PAYMENT_REFERENCE", "AMOUNT", "TRANSACTION_LOCATION"
        };

        for (String requiredHeader : requiredHeaders) {
            boolean found = false;
            for (Cell cell : headerRow) {
                if (requiredHeader.equalsIgnoreCase(cell.getStringCellValue().trim())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Missing required header: " + requiredHeader +
                                ". Please ensure it is a debit successful stacked file you uploaded.");
            }
        }
    }
    private static void validateCreditFailedHeaders(Row headerRow) {
        String[] requiredHeaders = {
                "transaction_id", "session_id", "status", "message", "nip_response_code",
                "name_enquiry_ref", "destination_institution_code", "channel_code",
                "beneficiary_account_name", "beneficiary_account_number", "beneficiary_bvn",
                "beneficiary_kyc_level", "originator_account_name", "originator_account_number",
                "originator_bvn", "originator_kyc_level", "narration", "payment_reference",
                "amount", "transaction_location", "initiatior_account_name", "initiatior_account_number"
        };
        for (String requiredHeader : requiredHeaders) {
            boolean found = false;
            for (Cell cell : headerRow) {
                if (requiredHeader.equalsIgnoreCase(cell.getStringCellValue().trim())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Missing required header: " + requiredHeader +
                                ". Please ensure it is a credit failed stacked file you uploaded.");
            }
        }
    }
    private static void validateCreditSuccessfulHeaders(Row headerRow) {
        String[] requiredHeaders = {
                "TRANSACTION_ID", "SESSION_ID", "STATUS", "MESSAGE", "NIP_RESPONSE_CODE", "NAME_ENQUIRY_REF",
                "DESTINATION_INSTITUTION_CODE", "CHANNEL_CODE", "BENEFICIARY_ACCOUNT_NAME", "BENEFICIARY_ACCOUNT_NUMBER",
                "BENEFICIARY_BVN", "BENEFICIARY_KYC_LEVEL", "ORIGINATOR_ACCOUNT_NAME", "ORIGINATOR_ACCOUNT_NUMBER",
                "ORIGINATOR_BVN", "ORIGINATOR_KYC_LEVEL", "NARRATION", "PAYMENT_REFERENCE", "AMOUNT", "TRANSACTION_LOCATION",
                "INITIATIOR_ACCOUNT_NAME", "INITIATIOR_ACCOUNT_NUMBER"
        };

        for (String requiredHeader : requiredHeaders) {
            boolean found = false;
            for (Cell cell : headerRow) {
                if (requiredHeader.equalsIgnoreCase(cell.getStringCellValue().trim())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Missing required header: " + requiredHeader +
                                ". Please ensure it is a credit successful stacked file you uploaded.");
            }
        }
    }
    private static void validateDebitAdviceFailedHeaders(Row headerRow) {
        String[] requiredHeaders = {
                "TRANSACTION_ID", "SESSION_ID", "STATUS", "MESSAGE", "NIP_RESPONSE_CODE", "NAME_ENQUIRY_REF",
                "DESTINATION_INSTITUTION_CODE", "CHANNEL_CODE", "BENEFICIARY_ACCOUNT_NAME", "BENEFICIARY_ACCOUNT_NUMBER",
                "BENEFICIARY_BVN", "BENEFICIARY_KYC_LEVEL", "DEBIT_ACCOUNT_NAME", "DEBIT_ACCOUNT_NUMBER", "DEBIT_BVN",
                "DEBIT_KYC_LEVEL", "NARRATION", "PAYMENT_REFERENCE", "AMOUNT", "TRANSACTION_LOCATION"
        };

        for (String requiredHeader : requiredHeaders) {
            boolean found = false;
            for (Cell cell : headerRow) {
                if (requiredHeader.equalsIgnoreCase(cell.getStringCellValue().trim())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Missing required header: " + requiredHeader +
                                ". Please ensure it is a debit advice failed stacked file you uploaded.");
            }
        }
    }
}

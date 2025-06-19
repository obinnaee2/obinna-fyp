package com.fyp.reconciliation_automation.service;

import com.fyp.reconciliation_automation.entity.FundsTransfer;
import com.fyp.reconciliation_automation.entity.MetaCollections;
import com.fyp.reconciliation_automation.entity.MetaCreditsAndReversals;
import com.fyp.reconciliation_automation.entity.MetaDebits;
import com.fyp.reconciliation_automation.entity.NibbsCentralRecon;
import com.fyp.reconciliation_automation.entity.PremiumTrustBankStatement;
import com.fyp.reconciliation_automation.entity.PremiumTrustBankStatementCredits;
import com.fyp.reconciliation_automation.entity.PremiumTrustBankStatementDebits;
import com.fyp.reconciliation_automation.entity.PremiumTrustOthers;
import com.fyp.reconciliation_automation.entity.PremiumTrustReversals;
import com.fyp.reconciliation_automation.entity.PremiumTrustVPS;
import com.fyp.reconciliation_automation.entity.ProvidusBankStatement;
import com.fyp.reconciliation_automation.entity.ProvidusBankStatementCredits;
import com.fyp.reconciliation_automation.entity.ProvidusBankStatementDebits;
import com.fyp.reconciliation_automation.entity.ProvidusBankStatementReversals;
import com.fyp.reconciliation_automation.entity.ProvidusBankVPS;
import com.fyp.reconciliation_automation.entity.ProvidusOthers;
import com.fyp.reconciliation_automation.entity.TempMetabase;
import com.fyp.reconciliation_automation.repository.FundsTransferRepository;
import com.fyp.reconciliation_automation.repository.MetaCollectionsRepository;
import com.fyp.reconciliation_automation.repository.MetaCreditsAndReversalsRepository;
import com.fyp.reconciliation_automation.repository.MetaDebitsRepository;
import com.fyp.reconciliation_automation.repository.NibbsCentralReconRepository;
import com.fyp.reconciliation_automation.repository.PremiumTrustCreditsRepository;
import com.fyp.reconciliation_automation.repository.PremiumTrustDebitsRepository;
import com.fyp.reconciliation_automation.repository.PremiumTrustOthersRepository;
import com.fyp.reconciliation_automation.repository.PremiumTrustRepository;
import com.fyp.reconciliation_automation.repository.PremiumTrustReversalRepository;
import com.fyp.reconciliation_automation.repository.PremiumTrustVpsRepository;
import com.fyp.reconciliation_automation.repository.ProvidusBankRepository;
import com.fyp.reconciliation_automation.repository.ProvidusBankStatementCreditsRepository;
import com.fyp.reconciliation_automation.repository.ProvidusBankStatementDebitsRepository;
import com.fyp.reconciliation_automation.repository.ProvidusBankStatementReversalRepository;
import com.fyp.reconciliation_automation.repository.ProvidusBankVpsRepository;
import com.fyp.reconciliation_automation.repository.ProvidusOthersRepository;
import com.fyp.reconciliation_automation.repository.TempMetabaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.Row;
import java.io.File;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


@Service
@Transactional
@Slf4j
public class WorksheetExportService {

    private final NibbsCentralReconRepository nibbsCentralReconRepository;
    private final MetaCreditsAndReversalsRepository metaCreditsAndReversalsRepository;
    private final MetaCollectionsRepository metaCollectionsRepository;
    private final MetaDebitsRepository metaDebitsRepository;
    private final TempMetabaseRepository metaTempMetabaseRepository;
    private final PremiumTrustOthersRepository premiumTrustOthersRepository;
    private final PremiumTrustRepository premiumtrustBankStatementRepository;
    private final ProvidusBankRepository providusBankStatementRepository;
    private final ProvidusBankStatementCreditsRepository providusBankStatementCreditsRepository;
    private final ProvidusBankStatementDebitsRepository providusBankStatementDebitsRepository;
    private final PremiumTrustCreditsRepository premiumtrustBankStatementCreditsRepository;
    private final PremiumTrustDebitsRepository premiumtrustBankStatementDebitsRepository ;
    private final PremiumTrustReversalRepository premiumtrustReversalRepository ;
    private final ProvidusBankStatementReversalRepository providusBankStatementReversalRepository;
    private final ProvidusBankVpsRepository providusBankVpsRepository;
    private final ProvidusOthersRepository providusOthersRepository;
    private final PremiumTrustVpsRepository premiumTrustVpsRepository;
    private final FundsTransferRepository fundsTransferRepository;



    @Autowired
    public WorksheetExportService(
            NibbsCentralReconRepository nibbsCentralReconRepository, MetaCreditsAndReversalsRepository metaCreditsAndReversalsRepository, MetaCollectionsRepository metaCollectionsRepository, MetaDebitsRepository metaDebitsRepository, TempMetabaseRepository tempMetabaseRepository, TempMetabaseRepository metaTempMetabaseRepository, PremiumTrustOthersRepository premiumTrustOthersRepository,
            PremiumTrustRepository premiumtrustBankStatementRepository,
            ProvidusBankRepository providusBankStatementRepository,
            ProvidusBankStatementCreditsRepository providusBankStatementCreditsRepository,
            ProvidusBankStatementDebitsRepository providusBankStatementDebitsRepository, PremiumTrustCreditsRepository premiumtrustBankStatementCreditsRepository, PremiumTrustDebitsRepository premiumtrustBankStatementDebitsRepository, PremiumTrustReversalRepository premiumtrustReversalRepository, ProvidusBankStatementReversalRepository providusBankStatementReversalRepository, ProvidusBankVpsRepository providusBankVpsRepository, ProvidusOthersRepository providusOthersRepository, PremiumTrustVpsRepository premiumTrustVpsRepository, FundsTransferRepository fundsTransferRepository) {
        this.nibbsCentralReconRepository = nibbsCentralReconRepository;
        this.metaCreditsAndReversalsRepository = metaCreditsAndReversalsRepository;
        this.metaCollectionsRepository = metaCollectionsRepository;
        this.metaDebitsRepository = metaDebitsRepository;
        this.metaTempMetabaseRepository = metaTempMetabaseRepository;
        this.premiumTrustOthersRepository = premiumTrustOthersRepository;
        this.premiumtrustBankStatementRepository = premiumtrustBankStatementRepository;
        this.providusBankStatementRepository = providusBankStatementRepository;
        this.providusBankStatementCreditsRepository = providusBankStatementCreditsRepository;
        this.providusBankStatementDebitsRepository = providusBankStatementDebitsRepository;
        this.premiumtrustBankStatementCreditsRepository = premiumtrustBankStatementCreditsRepository;
        this.premiumtrustBankStatementDebitsRepository = premiumtrustBankStatementDebitsRepository;
        this.premiumtrustReversalRepository = premiumtrustReversalRepository;
        this.providusBankStatementReversalRepository = providusBankStatementReversalRepository;
        this.providusBankVpsRepository = providusBankVpsRepository;
        this.providusOthersRepository = providusOthersRepository;
        this.premiumTrustVpsRepository = premiumTrustVpsRepository;
        this.fundsTransferRepository = fundsTransferRepository;
    }
    static {
        System.setProperty("org.apache.poi.ss.ignoreMissingFontSystem", "true");
    }

    public ByteArrayOutputStream exportWorksheetForReportType(File toUpload,ReportType reportType) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             SXSSFWorkbook workbook = new SXSSFWorkbook()) {

            exportNibbsCentralRecon(workbook);
            exportMetabaseTransactions(workbook);
            exportMetaCollectionTransactions(workbook);
            exportMetaDebitsTransactions(workbook);
            exportMetaCreditsAndReversalsTransactions(workbook);


            switch (reportType) {
                case PROVIDUS_NIBSS_PREMIUM_TRUST:
                    exportPremiumtrustBankStatement(workbook);
                    exportPremiumtrustBankStatementCredits(workbook);
                    exportPremiumtrustBankStatementDebits(workbook);
                    exportPremiumtrustBankStatementReversals(workbook);
                    exportPremiumtrustOthers(workbook);
                    exportPremiumtrustBankVPS(workbook);
                    exportFundsTransfer(workbook);

                    exportProvidusBankStatementReversals(workbook);
                    exportProvidusBankStatementCredits(workbook);
                    exportProvidusBankStatementDebits(workbook);
                    exportProvidusBankStatement(workbook);
                    exportProvidusOthers(workbook);
                    exportProvidusBankVPS(workbook);
                    break;
                case PREMIUM_TRUST_NIBSS:
                    exportPremiumtrustBankStatement(workbook);
                    exportPremiumtrustBankStatementCredits(workbook);
                    exportPremiumtrustBankStatementDebits(workbook);
                    exportPremiumtrustBankStatementReversals(workbook);
                    exportPremiumtrustOthers(workbook);
                    exportPremiumtrustBankVPS(workbook);
                    break;

                case PROVIDUS_NIBSS:
                    exportProvidusBankStatementReversals(workbook);
                    exportProvidusBankStatementCredits(workbook);
                    exportProvidusBankStatementDebits(workbook);
                    exportProvidusBankStatement(workbook);
                    exportProvidusOthers(workbook);
                    exportFundsTransfer(workbook);
                    exportProvidusBankVPS(workbook);
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported ReportType");
            }

            workbook.write(out);

            try (FileOutputStream fos = new FileOutputStream(toUpload)) {
                fos.write(out.toByteArray());
            }
            return out;
        }
    }



//    private void exportMetabaseTransactions(SXSSFWorkbook workbook, LocalDate startDate, LocalDate endDate) {
//        List<MetabaseTransaction> transactions;
//
//        if (endDate == null) {
//            transactions = metabaseTransactionsRepository.findByTransactionDate(startDate);
//        } else {
//            transactions = metabaseTransactionsRepository.findByTransactionDateRange(startDate, endDate);
//        }
//
//        log.info("Number of transactions retrieved: {}", transactions.size());
//
//        Sheet debitSheet = workbook.createSheet("Meta Debits");
//        Sheet creditSheet = workbook.createSheet("Meta Credits and Reversals");
//        Sheet allTransactionsSheet = workbook.createSheet("All Metabase Transactions");
//
//        createHeaderRow(debitSheet);
//        createHeaderRow(creditSheet);
//        createHeaderRow(allTransactionsSheet);
//
//        int debitRowNum = 1;
//        int creditRowNum = 1;
//        int allTransactionsRowNum = 1;
//
//        for (MetabaseTransaction transaction : transactions) {
//            String transactionType = transaction.getTransactionType();
//
//            if (transactionType != null && transactionType.toLowerCase().matches(".*(debit|deb|debits).*")) {
//                Row row = debitSheet.createRow(debitRowNum++);
//                populateRowWithTransactionData(row, transaction);
//            } else if (transactionType != null && transactionType.toLowerCase().matches(".*(credit|credits|cred|reverse|reversal|rvsl|revers|rvs).*")) {
//                Row row = creditSheet.createRow(creditRowNum++);
//                populateRowWithTransactionData(row, transaction);
//            }
//
//            Row allRow = allTransactionsSheet.createRow(allTransactionsRowNum++);
//            populateRowWithTransactionData(allRow, transaction);
//        }
//    }


    private void exportMetaDebitsTransactions(Workbook workbook) {
        List<MetaDebits> metaDebitsData = metaDebitsRepository.findAll();
        Sheet sheet = workbook.createSheet("Meta Debits");
        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Account Name");
        header.createCell(1).setCellValue("Transaction Reference");
        header.createCell(2).setCellValue("Transaction Amount");
        header.createCell(3).setCellValue("Transaction Fee");
        header.createCell(4).setCellValue("Debit Amount");
        header.createCell(5).setCellValue("Credit Amount");
        header.createCell(6).setCellValue("Balance Before");
        header.createCell(7).setCellValue("Balance After");
        header.createCell(8).setCellValue("Transaction Type");
        header.createCell(9).setCellValue("Transaction Narration");
        header.createCell(10).setCellValue("Transaction Status");
        header.createCell(11).setCellValue("Response Code");
        header.createCell(12).setCellValue("Response Message");
        header.createCell(13).setCellValue("Bank Name");
        header.createCell(14).setCellValue("To Account");
        header.createCell(15).setCellValue("Payout Processor");
        header.createCell(16).setCellValue("Currency");
        header.createCell(17).setCellValue("Banks Name");
        header.createCell(18).setCellValue("Banks Currency Code");
        header.createCell(19).setCellValue("Transaction Date");
        header.createCell(20).setCellValue("Bank Chargesy");
        header.createCell(21).setCellValue("Session ID");
        header.createCell(22).setCellValue("Match Statement");
        header.createCell(23).setCellValue("Matched Bank");
        header.createCell(24).setCellValue("Nibbs Match");
        header.createCell(25).setCellValue("Nibbs Status");
        header.createCell(26).setCellValue("Status");

        int rowNum = 1;
        for (MetaDebits data : metaDebitsData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getAccountName());
            row.createCell(1).setCellValue(data.getTransactionReference());
            row.createCell(2).setCellValue(data.getTransactionAmount());
            row.createCell(3).setCellValue(data.getTransactionFee());
            row.createCell(4).setCellValue(data.getDebitAmount());
            row.createCell(5).setCellValue(data.getCreditAmount());
            row.createCell(6).setCellValue(data.getBalanceBefore());
            row.createCell(7).setCellValue(data.getBalanceAfter());
            row.createCell(8).setCellValue(data.getTransactionType());
            row.createCell(9).setCellValue(data.getTransactionNarration());
            row.createCell(10).setCellValue(data.getTransactionStatus());
            row.createCell(11).setCellValue(data.getResponseCode());
            row.createCell(12).setCellValue(data.getResponseMessage());
            row.createCell(13).setCellValue(data.getBankName());
            row.createCell(14).setCellValue(data.getToAccount());
            row.createCell(15).setCellValue(data.getPayoutProcessor());
            row.createCell(16).setCellValue(data.getCurrency());
            row.createCell(17).setCellValue(data.getBanksName());
            row.createCell(18).setCellValue(data.getBanksCurrencyCode());
            row.createCell(19).setCellValue(data.getTransactionDate());
            row.createCell(20).setCellValue(data.getBankChargesy());
            row.createCell(21).setCellValue(data.getSessionId());
            row.createCell(22).setCellValue(data.getMatchStatement());
            row.createCell(23).setCellValue(data.getMatchedBank());
            row.createCell(24).setCellValue(data.getNibbsMatch());
            row.createCell(25).setCellValue(data.getNibbsStatus());
            row.createCell(26).setCellValue(data.getStatus());
        }
    }



    private void exportMetaCreditsAndReversalsTransactions(Workbook workbook) {
        List<MetaCreditsAndReversals> metaCreditsAndReversalsData = metaCreditsAndReversalsRepository.findAll();
        Sheet sheet = workbook.createSheet("Meta Credits and Reversals");
        Row header = sheet.createRow(0);


        header.createCell(0).setCellValue("Account Name");
        header.createCell(1).setCellValue("Transaction Reference");
        header.createCell(2).setCellValue("Transaction Amount");
        header.createCell(3).setCellValue("Transaction Fee");
        header.createCell(4).setCellValue("Debit Amount");
        header.createCell(5).setCellValue("Credit Amount");
        header.createCell(6).setCellValue("Balance Before");
        header.createCell(7).setCellValue("Balance After");
        header.createCell(8).setCellValue("Transaction Type");
        header.createCell(9).setCellValue("Transaction Narration");
        header.createCell(10).setCellValue("Transaction Status");
        header.createCell(11).setCellValue("Response Code");
        header.createCell(12).setCellValue("Response Message");
        header.createCell(13).setCellValue("Bank Name");
        header.createCell(14).setCellValue("To Account");
        header.createCell(15).setCellValue("Payout Processor");
        header.createCell(16).setCellValue("Currency");
        header.createCell(17).setCellValue("Banks Name");
        header.createCell(18).setCellValue("Banks Currency Code");
        header.createCell(19).setCellValue("Transaction Date");
        header.createCell(20).setCellValue("Bank Chargesy");
        header.createCell(21).setCellValue("Session ID");
        header.createCell(22).setCellValue("MATCH VA");
        header.createCell(23).setCellValue("Matched Bank");
        header.createCell(24).setCellValue("STATUS");

        int rowNum = 1;
        for (MetaCreditsAndReversals data : metaCreditsAndReversalsData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getAccountName());
            row.createCell(1).setCellValue(data.getTransactionReference());
            row.createCell(2).setCellValue(data.getTransactionAmount());
            row.createCell(3).setCellValue(data.getTransactionFee());
            row.createCell(4).setCellValue(data.getDebitAmount());
            row.createCell(5).setCellValue(data.getCreditAmount());
            row.createCell(6).setCellValue(data.getBalanceBefore());
            row.createCell(7).setCellValue(data.getBalanceAfter());
            row.createCell(8).setCellValue(data.getTransactionType());
            row.createCell(9).setCellValue(data.getTransactionNarration());
            row.createCell(10).setCellValue(data.getTransactionStatus());
            row.createCell(11).setCellValue(data.getResponseCode());
            row.createCell(12).setCellValue(data.getResponseMessage());
            row.createCell(13).setCellValue(data.getBankName());
            row.createCell(14).setCellValue(data.getToAccount());
            row.createCell(15).setCellValue(data.getPayoutProcessor());
            row.createCell(16).setCellValue(data.getCurrency());
            row.createCell(17).setCellValue(data.getBanksName());
            row.createCell(18).setCellValue(data.getBanksCurrencyCode());
            row.createCell(19).setCellValue(data.getTransactionDate());
            row.createCell(20).setCellValue(data.getBankChargesy());
            row.createCell(21).setCellValue(data.getSessionId());
            row.createCell(22).setCellValue(data.getMatchVa());
            row.createCell(23).setCellValue(data.getMatchedBank());
            row.createCell(24).setCellValue(data.getStatus());
        }
    }

    private void exportMetaCollectionTransactions(Workbook workbook) {
        List<MetaCollections> metaCollectionsData = metaCollectionsRepository.findAll();
        Sheet sheet = workbook.createSheet("Meta Collections");
        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Transaction Value Amount");
        header.createCell(1).setCellValue("Fee Amount");
        header.createCell(2).setCellValue("Partner Transaction Amount");
        header.createCell(3).setCellValue("Partner Fee Amount");
        header.createCell(4).setCellValue("Currency FK");
        header.createCell(5).setCellValue("Business FK Name");
        header.createCell(6).setCellValue("Business Branch FK Name");
        header.createCell(7).setCellValue("Partner");
        header.createCell(8).setCellValue("Partner Transaction Reference");
        header.createCell(9).setCellValue("Ended At");
        header.createCell(10).setCellValue("Match");
        header.createCell(11).setCellValue("Match Status");

        int rowNum = 1;
        for (MetaCollections data : metaCollectionsData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getTransactionValueAmount());
            row.createCell(1).setCellValue(data.getFeeAmount());
            row.createCell(2).setCellValue(data.getPartnerTransactionAmount());
            row.createCell(3).setCellValue(data.getPartnerFeeAmount());
            row.createCell(4).setCellValue(data.getCurrencyFk());
            row.createCell(5).setCellValue(data.getBusinessFkName());
            row.createCell(6).setCellValue(data.getBusinessBranchFkName());
            row.createCell(7).setCellValue(data.getPartner());
            row.createCell(8).setCellValue(data.getPartnerTransactionReference());
            row.createCell(9).setCellValue(data.getEndedAt());
            row.createCell(10).setCellValue(data.getMatch());
            row.createCell(11).setCellValue(data.getMatchStatus());
        }
    }

    private void exportMetabaseTransactions(Workbook workbook) {
        List<TempMetabase> metabaseData = metaTempMetabaseRepository.findAll();
        Sheet sheet = workbook.createSheet("Metabase Transactions");
        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Account Name");
        header.createCell(1).setCellValue("Transaction Reference");
        header.createCell(2).setCellValue("Transaction Amount");
        header.createCell(3).setCellValue("Transaction Fee");
        header.createCell(4).setCellValue("Debit Amount");
        header.createCell(5).setCellValue("Credit Amount");
        header.createCell(6).setCellValue("Balance Before");
        header.createCell(7).setCellValue("Balance After");
        header.createCell(8).setCellValue("Bank Charges Y");
        header.createCell(9).setCellValue("Transaction Type");
        header.createCell(10).setCellValue("Transaction Narration");
        header.createCell(11).setCellValue("Transaction Status");
        header.createCell(12).setCellValue("Response Code");
        header.createCell(13).setCellValue("Response Message");
        header.createCell(14).setCellValue("Session ID");
        header.createCell(15).setCellValue("Bank Name");
        header.createCell(16).setCellValue("To Account");
        header.createCell(17).setCellValue("Payout Processor");
        header.createCell(18).setCellValue("Currency");
        header.createCell(19).setCellValue("Banks Name");
        header.createCell(20).setCellValue("Banks Currency Code");
        header.createCell(21).setCellValue("Transaction Date");

        int rowNum = 1;
        for (TempMetabase data : metabaseData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getAccountName());
            row.createCell(1).setCellValue(data.getTransactionReference());
            row.createCell(2).setCellValue(data.getTransactionAmount());
            row.createCell(3).setCellValue(data.getTransactionFee());
            row.createCell(4).setCellValue(data.getDebitAmount());
            row.createCell(5).setCellValue(data.getCreditAmount());
            row.createCell(6).setCellValue(data.getBalanceBefore());
            row.createCell(7).setCellValue(data.getBalanceAfter());
            row.createCell(8).setCellValue(data.getBankChargesY());
            row.createCell(9).setCellValue(data.getTransactionType());
            row.createCell(10).setCellValue(data.getTransactionNarration());
            row.createCell(11).setCellValue(data.getTransactionStatus());
            row.createCell(12).setCellValue(data.getResponseCode());
            row.createCell(13).setCellValue(data.getResponseMessage());
            row.createCell(14).setCellValue(data.getSessionID());
            row.createCell(15).setCellValue(data.getBankName());
            row.createCell(16).setCellValue(data.getToAccount());
            row.createCell(17).setCellValue(data.getPayoutProcessor());
            row.createCell(18).setCellValue(data.getCurrency());
            row.createCell(19).setCellValue(data.getBanksName());
            row.createCell(20).setCellValue(data.getBanksCurrencyCode());
            row.createCell(21).setCellValue(data.getTransactionDate());
        }
    }


    private void exportNibbsCentralRecon(Workbook workbook) {
        List<NibbsCentralRecon> centralReconData = nibbsCentralReconRepository.findAll();
        Sheet sheet = workbook.createSheet("NIBBS");
        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Transaction Date");
        header.createCell(1).setCellValue("Transaction Reference");
        header.createCell(2).setCellValue("Session ID");
        header.createCell(3).setCellValue("Debit Successful");
        header.createCell(4).setCellValue("Credit Successful");
        header.createCell(5).setCellValue("Debit Failed");
        header.createCell(6).setCellValue("Credit Failed");
        header.createCell(7).setCellValue("Debit Advise Successful");
        header.createCell(8).setCellValue("Debit Advise Failed");
        header.createCell(9).setCellValue("Expected Status");
        header.createCell(10).setCellValue("Originator Account Number");
        header.createCell(11).setCellValue("Amount");
        header.createCell(12).setCellValue("Bank Match");
        header.createCell(13).setCellValue("Narration");
        header.createCell(14).setCellValue("Meta Match");
        header.createCell(15).setCellValue("Final Status");

        int rowNum = 1;
        for (NibbsCentralRecon data : centralReconData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data.getTransactionDate().toString());
            row.createCell(1).setCellValue(data.getTransactionReference());
            row.createCell(2).setCellValue(data.getSessionId());
            row.createCell(3).setCellValue(data.getDebitSuccessful() != null ? data.getDebitSuccessful().toString() : "null");
            row.createCell(4).setCellValue(data.getCreditSuccessful() != null ? data.getCreditSuccessful().toString() : "null");
            row.createCell(5).setCellValue(data.getDebitFailed() != null ? data.getDebitFailed().toString() : "null");
            row.createCell(6).setCellValue(data.getCreditFailed() != null ? data.getCreditFailed().toString() : "null");
            row.createCell(7).setCellValue(data.getDebitAdviseSuccessful() != null ? data.getDebitAdviseSuccessful().toString() : "null");
            row.createCell(8).setCellValue(data.getDebitAdviseFailed() != null ? data.getDebitAdviseFailed().toString() : "null");
            row.createCell(9).setCellValue(data.getExpectedStatus());
            row.createCell(10).setCellValue(data.getOriginatorAccountNumber());
            row.createCell(11).setCellValue(data.getAmount());
            row.createCell(12).setCellValue(data.getBankMatch());
            row.createCell(13).setCellValue(data.getNarration());
            row.createCell(14).setCellValue(data.getMetaMatch());
            row.createCell(15).setCellValue(data.getFinalStatus());
        }
    }


    private void exportPremiumtrustBankStatement(Workbook workbook) {
        List<PremiumTrustBankStatement> bankStatements = premiumtrustBankStatementRepository.findAll();
        Sheet sheet = workbook.createSheet("PT Bank Statements");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Transaction Date");
        header.createCell(1).setCellValue("Value Date");
        header.createCell(2).setCellValue("Transaction Details");
        header.createCell(3).setCellValue("Institution Number");
        header.createCell(4).setCellValue("Debit Amount");
        header.createCell(5).setCellValue("Credit Amount");
        header.createCell(6).setCellValue("Balance");


        int rowNum = 1;
        for (PremiumTrustBankStatement statement : bankStatements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(statement.getTransactionDate());
            row.createCell(1).setCellValue(statement.getValueDate());
            row.createCell(2).setCellValue(statement.getTransactionDetails());
            row.createCell(3).setCellValue(statement.getInstitutionNumber());
            row.createCell(4).setCellValue(statement.getDebitAmount());
            row.createCell(5).setCellValue(statement.getCreditAmount());
            row.createCell(6).setCellValue(statement.getBalance());

        }
    }
    private void exportPremiumtrustBankStatementCredits(SXSSFWorkbook workbook) {

        List<PremiumTrustBankStatementCredits> bankStatements = premiumtrustBankStatementCreditsRepository.findAll();
        Sheet sheet = workbook.createSheet("PT bank Statements Credits");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Transaction Date");
        header.createCell(1).setCellValue("Value Date");
        header.createCell(2).setCellValue("Transaction Details");
        header.createCell(3).setCellValue("Institution Number");
        header.createCell(4).setCellValue("Debit Amount");
        header.createCell(5).setCellValue("Credit Amount");
        header.createCell(6).setCellValue("Balance");
        header.createCell(7).setCellValue("Session Id");
        header.createCell(8).setCellValue("Match");
        header.createCell(9).setCellValue("Status");

        int rowNum = 1;
        for (PremiumTrustBankStatementCredits statement : bankStatements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(statement.getTransactionDate());
            row.createCell(1).setCellValue(statement.getValueDate());
            row.createCell(2).setCellValue(statement.getTransactionDetails());
            row.createCell(3).setCellValue(statement.getInstitutionNumber());
            row.createCell(4).setCellValue(statement.getDebitAmount());
            row.createCell(5).setCellValue(statement.getCreditAmount());
            row.createCell(6).setCellValue(statement.getBalance());
            row.createCell(7).setCellValue(statement.getSessionId());
            row.createCell(8).setCellValue(statement.getMatch());
            row.createCell(9).setCellValue(statement.getStatus());
        }
    }
    private void exportPremiumtrustBankStatementDebits(SXSSFWorkbook workbook) {

        List<PremiumTrustBankStatementDebits> bankStatements = premiumtrustBankStatementDebitsRepository.findAll();
        Sheet sheet = workbook.createSheet("PT Debits");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Transaction Date");
        header.createCell(1).setCellValue("Value Date");
        header.createCell(2).setCellValue("Transaction Details");
        header.createCell(3).setCellValue("Institution Number");
        header.createCell(4).setCellValue("Debit Amount");
        header.createCell(5).setCellValue("Credit Amount");
        header.createCell(6).setCellValue("Balance");
        header.createCell(7).setCellValue("Session Id");
        header.createCell(8).setCellValue("Match");
        header.createCell(9).setCellValue("Status");

        int rowNum = 1;
        for (PremiumTrustBankStatementDebits statement : bankStatements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(statement.getTransactionDate());
            row.createCell(1).setCellValue(statement.getValueDate());
            row.createCell(2).setCellValue(statement.getTransactionDetails());
            row.createCell(3).setCellValue(statement.getInstitutionNumber());
            row.createCell(4).setCellValue(statement.getDebitAmount());
            row.createCell(5).setCellValue(statement.getCreditAmount());
            row.createCell(6).setCellValue(statement.getBalance());
            row.createCell(7).setCellValue(statement.getSessionId());
            row.createCell(8).setCellValue(statement.getMatch());
            row.createCell(9).setCellValue(statement.getStatus());
        }
    }
    private void exportPremiumtrustOthers(SXSSFWorkbook workbook) {
        List<PremiumTrustOthers> bankStatements =  premiumTrustOthersRepository.findAll();
        Sheet sheet = workbook.createSheet("PT Others");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Transaction Date");
        header.createCell(1).setCellValue("Value Date");
        header.createCell(2).setCellValue("Transaction Details");
        header.createCell(3).setCellValue("Institution Number");
        header.createCell(4).setCellValue("Debit Amount");
        header.createCell(5).setCellValue("Credit Amount");
        header.createCell(6).setCellValue("Balance");
        header.createCell(7).setCellValue("Session Id");
        header.createCell(8).setCellValue("Match");
        header.createCell(9).setCellValue("Status");

        int rowNum = 1;
        for (PremiumTrustOthers statement : bankStatements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(statement.getTransactionDate());
            row.createCell(1).setCellValue(statement.getValueDate());
            row.createCell(2).setCellValue(statement.getTransactionDetails());
            row.createCell(3).setCellValue(statement.getInstitutionNumber());
            row.createCell(4).setCellValue(statement.getDebitAmount());
            row.createCell(5).setCellValue(statement.getCreditAmount());
            row.createCell(6).setCellValue(statement.getBalance());
            row.createCell(7).setCellValue(statement.getSessionId());
            row.createCell(8).setCellValue(statement.getMatch());
            row.createCell(9).setCellValue(statement.getStatus());
        }
    }

    private void exportPremiumtrustBankStatementReversals(SXSSFWorkbook workbook) {
        List<PremiumTrustReversals> bankStatements =  premiumtrustReversalRepository.findAll();
        Sheet sheet = workbook.createSheet("PT Failed and Reversal");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Transaction Date");
        header.createCell(1).setCellValue("Value Date");
        header.createCell(2).setCellValue("Transaction Details");
        header.createCell(3).setCellValue("Institution Number");
        header.createCell(4).setCellValue("Debit Amount");
        header.createCell(5).setCellValue("Credit Amount");
        header.createCell(6).setCellValue("Balance");
        header.createCell(7).setCellValue("Session Id");
        header.createCell(8).setCellValue("Match");
        header.createCell(9).setCellValue("Status");

        int rowNum = 1;
        for (PremiumTrustReversals statement : bankStatements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(statement.getTransactionDate());
            row.createCell(1).setCellValue(statement.getValueDate());
            row.createCell(2).setCellValue(statement.getTransactionDetails());
            row.createCell(3).setCellValue(statement.getInstitutionNumber());
            row.createCell(4).setCellValue(statement.getDebitAmount());
            row.createCell(5).setCellValue(statement.getCreditAmount());
            row.createCell(6).setCellValue(statement.getBalance());
            row.createCell(7).setCellValue(statement.getSessionId());
            row.createCell(8).setCellValue(statement.getMatch());
            row.createCell(9).setCellValue(statement.getStatus());
        }
    }
    private void exportPremiumtrustBankVPS(Workbook workbook) {
        List<PremiumTrustVPS> transactionList = premiumTrustVpsRepository.findAll();
        Sheet sheet = workbook.createSheet("PT Virtual Account");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Transaction Date");
        header.createCell(1).setCellValue("Value Date");
        header.createCell(2).setCellValue("Transaction Details");
        header.createCell(3).setCellValue("Institution Number");
        header.createCell(4).setCellValue("Debit Amount");
        header.createCell(5).setCellValue("Credit Amount");
        header.createCell(6).setCellValue("Balance");
        header.createCell(7).setCellValue("Reference Number");
        header.createCell(8).setCellValue("Session ID");
        header.createCell(9).setCellValue("Match");
        header.createCell(10).setCellValue("Status");
        int rowNum = 1;
        for (PremiumTrustVPS transaction : transactionList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(transaction.getTransactionDate());
            row.createCell(1).setCellValue(transaction.getValueDate());
            row.createCell(2).setCellValue(transaction.getTransactionDetails());
            row.createCell(3).setCellValue(transaction.getInstitutionNumber());
            row.createCell(4).setCellValue(transaction.getDebitAmount());
            row.createCell(5).setCellValue(transaction.getCreditAmount());
            row.createCell(6).setCellValue(transaction.getBalance());
            row.createCell(7).setCellValue(transaction.getReferenceNumber());
            row.createCell(8).setCellValue(transaction.getSessionId());
            row.createCell(9).setCellValue(transaction.getMatch());
            row.createCell(10).setCellValue(transaction.getStatus());
        }
    }


    private void exportFundsTransfer(Workbook workbook) {
        List<FundsTransfer> fundTransfers = fundsTransferRepository.findAll();
        Sheet sheet = workbook.createSheet("Fund Transfers");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Fund Transfer ID");
        header.createCell(1).setCellValue("Created On");
        header.createCell(2).setCellValue("Modified On");
        header.createCell(3).setCellValue("Bank Code");
        header.createCell(4).setCellValue("Currency");
        header.createCell(5).setCellValue("Destination Account");
        header.createCell(6).setCellValue("Destination Account Name");
        header.createCell(7).setCellValue("Merchant ID");
        header.createCell(8).setCellValue("Name Enquiry Reference");
        header.createCell(9).setCellValue("Narration");
        header.createCell(10).setCellValue("Processor");
        header.createCell(11).setCellValue("Request UUID");
        header.createCell(12).setCellValue("Response Code");
        header.createCell(13).setCellValue("Response Message");
        header.createCell(14).setCellValue("Session ID");
        header.createCell(15).setCellValue("Source Account");
        header.createCell(16).setCellValue("Transaction Amount");
        header.createCell(17).setCellValue("Transaction Reference");
        header.createCell(18).setCellValue("Transaction Status");
        header.createCell(19).setCellValue("Sender Sender ID");
        header.createCell(20).setCellValue("Requery Count");
        header.createCell(21).setCellValue("Retry Count");
        header.createCell(22).setCellValue("Requery Response Code");
        header.createCell(23).setCellValue("Requery Response Message");
        header.createCell(24).setCellValue("Source Account Name");
        header.createCell(25).setCellValue("Destination Bank Name");
        header.createCell(26).setCellValue("Processor Reference");
        header.createCell(27).setCellValue("Retry Processor");
        header.createCell(28).setCellValue("Retry Processor Reference");

        int rowNum = 1;
        for (FundsTransfer transfer : fundTransfers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(transfer.getFundTransferId());
            row.createCell(1).setCellValue(transfer.getCreatedOn());
            row.createCell(2).setCellValue(transfer.getModifiedOn());
            row.createCell(3).setCellValue(transfer.getBankCode());
            row.createCell(4).setCellValue(transfer.getCurrency());
            row.createCell(5).setCellValue(transfer.getDestAccount());
            row.createCell(6).setCellValue(transfer.getDestAccountName());
            row.createCell(7).setCellValue(transfer.getMerchantId());
            row.createCell(8).setCellValue(transfer.getNameEnquiryReference());
            row.createCell(9).setCellValue(transfer.getNarration());
            row.createCell(10).setCellValue(transfer.getProcessor());
            row.createCell(11).setCellValue(transfer.getRequestUuid());
            row.createCell(12).setCellValue(transfer.getResponseCode());
            row.createCell(13).setCellValue(transfer.getResponseMessage());
            row.createCell(14).setCellValue(transfer.getSessionId());
            row.createCell(15).setCellValue(transfer.getSourceAccount());
            row.createCell(16).setCellValue(transfer.getTransactionAmount());
            row.createCell(17).setCellValue(transfer.getTransactionReference());
            row.createCell(18).setCellValue(transfer.getTransactionStatus());
            row.createCell(19).setCellValue(transfer.getSenderSenderId());
            row.createCell(20).setCellValue(transfer.getRequeryCount());
            row.createCell(21).setCellValue(transfer.getRetryCount());
            row.createCell(22).setCellValue(transfer.getRequeryResponseCode());
            row.createCell(23).setCellValue(transfer.getRequeryResponseMessage());
            row.createCell(24).setCellValue(transfer.getSourceAccountName());
            row.createCell(25).setCellValue(transfer.getDestinationBankName());
            row.createCell(26).setCellValue(transfer.getProcessorReference());
            row.createCell(27).setCellValue( transfer.getRetryProcessor());
            row.createCell(28).setCellValue(transfer.getRetryProcessorReference());
        }
    }


    private void exportProvidusBankStatement(Workbook workbook) {
        List<ProvidusBankStatement> bankStatements = providusBankStatementRepository.findAll();
        Sheet sheet = workbook.createSheet("Providus Bank Statements");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Transaction Date");
        header.createCell(1).setCellValue("Value Date");
        header.createCell(2).setCellValue("Account Number");
        header.createCell(3).setCellValue("Account Name");
        header.createCell(4).setCellValue("Debit Amount");
        header.createCell(5).setCellValue("Credit Amount");
        header.createCell(6).setCellValue("Current Balance");
        header.createCell(7).setCellValue("Transaction Type");
        header.createCell(8).setCellValue("Remarks");
        header.createCell(9).setCellValue("Reference");
        header.createCell(10).setCellValue("Session ID");
        header.createCell(11).setCellValue("Payment Reference");
        header.createCell(12).setCellValue("Beneficiary Name");
        header.createCell(13).setCellValue("Account Transaction Date");
        header.createCell(14).setCellValue("Update Time");
        header.createCell(15).setCellValue("Transaction Sequence 1");
        header.createCell(16).setCellValue("Transaction Sequence 2");


        int rowNum = 1;
        for (ProvidusBankStatement statement : bankStatements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(statement.getTransactionDate());
            row.createCell(1).setCellValue(statement.getValueDate());
            row.createCell(2).setCellValue(statement.getAccountNumber());
            row.createCell(3).setCellValue(statement.getAccountName());
            row.createCell(4).setCellValue(statement.getDebitAmount());
            row.createCell(5).setCellValue(statement.getCreditAmount());
            row.createCell(6).setCellValue(statement.getCurrentBalance());
            row.createCell(7).setCellValue(statement.getTransactionType());
            row.createCell(8).setCellValue(statement.getRemarks());
            row.createCell(9).setCellValue(statement.getReference());
            row.createCell(10).setCellValue(statement.getSessionId());
            row.createCell(11).setCellValue(statement.getPaymentReference());
            row.createCell(12).setCellValue(statement.getBeneficiaryName());
            row.createCell(13).setCellValue(statement.getAccountTransactionDate());
            row.createCell(14).setCellValue(statement.getUpdTime());
            row.createCell(15).setCellValue(statement.getTransactionSequence1());
            row.createCell(16).setCellValue(statement.getTransactionSequence2());
        }
    }

    private void exportProvidusOthers(Workbook workbook) {
        List<ProvidusOthers> othersStatements = providusOthersRepository.findAll();
        Sheet sheet = workbook.createSheet("Providus Others");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Match");
        header.createCell(1).setCellValue("Status");
        header.createCell(2).setCellValue("Transaction Date");
        header.createCell(3).setCellValue("Value Date");
        header.createCell(4).setCellValue("Account Number");
        header.createCell(5).setCellValue("Account Name");
        header.createCell(6).setCellValue("Debit Amount");
        header.createCell(7).setCellValue("Credit Amount");
        header.createCell(8).setCellValue("Current Balance");
        header.createCell(9).setCellValue("Transaction Type");
        header.createCell(10).setCellValue("Remarks");
        header.createCell(11).setCellValue("Reference");
        header.createCell(12).setCellValue("Session ID");
        header.createCell(13).setCellValue("Payment Reference");
        header.createCell(14).setCellValue("Beneficiary Name");
        header.createCell(15).setCellValue("Actual Transaction Date");
        header.createCell(16).setCellValue("Update Time");
        header.createCell(17).setCellValue("Transaction Sequence 1");
        header.createCell(18).setCellValue("Transaction Sequence 2");

        int rowNum = 1;
        for (ProvidusOthers statement : othersStatements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(statement.getMatch());
            row.createCell(1).setCellValue(statement.getStatus());
            row.createCell(2).setCellValue(statement.getTransactionDate());
            row.createCell(3).setCellValue(statement.getValueDate());
            row.createCell(4).setCellValue(statement.getAccountNumber());
            row.createCell(5).setCellValue(statement.getAccountName());
            row.createCell(6).setCellValue(statement.getDebit());
            row.createCell(7).setCellValue(statement.getCredit());
            row.createCell(8).setCellValue(statement.getCurrentBalance());
            row.createCell(9).setCellValue(statement.getTransactionType());
            row.createCell(10).setCellValue(statement.getRemarks());
            row.createCell(11).setCellValue(statement.getReference());
            row.createCell(12).setCellValue(statement.getSessionId());
            row.createCell(13).setCellValue(statement.getPaymentReference());
            row.createCell(14).setCellValue(statement.getBeneficiaryName());
            row.createCell(15).setCellValue(statement.getActualTransactionDate());
            row.createCell(16).setCellValue(statement.getUpdateTime());
            row.createCell(17).setCellValue(statement.getTransactionSequence1());
            row.createCell(18).setCellValue(statement.getTransactionSequence2());
        }
    }

    private void exportProvidusBankVPS(Workbook workbook) {

        List<ProvidusBankVPS> vpsList = providusBankVpsRepository.findAll();
        Sheet sheet = workbook.createSheet("Providus Bank VPS");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Session ID");
        header.createCell(2).setCellValue("Settlement Ref");
        header.createCell(3).setCellValue("Merchant ID");
        header.createCell(4).setCellValue("Transaction Amount Minor");
        header.createCell(5).setCellValue("Settled Amount Minor");
        header.createCell(6).setCellValue("Charge Amount Minor");
        header.createCell(7).setCellValue("VAT Amount Minor");
        header.createCell(8).setCellValue("Currency");
        header.createCell(9).setCellValue("Notification Acknowledgement");
        header.createCell(10).setCellValue("Num Retries");
        header.createCell(11).setCellValue("Retry Batch ID");
        header.createCell(12).setCellValue("Failed Count");
        header.createCell(13).setCellValue("Source Account Name");
        header.createCell(14).setCellValue("Source Account No");
        header.createCell(15).setCellValue("Source Bank Code");
        header.createCell(16).setCellValue("Virtual Account No");
        header.createCell(17).setCellValue("Account Ref Code");
        header.createCell(18).setCellValue("Created At");
        header.createCell(19).setCellValue("Updated At");
        header.createCell(20).setCellValue("Narration");
        header.createCell(21).setCellValue("Channel ID");
        header.createCell(22).setCellValue("Post Flag");
        header.createCell(23).setCellValue("Stamp Duty Flag");
        header.createCell(24).setCellValue("CBA Tran Time");
        header.createCell(25).setCellValue("Reason");
        header.createCell(26).setCellValue("Reversal Session ID");
        header.createCell(27).setCellValue("Settlement Notification Retry Batch ID");
        header.createCell(28).setCellValue("Match Meta");
        header.createCell(29).setCellValue("Match Statement");
        header.createCell(30).setCellValue("Status");

        int rowNum = 1;
        for (ProvidusBankVPS vps : vpsList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(vps.getId());
            row.createCell(1).setCellValue(vps.getSessionId());
            row.createCell(2).setCellValue(vps.getSettlementRef());
            row.createCell(3).setCellValue(vps.getMerchantId());
            row.createCell(4).setCellValue(vps.getTransactionAmountMinor());
            row.createCell(5).setCellValue(vps.getSettledAmountMinor());
            row.createCell(6).setCellValue(vps.getChargeAmountMinor());
            row.createCell(7).setCellValue(vps.getVatAmountMinor());
            row.createCell(8).setCellValue(vps.getCurrency());
            row.createCell(9).setCellValue(vps.getNotificationAcknowledgement());
            row.createCell(10).setCellValue(vps.getNumRetries());
            row.createCell(11).setCellValue(vps.getRetryBatchId());
            row.createCell(12).setCellValue(vps.getFailedCount());
            row.createCell(13).setCellValue(vps.getSourceAcctName());
            row.createCell(14).setCellValue(vps.getSourceAcctNo());
            row.createCell(15).setCellValue(vps.getSourceBankCode());
            row.createCell(16).setCellValue(vps.getVirtualAcctNo());
            row.createCell(17).setCellValue(vps.getAccountRefCode());
            row.createCell(18).setCellValue(vps.getCreatedAt());
            row.createCell(19).setCellValue(vps.getUpdatedAt());
            row.createCell(20).setCellValue(vps.getNarration());
            row.createCell(21).setCellValue(vps.getChannelId());
            row.createCell(22).setCellValue(vps.getPostFlg());
            row.createCell(23).setCellValue(vps.getStampDutyFlg());
            row.createCell(24).setCellValue(vps.getCbaTranTime());
            row.createCell(25).setCellValue(vps.getReason());
            row.createCell(26).setCellValue(vps.getReversalSessionId());
            row.createCell(27).setCellValue(vps.getSettlementNotificationRetryBatchId());
            row.createCell(28).setCellValue(vps.getMatchMeta());
            row.createCell(29).setCellValue(vps.getMatchStatement());
            row.createCell(30).setCellValue(vps.getStatus());
        }
    }
    private void exportProvidusBankStatementDebits(SXSSFWorkbook workbook) {
        List<ProvidusBankStatementDebits> bankStatements = providusBankStatementDebitsRepository.findAll();
        Sheet sheet = workbook.createSheet("Providus BS  Debits");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Transaction Date");
        header.createCell(1).setCellValue("Value Date");
        header.createCell(2).setCellValue("Account Number");
        header.createCell(3).setCellValue("Account Name");
        header.createCell(4).setCellValue("Debit Amount");
        header.createCell(5).setCellValue("Credit Amount");
        header.createCell(6).setCellValue("Current Balance");
        header.createCell(7).setCellValue("Transaction Type");
        header.createCell(8).setCellValue("Remarks");
        header.createCell(9).setCellValue("Reference");
        header.createCell(10).setCellValue("Session ID");
        header.createCell(11).setCellValue("Payment Reference");
        header.createCell(12).setCellValue("Beneficiary Name");
        header.createCell(13).setCellValue("Account Transaction Date");
        header.createCell(14).setCellValue("Update Time");
        header.createCell(15).setCellValue("Transaction Sequence 1");
        header.createCell(16).setCellValue("Transaction Sequence 2");
        header.createCell(17).setCellValue("Match");
        header.createCell(18).setCellValue("Status");
        header.createCell(19).setCellValue("Other Status");


        int rowNum = 1;
        for (ProvidusBankStatementDebits statement : bankStatements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(statement.getTransactionDate());
            row.createCell(1).setCellValue(statement.getValueDate());
            row.createCell(2).setCellValue(statement.getAccountNumber());
            row.createCell(3).setCellValue(statement.getAccountName());
            row.createCell(4).setCellValue(statement.getDebitAmount());
            row.createCell(5).setCellValue(statement.getCreditAmount());
            row.createCell(6).setCellValue(statement.getCurrentBalance());
            row.createCell(7).setCellValue(statement.getTransactionType());
            row.createCell(8).setCellValue(statement.getRemarks());
            row.createCell(9).setCellValue(statement.getReference());
            row.createCell(10).setCellValue(statement.getSessionId());
            row.createCell(11).setCellValue(statement.getPaymentReference());
            row.createCell(12).setCellValue(statement.getBeneficiaryName());
            row.createCell(13).setCellValue(statement.getAccountTransactionDate());
            row.createCell(14).setCellValue(statement.getUpdTime());
            row.createCell(15).setCellValue(statement.getTransactionSequence1());
            row.createCell(16).setCellValue(statement.getTransactionSequence2());
            row.createCell(17).setCellValue(statement.getMatch());
            row.createCell(18).setCellValue(statement.getStatus());
            row.createCell(19).setCellValue(statement.getOtherStatus());
        }

    }
    private void exportProvidusBankStatementCredits(SXSSFWorkbook workbook) {
        List<ProvidusBankStatementCredits> bankStatements = providusBankStatementCreditsRepository.findAll();
        Sheet sheet = workbook.createSheet("Providus BS  credits");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Transaction Date");
        header.createCell(1).setCellValue("Value Date");
        header.createCell(2).setCellValue("Account Number");
        header.createCell(3).setCellValue("Account Name");
        header.createCell(4).setCellValue("Debit Amount");
        header.createCell(5).setCellValue("Credit Amount");
        header.createCell(6).setCellValue("Current Balance");
        header.createCell(7).setCellValue("Transaction Type");
        header.createCell(8).setCellValue("Remarks");
        header.createCell(9).setCellValue("Reference");
        header.createCell(10).setCellValue("Session ID");
        header.createCell(11).setCellValue("Payment Reference");
        header.createCell(12).setCellValue("Beneficiary Name");
        header.createCell(13).setCellValue("Account Transaction Date");
        header.createCell(14).setCellValue("Update Time");
        header.createCell(15).setCellValue("Transaction Sequence 1");
        header.createCell(16).setCellValue("Transaction Sequence 2");
        header.createCell(17).setCellValue("Match");
        header.createCell(18).setCellValue("Status");
        header.createCell(19).setCellValue("Other Status");


        int rowNum = 1;
        for (ProvidusBankStatementCredits statement : bankStatements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(statement.getTransactionDate());
            row.createCell(1).setCellValue(statement.getValueDate());
            row.createCell(2).setCellValue(statement.getAccountNumber());
            row.createCell(3).setCellValue(statement.getAccountName());
            row.createCell(4).setCellValue(statement.getDebitAmount());
            row.createCell(5).setCellValue(statement.getCreditAmount());
            row.createCell(6).setCellValue(statement.getCurrentBalance());
            row.createCell(7).setCellValue(statement.getTransactionType());
            row.createCell(8).setCellValue(statement.getRemarks());
            row.createCell(9).setCellValue(statement.getReference());
            row.createCell(10).setCellValue(statement.getSessionId());
            row.createCell(11).setCellValue(statement.getPaymentReference());
            row.createCell(12).setCellValue(statement.getBeneficiaryName());
            row.createCell(13).setCellValue(statement.getAccountTransactionDate());
            row.createCell(14).setCellValue(statement.getUpdTime());
            row.createCell(15).setCellValue(statement.getTransactionSequence1());
            row.createCell(16).setCellValue(statement.getTransactionSequence2());
            row.createCell(17).setCellValue(statement.getMatch());
            row.createCell(18).setCellValue(statement.getStatus());
            row.createCell(19).setCellValue(statement.getOtherStatus());
        }

    }
    private void exportProvidusBankStatementReversals(SXSSFWorkbook workbook) {
        List<ProvidusBankStatementReversals> bankStatements = providusBankStatementReversalRepository.findAll();
        Sheet sheet = workbook.createSheet("Providus Failed and Reversals");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Transaction Date");
        header.createCell(1).setCellValue("Value Date");
        header.createCell(2).setCellValue("Account Number");
        header.createCell(3).setCellValue("Account Name");
        header.createCell(4).setCellValue("Debit Amount");
        header.createCell(5).setCellValue("Credit Amount");
        header.createCell(6).setCellValue("Current Balance");
        header.createCell(7).setCellValue("Transaction Type");
        header.createCell(8).setCellValue("Remarks");
        header.createCell(9).setCellValue("Reference");
        header.createCell(10).setCellValue("Session ID");
        header.createCell(11).setCellValue("Payment Reference");
        header.createCell(12).setCellValue("Beneficiary Name");
        header.createCell(13).setCellValue("Account Transaction Date");
        header.createCell(14).setCellValue("Update Time");
        header.createCell(15).setCellValue("Transaction Sequence 1");
        header.createCell(16).setCellValue("Transaction Sequence 2");
        header.createCell(17).setCellValue("Match");
        header.createCell(18).setCellValue("Status");



        int rowNum = 1;
        for (ProvidusBankStatementReversals statement : bankStatements) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(statement.getTransactionDate());
            row.createCell(1).setCellValue(statement.getValueDate());
            row.createCell(2).setCellValue(statement.getAccountNumber());
            row.createCell(3).setCellValue(statement.getAccountName());
            row.createCell(4).setCellValue(statement.getDebitAmount());
            row.createCell(5).setCellValue(statement.getCreditAmount());
            row.createCell(6).setCellValue(statement.getCurrentBalance());
            row.createCell(7).setCellValue(statement.getTransactionType());
            row.createCell(8).setCellValue(statement.getRemarks());
            row.createCell(9).setCellValue(statement.getReference());
            row.createCell(10).setCellValue(statement.getSessionId());
            row.createCell(11).setCellValue(statement.getPaymentReference());
            row.createCell(12).setCellValue(statement.getBeneficiaryName());
            row.createCell(13).setCellValue(statement.getAccountTransactionDate());
            row.createCell(14).setCellValue(statement.getUpdTime());
            row.createCell(15).setCellValue(statement.getTransactionSequence1());
            row.createCell(16).setCellValue(statement.getTransactionSequence2());
            row.createCell(17).setCellValue(statement.getMatch());
            row.createCell(18).setCellValue(statement.getStatus());
        }
    }
}

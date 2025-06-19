package com.fyp.reconciliation_automation.service;

import com.fyp.reconciliation_automation.entity.CreditFailed;
import com.fyp.reconciliation_automation.entity.CreditSuccessful;
import com.fyp.reconciliation_automation.entity.DebitAdviceFailed;
import com.fyp.reconciliation_automation.entity.DebitAdviceSuccessful;
import com.fyp.reconciliation_automation.entity.DebitFailed;
import com.fyp.reconciliation_automation.entity.DebitSuccessful;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CSVUploadService {

    public static boolean isValidCsvFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "text/csv");
    }

    public static List<CreditFailed> getCreditFailedDataFromCsv(InputStream inputStream) {
        List<CreditFailed> creditFailedList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                StringBuilder combinedRow = new StringBuilder(line);
                String[] values = line.split("\\|");


                while (values.length < 22) {
                    String nextLine = reader.readLine();
                    if (nextLine == null) {
                        break;
                    }
                    combinedRow.append(" ").append(nextLine.trim());
                    values = combinedRow.toString().split("\\|");
                }


                if (values.length < 22) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect number of fields in the CSV file.");
                }

                CreditFailed credit = new CreditFailed();
                try {
                    credit.setTransactionId(values[0].trim());
                    credit.setSessionId(values[1].trim());
                    credit.setStatus(values[2].trim());
                    credit.setMessage(values[3].trim());
                    credit.setNipResponseCode(values[4].trim());
                    credit.setNameEnquiryRef(values[5].trim());
                    credit.setDestinationInstitutionCode(values[6].trim());
                    credit.setChannelCode(values[7].trim());
                    credit.setBeneficiaryAccountName(values[8].trim());
                    credit.setBeneficiaryAccountNumber(values[9].trim());
                    credit.setBeneficiaryBvn(values[10].trim());
                    credit.setBeneficiaryKycLevel(values[11].trim());
                    credit.setOriginatorAccountName(values[12].trim());
                    credit.setOriginatorAccountNumber(values[13].trim());
                    credit.setOriginatorBvn(values[14].trim());
                    credit.setOriginatorKycLevel(values[15].trim());
                    credit.setNarration(values[16].trim());
                    credit.setPaymentReference(values[17].trim());
                    credit.setAmount(values[18].trim());
                    credit.setTransactionLocation(values[19].trim());
                    credit.setInitiatorAccountName("N/A".equalsIgnoreCase(values[20].trim()) ? null : values[20].trim());
                    credit.setInitiatorAccountNumber("N/A".equalsIgnoreCase(values[21].trim()) ? null : values[21].trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                creditFailedList.add(credit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return creditFailedList;
    }

    public static List<CreditSuccessful> getCreditSuccessfulDataFromCsv(InputStream inputStream) {
        List<CreditSuccessful> creditSuccessfulList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                StringBuilder combinedRow = new StringBuilder(line);
                String[] values = line.split("\\|");

                while (values.length < 21) {
                    String nextLine = reader.readLine();
                    if (nextLine == null) {
                        break;
                    }
                    combinedRow.append(" ").append(nextLine.trim());
                    values = combinedRow.toString().split("\\|");
                }

                if (values.length < 21) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect number of fields in the CSV file.");
                }


                CreditSuccessful credit = new CreditSuccessful();
                try {
                    credit.setTransactionId(values[0].trim());
                    credit.setSessionId(values[1].trim());
                    credit.setStatus(values[2].trim());
                    credit.setMessage(values[3].trim());
                    credit.setNipResponseCode(values[4].trim());
                    credit.setNameEnquiryRef(values[5].trim());
                    credit.setDestinationInstitutionCode(values[6].trim());
                    credit.setChannelCode(values[7].trim());
                    credit.setBeneficiaryAccountName(values[8].trim());
                    credit.setBeneficiaryAccountNumber(values[9].trim());
                    credit.setBeneficiaryBvn(values[10].trim());
                    credit.setBeneficiaryKycLevel(values[11].trim());
                    credit.setOriginatorAccountName(values[12].trim());
                    credit.setOriginatorAccountNumber(values[13].trim());
                    credit.setOriginatorBvn(values[14].trim());
                    credit.setOriginatorKycLevel(values[15].trim());
                    credit.setNarration(values[16].trim());
                    credit.setPaymentReference(values[17].trim());
                    credit.setAmount(values[18].trim());
                    credit.setTransactionLocation(values[19].trim());
                    credit.setInitiatorAccountName("N/A".equalsIgnoreCase(values[20].trim()) ? null : values[20].trim());
                    credit.setInitiatorAccountNumber("N/A".equalsIgnoreCase(values[21].trim()) ? null : values[21].trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                creditSuccessfulList.add(credit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return creditSuccessfulList;
    }

    public static List<DebitAdviceFailed> getDebitAdviceFailedDataFromCsv(InputStream inputStream) {
        List<DebitAdviceFailed> debitAdviceFailedList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                StringBuilder combinedRow = new StringBuilder(line);
                String[] values = line.split("\\|");


                while (values.length < 20) {
                    String nextLine = reader.readLine();
                    if (nextLine == null) {
                        break;
                    }
                    combinedRow.append(" ").append(nextLine.trim());
                    values = combinedRow.toString().split("\\|");
                }


                if (values.length < 20) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect number of fields in the CSV file.");
                }


                DebitAdviceFailed debit = new DebitAdviceFailed();
                try {
                    debit.setTransactionId(values[0].trim());
                    debit.setSessionId(values[1].trim());
                    debit.setStatus(values[2].trim());
                    debit.setMessage(values[3].trim());
                    debit.setNipResponseCode(values[4].trim());
                    debit.setNameEnquiryRef(values[5].trim());
                    debit.setDestinationInstitutionCode(values[6].trim());
                    debit.setChannelCode(values[7].trim());
                    debit.setBeneficiaryAccountName(values[8].trim());
                    debit.setBeneficiaryAccountNumber(values[9].trim());
                    debit.setBeneficiaryBvn(values[10].trim());
                    debit.setBeneficiaryKycLevel(values[11].trim());
                    debit.setOriginatorAccountName(values[12].trim());
                    debit.setOriginatorAccountNumber(values[13].trim());
                    debit.setOriginatorBvn(values[14].trim());
                    debit.setOriginatorKycLevel(values[15].trim());
                    debit.setNarration(values[16].trim());
                    debit.setPaymentReference(values[17].trim());
                    debit.setAmount(values[18].trim());
                    debit.setTransactionLocation(values[19].trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                debitAdviceFailedList.add(debit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return debitAdviceFailedList;
    }

    public static List<DebitAdviceSuccessful> getDebitAdviceSuccessfulDataFromCsv(InputStream inputStream) {
        List<DebitAdviceSuccessful> debitAdviceSuccessfulList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                StringBuilder combinedRow = new StringBuilder(line);
                String[] values = line.split("\\|");


                while (values.length < 20) {
                    String nextLine = reader.readLine();
                    if (nextLine == null) {
                        break;
                    }
                    combinedRow.append(" ").append(nextLine.trim());
                    values = combinedRow.toString().split("\\|");
                }


                if (values.length < 20) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect number of fields in the CSV file.");
                }


                DebitAdviceSuccessful debit = new DebitAdviceSuccessful();
                try {
                    debit.setTransactionId(values[0].trim());
                    debit.setSessionId(values[1].trim());
                    debit.setStatus(values[2].trim());
                    debit.setMessage(values[3].trim());
                    debit.setNipResponseCode(values[4].trim());
                    debit.setNameEnquiryRef(values[5].trim());
                    debit.setDestinationInstitutionCode(values[6].trim());
                    debit.setChannelCode(values[7].trim());
                    debit.setBeneficiaryAccountName(values[8].trim());
                    debit.setBeneficiaryAccountNumber(values[9].trim());
                    debit.setBeneficiaryBvn(values[10].trim());
                    debit.setBeneficiaryKycLevel(values[11].trim());
                    debit.setOriginatorAccountName(values[12].trim());
                    debit.setOriginatorAccountNumber(values[13].trim());
                    debit.setOriginatorBvn(values[14].trim());
                    debit.setOriginatorKycLevel(values[15].trim());
                    debit.setNarration(values[16].trim());
                    debit.setPaymentReference(values[17].trim());
                    debit.setAmount(values[18].trim());
                    debit.setTransactionLocation(values[19].trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                debitAdviceSuccessfulList.add(debit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return debitAdviceSuccessfulList;
    }

    public static List<DebitFailed> getDebitFailedDataFromCsv(InputStream inputStream) {
        List<DebitFailed> debitFailedList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                StringBuilder combinedRow = new StringBuilder(line);
                String[] values = line.split("\\|");


                while (values.length < 20) {
                    String nextLine = reader.readLine();
                    if (nextLine == null) {
                        break;
                    }
                    combinedRow.append(" ").append(nextLine.trim());
                    values = combinedRow.toString().split("\\|");
                }


                if (values.length < 20) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect number of fields in the CSV file.");
                }


                DebitFailed debit = new DebitFailed();
                try {
                    debit.setTransactionId(values[0].trim());
                    debit.setSessionId(values[1].trim());
                    debit.setStatus(values[2].trim());
                    debit.setMessage(values[3].trim());
                    debit.setNipResponseCode(values[4].trim());
                    debit.setNameEnquiryRef(values[5].trim());
                    debit.setDestinationInstitutionCode(values[6].trim());
                    debit.setChannelCode(values[7].trim());
                    debit.setBeneficiaryAccountName(values[8].trim());
                    debit.setBeneficiaryAccountNumber(values[9].trim());
                    debit.setBeneficiaryBvn(values[10].trim());
                    debit.setBeneficiaryKycLevel(values[11].trim());
                    debit.setOriginatorAccountName(values[12].trim());
                    debit.setOriginatorAccountNumber(values[13].trim());
                    debit.setOriginatorBvn(values[14].trim());
                    debit.setOriginatorKycLevel(values[15].trim());
                    debit.setNarration(values[16].trim());
                    debit.setPaymentReference(values[17].trim());
                    debit.setAmount(values[18].trim());
                    debit.setTransactionLocation(values[19].trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                debitFailedList.add(debit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return debitFailedList;
    }

    public static List<DebitSuccessful> getDebitSuccessfulDataFromCsv(InputStream inputStream) {
        List<DebitSuccessful> debitSuccessfulList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                StringBuilder combinedRow = new StringBuilder(line);
                String[] values = line.split("\\|");


                while (values.length < 20) {
                    String nextLine = reader.readLine();
                    if (nextLine == null) {
                        break;
                    }
                    combinedRow.append(" ").append(nextLine.trim());
                    values = combinedRow.toString().split("\\|");
                }

                if (values.length < 20) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect number of fields in the CSV file.");
                }


                DebitSuccessful debit = new DebitSuccessful();
                try {
                    debit.setTransactionId(values[0].trim());
                    debit.setSessionId(values[1].trim());
                    debit.setStatus(values[2].trim());
                    debit.setMessage(values[3].trim());
                    debit.setNipResponseCode(values[4].trim());
                    debit.setNameEnquiryRef(values[5].trim());
                    debit.setDestinationInstitutionCode(values[6].trim());
                    debit.setChannelCode(values[7].trim());
                    debit.setBeneficiaryAccountName(values[8].trim());
                    debit.setBeneficiaryAccountNumber(values[9].trim());
                    debit.setBeneficiaryBvn(values[10].trim());
                    debit.setBeneficiaryKycLevel(values[11].trim());
                    debit.setOriginatorAccountName(values[12].trim());
                    debit.setOriginatorAccountNumber(values[13].trim());
                    debit.setOriginatorBvn(values[14].trim());
                    debit.setOriginatorKycLevel(values[15].trim());
                    debit.setNarration(values[16].trim());
                    debit.setPaymentReference(values[17].trim());
                    debit.setAmount(values[18].trim());
                    debit.setTransactionLocation(values[19].trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                debitSuccessfulList.add(debit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return debitSuccessfulList;
    }
}
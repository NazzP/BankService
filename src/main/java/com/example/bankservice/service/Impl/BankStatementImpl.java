package com.example.bankservice.service.Impl;

import com.example.bankservice.model.Transaction;
import com.example.bankservice.model.User;
import com.example.bankservice.repository.TransactionRepository;
import com.example.bankservice.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatementImpl {

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;

    private final static String FILE = "C:\\Users\\Admin\\Documents\\MyStatement.pdf";

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws DocumentException, FileNotFoundException {
        LocalDateTime start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDateTime end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE);

        List<Transaction> transactions = transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isAfter(start))
                .filter(transaction -> transaction.getCreatedAt().isBefore(end))
                .collect(Collectors.toList());

        designStatement(transactions, accountNumber, startDate, endDate);


        return transactions;
    }

    private void designStatement(List<Transaction> transactions, String accountNumber,
                                 String startDate, String endDate) throws FileNotFoundException, DocumentException {
        Rectangle statementSize = new Rectangle(PageSize.A4);

        Document document = new Document(statementSize);
        log.info("setting size of the document");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document,outputStream);
        document.open();

        PdfPTable bankInfoTable = new PdfPTable(1);

        // setting bankName
        PdfPCell bankName = new PdfPCell(new Phrase("The Java Bank"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);

        // setting bankAddress
        PdfPCell bankAddress = new PdfPCell(new Phrase("72, Some address, Kansas"));
        bankAddress.setBorder(0);
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        // statementInfo

        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell startTransactionDate = new PdfPCell(new Phrase("Start Date: " +startDate));
        startTransactionDate.setBorder(0);

        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);

        PdfPCell endTransactionDate = new PdfPCell(new Phrase("End Date: " +endDate));
        endTransactionDate.setBorder(0);

        User user = userRepository.findByAccountNumber(accountNumber);
        String userName = user.getFirstName() + " " +user.getLastName() + " " +user.getMiddleName();

        PdfPCell customerName = new PdfPCell(new Phrase("Customer Name: " +userName));
        customerName.setBorder(0);

        PdfPCell space = new PdfPCell();
        space.setBorder(0);

        PdfPCell address = new PdfPCell(new Phrase("Customer Address: " +user.getAddress()));
        address.setBorder(0);

        statementInfo.addCell(startTransactionDate);
        statementInfo.addCell(statement);
        statementInfo.addCell(endTransactionDate);
        statementInfo.addCell(customerName);
        statementInfo.addCell(space);
        statementInfo.addCell(address);

        // transactionTable

        PdfPTable transactionTable = new PdfPTable(4);

        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);

        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);

        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
        transactionAmount.setBackgroundColor(BaseColor.BLUE);
        transactionAmount.setBorder(0);

        PdfPCell status = new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.BLUE);
        status.setBorder(0);

        transactions.forEach(transaction -> {
            transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType().toString()));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionTable.addCell(new Phrase(transaction.getStatus().toString()));

        });

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(status);

        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionTable);

        document.close();

    }


}

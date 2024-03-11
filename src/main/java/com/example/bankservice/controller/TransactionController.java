package com.example.bankservice.controller;

import com.example.bankservice.model.Transaction;
import com.example.bankservice.service.Impl.BankStatementImpl;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/transactions/generateStatement")
public class TransactionController {

    @Autowired
    BankStatementImpl bankStatement;

    @GetMapping
    public List<Transaction> generateStatement(@RequestParam String accountNumber,
                                               @RequestParam String startDate,
                                               @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        return bankStatement.generateStatement(accountNumber, startDate, endDate);
    }

}

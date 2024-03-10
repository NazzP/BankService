package com.example.bankservice.service;

import com.example.bankservice.dto.TransactionDto;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}

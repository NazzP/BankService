package com.example.bankservice.service.Impl;

import com.example.bankservice.dto.TransactionDto;
import com.example.bankservice.model.Transaction;
import com.example.bankservice.repository.TransactionRepository;
import com.example.bankservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .accountNumber(transactionDto.getAccountNumber())
                .transactionType(transactionDto.getTransactionType())
                .amount(transactionDto.getAmount())
                .status("SUCCESS")
                .build();

        transactionRepository.save(transaction);
    }
}

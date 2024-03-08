package com.example.bankservice.service;

import com.example.bankservice.dto.BankResponse;
import com.example.bankservice.dto.CreditDebitRequest;
import com.example.bankservice.dto.EnquiryRequest;
import com.example.bankservice.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
}

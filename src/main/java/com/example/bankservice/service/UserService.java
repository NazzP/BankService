package com.example.bankservice.service;

import com.example.bankservice.dto.LoginDto;
import com.example.bankservice.dto.request.TransferRequest;
import com.example.bankservice.dto.response.BankResponse;
import com.example.bankservice.dto.request.CreditDebitRequest;
import com.example.bankservice.dto.request.EnquiryRequest;
import com.example.bankservice.dto.request.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
    BankResponse transfer(TransferRequest request);
    BankResponse login(LoginDto loginDto);
}

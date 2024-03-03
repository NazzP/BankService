package com.example.bankservice.service;

import com.example.bankservice.dto.BankResponse;
import com.example.bankservice.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
}

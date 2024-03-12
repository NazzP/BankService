package com.example.bankservice.controller;

import com.example.bankservice.dto.LoginDto;
import com.example.bankservice.dto.request.TransferRequest;
import com.example.bankservice.dto.response.BankResponse;
import com.example.bankservice.dto.request.CreditDebitRequest;
import com.example.bankservice.dto.request.EnquiryRequest;
import com.example.bankservice.dto.request.UserRequest;
import com.example.bankservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    UserService userService;


    @PostMapping("/login")
    public BankResponse createAccount(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }

    @PostMapping("/create")
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }

    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request){
        return userService.balanceEnquiry(request);
    }

    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request){
        return userService.nameEnquiry(request);
    }

    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }

    @PostMapping("/debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }

    @PostMapping("/transfer")
    public BankResponse transfer(@RequestBody TransferRequest request){
        return userService.transfer(request);
    }
}

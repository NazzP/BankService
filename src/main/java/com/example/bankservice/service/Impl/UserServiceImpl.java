package com.example.bankservice.service.Impl;

import com.example.bankservice.config.jwt.JwtTokenProvider;
import com.example.bankservice.dto.AccountInfo;
import com.example.bankservice.dto.EmailDetails;
import com.example.bankservice.dto.LoginDto;
import com.example.bankservice.dto.TransactionDto;
import com.example.bankservice.dto.request.CreditDebitRequest;
import com.example.bankservice.dto.request.EnquiryRequest;
import com.example.bankservice.dto.request.TransferRequest;
import com.example.bankservice.dto.request.UserRequest;
import com.example.bankservice.dto.response.BankResponse;
import com.example.bankservice.model.User;
import com.example.bankservice.repository.UserRepository;
import com.example.bankservice.service.EmailService;
import com.example.bankservice.service.TransactionService;
import com.example.bankservice.service.UserService;
import com.example.bankservice.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

     UserRepository userRepository;
     EmailService emailService;
     TransactionService transactionService;
     PasswordEncoder passwordEncoder;
     AuthenticationManager authenticationManager;
     JwtTokenProvider jwtTokenProvider;


    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .middleName(userRequest.getMiddleName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .role(userRequest.getRole())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congrats, your account was successfully created.\n" +
                        "Your account Details:\n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + "\n" +
                        "Account number: " + savedUser.getAccountNumber())
                .build();

        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .AccountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getMiddleName())
                        .AccountBalance(savedUser.getAccountBalance())
                        .AccountNumber(savedUser.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        if (!userRepository.existsByAccountNumber(request.getAccountNumber())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.USER_FOUND_CODE)
                .responseMessage(AccountUtils.USER_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .AccountBalance(foundUser.getAccountBalance())
                        .AccountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getMiddleName())
                        .AccountNumber(foundUser.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        if (!userRepository.existsByAccountNumber(request.getAccountNumber())) {
            return AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getMiddleName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        if (!userRepository.existsByAccountNumber(request.getAccountNumber())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        TransactionDto transactionDtoCredit = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDtoCredit);


        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESSFULLY_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESSFULLY_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .AccountBalance(userToCredit.getAccountBalance())
                        .AccountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getMiddleName())
                        .AccountNumber(userToCredit.getAccountNumber())
                        .build())
                .build();

    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        if (!userRepository.existsByAccountNumber(request.getAccountNumber())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());

        if (userToDebit.getAccountBalance().compareTo(request.getAmount()) < 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_ACCOUNT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_ACCOUNT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);

            TransactionDto transactionDtoDebit = TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .build();

            transactionService.saveTransaction(transactionDtoDebit);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .AccountBalance(userToDebit.getAccountBalance())
                            .AccountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getMiddleName())
                            .AccountNumber(userToDebit.getAccountNumber())
                            .build())
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        if (!userRepository.existsByAccountNumber(request.getSourceAccountNumber())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        if (!userRepository.existsByAccountNumber(request.getDestinationAccountNumber())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.DESTINATION_ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.DESTINATION_ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sourceUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        User destinationUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());

        if (sourceUser.getAccountBalance().compareTo(request.getAmount()) < 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_ACCOUNT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_ACCOUNT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        sourceUser.setAccountBalance(sourceUser.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceUser);

        TransactionDto transactionDtoTransferDebit = TransactionDto.builder()
                .accountNumber(sourceUser.getAccountNumber())
                .transactionType("TRANSFER_DEBIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDtoTransferDebit);

        destinationUser.setAccountBalance(destinationUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationUser);

        TransactionDto transactionDtoTransferCredit = TransactionDto.builder()
                .accountNumber(destinationUser.getAccountNumber())
                .transactionType("TRANSFER_CREDIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDtoTransferCredit);

        // Email alert that money was transferred to both accounts
        EmailDetails debitAlert = EmailDetails.builder()
                .recipient(sourceUser.getEmail())
                .subject("Transfer Debit Alert")
                .messageBody("The sum of " + request.getAmount() + " has been deducted from your account\n" +
                        "You have transferred it to: " + destinationUser.getFirstName() + " " + destinationUser.getLastName() + "\n" +
                        "Your current balance: " + sourceUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(debitAlert);

        EmailDetails creditAlert = EmailDetails.builder()
                .recipient(destinationUser.getEmail())
                .subject("Transfer Credit Alert")
                .messageBody("The sum of " + request.getAmount() + " has been added to your account\n" +
                        "You have got it from: " + sourceUser.getFirstName() + " " + sourceUser.getLastName() + "\n" +
                        "Your current balance: " + destinationUser.getAccountBalance())
                .build();

        emailService.sendEmailAlert(creditAlert);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();

    }

    @Override
    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        EmailDetails loginAlert = EmailDetails.builder()
                .subject("LOGIN ALERT")
                .recipient(loginDto.getEmail())
                .messageBody("You logged into your account\n" +
                        "if you didn't initiate this request, please contact your bank")
                .build();
        emailService.sendEmailAlert(loginAlert);

        return BankResponse.builder()
                .responseCode(AccountUtils.LOGIN_SUCCESSFUL_CODE)
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();
    }

}

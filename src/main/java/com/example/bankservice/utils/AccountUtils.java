package com.example.bankservice.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "Account with this email already exists";

    public static final String ACCOUNT_CREATION_SUCCESS_CODE = "002";
    public static final String ACCOUNT_CREATION_SUCCESS_MESSAGE = "Account was successfully created";

    public static final String ACCOUNT_NOT_EXISTS_CODE = "003";
    public static final String ACCOUNT_NOT_EXISTS_MESSAGE = "Account with provided number doesn't exist";

    public static final String USER_FOUND_CODE = "004";
    public static final String USER_FOUND_MESSAGE = "User account found";

    public static final String ACCOUNT_CREDITED_SUCCESSFULLY_CODE = "005";
    public static final String ACCOUNT_CREDITED_SUCCESSFULLY_MESSAGE = "Account was successfully credited";

    public static String generateAccountNumber(){
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        int randNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);
        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);

        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(year).append(randomNumber).toString();
    }
}

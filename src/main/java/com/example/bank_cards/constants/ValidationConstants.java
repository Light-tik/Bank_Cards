package com.example.bank_cards.constants;

public interface ValidationConstants {

    String USER_EMAIL_NOT_NULL = "User email is null. Please enter your email address";

    String USER_EMAIL_NOT_VALID = "User email is not valid. Please enter a valid email address";

    String EMAIL_SIZE_NOT_VALID = "Email size is not valid. Please enter a valid email address";

    String USERNAME_SIZE_NOT_VALID = "Username size is not valid. Please enter a valid username";

    String USER_NAME_NOT_NULL = "User name is null. Please enter a your name";

    String PASSWORD_NOT_NULL = "Password is null. Please enter a your password";

    String USER_NOT_FOUND = "User not found. Please try again";

    String USER_ALREADY_EXIST = "User is already exist. Please try again";

    String PASSWORD_NOT_VALID = "Incorrect password. Please enter a valid password";

    String CARD_NOT_FOUND = "Card not found. Please try again";

    String UNAUTHORISED = "Unauthorised. Please log in";

    String NO_RIGHTS = "Not enough rights to delete or update the card";

    String HTTP_MESSAGE_NOT_READABLE_EXCEPTION =  "Http request not valid" ;

    String INVALID_AMOUNT = "Invalid amount";

    String LIMIT_EXCEEDED = "Limit exceeded";

    String INSUFFICIENT_FUNDS = "Insufficient funds";

    String LIMIT_NOT_SET = "Limit not set";
}

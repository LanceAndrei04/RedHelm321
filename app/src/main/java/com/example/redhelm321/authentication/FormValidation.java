package com.example.redhelm321.authentication;


import android.text.TextUtils;

import java.util.Set;

public class FormValidation {

    public enum WarningMessage {
        INVALID_USERNAME("Invalid username"),
        INVALID_PHONE_NUMBER("Invalid phone number"),
        INVALID_PROVINCE("Invalid province"),
        INVALID_STATE("Invalid state"),
        INVALID_GENDER("Invalid gender"),
        INVALID_BIRTH_DATE("Invalid birth date"),
        INVALID_BIRTH_TIME("Invalid birth time"),
        INVALID_INTERESTS("Invalid interests"),
        USER_ALREADY_LOGGED_IN_WARNING("You are already logged in!"),
        USER_NOT_LOGGED_IN_WARNING("Please log in first."),
        INPUT_NULL_WARNING("Please fill up required fields."),
        INVALID_EMAIL_WARNING("Invalid email, please try another one."),
        INVALID_PASSWORD_WARNING("Password must contain 8 or more characters."),
        INVALID_CONFIRM_PASSWORD("Passwords do not match, try again");


        private final String message;

        WarningMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public enum FormValidationResult {
        INPUT_NULL,
        INVALID_EMAIL,
        INVALID_CONFIRM_PASSWORD,
        INVALID_PASSWORD,
        FORM_VALID,
        INVALID_USERNAME,
        INVALID_PHONE_NUMBER,
        INVALID_PROVINCE,
        INVALID_STATE,
        INVALID_GENDER,
        INVALID_BIRTH_DATE,
        INVALID_BIRTH_TIME,
        INVALID_INTERESTS,
    }


    private static final String emailPatternRegEx = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public static boolean isValidEmail(String email) {
        return email != null && email.matches(emailPatternRegEx);
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public static FormValidationResult isRegisterFormValid(String email, String password, String confirm_password) {

        if(email == null || password == null || confirm_password == null) return FormValidationResult.INPUT_NULL;

        if(!isValidEmail(email)) return FormValidationResult.INVALID_EMAIL;

        if(!isValidPassword(password)) return FormValidationResult.INVALID_PASSWORD;

        if(!password.equals(confirm_password)) return FormValidationResult.INVALID_CONFIRM_PASSWORD;

        return FormValidationResult.FORM_VALID;
    }

    public static FormValidationResult isDetailsFormValid(String username, String phoneNumber, String Province, String gender, String birthDate, String birthTime, String interests, Set<String> provinces) {
        if(TextUtils.isEmpty(username) || username.length() < 3) return FormValidationResult.INVALID_USERNAME;

        if(TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 11) return FormValidationResult.INVALID_PHONE_NUMBER;

        if(TextUtils.isEmpty(Province) || !provinces.contains(Province)) return FormValidationResult.INVALID_PROVINCE;

        if(TextUtils.isEmpty(gender)) return FormValidationResult.INVALID_GENDER;

        if(TextUtils.isEmpty(birthDate)) return FormValidationResult.INVALID_BIRTH_DATE;

        if(TextUtils.isEmpty(birthDate)) return FormValidationResult.INVALID_BIRTH_TIME;

        if(TextUtils.isEmpty(interests)) return FormValidationResult.INVALID_INTERESTS;

        return FormValidationResult.FORM_VALID;
    }

    public static FormValidationResult isLoginFormValid(String email, String password) {

        if(email == null || password == null) return FormValidationResult.INPUT_NULL;

        if(!isValidEmail(email)) return FormValidationResult.INVALID_EMAIL;

        if(!isValidPassword(password)) return FormValidationResult.INVALID_PASSWORD;

        return FormValidationResult.FORM_VALID;
    }
}

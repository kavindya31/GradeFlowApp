package com.example.mobileapp.utils;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Utility class for validating user input fields across the application,
 * including registration, login, and academic profile edits.
 */
public class ValidationHelper {

    /**
     * Validates full name input.
     * @param fullName The name string to validate.
     * @return true if valid, false otherwise.
     */
    public static boolean validateFullName(String fullName) {
        return !TextUtils.isEmpty(fullName) && fullName.trim().length() >= 2;
    }

    /**
     * Validates university input.
     * @param university The university string to validate.
     * @return true if valid, false otherwise.
     */
    public static boolean validateUniversity(String university) {
        return !TextUtils.isEmpty(university) && university.trim().length() >= 3;
    }

    /**
     * Validates degree input.
     * @param degree The degree string to validate.
     * @return true if valid, false otherwise.
     */
    public static boolean validateDegree(String degree) {
        return !TextUtils.isEmpty(degree) && degree.trim().length() >= 2;
    }

    /**
     * Validates email input format.
     * @param email The email string to validate.
     * @return true if valid email format, false otherwise.
     */
    public static boolean validateEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validates password strength (minimum 6 characters).
     * @param password The password string to validate.
     * @return true if valid, false otherwise.
     */
    public static boolean validatePassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    /**
     * Validates class credits input.
     * Credits must be a positive integer or float (e.g. 1 to 5).
     * @param creditsStr The credit input string.
     * @return true if valid, false otherwise.
     */
    public static boolean validateCredits(String creditsStr) {
        if (TextUtils.isEmpty(creditsStr)) return false;
        try {
            double val = Double.parseDouble(creditsStr);
            return val > 0 && val <= 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates class grades input.
     * Must be a valid letter grade (e.g. A, A-, B+, B, B-, C+, C, C-, D, F) or percentage.
     * @param gradeStr The grade input string.
     * @return true if valid, false otherwise.
     */
    public static boolean validateGrade(String gradeStr) {
        if (TextUtils.isEmpty(gradeStr)) return false;
        String cleanGrade = gradeStr.trim().toUpperCase();
        
        // Check letter grades
        if (cleanGrade.matches("^(A\\+?|A-|B\\+?|B-|C\\+?|C-|D\\+?|F)$")) {
            return true;
        }
        
        // Or check numeric percentage (0 to 100)
        try {
            double val = Double.parseDouble(cleanGrade);
            return val >= 0 && val <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

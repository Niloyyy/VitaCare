package com.example.vitacare_app_250;

import android.util.Patterns;
import android.widget.EditText;

import java.util.regex.Pattern;

public class ValidationUtils {

    // Password must contain at least 8 characters, including at least one number and one special character.
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         // at least 1 digit
                    "(?=.*[!@#$%^&+=*])" +  // at least 1 special character
                    "(?=\\S+$)" +           // no white spaces
                    ".{8,}" +               // at least 8 characters
                    "$");

    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && Patterns.PHONE.matcher(phone).matches() && phone.length() >= 10;
    }

    public static boolean isEmpty(EditText editText) {
        return editText == null || editText.getText().toString().trim().isEmpty();
    }
}

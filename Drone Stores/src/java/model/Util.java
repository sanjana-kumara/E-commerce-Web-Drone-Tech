package model;

public class Util {

    public static String genrateCode() {
        int r = (int) (Math.random() * 1000000);
        return String.format("%06d", r);
    }

    public static boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    }

    public static boolean isPasswordValid(String password) {
        return password.matches("^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$");
    }

    public static boolean isCodeValid(String code) { // Postal Code Validation
        return code.matches("^\\d{4,5}$");
    }

    // Mobile Number Validation (Sri Lankan format: 0771234567 or +94771234567)
    public static boolean isMobileValid(String mobile) {
        return mobile.matches("^(?:\\+94|0)?7[0-9]{8}$");
    }

    public static boolean isInteger(String value) {

        return value.matches("^\\d+$");

    }

    public static boolean isDouble(String value) {

        return value.matches("^\\d+(\\.\\d{2})?$");

    }

    public static boolean hasEmpty(String... values) {
    for (String v : values) {
        if (v == null || v.trim().isEmpty()) return true;
    }
    return false;
}

    
}

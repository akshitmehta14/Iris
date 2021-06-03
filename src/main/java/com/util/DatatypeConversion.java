package com.util;

import java.time.LocalDate;

public class DatatypeConversion {
    public static double convertStringToDouble(String number){
        return Double.parseDouble(number);
    }

    public static int convertStringToInteger(String number){
        return Integer.parseInt(number);
    }

    public static String convertDoubleToString(double number){
        return String.valueOf(number);
    }

    public static String convertDateToString(LocalDate date){
        return date.toString();
    }
}

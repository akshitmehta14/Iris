package com.util;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class DateUtil {
    public static int differenceInDays(LocalDate start, LocalDate end){
        return (int) DAYS.between(start,end);
    }
}

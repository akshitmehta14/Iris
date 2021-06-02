package com.util;

public class NumberUtil {
    public static boolean negative(int x) {
        if(x<0) {
            return true;
        }
        return false;
    }
    public static boolean negative(double x) {
        if(x<0) {
            return true;
        }
        return false;
    }

    public static boolean greaterThan100(int x){
        if(x>100){
            return true;
        }
        return false;
    }

    public static boolean greaterThan100(double x){
        if(x>100){
            return true;
        }
        return false;
    }

}

package com.pt.network.utils;

/**
 * @author: yorkzhang
 * @time: 16/7/21 11:13
 * @email: xtcqw13@126.com
 * @note:
 */
public class Util {

    private static final boolean DEBUG = false;

    public static int convertToInt(String intStr, int defValue) {
        try {
            return Integer.parseInt(intStr);
        } catch (Exception e) {
        }
        return defValue;
    }

    public static boolean isTestEnvironment() {
        return DEBUG;
    }
}

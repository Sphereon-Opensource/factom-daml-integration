package com.sphereon.da.ledger.mithra.utils.fatd.util;

public class StringUtils {
    private StringUtils() {
    }

    public static boolean isNullOrEmpty(final String input) {
        return input == null || "".equals(input);
    }
}

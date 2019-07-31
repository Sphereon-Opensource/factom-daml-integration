package com.sphereon.da.ledger.mithra.utils;

import org.apache.commons.codec.binary.Hex;

public class TransactionUtils {
    public static String createTransactionHex(String from, String to, long amount){
        //TODO: fix this
        String tx = String.format("{\"inputs\":{\"%s\":%d},\"outputs\":{\"%s\":%d}}",
                from, amount, to, amount);
        return Hex.encodeHexString(tx.getBytes());
    }
}

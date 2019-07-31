package com.sphereon.da.ledger.mithra.utils.fatd.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(final String message) {
        super(message);
    }
}

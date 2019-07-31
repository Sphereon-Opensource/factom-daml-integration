package com.sphereon.da.ledger.mithra.utils.fatd.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(final String message) {
        super(message);
    }
}

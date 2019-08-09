package com.sphereon.da.ledger.mithra.utils.fatd.exception;

public class InvalidTransactionException extends IllegalArgumentException {
    public InvalidTransactionException(final String message) {
        super(message);
    }

    public InvalidTransactionException(final String message, final Exception cause) {
        super(message, cause);
    }
}

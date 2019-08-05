package com.sphereon.da.ledger.mithra.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sphereon.da.ledger.mithra.dto.FatTransaction;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final ObjectMapper mapper;

    public TransactionService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String createTransactionHex(String from, String to, long amount) {
        final FatTransaction tx = new FatTransaction(from, to, amount);
        final String txString = txStringFromTxObj(tx);
        return Hex.encodeHexString(txString.getBytes());
    }

    private String txStringFromTxObj(FatTransaction tx) throws RuntimeException {
        try {
            return mapper.writeValueAsString(tx);
        } catch (JsonProcessingException e) {
            final String transactionInfo = String.format("inputs: %s, outputs: %s", tx.getInputs().toString(), tx.getOutputs().toString());
            throw new RuntimeException(String.format("Could not map transaction to string. Tx: %s", transactionInfo));
        }
    }
}

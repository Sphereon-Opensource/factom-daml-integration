package com.sphereon.da.ledger.mithra.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sphereon.da.ledger.mithra.dto.FatTransaction;
import com.sphereon.da.ledger.mithra.utils.fatd.FactomTransaction;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final ObjectMapper mapper;

    public TransactionService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String createTransactionHex(String from, String to, long amount) {
        FatTransaction tx = new FatTransaction(from, to, amount);
        String tx_string = txStringFromTxObj(tx);
        return Hex.encodeHexString(tx_string.getBytes());
    }

    private String txStringFromTxObj(FatTransaction tx) throws RuntimeException {
        try {
            String tx_string = mapper.writeValueAsString(tx);
            return tx_string;
        } catch (JsonProcessingException e) {
            String transactionInfo = String.format("inputs: %s, outputs: %s", tx.getInputs().toString(),
                    tx.getOutputs().toString());
            throw new RuntimeException(String.format("Could not map transaction to string. Tx: %s", transactionInfo));

        }
    }
}

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

    public String createTransactionHex(String from, String to, long amount) throws JsonProcessingException {
        FatTransaction tx = new FatTransaction(from, to, amount);
        String tx_string = mapper.writeValueAsString(tx);
        return Hex.encodeHexString(tx_string.getBytes());
    }
}

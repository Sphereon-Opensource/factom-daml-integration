package com.sphereon.da.ledger.mithra.dto;

import java.util.Map;

import static java.util.Collections.singletonMap;

public class FatTransaction {
    private final Map<String, Long> inputs;
    private final Map<String, Long> outputs;

    public FatTransaction(String from, String to, long amount) {
        this.inputs = singletonMap(from, amount);
        this.outputs = singletonMap(to, amount);
    }

    public Map<String, Long> getOutputs() {
        return outputs;
    }

    public Map<String, Long> getInputs() {
        return inputs;
    }
}

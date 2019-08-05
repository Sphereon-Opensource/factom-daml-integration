package com.sphereon.da.ledger.mithra.dto;

import java.util.HashMap;
import java.util.Map;

public class FatTransaction {
    private final Map<String, Long> inputs;
    private final Map<String, Long> outputs;

    public FatTransaction(String from, String to, long amount) {
        this.inputs = new HashMap<String, Long>() {{
            put(from, amount);
        }};
        this.outputs = new HashMap<String, Long>() {{
            put(to, amount);
        }};
    }

    public Map<String, Long> getOutputs() {
        return outputs;
    }

    public Map<String, Long> getInputs() {
        return inputs;
    }
}

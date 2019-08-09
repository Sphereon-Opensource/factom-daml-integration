package com.sphereon.da.ledger.mithra.utils.fatd.dto;

import javax.xml.bind.annotation.XmlElement;
import java.util.Map;

public class TransactionData {
    @XmlElement
    private Map<String, Long> inputs;

    @XmlElement
    private Map<String, Long> outputs;

    public TransactionData() {
    }

    public TransactionData(final Map<String, Long> inputs,
                           final Map<String, Long> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public Map<String, Long> getInputs() {
        return inputs;
    }

    public Map<String, Long> getOutputs() {
        return outputs;
    }
}

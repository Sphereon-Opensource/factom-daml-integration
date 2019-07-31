package com.sphereon.da.ledger.mithra.utils.fatd.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;

public class Input {
    @XmlElement
    private String address;

    @XmlElement
    private long amount;

    public Input() {
    }

    @JsonCreator
    public Input(@JsonProperty("address") final String address, @JsonProperty("amount") final long amount) {
        this.address = address;
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public long getAmount() {
        return amount;
    }
}

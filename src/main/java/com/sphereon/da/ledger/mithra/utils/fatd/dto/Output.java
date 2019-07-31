package com.sphereon.da.ledger.mithra.utils.fatd.dto;

import javax.xml.bind.annotation.XmlElement;

public class Output {
    @XmlElement
    private String address;

    @XmlElement
    private long amount;

    public Output() {
    }

    public Output(final String address, final long amount) {
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

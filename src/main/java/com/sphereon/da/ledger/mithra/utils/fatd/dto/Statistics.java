package com.sphereon.da.ledger.mithra.utils.fatd.dto;

import javax.xml.bind.annotation.XmlElement;

public class Statistics {
    @XmlElement
    private long supply;

    @XmlElement
    private long circulatingSupply;

    @XmlElement
    private long transactions;

    @XmlElement
    private long issuanceTimestamp;

    @XmlElement
    private long lastTransactionTimestamp;

    public Statistics() {
    }

    public Statistics(final long supply, final long circulatingSupply, final long transactions, final long issuanceTimestamp, final long lastTransactionTimestamp) {
        this.supply = supply;
        this.circulatingSupply = circulatingSupply;
        this.transactions = transactions;

        this.issuanceTimestamp = issuanceTimestamp;
        this.lastTransactionTimestamp = lastTransactionTimestamp;
    }

    public long getSupply() {
        return supply;
    }

    public long getCirculatingSupply() {
        return circulatingSupply;
    }

    public long getTransactions() {
        return transactions;
    }

    public long getIssuanceTimestamp() {
        return issuanceTimestamp;
    }

    public long getLastTransactionTimestamp() {
        return lastTransactionTimestamp;
    }
}

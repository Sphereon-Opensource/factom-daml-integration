package com.sphereon.da.ledger.mithra.utils.fatd.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sphereon.da.ledger.mithra.utils.fatd.FactomTransaction;

import javax.xml.bind.annotation.XmlElement;

public class FactomTransactionRpc implements FactomTransaction {
    @XmlElement
    private String txid;

    @XmlElement
    private String entryhash;

    @XmlElement
    private String chainid;

    public FactomTransactionRpc() {
    }

    @JsonCreator
    public FactomTransactionRpc(@JsonProperty("txid") final String txid, @JsonProperty("entryhash") final String entryhash,
                                @JsonProperty("chainid") final String chainid) {
        this.txid = txid;
        this.entryhash = entryhash;
        this.chainid = chainid;
    }

    public String getTxId() {
        return txid;
    }

    @Override
    public String getEntryHash() {
        return entryhash;
    }
}

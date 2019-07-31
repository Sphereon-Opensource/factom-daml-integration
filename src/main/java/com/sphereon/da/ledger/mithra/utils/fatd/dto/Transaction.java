package com.sphereon.da.ledger.mithra.utils.fatd.dto;

import javax.xml.bind.annotation.XmlElement;
import java.time.Instant;

public class Transaction implements Comparable<Transaction>{
    @XmlElement
    private String entryhash;

    @XmlElement
    private TransactionData data;

    @XmlElement
    private long blockheight;

    @XmlElement
    private Long timestamp;

    public Transaction() {
    }

    public Transaction(final String entryhash,
                       final TransactionData data,
                       final long blockheight,
                       final Instant timestamp) {
        this.entryhash = entryhash;
        this.data = data;
        this.blockheight = blockheight;
        this.timestamp = timestamp.toEpochMilli();
    }

    public String getEntryHash() {
        return entryhash;
    }

    public TransactionData getData() {
        return data;
    }

    public long getBlockHeight() {
        return blockheight;
    }

    public long getTimestamp() {
        return timestamp * 1000;
    }

    @Override
    public int compareTo(Transaction transaction) {
        Long compareTimestamp = transaction.getTimestamp();
        return compareTimestamp.compareTo(timestamp);
    }
}

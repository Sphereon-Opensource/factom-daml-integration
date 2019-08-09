package com.sphereon.da.ledger.mithra.utils.fatd.dto;

import javax.xml.bind.annotation.XmlElement;

public class Issuance {
    @XmlElement
    private String entryhash;

    @XmlElement
    private String type;

    @XmlElement
    private String issuer;

    @XmlElement
    private long supply;

    @XmlElement
    private String name;

    @XmlElement
    private String symbol;

    @XmlElement
    private String salt;

    @XmlElement
    private long timestamp;

    public Issuance() {
    }

    public Issuance(final String entryhash,
                    final String type,
                    final String issuer,
                    final long supply,
                    final String name,
                    final String symbol,
                    final String salt,
                    final long timestamp) {
        this.entryhash = entryhash;
        this.type = type;
        this.issuer = issuer;
        this.supply = supply;
        this.name = name;
        this.symbol = symbol;
        this.salt = salt;
        this.timestamp = timestamp;
    }

    public String getEntryhash() {
        return entryhash;
    }

    public String getType() {
        return type;
    }

    public String getIssuer() {
        return issuer;
    }

    public long getSupply() {
        return supply;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSalt() {
        return salt;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

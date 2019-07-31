package com.sphereon.da.ledger.mithra.utils.fatd.dto;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

public class ExternalId implements Serializable {
    @XmlElement
    private final String externalId;

    public ExternalId(final String externalId) {
        this.externalId = externalId;
    }

    public String getExternalId() {
        return externalId;
    }
}

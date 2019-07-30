package com.sphereon.da.ledger.mithra.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class FatToken {
    private final String tokenId;
    private final String tokenChainId;
    private final String issuerRootChainId;
    private final String coinbaseAddressPublic;

    //Store private key hardcoded for demo purposes (Factom testnet). Implement a keyvault for production
    private final String identityLevel1SecretAddress;

    public FatToken(@JsonProperty("tokenId") final String tokenId,
                    @JsonProperty("tokenChainId") final String tokenChainId,
                    @JsonProperty("issuerRootChainId") final String issuerRootChainId,
                    @JsonProperty("coinbaseAddressPublic") final String coinbaseAddressPublic,
                    @JsonProperty("identityLevel1SecretAddress") final String identityLevel1SecretAddress) {
        this.tokenId = tokenId;
        this.tokenChainId = tokenChainId;
        this.issuerRootChainId = issuerRootChainId;
        this.coinbaseAddressPublic = coinbaseAddressPublic;
        this.identityLevel1SecretAddress = identityLevel1SecretAddress;
    }

    public String getTokenId() {
        return tokenId;
    }

    public String getTokenChainId() {
        return tokenChainId;
    }

    public String getIssuerRootChainId() {
        return issuerRootChainId;
    }

    public String getIdentityLevel1SecretAddress() {
        return identityLevel1SecretAddress;
    }

    public String getCoinbaseAddressPublic() {
        return coinbaseAddressPublic;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("tokenId", tokenId)
                .add("tokenChainId", tokenChainId)
                .add("issuerRootChainId", issuerRootChainId)
                .add("coinbaseAddressPublic", coinbaseAddressPublic)
                .toString();
    }
}



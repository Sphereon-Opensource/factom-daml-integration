package com.sphereon.da.ledger.mithra.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FatToken {

    private final String tokenId;
    private final String tokenChainId;
    private final String issuerRootChainId;
    private final String coinbaseAddressPublic;

    //Store private key hardcoded for demo purposes (Factom testnet). Implement a keyvault for production
    private final String identityLevel1SecretAddress;

    public FatToken(@JsonProperty("tokenId") String tokenId, @JsonProperty("tokenChainId") String tokenChainId,
                    @JsonProperty("issuerRootChainId") String issuerRootChainId, @JsonProperty("coinbasePublicAddress") String coinbaseAddressPublic,
                    @JsonProperty("identityLevel1SecretAddress") String identityLevel1SecretAddress){
        this.tokenId = tokenId;
        this.tokenChainId = tokenChainId;
        this.issuerRootChainId = issuerRootChainId;
        this.coinbaseAddressPublic = coinbaseAddressPublic;
        this.identityLevel1SecretAddress = identityLevel1SecretAddress;
    }

    public String getTokenId(){return tokenId;}
    public String getTokenChainId(){return tokenChainId;}
    public String getIssuerRootChainId(){return issuerRootChainId;}
    public String getIdentityLevel1SecretAddress(){return identityLevel1SecretAddress;}

    public String getCoinbaseAddressPublic() {return coinbaseAddressPublic;}
}



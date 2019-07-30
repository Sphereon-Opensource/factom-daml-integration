package com.sphereon.da.ledger.mithra.utils;

import org.springframework.context.annotation.Bean;
import java.util.Arrays;
import java.util.List;

public class TokenUtils {

    @Bean
    public List<FatToken> fatTokenList(){
        return Arrays.asList(new FatToken("tokenId1", "tokenChainId1",
                "issuerRootChainId1","coinbaseAddress1","sk11"),
                new FatToken("tokenId2", "tokenChainId2",
                        "issuerRootChainId2","coinbaseAddress2","sk12"));
    }
}

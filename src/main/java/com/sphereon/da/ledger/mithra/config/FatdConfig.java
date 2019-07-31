package com.sphereon.da.ledger.mithra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sphereon.da.ledger.mithra.utils.fatd.FatdRpc;
import com.sphereon.da.ledger.mithra.utils.fatd.rpc.RpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.net.URL;

@Configuration
public class FatdConfig {
    @Bean
    @Primary
    public FatdRpc fatdRpc(final RpcClient rpcClient, @Value("${mithra.fatd.endpoint}") final URL url) {
        return new FatdRpc(rpcClient, url);
    }

    @Bean
    public RpcClient rpcClient(@Value("${rpc.timeout}") final int timeout, final ObjectMapper objectMapper) {
        return new RpcClient(timeout, objectMapper);
    }
}

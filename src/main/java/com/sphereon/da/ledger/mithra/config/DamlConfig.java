package com.sphereon.da.ledger.mithra.config;

import com.daml.ledger.rxjava.DamlLedgerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class DamlConfig {
    @Bean
    public DamlLedgerClient damlLedgerClient(@Value("${mithra.ledgerHost}") String ledgerHost, @Value("${mithra.ledgerPort}") int ledgerPort) {
        return DamlLedgerClient.forHostWithLedgerIdDiscovery(ledgerHost, ledgerPort, Optional.empty());
    }
}

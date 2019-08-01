package com.sphereon.da.ledger.mithra.services;

import com.daml.ledger.rxjava.DamlLedgerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
public class DamlLedgerService {
    private final String ledgerHost;
    private final int ledgerPort;
    private final DamlLedgerClient damlLedgerClient;

    public DamlLedgerService(@Value("${mithra.ledgerHost}") String ledgerHost, @Value("${mithra.ledgerPort}") int ledgerPort){
        this.ledgerHost = ledgerHost;
        this.ledgerPort = ledgerPort;
        this.damlLedgerClient = DamlLedgerClient.forHostWithLedgerIdDiscovery(this.ledgerHost, this.ledgerPort, Optional.empty());
    }

    @PostConstruct
    public void init(){
        damlLedgerClient.connect();
    }

    public String getLedgerId(){
        return damlLedgerClient.getLedgerId();
    }

    public DamlLedgerClient getDamlLedgerClient() {
        return damlLedgerClient;
    }
}

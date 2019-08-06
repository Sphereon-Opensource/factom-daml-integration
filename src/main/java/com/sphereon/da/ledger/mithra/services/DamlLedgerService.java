package com.sphereon.da.ledger.mithra.services;

import com.daml.ledger.rxjava.DamlLedgerClient;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DamlLedgerService {
    private final DamlLedgerClient damlLedgerClient;

    public DamlLedgerService(final DamlLedgerClient damlLedgerClient) {
        this.damlLedgerClient = damlLedgerClient;
    }

    @PostConstruct
    public void init() {
        damlLedgerClient.connect();
    }

    public String getLedgerId() {
        return damlLedgerClient.getLedgerId();
    }

    public DamlLedgerClient getDamlLedgerClient() {
        return damlLedgerClient;
    }
}

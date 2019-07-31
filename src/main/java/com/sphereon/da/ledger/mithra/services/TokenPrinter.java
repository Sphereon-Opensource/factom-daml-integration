package com.sphereon.da.ledger.mithra.services;

import com.sphereon.da.ledger.mithra.services.TokenService;
import com.sphereon.da.ledger.mithra.utils.FatToken;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TokenPrinter implements CommandLineRunner {
    private final TokenService tokenService;

    public TokenPrinter(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void run(String... args) {
        tokenService.getTokens().stream()
                .map(FatToken::toString)
                .forEach(System.out::println);
    }
}

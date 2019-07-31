package com.sphereon.da.ledger.mithra.bots;

import com.daml.ledger.javaapi.data.Command;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Record;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import com.sphereon.da.ledger.mithra.services.DamlLedgerService;
import com.sphereon.da.ledger.mithra.services.TokenService;
import com.sphereon.da.ledger.mithra.utils.FatToken;
import com.sphereon.da.ledger.mithra.utils.SigningUtils;
import io.reactivex.Flowable;
import com.sphereon.da.ledger.mithra.model.fat.transfer.UnsignedTransferTransaction;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Profile("client")
public class SignBot extends AbstractBot {

    private final String secretAddress;
    private final List<FatToken> tokens;
    private final static Logger log = LoggerFactory.getLogger(SignBot.class);

    public SignBot(@Value("mithra-${spring.profiles.active}") String appId,
                   DamlLedgerService damlLedgerService,
                   @Value("${mithra.party}") String party,
                   @Value("${mithra.token.secretAddress}") String secretAddress,
                   TokenService tokenService) {
        super.appId = appId;
        super.ledgerId = damlLedgerService.getLedgerId();
        super.party = party;
        this.secretAddress = secretAddress;
        this.tokens = tokenService.getTokens();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Flowable<CommandsAndPendingSet> process(LedgerViewFlowable.LedgerView<Record> ledgerView) {
        List<UnsignedTransferTransaction.Contract> unsignedTransferTransactions =
                (List<UnsignedTransferTransaction.Contract>)(List<?>)
                        getContracts(ledgerView, UnsignedTransferTransaction.TEMPLATE_ID);

        if (unsignedTransferTransactions.isEmpty()) {
            return Flowable.empty();
        }

        log.info(String.format("Got %d Transactions to Sign", unsignedTransferTransactions.size()));

        Map<Identifier, Set<String>> pending = new HashMap<>();
        pending.putIfAbsent(UnsignedTransferTransaction.TEMPLATE_ID, new HashSet<>());

        List<Command> commandList = unsignedTransferTransactions.stream().map(contract -> {
            try {
                FatToken token = tokens.stream().filter(o -> o.getTokenId().equals(contract.data.tokenId))
                        .findFirst().orElseThrow(()-> new IllegalArgumentException("Unknown token ID"));
                String tx_hex = contract.data.txToSign;
                String tx = new String(Hex.decodeHex(tx_hex.toCharArray()));
                List<String> exIds = SigningUtils.generateExIds(tx, token.getTokenChainId(), secretAddress);
                pending.get(UnsignedTransferTransaction.TEMPLATE_ID).add(contract.id.contractId);
                return contract.id.exerciseUnsignedTransferTransaction_Sign(tx_hex, exIds);
            } catch (Exception e) {
                //TODO: fix exception handling
                throw new NullPointerException();
            }
        }).collect(Collectors.toList());

        if (!commandList.isEmpty()) {
            return toCommandsAndPendingSet(commandList, pending);
        } else {
            return Flowable.empty();
        }
    }
}

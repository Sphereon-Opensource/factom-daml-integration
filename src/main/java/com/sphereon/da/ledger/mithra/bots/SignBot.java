package com.sphereon.da.ledger.mithra.bots;

import com.daml.ledger.javaapi.data.Command;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Record;
import com.daml.ledger.javaapi.data.TransactionFilter;
import com.daml.ledger.rxjava.components.Bot;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import com.google.common.collect.Sets;
import com.sphereon.da.ledger.mithra.dto.FatToken;
import com.sphereon.da.ledger.mithra.model.fat.transfer.UnsignedTransferTransaction;
import com.sphereon.da.ledger.mithra.services.DamlLedgerService;
import com.sphereon.da.ledger.mithra.services.TokenService;
import com.sphereon.da.ledger.mithra.utils.LedgerUtils;
import com.sphereon.da.ledger.mithra.utils.SigningUtils;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@Component
@Profile("client")
public class SignBot extends AbstractBot {

    private final String secretAddress;
    private final List<FatToken> tokens;
    private final static Logger log = LoggerFactory.getLogger(SignBot.class);
    private final SigningUtils signingUtils;

    public SignBot(@Value("mithra-${spring.profiles.active}") String appId,
                   DamlLedgerService damlLedgerService,
                   @Value("${mithra.party}") String party,
                   @Value("${mithra.token.secretAddress}") String secretAddress,
                   TokenService tokenService,
                   SigningUtils signingUtils) {
        super.appId = appId;
        super.ledgerId = damlLedgerService.getLedgerId();
        super.party = party;
        super.ledgerClient = damlLedgerService.getDamlLedgerClient();
        this.secretAddress = secretAddress;
        this.tokens = tokenService.getTokens();
        this.signingUtils = signingUtils;
    }

    @Override
    public Flowable<CommandsAndPendingSet> process(LedgerViewFlowable.LedgerView<Record> ledgerView) {
        List<UnsignedTransferTransaction.Contract> unsignedTransferTransactions =
                getContracts(ledgerView, UnsignedTransferTransaction.TEMPLATE_ID).stream()
                        .map(contract -> (UnsignedTransferTransaction.Contract) contract)
                        .collect(toList());

        if (unsignedTransferTransactions.isEmpty()) {
            return Flowable.empty();
        }

        log.info(String.format("Got %d Transactions to Sign", unsignedTransferTransactions.size()));

        Map<Identifier, Set<String>> pending = new HashMap<>();
        pending.putIfAbsent(UnsignedTransferTransaction.TEMPLATE_ID, new HashSet<>());

        List<Command> commandList = unsignedTransferTransactions.stream()
                .map(contract -> {
                    FatToken token = tokens.stream()
                            .filter(o -> o.getTokenId().equals(contract.data.tokenId))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Unknown token ID"));
                    String tx_hex = contract.data.txToSign;
                    String tx = new String(signingUtils.decodeHexString(tx_hex));
                    List<String> exIds = signingUtils.generateExIds(tx, token.getTokenChainId(), secretAddress);
                    pending.get(UnsignedTransferTransaction.TEMPLATE_ID).add(contract.id.contractId);
                    return contract.id.exerciseUnsignedTransferTransaction_Sign(tx_hex, exIds);
                })
                .collect(toList());

        if (!commandList.isEmpty()) {
            return toCommandsAndPendingSet(commandList, pending);
        } else {
            return Flowable.empty();
        }
    }

    @PostConstruct
    public void init() {
        Set<Identifier> unsignedTransactionTids = Sets.newHashSet(UnsignedTransferTransaction.TEMPLATE_ID);
        TransactionFilter unsignedTransactionFilter = LedgerUtils.filterFor(unsignedTransactionTids, party);
        Bot.wire(appId, ledgerClient, unsignedTransactionFilter, this::process, super::getRecordFromContract);
    }
}

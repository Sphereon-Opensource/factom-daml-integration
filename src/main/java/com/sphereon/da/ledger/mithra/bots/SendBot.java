package com.sphereon.da.ledger.mithra.bots;

import com.daml.ledger.javaapi.data.Command;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Record;
import com.daml.ledger.javaapi.data.TransactionFilter;
import com.daml.ledger.rxjava.components.Bot;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import com.sphereon.da.ledger.mithra.dto.FatToken;
import com.sphereon.da.ledger.mithra.model.fat.transfer.SignedTransferTransaction;
import com.sphereon.da.ledger.mithra.model.fat.utils.SendStatus;
import com.sphereon.da.ledger.mithra.model.fat.utils.sendstatus.Pending;
import com.sphereon.da.ledger.mithra.model.fat.utils.sendstatus.Sent;
import com.sphereon.da.ledger.mithra.services.DamlLedgerService;
import com.sphereon.da.ledger.mithra.services.TokenService;
import com.sphereon.da.ledger.mithra.utils.LedgerUtils;
import com.sphereon.da.ledger.mithra.utils.fatd.FactomTransaction;
import com.sphereon.da.ledger.mithra.utils.fatd.FatdRpc;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@Component
@Profile("operator")
public class SendBot extends AbstractBot {
    private final static Logger log = LoggerFactory.getLogger(SendBot.class);
    private final FatdRpc rpcClient;
    private List<FatToken> tokens;

    public SendBot(@Value("mithra-${spring.profiles.active}") String appId,
                   DamlLedgerService damlLedgerService,
                   @Value("${mithra.party}") String party,
                   FatdRpc rpcClient,
                   TokenService tokenService) {
        super.appId = appId;
        super.ledgerId = damlLedgerService.getLedgerId();
        super.party = party;
        super.ledgerClient = damlLedgerService.getDamlLedgerClient();
        this.rpcClient = rpcClient;
        this.tokens = tokenService.getTokens();
    }

    @Override
    public Flowable<CommandsAndPendingSet> process(LedgerViewFlowable.LedgerView<Record> ledgerView) {
        List<SignedTransferTransaction.Contract> signedTransferTransactions =
                getContracts(ledgerView, SignedTransferTransaction.TEMPLATE_ID).stream()
                        .map(contract -> (SignedTransferTransaction.Contract) contract)
                        .collect(toList());

        List<SignedTransferTransaction.Contract> signedAndPendingTransferTransactions = signedTransferTransactions.stream()
                .filter(c -> c.data.sendStatus instanceof Pending)
                .collect(toList());

        if (signedAndPendingTransferTransactions.isEmpty()) {
            return Flowable.empty();
        }

        log.info(String.format("Got %d Signed Transactions that are pending", signedAndPendingTransferTransactions.size()));

        Map<Identifier, Set<String>> pending = new HashMap<>();
        pending.putIfAbsent(SignedTransferTransaction.TEMPLATE_ID, new HashSet<>());

        List<Command> commandList = signedAndPendingTransferTransactions.stream()
                .map(contract -> {
                    pending.get(SignedTransferTransaction.TEMPLATE_ID).add(contract.id.contractId);
                    try {
                        FatToken token = tokens.stream()
                                .filter(o -> o.getTokenId().equals(contract.data.tokenId))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Unknown token ID"));
                        FactomTransaction fat_tx = rpcClient.sendTransaction(
                                token.getTokenChainId(),
                                contract.data.signedTx,
                                contract.data.exIds);
                        String txHash = fat_tx.getEntryHash();
                        SendStatus sendStatus = new Sent(
                                Instant.now(),
                                rpcClient.getUrl().toString(),
                                Optional.of(txHash));
                        return contract.id.exerciseSignedTransferTransaction_Sent(sendStatus);
                    } catch (Exception e) {
                        String reason = String.format("Failed to send %s from %s to %s. Exception: %s",
                                contract.data.value, contract.data.from, contract.data.to, e.getMessage());
                        log.error(reason);
                        return contract.id.exerciseSignedTransferTransaction_Fail(reason);
                    }
                })
                .collect(toList());

        if (commandList.isEmpty()) {
            return Flowable.empty();
        }
        return toCommandsAndPendingSet(commandList, pending);
    }

    @PostConstruct
    public void init() {
        Set<Identifier> signedTransactionTids = new HashSet<>(singletonList(SignedTransferTransaction.TEMPLATE_ID));
        TransactionFilter signedTransactionFilter = LedgerUtils.filterFor(signedTransactionTids, party);
        Bot.wire(appId, ledgerClient, signedTransactionFilter, this::process, super::getRecordFromContract);
    }
}

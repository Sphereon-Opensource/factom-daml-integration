package com.sphereon.da.ledger.mithra.bots;

import com.daml.ledger.javaapi.data.Command;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Record;
import com.daml.ledger.javaapi.data.TransactionFilter;
import com.daml.ledger.rxjava.components.Bot;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import com.sphereon.da.ledger.mithra.model.fat.transfer.TransferRequest;
import com.sphereon.da.ledger.mithra.services.DamlLedgerService;
import com.sphereon.da.ledger.mithra.services.TransactionService;
import com.sphereon.da.ledger.mithra.utils.LedgerUtils;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Profile("operator")
public class TransferBot extends AbstractBot {

    private final static Logger log = LoggerFactory.getLogger(TransferBot.class);
    private TransactionService transactionService;

    public TransferBot(@Value("mithra-${spring.profiles.active}") String appId,
                       DamlLedgerService damlLedgerService,
                       @Value("${mithra.party}") String party,
                       TransactionService transactionService) {
        this.transactionService = transactionService;
        super.appId = appId;
        super.ledgerId = damlLedgerService.getLedgerId();
        super.party = party;
        super.ledgerClient = damlLedgerService.getDamlLedgerClient();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Flowable<CommandsAndPendingSet> process(LedgerViewFlowable.LedgerView<Record> ledgerView) {
        List<TransferRequest.Contract> transferRequests =
                (List<TransferRequest.Contract>) (List<?>) getContracts(ledgerView, TransferRequest.TEMPLATE_ID);

        if (transferRequests.isEmpty()) {
            return Flowable.empty();
        }

        log.info(String.format("Got %d TransferRequest(s)", transferRequests.size()));
        Map<Identifier, Set<String>> pending = new HashMap<>();
        pending.putIfAbsent(TransferRequest.TEMPLATE_ID, new HashSet<>());

        List<Command> commandList = transferRequests.stream().map(contract -> {
            pending.get(TransferRequest.TEMPLATE_ID).add(contract.id.contractId);
            String tx_hex = null;
            tx_hex = transactionService.createTransactionHex(
                    contract.data.from,
                    contract.data.to,
                    contract.data.value);
            pending.get(TransferRequest.TEMPLATE_ID).add(contract.id.contractId);
            return contract.id.exerciseTransferRequest_Accept(tx_hex);
        }).collect(Collectors.toList());

        if (!commandList.isEmpty()) {
            return toCommandsAndPendingSet(commandList, pending);
        } else {
            return Flowable.empty();
        }
    }

    @PostConstruct
    public void init(){
        Set<Identifier> transferRequestTids = new HashSet<>(Collections.singletonList(TransferRequest.TEMPLATE_ID));
        TransactionFilter transferRequestFilter = LedgerUtils.filterFor(transferRequestTids, party);
        Bot.wire(appId, ledgerClient, transferRequestFilter, this::process, super::getRecordFromContract);
    }

}

package com.sphereon.da.ledger.mithra.bots;

import com.daml.ledger.javaapi.data.Command;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Record;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import com.sphereon.da.ledger.mithra.services.DamlLedgerService;
import com.sphereon.da.ledger.mithra.utils.TransactionUtils;
import io.reactivex.Flowable;
import com.sphereon.da.ledger.mithra.model.fat.transfer.TransferRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Profile("operator")
public class TransferBot extends AbstractBot {

    private final static Logger log = LoggerFactory.getLogger(TransferBot.class);

    public TransferBot(@Value("mithra-${spring.profiles.active}") String appId,
                       DamlLedgerService damlLedgerService,
                       @Value("${mithra.party}") String party) {
        super.appId = appId;
        super.ledgerId = damlLedgerService.getLedgerId();
        super.party = party;
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
            String tx_hex = TransactionUtils.createTransactionHex(
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
}

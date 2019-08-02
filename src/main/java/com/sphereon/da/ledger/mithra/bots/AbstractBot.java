package com.sphereon.da.ledger.mithra.bots;

import com.daml.ledger.javaapi.data.*;
import com.daml.ledger.rxjava.DamlLedgerClient;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import com.daml.ledger.rxjava.components.helpers.CreatedContract;
import io.reactivex.Flowable;
import io.reactivex.functions.Function3;
import com.sphereon.da.ledger.mithra.model.fat.onboarding.Operator;
import com.sphereon.da.ledger.mithra.model.fat.onboarding.User;
import com.sphereon.da.ledger.mithra.model.fat.onboarding.UserInvitation;
import com.sphereon.da.ledger.mithra.model.fat.transfer.SignedTransferTransaction;
import com.sphereon.da.ledger.mithra.model.fat.transfer.TransferRequest;
import com.sphereon.da.ledger.mithra.model.fat.transfer.UnsignedTransferTransaction;
import org.pcollections.HashTreePMap;
import org.pcollections.HashTreePSet;
import org.pcollections.PMap;
import org.pcollections.PSet;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public abstract class AbstractBot {
    protected String appId;
    protected String party;
    protected String ledgerId;
    protected DamlLedgerClient ledgerClient;

    public abstract Flowable<CommandsAndPendingSet> process(LedgerViewFlowable.LedgerView<Record> ledgerView);

    public Record getRecordFromContract(CreatedContract contract) {
        return contract.getCreateArguments();
    }

    protected Flowable<CommandsAndPendingSet> toCommandsAndPendingSet(List<Command> commandList, Map<Identifier, Set<String>> pending) {
        SubmitCommandsRequest commandsRequest = new SubmitCommandsRequest(
                UUID.randomUUID().toString(),
                appId,
                UUID.randomUUID().toString(),
                party,
                Instant.EPOCH,
                Instant.EPOCH.plusSeconds(10),
                commandList
        );

        CommandsAndPendingSet commandsAndPendingSet = new CommandsAndPendingSet(commandsRequest, toPMapPSet(pending));
        return Flowable.fromIterable(Stream.of(commandsAndPendingSet)::iterator);
    }

    protected List<Contract> getContracts(LedgerViewFlowable.LedgerView<Record> ledgerView, Identifier templateId) {
        Function3<String, Record, Optional<String>, Contract> decoder = Optional.ofNullable(decoders.get(templateId))
                .orElseThrow(() -> new IllegalArgumentException("No template found for identifier " + templateId));

        List<Contract> contractList = new ArrayList<>();
        ledgerView.getContracts(templateId).forEach((key, value) -> {
            try {
                Contract contract = decoder.apply(key, value, Optional.empty());
                contractList.add(contract);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return contractList;
    }

    private PMap<Identifier, PSet<String>> toPMapPSet(Map<Identifier, Set<String>> pending) {
        Map<Identifier, PSet<String>> pPending = new HashMap<>();
        for (Map.Entry<Identifier, Set<String>> entry : pending.entrySet()) {
            pPending.put(entry.getKey(), HashTreePSet.from(entry.getValue()));
        }
        return HashTreePMap.from(pPending);
    }

    private static HashMap<Identifier, Function3<String, Record, Optional<String>, Contract>> decoders;

    static {
        decoders = new HashMap<>();
        decoders.put(Operator.TEMPLATE_ID, Operator.Contract::fromIdAndRecord);
        decoders.put(UserInvitation.TEMPLATE_ID, UserInvitation.Contract::fromIdAndRecord);
        decoders.put(User.TEMPLATE_ID, User.Contract::fromIdAndRecord);
        decoders.put(SignedTransferTransaction.TEMPLATE_ID, SignedTransferTransaction.Contract::fromIdAndRecord);
        decoders.put(UnsignedTransferTransaction.TEMPLATE_ID, UnsignedTransferTransaction.Contract::fromIdAndRecord);
        decoders.put(TransferRequest.TEMPLATE_ID, TransferRequest.Contract::fromIdAndRecord);
    }
}

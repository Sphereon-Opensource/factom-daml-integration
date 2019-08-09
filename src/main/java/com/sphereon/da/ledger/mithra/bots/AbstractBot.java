package com.sphereon.da.ledger.mithra.bots;

import com.daml.ledger.javaapi.data.Command;
import com.daml.ledger.javaapi.data.Contract;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Record;
import com.daml.ledger.javaapi.data.SubmitCommandsRequest;
import com.daml.ledger.rxjava.DamlLedgerClient;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import com.daml.ledger.rxjava.components.helpers.CreatedContract;
import com.sphereon.da.ledger.mithra.model.fat.onboarding.Operator;
import com.sphereon.da.ledger.mithra.model.fat.onboarding.User;
import com.sphereon.da.ledger.mithra.model.fat.onboarding.UserInvitation;
import com.sphereon.da.ledger.mithra.model.fat.transfer.SignedTransferTransaction;
import com.sphereon.da.ledger.mithra.model.fat.transfer.TransferRequest;
import com.sphereon.da.ledger.mithra.model.fat.transfer.UnsignedTransferTransaction;
import io.reactivex.Flowable;
import io.reactivex.functions.Function5;
import org.pcollections.HashTreePMap;
import org.pcollections.HashTreePSet;
import org.pcollections.PMap;
import org.pcollections.PSet;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
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
        Function5<String, Record, Optional<String>, Set<String>, Set<String>, Contract> decoder = Optional.ofNullable(decoders.get(templateId))
                .orElseThrow(() -> new IllegalArgumentException("No template found for identifier " + templateId));

        return ledgerView.getContracts(templateId).entrySet().stream()
                .flatMap(entry -> {
                    try {
                        return Stream.of(decoder.apply(entry.getKey(), entry.getValue(), Optional.empty(), Collections.emptySet(), Collections.emptySet()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Stream.empty();
                    }
                })
                .collect(toList());
    }

    private PMap<Identifier, PSet<String>> toPMapPSet(Map<Identifier, Set<String>> pending) {
        final Map<Identifier, PSet<String>> pPending = pending.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, entry -> HashTreePSet.from(entry.getValue())));
        return HashTreePMap.from(pPending);
    }

    private static HashMap<Identifier, Function5<String, Record, Optional<String>, Set<String>, Set<String>, Contract>> decoders;

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

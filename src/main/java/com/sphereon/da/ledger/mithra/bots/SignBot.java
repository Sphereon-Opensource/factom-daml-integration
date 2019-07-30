package com.sphereon.da.ledger.mithra.bots;

import com.daml.ledger.javaapi.data.Command;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Record;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import io.reactivex.Flowable;
import mithra.model.fat.transfer.UnsignedTransferTransaction;
import mithra.utils.fatd.Mthr;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class SignBot extends AbstractBot {

    private final static Logger log = LoggerFactory.getLogger(SignBot.class);

    public SignBot(String appId, String ledgerId, String party) {
        super.appId = appId;
        super.ledgerId = ledgerId;
        super.party = party;
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

        String secretKey = mithra.ClientMain.PRIVATE_KEY;

        List<Command> commandList = unsignedTransferTransactions.stream().map(contract -> {
            try {
                String tx_hex = contract.data.txToSign;
                String tx = new String(Hex.decodeHex(tx_hex.toCharArray()));
                List<String> exIds = mithra.utils.SigningUtils.generateExIds(tx, Mthr.TOKEN_CHAIN_ID, secretKey);
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

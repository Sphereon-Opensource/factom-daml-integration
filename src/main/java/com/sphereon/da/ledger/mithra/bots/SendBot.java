package mithra.bots;

import com.daml.ledger.javaapi.data.Command;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Record;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import mithra.model.fat.transfer.SignedTransferTransaction;
import mithra.model.fat.utils.SendStatus;
import mithra.model.fat.utils.sendstatus.Pending;
import mithra.model.fat.utils.sendstatus.Sent;
import io.reactivex.Flowable;
import mithra.utils.FatToken;
import mithra.utils.fatd.FactomTransaction;
import mithra.utils.fatd.FatdRpc;
import mithra.utils.fatd.Mthr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class SendBot extends AbstractBot {

    private final static Logger log = LoggerFactory.getLogger(SendBot.class);
    private final FatdRpc rpcClient;
    private List<FatToken> tokens;

    public SendBot(String appId, String ledgerId, String party, FatdRpc rpcClient, List<FatToken> tokens) {
        super.appId = appId;
        super.ledgerId = ledgerId;
        super.party = party;
        this.rpcClient = rpcClient;
        this.tokens = tokens;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Flowable<CommandsAndPendingSet> process(LedgerViewFlowable.LedgerView<Record> ledgerView) {

        List<SignedTransferTransaction.Contract> signedTransferTransactions =
                (List<SignedTransferTransaction.Contract>)(List<?>)
                        getContracts(ledgerView, SignedTransferTransaction.TEMPLATE_ID);
        List<SignedTransferTransaction.Contract> signedAndPendingTransferTransactions =
                signedTransferTransactions.stream().filter(c -> c.data.sendStatus instanceof Pending)
                        .collect(Collectors.toList());

        if (signedAndPendingTransferTransactions.isEmpty()) {
            return Flowable.empty();
        }

        log.info(String.format("Got %d Signed Transactions that are pending", signedAndPendingTransferTransactions.size()));

        Map<Identifier, Set<String>> pending = new HashMap<>();
        pending.putIfAbsent(SignedTransferTransaction.TEMPLATE_ID, new HashSet<>());

        List<Command> commandList = signedAndPendingTransferTransactions.stream().map(contract -> {
            pending.get(SignedTransferTransaction.TEMPLATE_ID).add(contract.id.contractId);
            try {
                FatToken token = tokens.stream().filter(o -> o.getTokenId().equals(contract.data.tokenId)).findFirst().get();
                FactomTransaction fat_tx = rpcClient.sendTransaction(
                        token.getTokenChainId(),
                        contract.data.signedTx,
                        contract.data.exIds);
                Thread.sleep(1000);
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
        }).collect(Collectors.toList());

        if (!commandList.isEmpty()) {
            return toCommandsAndPendingSet(commandList, pending);
        } else {
            return Flowable.empty();
        }
    }
}

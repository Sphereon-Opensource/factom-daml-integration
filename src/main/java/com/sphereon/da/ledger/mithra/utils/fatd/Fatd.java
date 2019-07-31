package com.sphereon.da.ledger.mithra.utils.fatd;

import com.sphereon.da.ledger.mithra.utils.fatd.dto.Issuance;
import com.sphereon.da.ledger.mithra.utils.fatd.dto.Statistics;
import com.sphereon.da.ledger.mithra.utils.fatd.dto.Transaction;
import com.sphereon.da.ledger.mithra.utils.fatd.rpc.RpcException;

import java.util.List;
import java.util.Optional;

public interface Fatd {
    long getBalance(String address, String tokenChainId, String tokenId, String issuanceChainId) throws RpcException;

    List<Transaction> getTransactionHistory(String address, String tokenId, String issuerRootChainId);

    FactomTransaction sendTransaction(String tokenChainId, String tx, List<String> externalIds);

    Optional<Transaction> getTransaction(String entryHash, String tokenId, String issuerRootChainId);

    Issuance getIssuance(String tokenId, String issuerRootChainId);

    Statistics getStats(String tokenId, String issuerRootChainId);

    Issuance issueToken(String issuanceRequest);
}

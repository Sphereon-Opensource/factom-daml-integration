package com.sphereon.da.ledger.mithra.utils.fatd;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sphereon.da.ledger.mithra.utils.fatd.dto.FactomTransactionRpc;
import com.sphereon.da.ledger.mithra.utils.fatd.dto.Issuance;
import com.sphereon.da.ledger.mithra.utils.fatd.dto.Statistics;
import com.sphereon.da.ledger.mithra.utils.fatd.dto.Transaction;
import com.sphereon.da.ledger.mithra.utils.fatd.exception.InsufficientBalanceException;
import com.sphereon.da.ledger.mithra.utils.fatd.rpc.RpcClient;
import com.sphereon.da.ledger.mithra.utils.fatd.rpc.RpcException;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import static com.sphereon.da.ledger.mithra.utils.fatd.FatRpcMethod.GET_BALANCE;
import static com.sphereon.da.ledger.mithra.utils.fatd.FatRpcMethod.GET_HISTORY;
import static com.sphereon.da.ledger.mithra.utils.fatd.FatRpcMethod.GET_ISSUANCE;
import static com.sphereon.da.ledger.mithra.utils.fatd.FatRpcMethod.GET_TRANSACTION;
import static com.sphereon.da.ledger.mithra.utils.fatd.FatRpcMethod.ISSUE_TOKEN;
import static com.sphereon.da.ledger.mithra.utils.fatd.FatRpcMethod.SEND_TRANSACTION;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class FatdRpc implements Fatd {
    private final URL url;
    private final RpcClient exchange;

    public FatdRpc(final RpcClient rpcClient, final URL url) {
        this.url = url;
        this.exchange = rpcClient;
    }

    @Override
    public long getBalance(final String address, final String tokenChainId, final String tokenId, final String issuerRootChainId) {
        final FatRpcRequest request = GET_BALANCE.toRequestBuilder()
                .param("address", address)
                .param("chainid", tokenChainId)
                .build();
        return exchange.execute(url, request, Long.class, exchange.typeSupplierFrom(Long.class)).getResult();
    }

    @Override
    /**
     * Response code indicates that the address doesn't have any transactions. However, there might be unprocessed transactions.
     *
     * @see <a href="https://github.com/Factom-Asset-Tokens/fatd/blob/master/RPC.md">FAT RPC documentation</a>
     */
    public List<Transaction> getTransactionHistory(String address, String tokenChainId, String issuerRootChainId) {
        final FatRpcRequest request = GET_HISTORY.toRequestBuilder()
                .param("chainid", tokenChainId)
                .param("addresses", singletonList(address))
                .param("limit", 100)
                .param("order", "desc")
                .build();
        try {
            return exchange.execute(url, request, List.class, exchange.typeSupplierFrom(new TypeReference<List<Transaction>>() {}))
                    .getResult();
        } catch (RpcException e) {
            if (errorCodeEquals(e, -32803)) {
                return emptyList();
            }
            throw e;
        }
    }

    private boolean errorCodeEquals(final RpcException e, final int errorCode) {
        if (e == null || e.getRpcErrorResponse() == null || e.getRpcErrorResponse().getError() == null) {
            return false;
        }
        return new Integer(errorCode).equals(e.getRpcErrorResponse().getError().getCode());
    }

    @Override
    public FactomTransaction sendTransaction(final String tokenChainId, final String tx, final List<String> externalIds) {
        final FatRpcRequest request = SEND_TRANSACTION.toRequestBuilder()
                .param("content", tx)
                .param("chainid", tokenChainId)
                .param("extids", externalIds)
                .build();
        try {
            return exchange.execute(url, request, FactomTransactionRpc.class, exchange.typeSupplierFrom(FactomTransactionRpc.class)).getResult();
        } catch (RpcException e) {
            if (errorCodeEquals(e, -32804)) {
                throw new InsufficientBalanceException((String) e.getRpcErrorResponse().getError().getData());
            }
            throw e;
        }
    }

    @Override
    public Optional<Transaction> getTransaction(String entryHash, String tokenId, String issuerRootChainId) {
        final FatRpcRequest request = GET_TRANSACTION.toRequestBuilder()
                .param("entryHash", entryHash)
                .build();
        return exchange.execute(url, request, List.class, exchange.typeSupplierFrom(new TypeReference<List<Transaction>>() {}))
                .getResult()
                .stream()
                .findFirst();
    }

    /**
     * @see <a href="https://github.com/Factom-Asset-Tokens/fat-js/blob/development/rpc/RPC.js#L142">Fat-JS usage</a>
     */
    @Override
    public Issuance getIssuance(final String tokenId, final String issuerRootChainId) {
        final FatRpcRequest request = GET_ISSUANCE.toRequestBuilder()
                .param("token-id", tokenId)
                .param("issuer-id", issuerRootChainId)
                .build();
        return exchange.execute(url, request, Issuance.class, exchange.typeSupplierFrom(Issuance.class)).getResult();
    }

    @Override
    public Statistics getStats(final String tokenId, final String issuanceChainId) {
        return null;
    }

    @Override
    public Issuance issueToken(String issuanceRequest) {
        final FatRpcRequest request = ISSUE_TOKEN.toRequestBuilder()
                .param("issuance", issuanceRequest)
                .build();
        return exchange.execute(url, request, Issuance.class, exchange.typeSupplierFrom(Issuance.class)).getResult();
    }

    public URL getUrl() {
        return url;
    }
}

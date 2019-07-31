/*
 * Copyright 2018 Blockchain Innovation Foundation <https://blockchain-innovation.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sphereon.da.ledger.mithra.utils.fatd;

import java.util.Collection;
import java.util.Locale;

/**
 * This represents all RpcMethods accepted by factomd and walletd. Please note that the enum names used here sometimes differ slightly
 * from the official names (used as argument). This is because the factomd and walletd APIs aren't fully consistent.
 * Entry Credits are for instance referenced as ec, entry-credit, entrycredit
 */
public enum FatRpcMethod {
    // Transaction API
    SEND_TRANSACTION("send-transaction"),
    GET_TRANSACTION("get-transaction"),
    GET_HISTORY("get-transactions"),

    //Balance API
    GET_BALANCE("get-balance"),

    //Stats
    GET_STATS("get-stats"),
    GET_ISSUANCE("get-issuance"),

    //Token
    ISSUE_TOKEN("issue-token"),
    DISTRIBUTE_TOKEN("distribute-token");

    private final String method;

    /**
     * Construct a method using it's official RPC method name from the factomd and walletd API.
     *
     * @param method
     */
    FatRpcMethod(String method) {
        this.method = method;
    }

    /**
     * Creates the {@link FatRpcMethod} from the official walletd/factomd JSON values.
     *
     * @param value The method value as used in json
     * @return The FatRpcMethod that corresponds to the supplied string
     */
    public static FatRpcMethod fromJsonValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Cannot have a null rpc method");
        }
        for (FatRpcMethod method : FatRpcMethod.values()) {
            if (method.getMethod().equalsIgnoreCase(value)) {
                return method;
            }
        }

        return valueOf(value.toUpperCase(Locale.getDefault()));
    }

    /**
     * Create a new Rpc request builder using this Rpc Method.
     *
     * @return A new Rpc Request Builder using this method.
     */
    public FatRpcRequest.Builder toRequestBuilder() {
        return new FatRpcRequest.Builder(this);
    }

    /**
     * Create a new Rpc request without parameters using this Rpc Method.
     *
     * @return A new Rpc Request using this method.
     */
    public FatRpcRequest toRequest() {
        return new FatRpcRequest(this);
    }


    /**
     * Create a new Rpc request with at least one parameter using this Rpc Method.
     *
     * @param param       A parameter for the new request.
     * @param extraParams Optional extra parameters
     * @return A new Rpc Request using this method.
     */

    public FatRpcRequest toRequest(FatRpcRequest.Param<?> param, FatRpcRequest.Param<?>... extraParams) {
        return new FatRpcRequest(this, param, extraParams);
    }

    /**
     * Create a new Rpc request with a collection of parameters using this Rpc Method.
     *
     * @param params A parameter collection for the new request.
     * @return A new Rpc Request using this method.
     */

    public FatRpcRequest toRequest(Collection<FatRpcRequest.Param<?>> params) {
        return new FatRpcRequest(this, params);
    }

    /**
     * Get the official walletd/factomd request method value.
     *
     * @return The walletd/factomd method value
     */
    public String getMethod() {
        return method;
    }
}

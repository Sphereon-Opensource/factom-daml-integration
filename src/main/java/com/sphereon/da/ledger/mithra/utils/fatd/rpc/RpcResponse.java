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

package com.sphereon.da.ledger.mithra.utils.fatd.rpc;

/**
 * This object represents the result of a JSON-RPC API call. JSON-RPC is a remote procedure call protocol encoded in JSON.
 *
 * @param <Result> The type result of the API call
 */
public class RpcResponse<Result> {
    private int id;
    private String jsonrpc;
    private Result result;

    public RpcResponse() {
    }

    /**
     * The id that correlates with the id provided in the Rpc Request.
     *
     * @return The correlated id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the Json RPC version ("2.0").
     *
     * @return "2.0"
     */
    public String getJsonrpc() {
        return jsonrpc;
    }

    /**
     * Gets the typed deserilized result from the response of the factomd or walletd node.
     *
     * @return The typed result
     */
    public Result getResult() {
        return result;
    }
}

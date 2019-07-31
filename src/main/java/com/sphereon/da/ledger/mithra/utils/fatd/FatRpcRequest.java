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

import com.sphereon.da.ledger.mithra.utils.fatd.rpc.RpcException;
import com.sphereon.da.ledger.mithra.utils.fatd.rpc.RpcIllegalParameterException;
import com.sphereon.da.ledger.mithra.utils.fatd.util.Range;

import java.util.*;

import static com.sphereon.da.ledger.mithra.utils.fatd.util.StringUtils.isNullOrEmpty;

/**
 * The Factom APIs use JSON-RPC, which is a remote procedure call protocol encoded in JSON.
 * This RPC request object represents the the request of a API call to the factomd or walletd node
 */
public class FatRpcRequest {
    private static final String VERSION = "2.0";
    private final String jsonrpc = VERSION;
    private FatRpcMethod method;
    private int id;
    private Map<String, Object> params;

    /**
     * Rpc Request without parameters.
     *
     * @param fatRpcMethod The Rpc method.
     */
    public FatRpcRequest(final FatRpcMethod fatRpcMethod) {
        this.method = fatRpcMethod;
    }

    /**
     * RPC request with variable amount of parameters (at least one).
     *
     * @param fatRpcMethod The Rpc method.
     * @param firstParam   The first parameter.
     * @param extraParams  Additional parameters if any.
     */
    public FatRpcRequest(final FatRpcMethod fatRpcMethod, final Param<?> firstParam, final Param<?>... extraParams) {
        this(fatRpcMethod);
        addParam(firstParam);
        Arrays.stream(extraParams).forEach(this::addParam);
    }

    /**
     * Rpc Request with parameters.
     *
     * @param fatRpcMethod The rpc method.
     * @param params       Zero or more parameters.
     */
    public FatRpcRequest(FatRpcMethod fatRpcMethod, Collection<Param<?>> params) {
        this(fatRpcMethod);
        setParams(params);
    }

    /**
     * Get the Rpc Method associated with the request.
     *
     * @return Rpc method
     */
    public String getMethod() {
        return method.getMethod();
    }

    /**
     * Get the id of the request. The Id can be set by the user to correlate requests and responses. A response will contain the id as well.
     *
     * @return The user specified id or 0.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the id of the request. The Id can be set by the user to correlate requests and responses. A response will contain the id as well.
     *
     * @param id The id to correlate responses.
     * @return This request.
     */
    public FatRpcRequest setId(int id) {
        this.id = id;
        return this;
    }

    /**
     * The json rpc version ("2.0").
     *
     * @return version "2.0"
     */
    public String getJsonRPC() {
        return jsonrpc;
    }

    /**
     * Get the parameters for this request as map of string keys and object values, as used when serializing.
     *
     * @return The supplied parameters.
     */
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * Sets the parameters for this request.
     *
     * @param params The parameters.
     * @return This request.
     */
    public FatRpcRequest setParams(Collection<Param<?>> params) {
        if (params == null) {
            this.params = null;
        } else {
            this.params = new HashMap<>();
            params.forEach(param -> param.addToMap(this.params));
        }
        return this;
    }

    /**
     * Returns the params as a list using their original internal Param object.
     *
     * @return Params objects in a list.
     */
    private List<Param<?>> getParamsAsList() {
        if (params == null) {
            return null;
        }
        List<Param<?>> paramList = new ArrayList<>();
        params.forEach((key, value) -> paramList.add(new Param<>(key, value)));
        return paramList;
    }

    /**
     * Add a single Parameter to the list of parameters.
     *
     * @param param The parameter to add.
     * @return This request.
     */
    public FatRpcRequest addParam(Param<?> param) {
        if (param == null) {
            throw new RpcIllegalParameterException("Cannot add a null param to an RPC method");
        } else if (params == null) {
            this.params = new HashMap<>();
        }
        this.params = param.addToMap(params);
        return this;
    }

    /**
     * Make sure the method is set and when the request contains parameters that these are valid as well.
     *
     * @return This request.
     */
    protected FatRpcRequest assertValid() {
        if (getMethod() == null) {
            throw new RpcException("Cannot build a request without an RPC method specified");
        }
        if (getParamsAsList() == null) {
            return this;
        }
        getParamsAsList().forEach(FatRpcRequest.Param::assertValid);
        return this;
    }

    /**
     * A Parameter that has a key an typed value object.
     *
     * @param <T> The value type of the parameter.
     */
    public static class Param<T> {
        private String key;
        private T value;

        /**
         * Create a param using key and value.
         *
         * @param key   The key.
         * @param value The value.
         */
        protected Param(String key, T value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Gets the key of the parameter.
         *
         * @return The key.
         */
        public String getKey() {
            return key;
        }

        /**
         * Allows to replace the key after construction.
         *
         * @param key The new key.
         */
        public void setKey(String key) {
            this.key = key;
        }

        /**
         * Get the typed value of the parameter.
         *
         * @return The value.
         */
        public T getValue() {
            return value;
        }

        /**
         * Allows to change a value of a parameter after construction.
         *
         * @param value The new value.
         */
        public void setValue(T value) {
            this.value = value;
        }

        /**
         * Makes sure the parameter is value (contains a key and value).
         */
        public void assertValid() {
            if (isNullOrEmpty(getKey())) {
                throw new RpcIllegalParameterException("A RPC param cannot have a null key");
            }
            if (getValue() == null || isNullOrEmpty(getValue().toString())) {
                throw new RpcIllegalParameterException("A RPC param cannot have a null value");
            }
        }

        /**
         * Add current parameter to a map. Always use the result value. This method works by reference, unless you supply a null input of course.
         *
         * @param map The map where this param should be added to.
         * @return The result map. Will be the input map with the Param added if the input map was not null.
         */
        protected Map<String, Object> addToMap(Map<String, Object> map) {
            Map<String, Object> result = map;
            if (result == null) {
                result = new HashMap<>();
            }
            result.put(getKey(), getValue());
            return result;
        }
    }

    /**
     * String implementation of Param.
     */
    public static class StringParam extends Param<String> {
        public StringParam(String key, String value) {
            super(key, value);
        }
    }

    /**
     * List implementation of Param.
     */
    public static class ListParam extends Param<List> {
        public ListParam(String key, List value) {
            super(key, value);
        }
    }

    /**
     * Number implementation of Param.
     */
    public static class NumberParam extends Param<Number> {
        public NumberParam(String key, Number value) {
            super(key, value);
        }
    }

    /**
     * Range (begin, end) implementation of Param.
     */
    public static class RangeParam extends Param<Range> {
        public RangeParam(String key, Range value) {
            super(key, value);
        }
    }

    /**
     * Builder for Rpc Requests.
     */
    public static class Builder {
        private final FatRpcMethod method;
        // We allow both using the builder or not
        private int id;
        private List<Param<?>> params;

        /**
         * Create a builder from using an Rpc method.
         *
         * @param fatRpcMethod The Rpc method.
         */
        public Builder(FatRpcMethod fatRpcMethod) {
            this.method = fatRpcMethod;
            clearParams();
        }

        /**
         * Set the id of the request. The Id can be set by the user to correlate requests and responses. A response will contain the id as well.
         *
         * @param id {@link #setId(int)}
         * @return This builder.
         * @see #setId(int)
         */
        public Builder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Adds a generic typed Parameter to the request.
         *
         * @param param The param.
         * @return This builder.
         */
        public Builder param(FatRpcRequest.Param<?> param) {
            this.params.add(param);
            return this;
        }

        /**
         * Adds a number Parameter to the request.
         *
         * @param paramKey   The parameter key.
         * @param paramValue The param number value.
         * @return This builder.
         */
        public Builder param(String paramKey, Number paramValue) {
            param(new FatRpcRequest.NumberParam(paramKey, paramValue));
            return this;
        }

        /**
         * Adds a string Parameter to the request.
         *
         * @param paramKey   The parameter key.
         * @param paramValue The param string value.
         * @return This builder.
         */
        public Builder param(String paramKey, String paramValue) {
            param(new FatRpcRequest.StringParam(paramKey, paramValue));
            return this;
        }

        public <T> Builder param(String paramKey, List<T> paramList) {
            param(new FatRpcRequest.ListParam(paramKey, paramList));
            return this;
        }

        /**
         * Adds a range Parameter to the request.
         *
         * @param paramKey   The parameter key.
         * @param paramValue The param range value.
         * @return This builder.
         */
        public Builder param(String paramKey, Range paramValue) {
            param(new FatRpcRequest.RangeParam(paramKey, paramValue));
            return this;
        }

        /**
         * Clears all parameers gathered so far.
         *
         * @return This builder.
         */
        public Builder clearParams() {
            this.params = new ArrayList<>();
            return this;
        }

        /**
         * Builds a new Rpc Request from current builder.
         *
         * @return The Rpc request.
         */
        public FatRpcRequest build() {
            FatRpcRequest fatRpcRequest = new FatRpcRequest(method);
            fatRpcRequest.setId(id);
            if (params != null) {
                fatRpcRequest.setParams(params);
            }
            return fatRpcRequest.assertValid();
        }
    }
}


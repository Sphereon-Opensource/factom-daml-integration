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

import java.io.Serializable;

public class RpcErrorResponse implements Serializable {
    private Error error;
    private int id;
    private String jsonrpc;

    public int getId() {
        return id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }


    public Error getError() {
        return error;
    }

    @Override
    public String toString() {
        return "RpcErrorResponse{" +
                "error=" + error +
                ", id=" + id +
                ", jsonrpc='" + jsonrpc + '\'' +
                '}';
    }

    public static class Error implements Serializable {
        private int code;
        private String message;
        private Object data;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }

        @Override
        public String toString() {
            return "Error{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
}

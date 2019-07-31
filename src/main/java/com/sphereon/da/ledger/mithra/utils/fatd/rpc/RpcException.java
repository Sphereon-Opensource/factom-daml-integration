package com.sphereon.da.ledger.mithra.utils.fatd.rpc;

public class RpcException extends RuntimeException {
    private final RpcErrorResponse rpcErrorResponse;
    private final Integer responseCode;
    private final String responseMessage;

    public RpcException(final RpcErrorResponse rpcErrorResponse, final Integer responseCode, final String responseMessage, Throwable e) {
        super(e);
        this.rpcErrorResponse = rpcErrorResponse;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public RpcException(final Throwable e) {
        this(null, null, null, e);
    }

    public RpcException(final RpcErrorResponse rpcErrorResponse) {
        super();
        this.rpcErrorResponse = rpcErrorResponse;
        this.responseCode = null;
        this.responseMessage = null;
    }

    public RpcException(final String message) {
        super(message);
        this.rpcErrorResponse = null;
        this.responseCode = null;
        this.responseMessage = null;
    }

    public RpcException(String message, Throwable e) {
        super(message, e);
        this.rpcErrorResponse = null;
        this.responseCode = null;
        this.responseMessage = null;
    }

    public RpcErrorResponse getRpcErrorResponse() {
        return rpcErrorResponse;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}

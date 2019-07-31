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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sphereon.da.ledger.mithra.utils.fatd.dto.Transaction;
import com.sphereon.da.ledger.mithra.utils.fatd.util.HttpHeaders;
import com.sphereon.da.ledger.mithra.utils.fatd.util.HttpMethod;
import com.sphereon.da.ledger.mithra.utils.fatd.util.MediaType;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RpcClient {
    private int timeout;
    private ObjectMapper objectMapper;


    public RpcClient(final int timeout, final ObjectMapper objectMapper) {
        this.timeout = timeout;
        this.objectMapper = objectMapper;
    }

    /**
     * Opens an HTTP connection to {@code url}, transmits the serialized interpretation of {@code body} and attempts to deserialize the response into {@code rpcResultClass}.
     *
     * @throws RpcException If an error occurred in connecting, writing or reading from the server, including (de)serializing the request and response. This
     *                      includes the scenario in which a non-200 HTTP response is received, but the server send a message nevertheless.
     */
    public <Request, Result> RpcResponse<Result> execute(final URL url,
                                                         final Request body,
                                                         final Class<Result> resultType,
                                                         final Supplier<JavaType> typeSupplier) throws RpcException {
        final HttpURLConnection httpURLConnection = openConnection(url);
        sendRequest(jsonFrom(body), httpURLConnection);
        return retrieveResponse(httpURLConnection, resultType, typeSupplier);
    }

    private <Request> String jsonFrom(final Request rpcRequest) {
        try {
            return objectMapper.writeValueAsString(rpcRequest);
        } catch (JsonProcessingException e) {
            throw new RpcException("Failed to serialize", e);
        }
    }

    private HttpURLConnection openConnection(final URL url) {
        try {
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeout * 1000);
            connection.setReadTimeout(timeout * 1000);
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);

            connection.setDoOutput(true);

            connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            connection.setRequestMethod(HttpMethod.POST);

            return connection;
        } catch (IOException e) {
            throw new RpcException(e);
        }
    }

    private void sendRequest(final String body, HttpURLConnection httpURLConnection) {
        try (OutputStream outputStream = httpURLConnection.getOutputStream(); OutputStreamWriter out = new OutputStreamWriter(outputStream, Charset.defaultCharset())) {
            out.write(body);
        } catch (IOException e) {
            throw new RpcException("Failed to write body to RPC connection", e);
        }
    }

    @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.AvoidCatchingGenericException"})
    private <Result> RpcResponse<Result> retrieveResponse(final HttpURLConnection httpURLConnection,
                                                          final Class<Result> resultType,
                                                          final Supplier<JavaType> typeSupplier) {
        int responseCode;
        String responseMessage;
        try {
            responseCode = httpURLConnection.getResponseCode();
            responseMessage = httpURLConnection.getResponseMessage();
        } catch (IOException e) {
            throw new RpcException("Failed to connect to the server", e);
        }

        String json;
        try (BufferedReader reader = from(httpURLConnection)) {
            json = reader.lines().collect(Collectors.joining());
        } catch (IOException e) {
            /* ErrorStream is available on non-200 HTTP result, in which case an exception is thrown. */
            final RpcErrorResponse rpcErrorResponse = deserializeErroneousResponse(httpURLConnection.getErrorStream());
            throw new RpcException(rpcErrorResponse, responseCode, responseMessage, e);
        }

        final Map<String, Object> jsonObject;
        try {
            jsonObject = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RpcException("Failed to deserialize response: " + json, e);
        }

        if (jsonObject.containsKey("error")) {
            final RpcErrorResponse rpcErrorResponse = deserializeErroneousResponse(json);
            throw new RpcException(rpcErrorResponse);
        }

        return deserialize(json, resultType, typeSupplier);
    }

    private BufferedReader from(final HttpURLConnection httpURLConnection) throws IOException {
        final InputStream is = httpURLConnection.getInputStream();
        final InputStreamReader streamReader = new InputStreamReader(is, Charset.defaultCharset());
        return new BufferedReader(streamReader);
    }

    private RpcErrorResponse deserializeErroneousResponse(final InputStream errorData) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(errorData, Charset.defaultCharset()))) {
            final String errorMessage = br.lines().collect(Collectors.joining(System.lineSeparator()));
            return objectMapper.readValue(errorMessage, RpcErrorResponse.class);
        } catch (RuntimeException | IOException e) {
            /* Catch RuntimeException because errorData may be null or there may be an error reading */
            throw new RpcException("Failed to read error message from HTTP connection", e);
        }
    }

    private RpcErrorResponse deserializeErroneousResponse(final String json) {
        try {
            return objectMapper.readValue(json, RpcErrorResponse.class);
        } catch (IOException e) {
            throw new RpcException("Failed to read error message from HTTP connection", e);
        }
    }

    private <Result> RpcResponse<Result> deserialize(final String json,
                                                     final Class<Result> resultType,
                                                     final Supplier<JavaType> typeSupplier) {
        try {
            final JavaType javaType = objectMapper.getTypeFactory().constructParametricType(RpcResponse.class, typeSupplier.get());
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new RpcException("Failed to deserialize response: " + json, e);
        }
    }

    public Supplier<JavaType> typeSupplierFrom(TypeReference<List<Transaction>> genericType) {
        return () -> objectMapper.getTypeFactory().constructType(genericType);
    }

    public <Result> Supplier<JavaType> typeSupplierFrom(Class<Result> clazz) {
        return () -> objectMapper.getTypeFactory().constructType(clazz);
    }
}

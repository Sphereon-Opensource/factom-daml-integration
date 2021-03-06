package com.sphereon.da.ledger.mithra.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.blockchain_innovation.factom.client.api.AddressKeyConversions;
import org.blockchain_innovation.factom.client.api.errors.FactomRuntimeException;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.client.api.ops.SigningOperations;
import org.blockchain_innovation.factom.client.impl.OfflineAddressKeyConversions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SigningUtilsTest {
    private static final String DUMMY_TOKEN_CHAIN_ID = "5e58e08f787caadc2cbab3ae49bc2264c4a93476498037d6dfb304b513d1b58c";
    private static final String DUMMY_TRANSACTION = "{\"inputs\":{\"FA2wS1wbcsfDzzsKWPS2soz6aomzFDBTdCDWs32g56xZkm7n5fxb\":10},\"outputs\":{\"FA2g46zBReeuVhDWxKgvfmCFDtqJ9jukg4E83Cu73pBp9SYXjuct\":10}";
    private SigningUtils signingUtils;
    private SigningOperations signingOperations;
    private OfflineAddressKeyConversions addressKeyConversions;

    @BeforeEach
    void setup() {
        signingOperations = mock(SigningOperations.class);
        addressKeyConversions = mock(OfflineAddressKeyConversions.class);
        signingUtils = new SigningUtils(Clock.fixed(Instant.ofEpochSecond(123456789), ZoneId.of("Europe/Amsterdam")), signingOperations, addressKeyConversions);
    }

    @Test
    void invalidSecretAddressWillThrowException() {
        assertThrows(FactomRuntimeException.class, () -> signingUtils.generateExIds(DUMMY_TRANSACTION, DUMMY_TOKEN_CHAIN_ID, ""));
    }

    @Test
    void generateExIds() throws DecoderException {
        when(addressKeyConversions.addressToPublicAddress("Fs2bNdadxK3PWod3hghz589gWCgtNruBCZMLyVJUwfiPMkNTNcc1"))
                .thenReturn("FA2wS1wbcsfDzzsKWPS2soz6aomzFDBTdCDWs32g56xZkm7n5fxb");

        when(addressKeyConversions.addressToKey(eq("FA2wS1wbcsfDzzsKWPS2soz6aomzFDBTdCDWs32g56xZkm7n5fxb"), eq(Encoding.HEX)))
                .thenReturn(toHex("dummy-key"));

        when(signingOperations.sign(any(), any(Address.class))).thenReturn("Foo".getBytes());

        final List<String> externalIds = signingUtils.generateExIds(DUMMY_TRANSACTION, DUMMY_TOKEN_CHAIN_ID, "Fs2bNdadxK3PWod3hghz589gWCgtNruBCZMLyVJUwfiPMkNTNcc1");
        assertEquals(3, externalIds.size());
        assertEquals("123456789", hexToString(externalIds.get(0)));
        assertEquals(hexToString("01") + "dummy-key", hexToString(externalIds.get(1)));
        assertEquals("Foo", hexToString(externalIds.get(2)));
    }

    private String toHex(final String input) {
        return Hex.encodeHexString(input.getBytes());
    }

    private String hexToString(final String input) throws DecoderException {
        return new String(Hex.decodeHex(input.toCharArray()));
    }
}

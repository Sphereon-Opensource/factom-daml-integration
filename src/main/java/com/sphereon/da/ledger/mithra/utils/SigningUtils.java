package com.sphereon.da.ledger.mithra.utils;

import com.google.common.primitives.Bytes;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.blockchain_innovation.factom.client.api.model.Address;
import org.blockchain_innovation.factom.client.api.ops.Digests;
import org.blockchain_innovation.factom.client.api.ops.Encoding;
import org.blockchain_innovation.factom.client.api.ops.SigningOperations;
import org.blockchain_innovation.factom.client.impl.OfflineAddressKeyConversions;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class SigningUtils {

    public List<String> generateExIds(String tx, String tokenChainId, String secretAddress) throws NullPointerException {
        // generate public key from private key and include in rcd
        String publicKey = secretAddressToPublicKey(secretAddress);
        Address secretAddressObj = new Address(secretAddress);

        byte[] rcd, timeStampBytes, tokenChainIdBytes, contentBytes, rcdType1bytes;

        rcdType1bytes = decodeHexString("01");
        rcd = Bytes.concat(rcdType1bytes, decodeHexString(publicKey));

        Date now = new Date();
        long unixSeconds = now.getTime() / 1000L;
        String timeStamp = String.valueOf(unixSeconds);
        timeStampBytes = timeStamp.getBytes();
        tokenChainIdBytes = decodeHexString(tokenChainId);
        contentBytes = tx.getBytes();

        byte[] toSignBytes = Bytes.concat("0".getBytes(),
                timeStampBytes, tokenChainIdBytes, contentBytes);
        SigningOperations sign = new SigningOperations();

        byte[] signature = sign.sign(Digests.SHA_512.digest(toSignBytes), secretAddressObj);

        return Arrays.asList(Hex.encodeHexString(timeStampBytes),
                Hex.encodeHexString(rcd), Hex.encodeHexString(signature));
    }

    public byte[] decodeHexString(String hexString) throws RuntimeException {
        try {
            return Hex.decodeHex(hexString.toCharArray());
        } catch (DecoderException e) {
            throw new RuntimeException(String.format("Could not decode hex string: %s", hexString));
        }
    }

    private String secretAddressToPublicKey(String secretAddress) {
        OfflineAddressKeyConversions addressKeyConversions = new OfflineAddressKeyConversions();
        String publicAddress = addressKeyConversions.addressToPublicAddress(secretAddress);
        return addressKeyConversions.addressToKey(publicAddress, Encoding.HEX);
    }
}

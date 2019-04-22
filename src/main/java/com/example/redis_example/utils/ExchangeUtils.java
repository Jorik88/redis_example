package com.example.redis_example.utils;

import org.apache.commons.codec.binary.Hex;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ExchangeUtils {

    private static final String DOT_DELIMITER = ".";
    private static final String HMAC_SHA256 = "HmacSHA256";

    public static String createAuthSignature(String publicKey, String privateKey) throws NoSuchAlgorithmException, InvalidKeyException {
        ZoneId zoneId = ZoneId.systemDefault();
        long date = LocalDateTime.now().atZone(zoneId).toEpochSecond();

        String payload = String.join(DOT_DELIMITER, Long.toString(date), publicKey);
        String digestValue = encode(privateKey, payload);

        return String.join(DOT_DELIMITER, Long.toString(date), publicKey, digestValue);
    }

    private static String encode(String key, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256HMAC = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
        sha256HMAC.init(secretKey);

        return Hex.encodeHexString(sha256HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}

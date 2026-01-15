package com.userservice.expmbff.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptUtil {

    @Value("${data.encrypt.key}")
    private String key;

    public static String ENCRYPT_KEY;

    private static final String ALGO = "AES";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        ENCRYPT_KEY = key;
    }
    /**
     * Encrypts any data type using the default ENCRYPT_KEY
     * @param data The data to encrypt (can be String, int, double, or any serializable object)
     * @param <T> The type of data being encrypted
     * @return Encrypted string in Base64 format
     * @throws Exception if encryption fails
     */
    public static <T> String encrypt(T data) throws Exception {
        if(data == null || data.toString().isBlank()) return "";
        return encrypt(data, ENCRYPT_KEY);
    }

    /**
     * Encrypts any data type with a custom key
     * @param data The data to encrypt (can be String, int, double, or any serializable object)
     * @param key Custom encryption key
     * @param <T> The type of data being encrypted
     * @return Encrypted string in Base64 format
     * @throws Exception if encryption fails
     */
    private static <T> String encrypt(T data, String key) throws Exception {
        // Convert data to JSON string for serialization
        String jsonData;
        if (data instanceof String) {
            jsonData = (String) data;
        } else {
            jsonData = objectMapper.writeValueAsString(data);
        }

        Cipher cipher = Cipher.getInstance(ALGO);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(jsonData.getBytes()));
    }

    /**
     * Decrypts data and returns it as String using the default ENCRYPT_KEY
     * @param encrypted The encrypted string in Base64 format
     * @return Decrypted string
     * @throws Exception if decryption fails
     */
    public static String decrypt(String encrypted) throws Exception {
        if(encrypted == null || encrypted.isBlank()) return "";
        return decrypt(encrypted, ENCRYPT_KEY, String.class);
    }

    /**
     * Decrypts data and returns it as the specified type with a custom key
     * @param encrypted The encrypted string in Base64 format
     * @param key Custom decryption key
     * @param clazz The class type to deserialize into
     * @param <T> The type of data being decrypted
     * @return Decrypted data of type T
     * @throws Exception if decryption fails
     */
    private static <T> T decrypt(String encrypted, String key, Class<T> clazz) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), ALGO);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        String decryptedJson = new String(cipher.doFinal(Base64.getDecoder().decode(encrypted)));

        // Deserialize based on type
        if (clazz == String.class) {
            return clazz.cast(decryptedJson);
        } else {
            return objectMapper.readValue(decryptedJson, clazz);
        }
    }
}

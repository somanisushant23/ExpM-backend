package com.userservice.expmbff.utils.vault;

import org.springframework.stereotype.Component;

/**
 * Helper class to retrieve credentials from Windows Credential Manager
 *
 * Usage:
 * 1. Store credentials in Windows Credential Manager using cmdkey or Credential Manager UI:
 *    cmdkey /generic:"MyApp/DatabasePassword" /user:"username" /pass:"password"
 *
 * 2. Read credentials programmatically:
 *    String password = CredentialManagerHelper.getDatabasePassword();
 */
@Component
public class CredentialManagerHelper {

    // Credential target names - customize these for your application
    private static final String DB_PASSWORD_KEY = "expM-bff/DB_PASSWORD";
    private static final String JWT_SECRET_KEY = "expM-bff/JWT_SECRET";
    private static final String DB_URL_KEY = "expM-bff/DB_URL";
    private static final String DB_USERNAME_KEY = "expM-bff/DB_USERNAME";

    /**
     * Read database password from Windows Credential Manager
     * @return The database password, or null if not found
     */
    public static String getDatabasePassword() {
        return WindowsCredentialReader.readGenericCredential(DB_PASSWORD_KEY);
    }

    /**
     * Read JWT secret from Windows Credential Manager
     * @return The JWT secret, or null if not found
     */
    public static String getJwtSecret() {
        return WindowsCredentialReader.readGenericCredential(JWT_SECRET_KEY);
    }

    /**
     * Read database URL from Windows Credential Manager
     * @return The database URL, or null if not found
     */
    public static String getDatabaseUrl() {
        return WindowsCredentialReader.readGenericCredential(DB_URL_KEY);
    }

    /**
     * Read database username from Windows Credential Manager
     * @return The database username, or null if not found
     */
    public static String getDatabaseUsername() {
        return WindowsCredentialReader.readGenericCredential(DB_USERNAME_KEY);
    }

    /**
     * Read a custom credential by target name
     * @param targetName The name of the credential in Windows Credential Manager
     * @return The credential value, or null if not found
     */
    public static String getCredential(String targetName) {
        return WindowsCredentialReader.readGenericCredential(targetName);
    }

    /**
     * Read a credential with fallback to environment variable
     * @param targetName The name of the credential in Windows Credential Manager
     * @param envVarName The environment variable name to use as fallback
     * @return The credential value from Windows Credential Manager, or from environment variable, or null
     */
    public static String getCredentialWithFallback(String targetName, String envVarName) {
        String value = WindowsCredentialReader.readGenericCredential(targetName);
        if (value == null || value.isEmpty()) {
            value = System.getenv(envVarName);
        }
        return value;
    }

    /**
     * Read database password with fallback to environment variable
     * @return The database password from Credential Manager or DB_PASSWORD env var
     */
    public static String getDatabasePasswordWithFallback() {
        return getCredentialWithFallback(DB_PASSWORD_KEY, "DB_PASSWORD");
    }

    /**
     * Read JWT secret with fallback to environment variable
     * @return The JWT secret from Credential Manager or JWT_SECRET env var
     */
    public static String getJwtSecretWithFallback() {
        return getCredentialWithFallback(JWT_SECRET_KEY, "JWT_SECRET");
    }

    /**
     * Read database URL with fallback to environment variable
     * @return The database URL from Credential Manager or DB_URL env var
     */
    public static String getDatabaseUrlWithFallback() {
        return getCredentialWithFallback(DB_URL_KEY, "DB_URL");
    }

    /**
     * Read database username with fallback to environment variable
     * @return The database username from Credential Manager or DB_USERNAME env var
     */
    public static String getDatabaseUsernameWithFallback() {
        return getCredentialWithFallback(DB_USERNAME_KEY, "DB_USERNAME");
    }
}


package com.userservice.expmbff.utils.vault;

import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.PointerByReference;

public class WindowsCredentialReader {

    // Define the CREDENTIAL structure
    public static class CREDENTIAL extends Structure {
        public int Flags;
        public int Type;
        public WString TargetName;
        public WString Comment;
        public WinBase.FILETIME LastWritten;
        public int CredentialBlobSize;
        public Pointer CredentialBlob;
        public int Persist;
        public int AttributeCount;
        public Pointer Attributes;
        public WString TargetAlias;
        public WString UserName;

        public CREDENTIAL() {
            super();
        }

        public CREDENTIAL(Pointer p) {
            super(p);
        }

        @Override
        protected java.util.List<String> getFieldOrder() {
            return java.util.Arrays.asList(
                "Flags", "Type", "TargetName", "Comment", "LastWritten",
                "CredentialBlobSize", "CredentialBlob", "Persist",
                "AttributeCount", "Attributes", "TargetAlias", "UserName"
            );
        }

        public static class ByReference extends CREDENTIAL implements Structure.ByReference {
        }
    }

    // Define the Advapi32 interface for credential operations
    public interface Advapi32Ext extends Advapi32 {
        Advapi32Ext INSTANCE = Native.load("Advapi32", Advapi32Ext.class);

        boolean CredReadW(
            WString targetName,
            int type,
            int flags,
            PointerByReference credential
        );

        boolean CredFree(Pointer credential);
    }

    // Credential types
    public static final int CRED_TYPE_GENERIC = 1;
    public static final int CRED_TYPE_DOMAIN_PASSWORD = 2;
    public static final int CRED_TYPE_DOMAIN_CERTIFICATE = 3;
    public static final int CRED_TYPE_DOMAIN_VISIBLE_PASSWORD = 4;

    /**
     * Read a credential from Windows Credential Manager
     *
     * @param targetName The name of the credential to read (e.g., "MyApp/DatabasePassword")
     * @param type The credential type (use CRED_TYPE_GENERIC for generic credentials)
     * @return The credential value as a String, or null if not found
     * @throws RuntimeException if there's an error reading the credential
     */
    public static String readCredential(String targetName, int type) {
        PointerByReference credPointer = new PointerByReference();

        try {
            // Call CredReadW to retrieve the credential
            boolean success = Advapi32Ext.INSTANCE.CredReadW(
                new WString(targetName),
                type,
                0,
                credPointer
            );

            if (!success) {
                int errorCode = Native.getLastError();
                if (errorCode == WinError.ERROR_NOT_FOUND) {
                    return null; // Credential not found
                }
                throw new RuntimeException("Failed to read credential. Error code: " + errorCode);
            }

            // Parse the credential structure
            CREDENTIAL credential = new CREDENTIAL(credPointer.getValue());
            credential.read();

            // Extract the credential blob (password/secret)
            if (credential.CredentialBlobSize > 0 && credential.CredentialBlob != null) {
                byte[] credentialBytes = credential.CredentialBlob.getByteArray(0, credential.CredentialBlobSize);
                // Assuming the credential is stored as a UTF-16LE string (Windows default)
                return new String(credentialBytes, java.nio.charset.StandardCharsets.UTF_16LE);
            }

            return null;
        } finally {
            // Free the credential memory
            if (credPointer.getValue() != null) {
                Advapi32Ext.INSTANCE.CredFree(credPointer.getValue());
            }
        }
    }

    /**
     * Read a generic credential from Windows Credential Manager
     *
     * @param targetName The name of the credential to read
     * @return The credential value as a String, or null if not found
     */
    public static String readGenericCredential(String targetName) {
        return readCredential(targetName, CRED_TYPE_GENERIC);
    }

    /**
     * Read a domain password credential from Windows Credential Manager
     * Note: Domain passwords created with 'cmdkey /add' may not have accessible password blobs.
     * Use 'cmdkey /generic' instead for programmatic access.
     *
     * @param targetName The name of the credential to read
     * @return The credential value as a String, or null if not found
     */
    public static String readDomainPasswordCredential(String targetName) {
        return readCredential(targetName, CRED_TYPE_DOMAIN_PASSWORD);
    }

    /**
     * Read a credential, trying both Generic and Domain Password types
     *
     * @param targetName The name of the credential to read
     * @return The credential value as a String, or null if not found
     */
    public static String readCredentialAnyType(String targetName) {
        // Try Generic first (most common for programmatic access)
        String value = readGenericCredential(targetName);
        if (value != null) {
            return value;
        }

        // Try Domain Password as fallback
        return readDomainPasswordCredential(targetName);
    }

    /**
     * Check if a credential exists in Windows Credential Manager
     *
     * @param targetName The name of the credential to check
     * @param type The credential type
     * @return true if the credential exists, false otherwise
     */
    public static boolean credentialExists(String targetName, int type) {
        PointerByReference credPointer = new PointerByReference();

        try {
            boolean success = Advapi32Ext.INSTANCE.CredReadW(
                new WString(targetName),
                type,
                0,
                credPointer
            );

            return success;
        } finally {
            if (credPointer.getValue() != null) {
                Advapi32Ext.INSTANCE.CredFree(credPointer.getValue());
            }
        }
    }

    // Example usage with debugging
    public static void main(String[] args) {
        try {
            System.out.println("Testing credential reading with debugging...\n");

            // Test the actual credential found in cmdkey /list
            System.out.println("==================================================");
            System.out.println("Testing DOMAIN PASSWORD type credentials");
            System.out.println("==================================================\n");

            String[] domainTargets = {
                "ExpenseManagerDB",
                "Domain:target=ExpenseManagerDB"
            };

            /*for (String targetName : domainTargets) {
                System.out.println("--------------------------------------------------");
                System.out.println("Testing target: " + targetName + " (DOMAIN_PASSWORD)");
                System.out.println("--------------------------------------------------");

                PointerByReference credPointer = new PointerByReference();

                try {
                    boolean success = Advapi32Ext.INSTANCE.CredReadW(
                        new WString(targetName),
                        CRED_TYPE_DOMAIN_PASSWORD,
                        0,
                        credPointer
                    );

                    System.out.println("CredReadW returned: " + success);

                    if (!success) {
                        int errorCode = Native.getLastError();
                        System.out.println("Error code: " + errorCode + " (ERROR_NOT_FOUND = " + WinError.ERROR_NOT_FOUND + ")");
                    } else {
                        CREDENTIAL credential = new CREDENTIAL(credPointer.getValue());
                        credential.read();

                        System.out.println("✓ Credential found!");
                        System.out.println("  Type: " + credential.Type);
                        System.out.println("  TargetName: " + credential.TargetName);
                        System.out.println("  UserName: " + credential.UserName);
                        System.out.println("  CredentialBlobSize: " + credential.CredentialBlobSize);

                        if (credential.CredentialBlobSize > 0 && credential.CredentialBlob != null) {
                            byte[] credentialBytes = credential.CredentialBlob.getByteArray(0, credential.CredentialBlobSize);
                            String passwordUTF16LE = new String(credentialBytes, java.nio.charset.StandardCharsets.UTF_16LE);
                            System.out.println("  Password length: " + passwordUTF16LE.length());
                            System.out.println("  Password preview: " + passwordUTF16LE.substring(0, Math.min(3, passwordUTF16LE.length())) + "...");
                        }
                    }
                } finally {
                    if (credPointer.getValue() != null) {
                        Advapi32Ext.INSTANCE.CredFree(credPointer.getValue());
                    }
                }
                System.out.println();
            }*/

            // Test multiple target names for generic type
            String[] targetNames = {
                "expM-bff/DB_PASSWORD"
            };

            System.out.println("==================================================");
            System.out.println("Testing GENERIC type credentials");
            System.out.println("==================================================\n");

            for (String targetName : targetNames) {
                System.out.println("--------------------------------------------------");
                System.out.println("Testing target: " + targetName + " (GENERIC)");
                System.out.println("--------------------------------------------------");

                PointerByReference credPointer = new PointerByReference();

                try {
                    // Try to read the credential with detailed debugging
                    boolean success = Advapi32Ext.INSTANCE.CredReadW(
                        new WString(targetName),
                        CRED_TYPE_GENERIC,
                        0,
                        credPointer
                    );

                    System.out.println("CredReadW returned: " + success);

                    if (!success) {
                        int errorCode = Native.getLastError();
                        System.out.println("Error code: " + errorCode);
                        System.out.println("ERROR_NOT_FOUND = " + WinError.ERROR_NOT_FOUND);

                        if (errorCode == WinError.ERROR_NOT_FOUND) {
                            System.out.println("Result: Credential not found in Windows Credential Manager");
                        } else {
                            System.out.println("Result: Error reading credential");
                        }
                    } else {
                        // Parse the credential structure
                        CREDENTIAL credential = new CREDENTIAL(credPointer.getValue());
                        credential.read();

                        System.out.println("Credential structure read successfully");
                        System.out.println("  CredentialBlobSize: " + credential.CredentialBlobSize);
                        System.out.println("  CredentialBlob pointer: " + credential.CredentialBlob);
                        System.out.println("  Type: " + credential.Type);
                        System.out.println("  UserName: " + credential.UserName);
                        System.out.println("  TargetName: " + credential.TargetName);

                        // Extract the credential blob (password/secret)
                        if (credential.CredentialBlobSize > 0 && credential.CredentialBlob != null) {
                            byte[] credentialBytes = credential.CredentialBlob.getByteArray(0, credential.CredentialBlobSize);
                            System.out.println("  Raw bytes length: " + credentialBytes.length);

                            // Try different encodings
                            String passwordUTF16LE = new String(credentialBytes, java.nio.charset.StandardCharsets.UTF_16LE);
                            String passwordUTF8 = new String(credentialBytes, java.nio.charset.StandardCharsets.UTF_8);
                            String passwordISO = new String(credentialBytes, java.nio.charset.StandardCharsets.ISO_8859_1);

                            System.out.println("  Password (UTF-16LE) length: " + passwordUTF16LE.length());
                            System.out.println("  Password (UTF-8) length: " + passwordUTF8.length());
                            System.out.println("  Password (ISO-8859-1) length: " + passwordISO.length());

                            // Print first few bytes for debugging (hex)
                            System.out.print("  First bytes (hex): ");
                            for (int i = 0; i < Math.min(10, credentialBytes.length); i++) {
                                System.out.printf("%02X ", credentialBytes[i]);
                            }
                            System.out.println();

                            System.out.println("\nResult: ✓ Credential found and read successfully!");
                            System.out.println("Password value (first 3 chars): " +
                                (passwordUTF16LE.length() >= 3 ? passwordUTF16LE.substring(0, 3) + "..." : passwordUTF16LE));
                        } else {
                            System.out.println("\nResult: Credential found but blob is empty or null");
                        }
                    }
                } finally {
                    if (credPointer.getValue() != null) {
                        Advapi32Ext.INSTANCE.CredFree(credPointer.getValue());
                    }
                }

                System.out.println();
            }

            System.out.println("\n==================================================");
            System.out.println("To add a test credential, run:");
            System.out.println("  cmdkey /generic:\"expM-bff/DB_PASSWORD\" /user:\"test\" /pass:\"myTestPassword123\"");
            System.out.println("\nTo list all credentials, run:");
            System.out.println("  cmdkey /list");
            System.out.println("==================================================");

        } catch (Exception e) {
            System.err.println("Error reading credential: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

# EncryptUtil Usage Guide

The `EncryptUtil` class now supports encrypting and decrypting any data type including String, Integer, Double, and complex objects.

## Features
- Generic type support for encryption/decryption
- Automatic JSON serialization for non-String types
- Default encryption key from configuration (`data.encrypt.key`)
- Optional custom key support

## Usage Examples

### Encrypting Data

#### String
```java
// Using default ENCRYPT_KEY
String encrypted = EncryptUtil.encrypt("Hello World");

// Using custom key
String encrypted = EncryptUtil.encrypt("Hello World", "myCustomKey123");
```

#### Integer
```java
// Using default ENCRYPT_KEY
String encrypted = EncryptUtil.encrypt(12345);

// Using custom key
String encrypted = EncryptUtil.encrypt(12345, "myCustomKey123");
```

#### Double
```java
// Using default ENCRYPT_KEY
String encrypted = EncryptUtil.encrypt(123.45);

// Using custom key
String encrypted = EncryptUtil.encrypt(123.45, "myCustomKey123");
```

#### Complex Objects
```java
InvestmentDto dto = new InvestmentDto();
dto.setAmount(1000.0);
dto.setDescription("Test investment");

// Using default ENCRYPT_KEY
String encrypted = EncryptUtil.encrypt(dto);

// Using custom key
String encrypted = EncryptUtil.encrypt(dto, "myCustomKey123");
```

### Decrypting Data

#### String (default)
```java
// Using default ENCRYPT_KEY
String decrypted = EncryptUtil.decrypt(encrypted);

// Using custom key
String decrypted = EncryptUtil.decrypt(encrypted, "myCustomKey123");
```

#### Integer
```java
// Using default ENCRYPT_KEY
Integer decrypted = EncryptUtil.decrypt(encrypted, Integer.class);

// Using custom key
Integer decrypted = EncryptUtil.decrypt(encrypted, "myCustomKey123", Integer.class);
```

#### Double
```java
// Using default ENCRYPT_KEY
Double decrypted = EncryptUtil.decrypt(encrypted, Double.class);

// Using custom key
Double decrypted = EncryptUtil.decrypt(encrypted, "myCustomKey123", Double.class);
```

#### Complex Objects
```java
// Using default ENCRYPT_KEY
InvestmentDto decrypted = EncryptUtil.decrypt(encrypted, InvestmentDto.class);

// Using custom key
InvestmentDto decrypted = EncryptUtil.decrypt(encrypted, "myCustomKey123", InvestmentDto.class);
```

## Method Signatures

### Encryption
- `encrypt(T data)` - Encrypts data using default ENCRYPT_KEY
- `encrypt(T data, String key)` - Encrypts data using custom key

### Decryption
- `decrypt(String encrypted)` - Decrypts as String using default ENCRYPT_KEY
- `decrypt(String encrypted, String key)` - Decrypts as String using custom key
- `decrypt(String encrypted, Class<T> clazz)` - Decrypts as type T using default ENCRYPT_KEY
- `decrypt(String encrypted, String key, Class<T> clazz)` - Decrypts as type T using custom key

## Configuration

Set the default encryption key in `application.properties`:
```properties
data.encrypt.key=${ENCRYPT_SECRET_KEY}
```

Or set the environment variable:
```bash
export ENCRYPT_SECRET_KEY=your16CharKey123
```

**Note:** AES requires a key of exactly 16, 24, or 32 bytes in length.


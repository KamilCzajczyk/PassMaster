# PassMaster

PassMaster is a modern, secure password manager for Android, built with Jetpack Compose. It allows you to safely store, manage, and export your passwords, with strong encryption and a user-friendly interface.

## Screenshots

<img src="https://github.com/user-attachments/assets/52ee2bf1-a60f-4157-b37a-3105ee083f85" width="auto" height="800"/>
<img src="https://github.com/user-attachments/assets/bce194fc-a3d0-4232-ada4-53d90d09d679" width="auto" height="800"/>

## Features

- **Master Password Protection**: All your data is encrypted with a master password.
- **Recovery Key**: Generate a recovery key to regain access if you forget your master password.
- **Password Storage**: Save service names, logins, and passwords securely.
- **Favorites**: Mark important passwords as favorites for quick access.
- **Password Export**: Export your passwords to a CSV file.
- **Data Breach Check**: Check if your passwords have been compromised.
- **Clipboard Copy**: Copy passwords to clipboard with a single tap.
- **Password Generator**: Generate strong, secure passwords.

## Security & Encryption

- **Key Derivation**  
  The master password is never used directly for encryption. Instead, a secure key is derived from the master password and a random salt using PBKDF2 with HMAC-SHA256, 10,000 iterations, and a 256-bit key length. This significantly increases resistance to brute-force attacks.

- **Master Encryption Key (MEK)**  
  A random 256-bit Master Encryption Key (MEK) is generated for encrypting your data. The MEK itself is encrypted using a key derived from your master password (Data Encryption Key, DEK) and a random IV (Initialization Vector).

- **AES Encryption**  
  All sensitive data is encrypted using AES in CBC mode with PKCS5 padding. Each encryption operation uses a new random IV, which is prepended to the ciphertext for safe decryption.

- **Password Generation**  
  The app includes a secure password generator that creates strong, random passwords using uppercase, lowercase, digits, and special characters.

- **Salt and IV Handling**  
  Salts and IVs are generated using a secure random number generator and encoded in Base64 for storage.

- **No Plaintext Storage**  
  The master password, MEK, and all sensitive data are never stored in plaintext. Only encrypted values are saved.

**Note:** Your recovery key is not stored anywhere. If you lose it and forget your master password, your data cannot be recovered.

package com.example.passman

import android.content.Context
import android.content.SharedPreferences
import com.example.passman.security.generateSalt
import com.example.passman.security.deriveMasterKeyHash
import com.example.passman.security.encryptMEK
import com.example.passman.security.generateMEK
import com.example.passman.security.toHexString
import java.security.SecureRandom
import java.util.Base64
import android.util.Log
import com.example.passman.security.decryptMEK

class SecuritySetup(private val context: Context){
    private val sharedPreferences =
        context.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)

    fun setupFirstLogin(masterPassword: String): Boolean{
        try {
            // 1. Wygeneruj sól dla hasła
            val salt = generateSalt()

            // 2. Derywuj klucz z hasła głównego
            val masterKeyHash = deriveMasterKeyHash(masterPassword, salt)
            val dek = masterKeyHash.copyOf() // Klucz derywowany z hasła użyty do szyfrowania MEK

            // 3. Wygeneruj losowy wektor inicjalizacji (IV)
            val iv = ByteArray(16)
            SecureRandom().nextBytes(iv)
            val ivString = Base64.getEncoder().encodeToString(iv)

            // 4. Wygeneruj klucz szyfrowania głównego (MEK)
            val mek = generateMEK()

            // 5. Zaszyfruj MEK używając klucza derywowanego z hasła (DEK)
            val encryptedMEK = encryptMEK(mek, dek, iv)

            // 6. Zapisz dane zabezpieczeń
            val editor = sharedPreferences.edit()
            editor.putString("salt", salt)
            editor.putString("master_key_hash", masterKeyHash.toHexString())
            editor.putString("encrypted_mek", encryptedMEK)
            editor.putString("iv", ivString)
            //editor.putBoolean("is_first_login_completed", true)

            val result = editor.commit()

            // 7. Zapisz w pamięci tymczasowej MEK dla bieżącej sesji
            if (result) {
                SessionManager.storeMEK(mek)
                return true
            }

            return false
        } catch (e: Exception) {
            Log.e("SecuritySetup", "Error during first login setup", e)
            return false
        }
    }
    fun isSetupCompleted(): Boolean {
        return sharedPreferences.getBoolean("is_first_login_completed", false)
    }

    private fun generateRecoveryKey(): String {
        // Wygeneruj bezpieczny 12-znakowy klucz odzyskiwania
        val chars = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789"
        val key = (1..12).map { chars.random() }.joinToString("")

        // Podziel na grupy po 4 znaki dla lepszej czytelności
        return key
    }

    private fun resetMasterPassword(newMasterPassword: String, mek: ByteArray): Boolean {
        try {
            // Wygeneruj nową sól dla nowego hasła
            val newSalt = generateSalt()
            val newMasterKeyHash = deriveMasterKeyHash(newMasterPassword, newSalt)
            val newDek = newMasterKeyHash.copyOf()

            // Wygeneruj nowy IV
            val newIv = ByteArray(16)
            SecureRandom().nextBytes(newIv)

            // Zaszyfruj MEK nowym kluczem
            val newEncryptedMEK = encryptMEK(mek, newDek, newIv)

            // Zapisz nowe dane
            val editor = sharedPreferences.edit()
            editor.putString("salt", newSalt)
            editor.putString("master_key_hash", newMasterKeyHash.toHexString())
            editor.putString("encrypted_mek", newEncryptedMEK)
            editor.putString("iv", Base64.getEncoder().encodeToString(newIv))

            val result = editor.commit()

            if (result) {
                SessionManager.storeMEK(mek)
                Log.i("SecurityLogin", "Master password reset successful")
            }

            return result

        } catch (e: Exception) {
            Log.e("SecurityLogin", "Error resetting master password", e)
            return false
        }
    }

    fun setupRecoveryKey(): String {
        val recoveryKey = generateRecoveryKey()

        // Wygeneruj sól dla recovery key
        val recoverySalt = generateSalt()

        // Derywuj klucz z recovery key (podobnie jak z master password)
        val recoveryKeyHash = deriveMasterKeyHash(recoveryKey, recoverySalt)

        // Zaszyfruj MEK używając recovery key jako DEK
        val mek = SessionManager.getMEK() ?: throw IllegalStateException("MEK not available")
        val recoveryIv = ByteArray(16)
        SecureRandom().nextBytes(recoveryIv)
        val encryptedMEKWithRecovery = encryptMEK(mek, recoveryKeyHash, recoveryIv)

        // Zapisz dane recovery
        val editor = sharedPreferences.edit()
        editor.putString("recovery_salt", recoverySalt)
        editor.putString("recovery_key_hash", recoveryKeyHash.toHexString())
        editor.putString("encrypted_mek_recovery", encryptedMEKWithRecovery)
        editor.putString("recovery_iv", Base64.getEncoder().encodeToString(recoveryIv))
        editor.putBoolean("recovery_enabled", true)
        editor.commit()

        return recoveryKey
    }

    fun recoverWithRecoveryKey(recoveryKey: String, newMasterPassword: String): Boolean {
        try {
            // Sprawdź czy recovery jest włączone
            if (!sharedPreferences.getBoolean("recovery_enabled", false)) {
                Log.w("SecurityLogin", "Recovery not enabled")
                return false
            }

            // Pobierz dane recovery
            val recoverySalt = sharedPreferences.getString("recovery_salt", null) ?: return false
            val storedRecoveryKeyHash = sharedPreferences.getString("recovery_key_hash", null) ?: return false
            val encryptedMEKRecovery = sharedPreferences.getString("encrypted_mek_recovery", null) ?: return false
            val recoveryIvString = sharedPreferences.getString("recovery_iv", null) ?: return false

            val recoveryIv = Base64.getDecoder().decode(recoveryIvString)

            // Zweryfikuj recovery key
            val inputRecoveryKeyHash = deriveMasterKeyHash(recoveryKey.replace("-", ""), recoverySalt)
            if (inputRecoveryKeyHash.toHexString() != storedRecoveryKeyHash) {
                Log.w("SecurityLogin", "Invalid recovery key")
                return false
            }

            // Odszyfruj MEK używając recovery key
            val mek = decryptMEK(encryptedMEKRecovery, inputRecoveryKeyHash, recoveryIv)

            // Ustaw nowe master password
            return resetMasterPassword(newMasterPassword, mek)

        } catch (e: Exception) {
            Log.e("SecurityLogin", "Error during recovery", e)
            return false
        }
    }
    object SessionManager {
        private var mek: ByteArray? = null

        fun storeMEK(key: ByteArray) {
            mek = key.copyOf()
        }

        fun getMEK(): ByteArray? {
            return mek?.copyOf()
        }

        fun clearMEK() {
            mek = null
        }
    }
}

class SecurityLogin(private val context: Context){
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "security_prefs", Context.MODE_PRIVATE
    )

    fun login(masterPassword: String): Boolean {
        try {
            // 1. Pobierz zapisane dane bezpieczeństwa
            val salt = sharedPreferences.getString("salt", null) ?: return false
            val storedMasterKeyHash = sharedPreferences.getString("master_key_hash", null) ?: return false
            val encryptedMEK = sharedPreferences.getString("encrypted_mek", null) ?: return false
            val ivString = sharedPreferences.getString("iv", null) ?: return false

            // 2. Przekształć IV z Base64 do tablicy bajtów
            val iv = Base64.getDecoder().decode(ivString)

            // 3. Wygeneruj hash wprowadzonego hasła
            val inputMasterKeyHash = deriveMasterKeyHash(masterPassword, salt)

            // 4. Porównaj hashe, aby zweryfikować hasło
            if (inputMasterKeyHash.toHexString() != storedMasterKeyHash) {
                // Hasło nieprawidłowe
                Log.w("SecurityLogin", "Invalid password attempt")
                return false
            }

            // 5. Odszyfruj MEK używając klucza derywowanego z hasła (DEK)
            val dek = inputMasterKeyHash.copyOf()
            val mek = decryptMEK(encryptedMEK, dek, iv)

            // 6. Zapisz odszyfrowany MEK w pamięci sesji
            SecuritySetup.SessionManager.storeMEK(mek)

            Log.i("SecurityLogin", "Login successful")
            return true

        } catch (e: Exception) {
            Log.e("SecurityLogin", "Error during login", e)
            return false
        }
    }

//    fun verifyPassword(password: String): Boolean {
//        try {
//            val salt = sharedPreferences.getString("salt", null) ?: return false
//            val storedMasterKeyHash = sharedPreferences.getString("master_key_hash", null) ?: return false
//
//            val inputMasterKeyHash = deriveMasterKeyHash(password, salt)
//
//            return inputMasterKeyHash.toHexString() == storedMasterKeyHash
//
//        } catch (e: Exception) {
//            Log.e("SecurityLogin", "Error during password verification", e)
//            return false
//        }
//    }

    fun logout() {
        SecuritySetup.SessionManager.clearMEK()
        Log.i("SecurityLogin", "User logged out")
    }
}
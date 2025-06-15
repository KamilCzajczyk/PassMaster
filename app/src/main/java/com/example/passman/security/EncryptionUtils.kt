package com.example.passman.security


import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

fun generateSecurePassword():String{
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$"
    return (1..12)
        .map { chars.random() }
        .joinToString("")
}

fun generateSalt(byteCount: Int = 16): String {
    val bytes = ByteArray(byteCount)
    SecureRandom().nextBytes(bytes)
    return Base64.getEncoder().encodeToString(bytes)
}


fun deriveMasterKeyHash(
    password: String,
    salt: String,
    iterationCount: Int = 10000,
    keyLength: Int = 256
): ByteArray {
    val spec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), iterationCount, keyLength)
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    return factory.generateSecret(spec).encoded
}


fun generateMEK(): ByteArray {
    val mek = ByteArray(32) // 256-bitowy klucz (32 bajty)
    SecureRandom().nextBytes(mek)
    return mek
}


fun encryptMEK(mek: ByteArray, dek: ByteArray, iv: ByteArray): String{
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val secretKeySpec = SecretKeySpec(dek, "AES")
    val ivParameterSpec = IvParameterSpec(iv)

    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)

    val encryptedMEK = cipher.doFinal(mek)

    return Base64.getEncoder().encodeToString(encryptedMEK)
}


fun decryptMEK(encryptedMEK: String, dek: ByteArray, iv: ByteArray): ByteArray {

    val secretKeySpec = SecretKeySpec(dek, "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val ivParameterSpec = IvParameterSpec(iv)

    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)

    val encryptedMEKBytes = Base64.getDecoder().decode(encryptedMEK)

    return cipher.doFinal(encryptedMEKBytes)

}

object EncryptionUtils {
    fun encrypt(plainText: String, key: ByteArray): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)
        val keySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        // Prepend IV to ciphertext
        val combined = iv + encrypted
        return Base64.getEncoder().encodeToString(combined)
    }

    fun decrypt(cipherText: String, key: ByteArray): String {
        val combined = Base64.getDecoder().decode(cipherText)
        val iv = combined.copyOfRange(0, 16)
        val encrypted = combined.copyOfRange(16, combined.size)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivSpec = IvParameterSpec(iv)
        val keySpec = SecretKeySpec(key, "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }
}





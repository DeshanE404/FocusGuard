package com.example.focusguard.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object SecureHash {
    private const val SALT_BYTES = 16
    private const val ITERATIONS = 120_000
    private const val KEY_BITS = 256

    fun create(value: String): Pair<String, String> {
        val salt = ByteArray(SALT_BYTES).also(SecureRandom()::nextBytes)
        return encode(salt) to encode(derive(value, salt))
    }

    fun matches(value: String, saltBase64: String, hashBase64: String): Boolean {
        val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
        val expected = Base64.decode(hashBase64, Base64.NO_WRAP)
        return MessageDigest.isEqual(derive(value, salt), expected)
    }

    private fun derive(value: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(value.toCharArray(), salt, ITERATIONS, KEY_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun encode(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)
}

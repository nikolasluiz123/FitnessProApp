package br.com.fitnesspro.core.security

import java.security.MessageDigest
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object HashHelper {
    private const val PREFIX = "HASHED_"

    fun applyHash(value: String): String {
        val iterations = 10000
        val keyLength = 256
        val spec = PBEKeySpec(value.toCharArray(), getSalt(value), iterations, keyLength)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        val hashResult = Base64.getEncoder().encodeToString(hash)

        return "$PREFIX$hashResult"
    }

    private fun getSalt(value: String): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(value.toByteArray()).take(16).toByteArray()
    }
}
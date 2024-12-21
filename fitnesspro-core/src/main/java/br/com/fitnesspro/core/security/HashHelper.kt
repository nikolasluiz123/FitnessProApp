package br.com.fitnesspro.core.security

import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import java.security.MessageDigest

object HashHelper {

    fun applyHash(value: String): String {
        val hashResult = Argon2Kt().hash(
            mode = Argon2Mode.ARGON2_I,
            password = value.toByteArray(),
            salt = getSalt(value),
            tCostInIterations = 5,
            mCostInKibibyte = 65536
        )

        return hashResult.rawHashAsHexadecimal()
    }

    private fun getSalt(value: String): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(value.toByteArray()).take(16).toByteArray()
    }
}
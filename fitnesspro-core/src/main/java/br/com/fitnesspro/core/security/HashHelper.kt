package br.com.fitnesspro.core.security

import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import java.security.SecureRandom

object HashHelper {

    fun applyHash(value: String): String {
        val hashResult = Argon2Kt().hash(
            mode = Argon2Mode.ARGON2_I,
            password = value.toByteArray(),
            salt = getSalt(),
            tCostInIterations = 5,
            mCostInKibibyte = 65536
        )

        return hashResult.rawHashAsHexadecimal()
    }

    private fun getSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)

        return salt
    }
}
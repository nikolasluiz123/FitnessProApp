package br.com.fitnesspro.core.security

class DefaultPasswordHasher: IPasswordHasher {

    override fun hashPassword(password: String): String {
        return HashHelper.applyHash(password)
    }
}
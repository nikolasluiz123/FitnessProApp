package br.com.fitnesspro.core.security

interface IPasswordHasher {
    fun hashPassword(password: String): String
}
package br.com.fitnesspro.repository

import br.com.fitnesspro.local.access.UserDao
import br.com.fitnesspro.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun saveUser(user: User) {
        userDao.saveUser(user)
    }

    suspend fun isEmailUnique(email: String): Boolean {
        return userDao.isEmailUnique(email)
    }

    suspend fun getUserById(id: String): User {
        return userDao.findUserById(id)
    }
}
package br.com.fitnesspro.common.repository.common

import android.content.Context
import androidx.room.withTransaction
import br.com.fitnesspro.common.BuildConfig
import br.com.fitnesspro.core.extensions.dataStore
import br.com.fitnesspro.core.extensions.getAuthenticatedUserId
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.shared.communication.exception.ExpiredTokenException
import dagger.hilt.android.EntryPointAccessors

abstract class FitnessProRepository(protected val context: Context) {

    private val entryPoint = EntryPointAccessors.fromApplication<IFitnessProRepositoryEntryPoint>(context)

    suspend fun getAuthenticatedUser(): User? {
        return context.dataStore.getAuthenticatedUserId()?.let {  userId ->
            entryPoint.getUserDAO().findById(userId)
        }
    }

    suspend fun getValidToken(): String {
        val authenticatedUser = getAuthenticatedUser()

        return if (authenticatedUser != null) {
            return getValidUserToken() ?: throw ExpiredTokenException()
        } else {
            getValidDeviceToken() ?: BuildConfig.APP_JWT
        }
    }

    suspend fun runInTransaction(block: suspend () -> Unit) {
        entryPoint.getAppDatabase().withTransaction {
            block()
        }
    }

    private suspend fun getValidUserToken(): String? {
        val userId = getAuthenticatedUser()?.id!!
        return entryPoint.getServiceTokenDAO().findValidTokenByUserId(userId)
    }

    private suspend fun getValidDeviceToken(): String? {
        val deviceId = entryPoint.getDeviceRepository().getDeviceIdFromFirebase()
        return entryPoint.getServiceTokenDAO().findValidTokenByDeviceId(deviceId)
    }

}
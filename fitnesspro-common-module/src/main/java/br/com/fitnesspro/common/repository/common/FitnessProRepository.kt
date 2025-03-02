package br.com.fitnesspro.common.repository.common

import android.content.Context
import androidx.room.withTransaction
import br.com.fitnesspro.core.extensions.dataStore
import br.com.fitnesspro.core.extensions.getAuthenticatedUserId
import br.com.fitnesspro.model.general.User
import dagger.hilt.android.EntryPointAccessors

abstract class FitnessProRepository(protected val context: Context) {

    private val entryPoint = EntryPointAccessors.fromApplication<IFitnessProRepositoryEntryPoint>(context)

    suspend fun getAuthenticatedUser(): User? {
        return context.dataStore.getAuthenticatedUserId()?.let {  userId ->
            entryPoint.getUserDAO().findById(userId)
        }
    }

    suspend fun runInTransaction(block: suspend () -> Unit) {
        entryPoint.getAppDatabase().withTransaction {
            block()
        }
    }

}
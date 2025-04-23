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

    /**
     * Retorna o usuário autenticado recuperando o id dele do datastore.
     */
    suspend fun getAuthenticatedUser(): User? {
        return context.dataStore.getAuthenticatedUserId()?.let {  userId ->
            entryPoint.getUserDAO().findById(userId)
        }
    }

    /**
     * Retorna um token válido para autenticação.
     *
     * Se estiver autenticado, vai exigir que haja um token não expirado.
     *
     * Se não estiver autenticado, vai tentar usar o token de dispositivo para tentar garantir
     * rastreabilidade das operações. Se o token de dispositivo estiver expirado, vai usar o token
     * da aplicação.
     *
     * O token de apliação só expira por uma ação manual realizada.
     */
    @Throws(ExpiredTokenException::class)
    suspend fun getValidToken(): String {
        val authenticatedUser = getAuthenticatedUser()

        return if (authenticatedUser != null) {
            return getValidUserToken() ?: throw ExpiredTokenException()
        } else {
            getValidDeviceToken() ?: BuildConfig.APP_JWT
        }
    }

    /**
     * Permite executar um código de acesso ao banco dentro de uma transação, a qual pode dar rollback
     * caso ocorra algum erro.
     */
    suspend fun runInTransaction(block: suspend () -> Unit) {
        entryPoint.getAppDatabase().withTransaction {
            block()
        }
    }

    /**
     * Retorna um token válido do usuário autentiado.
     *
     * Se o usuário não estiver autentiado ou o token estiver expirado, vai retornar null.
     *
     * Essa função é útil para casos em que não queremos avisar o usuário ou possamos não executar
     * a ação.
     */
    suspend fun getValidUserToken(): String? {
        return getAuthenticatedUser()?.id?.let {
            entryPoint.getServiceTokenDAO().findValidTokenByUserId(it)
        }
    }

    /**
     * Retorna um token válido do dispositivo.
     */
    private suspend fun getValidDeviceToken(): String? {
        val deviceId = entryPoint.getDeviceRepository().getDeviceIdFromFirebase()
        return entryPoint.getServiceTokenDAO().findValidTokenByDeviceId(deviceId)
    }

}
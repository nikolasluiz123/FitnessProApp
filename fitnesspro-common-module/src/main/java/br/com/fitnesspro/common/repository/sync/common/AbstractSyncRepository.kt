package br.com.fitnesspro.common.repository.sync.common

import android.content.Context
import br.com.fitnesspro.common.injection.ISyncRepositoryEntryPoint
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.UserDAO
import dagger.hilt.android.EntryPointAccessors

abstract class AbstractSyncRepository(context: Context): FitnessProRepository(context) {

    private val entryPoint: ISyncRepositoryEntryPoint = EntryPointAccessors.fromApplication(context, ISyncRepositoryEntryPoint::class.java)

    protected val executionLogWebClient = entryPoint.getExecutionLogWebClient()

    protected val userDAO: UserDAO = entryPoint.getUserDAO()

    open fun getPageSize(): Int = 2000

}
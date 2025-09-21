package br.com.fitnesspro.common.repository.sync.common

import android.content.Context
import br.com.fitnesspro.common.injection.ISyncEntryPoint
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.model.enums.EnumSyncModule
import dagger.hilt.android.EntryPointAccessors

abstract class AbstractSyncRepository(context: Context): FitnessProRepository(context) {

    protected val syncEntryPoint: ISyncEntryPoint = EntryPointAccessors.fromApplication(context, ISyncEntryPoint::class.java)
    protected val executionLogWebClient = syncEntryPoint.getExecutionLogWebClient()

    open fun getPageSize(): Int = 1000

    abstract fun getModule(): EnumSyncModule

}
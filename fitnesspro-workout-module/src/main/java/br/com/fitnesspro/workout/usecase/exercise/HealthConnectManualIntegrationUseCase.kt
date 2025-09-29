package br.com.fitnesspro.workout.usecase.exercise

import android.util.Log
import br.com.android.work.manager.toolkit.workers.log.WorkerLogConstants
import br.com.fitnesspro.workout.repository.sync.importation.integration.HealthConnectIntegrationRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class HealthConnectManualIntegrationUseCase(
    private val healthConnectIntegrationRepository: HealthConnectIntegrationRepository
) {
    suspend operator fun invoke() = withContext(IO) {
        Log.i(WorkerLogConstants.WORKER_IMPORT, "${"-".repeat(50)} Iniciando Integração Health Connect ${javaClass.simpleName} ${"-".repeat(50)}")

        healthConnectIntegrationRepository.runInTransaction {
            healthConnectIntegrationRepository.runIntegration()
        }

        Log.i(WorkerLogConstants.WORKER_IMPORT, "${"-".repeat(50)} Finalizando Integração Health Connect ${javaClass.simpleName} ${"-".repeat(50)}")
    }

}
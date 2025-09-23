package br.com.fitnesspro.usecase

import android.content.Context
import android.util.Log
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.repository.sync.importation.GeneralModuleImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.storage.ReportStorageImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.storage.VideoStorageImportationRepository
import br.com.fitnesspro.core.extensions.dataStore
import br.com.fitnesspro.core.extensions.setRunExportWorker
import br.com.fitnesspro.core.extensions.setRunImportWorker
import br.com.fitnesspro.core.worker.LogConstants
import br.com.fitnesspro.scheduler.repository.sync.importation.SchedulerModuleImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.WorkoutModuleImportationRepository

class FullManualImportationUseCase(
    private val context: Context,
    private val userRepository: UserRepository,
    private val generalRepository: GeneralModuleImportationRepository,
    private val schedulerRepository: SchedulerModuleImportationRepository,
    private val workoutRepository: WorkoutModuleImportationRepository,
    private val reportRepository: ReportStorageImportationRepository,
    private val videoRepository: VideoStorageImportationRepository
) {

    suspend operator fun invoke() {
        try {
            Log.i(
                LogConstants.WORKER_IMPORT,
                "${"-".repeat(50)} Iniciando Importação Manual ${"-".repeat(50)}"
            )
            val serviceToken = userRepository.getValidToken()

            blockWorkersRun()

            userRepository.runInTransaction {
                importGeneralModule(serviceToken)
                importSchedulerModule(serviceToken)
                importWorkoutModule(serviceToken)
                importStorageFiles()
            }

            Log.i(
                LogConstants.WORKER_IMPORT,
                "${"-".repeat(50)} Finalizando Importação Manual ${"-".repeat(50)}"
            )
        } finally {
            unblockWorkersRun()
        }
    }

    private suspend fun importGeneralModule(serviceToken: String) {
        Log.i(LogConstants.WORKER_IMPORT, "Iniciando Importação do Módulo Geral")

        var executionsCount = 0
        var keepRunning: Boolean

        do {
            Log.i(LogConstants.WORKER_IMPORT, "Execução ${executionsCount++}")
            keepRunning = generalRepository.import(serviceToken)
        } while (keepRunning)

        Log.i(LogConstants.WORKER_IMPORT, "Finalizando Importação do Módulo Geral")
    }

    private suspend fun importSchedulerModule(serviceToken: String) {
        Log.i(LogConstants.WORKER_IMPORT, "Iniciando Importação do Módulo Agendamento")

        var executionsCount = 0
        var keepRunning: Boolean

        do {
            Log.i(LogConstants.WORKER_IMPORT, "Execução ${executionsCount++}")
            keepRunning = schedulerRepository.import(serviceToken)
        } while (keepRunning)

        Log.i(LogConstants.WORKER_IMPORT, "Finalizando Importação do Módulo Agendamento")
    }

    private suspend fun importWorkoutModule(serviceToken: String) {
        Log.i(LogConstants.WORKER_IMPORT, "Iniciando Importação do Módulo Treino")

        var executionsCount = 0
        var keepRunning: Boolean

        do {
            Log.i(LogConstants.WORKER_IMPORT, "Execução ${executionsCount++}")
            keepRunning = workoutRepository.import(serviceToken)
        } while (keepRunning)

        Log.i(LogConstants.WORKER_IMPORT, "Finalizando Importação do Módulo Treino")
    }

    private suspend fun importStorageFiles() {
        Log.i(LogConstants.WORKER_IMPORT, "Iniciando Download da Storage")

        var executionsCount = 0
        var keepRunning: Boolean

        do {
            Log.i(LogConstants.WORKER_IMPORT, "Execução ${executionsCount++} de Relatórios")
            keepRunning = reportRepository.import()
        } while (keepRunning)

        do {
            Log.i(LogConstants.WORKER_IMPORT, "Execução ${executionsCount++} de Vídeos")
            keepRunning = videoRepository.import()
        } while (keepRunning)

        Log.i(LogConstants.WORKER_IMPORT, "Finalizando Download da Storage")
    }

    private suspend fun blockWorkersRun() {
        context.dataStore.setRunImportWorker(false)
        context.dataStore.setRunExportWorker(false)
    }

    private suspend fun unblockWorkersRun() {
        context.dataStore.setRunImportWorker(true)
        context.dataStore.setRunExportWorker(true)
    }
}
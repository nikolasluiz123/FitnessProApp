package br.com.fitnesspro.common.workers.common.importation.periodic

import android.content.Context
import androidx.work.WorkerParameters
import br.com.android.firebase.toolkit.crashlytics.sendToFirebaseCrashlytics
import br.com.android.work.manager.toolkit.workers.coroutine.periodic.sync.AbstractImportationPeriodicWorker
import br.com.fitnesspro.common.injection.health.IHealthConnectIntegrationWorkerEntryPoint
import br.com.fitnesspro.core.worker.WorkerDelay
import dagger.hilt.android.EntryPointAccessors
import java.time.Duration
import java.time.temporal.ChronoUnit

/**
 * Classe base abstrata para Workers periódicos (via [FitnessProPeriodicCoroutineWorker])
 * responsáveis por disparar a integração de dados do Health Connect.
 *
 * Este Worker padroniza o seguinte comportamento:
 * 1. Obtenção de dependências Hilt (como Repositórios) dentro do Worker usando
 * uma [br.com.fitnesspro.common.injection.health.IHealthConnectIntegrationWorkerEntryPoint].
 * 2. Execução da lógica de integração (definida em [onIntegrateWithTransaction])
 * dentro de uma transação de banco de dados, garantindo atomicidade.
 * 3. Tratamento de erro padronizado, enviando exceções para o Crashlytics
 * e agendando uma nova tentativa ([Result.retry]).
 *
 * @see FitnessProPeriodicCoroutineWorker
 * @see br.com.fitnesspro.common.injection.health.IHealthConnectIntegrationWorkerEntryPoint
 * @see br.com.fitnesspro.common.repository.sync.importation.health.AbstractHealthConnectIntegrationRepository
 *
 * @author Nikolas Luiz Schmitt
 */
abstract class AbstractFitnessProImportationHealthConnectPeriodicWorker(
    context: Context,
    workerParams: WorkerParameters
) : AbstractImportationPeriodicWorker(context, workerParams) {

    protected val workerEntryPoint: IHealthConnectIntegrationWorkerEntryPoint = EntryPointAccessors.fromApplication(context, IHealthConnectIntegrationWorkerEntryPoint::class.java)
    protected val personRepository = workerEntryPoint.getPersonRepository()

    override fun getMaxRetryTimeMillis(): Long = Duration.of(WorkerDelay.FITNESS_PRO_DEFAULT_HC_IMPORT_WORKER_DELAY_HOURS * 3L, ChronoUnit.HOURS).toMillis()

    override fun getTransactionManager(): suspend (suspend () -> Unit) -> Unit {
        return personRepository::runInTransaction
    }

    override fun onBeforeErrorRetry(e: Exception) {
        super.onBeforeErrorRetry(e)
        e.sendToFirebaseCrashlytics()
    }
}
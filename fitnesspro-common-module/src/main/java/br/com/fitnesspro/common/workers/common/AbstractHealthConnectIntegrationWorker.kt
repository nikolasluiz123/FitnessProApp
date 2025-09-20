package br.com.fitnesspro.common.workers.common

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.injection.health.IHealthConnectIntegrationWorkerEntryPoint
import br.com.fitnesspro.core.worker.LogConstants
import br.com.fitnesspro.core.worker.periodic.FitnessProPeriodicCoroutineWorker
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
import dagger.hilt.android.EntryPointAccessors

/**
 * Classe base abstrata para Workers periódicos (via [FitnessProPeriodicCoroutineWorker])
 * responsáveis por disparar a integração de dados do Health Connect.
 *
 * Este Worker padroniza o seguinte comportamento:
 * 1. Obtenção de dependências Hilt (como Repositórios) dentro do Worker usando
 * uma [IHealthConnectIntegrationWorkerEntryPoint].
 * 2. Execução da lógica de integração (definida em [onIntegrateWithTransaction])
 * dentro de uma transação de banco de dados, garantindo atomicidade.
 * 3. Tratamento de erro padronizado, enviando exceções para o Crashlytics
 * e agendando uma nova tentativa ([Result.retry]).
 *
 * @see FitnessProPeriodicCoroutineWorker
 * @see IHealthConnectIntegrationWorkerEntryPoint
 * @see br.com.fitnesspro.common.repository.sync.importation.health.AbstractHealthConnectIntegrationRepository
 *
 * @author Nikolas Luiz Schmitt
 */
abstract class AbstractHealthConnectIntegrationWorker(
    context: Context,
    workerParams: WorkerParameters
) : FitnessProPeriodicCoroutineWorker(context, workerParams) {

    /**
     * Ponto de entrada (EntryPoint) do Hilt para permitir a injeção de
     * dependência dentro de classes que não são diretamente suportadas pelo Hilt,
     * como é o caso dos Workers.
     */
    protected val workerEntryPoint: IHealthConnectIntegrationWorkerEntryPoint = EntryPointAccessors.fromApplication(context, IHealthConnectIntegrationWorkerEntryPoint::class.java)

    /**
     * Instância do `PersonRepository` obtida através do [workerEntryPoint],
     * usada principalmente para executar a integração em uma transação.
     */
    protected val personRepository = workerEntryPoint.getPersonRepository()

    /**
     * Método abstrato onde a lógica de integração específica deve ser definida
     * pela classe filha.
     *
     * Ex: `override suspend fun onIntegrateWithTransaction() {
     * val person = personRepository.getLoggedPerson() ?: return
     * stepsIntegrationRepository.runIntegration(person.id)
     * caloriesIntegrationRepository.runIntegration(person.id)
     * }`
     */
    abstract suspend fun onIntegrateWithTransaction()

    /**
     * Ponto de entrada do [FitnessProPeriodicCoroutineWorker].
     * Este método encapsula a chamada ao [onIntegrateWithTransaction]
     * dentro de uma transação de banco de dados fornecida pelo [personRepository].
     */
    override suspend fun onWorkOneTime() {
        Log.i(LogConstants.WORKER_IMPORT, "${"-".repeat(50)} Iniciando Integração Health Connect ${javaClass.simpleName} ${"-".repeat(50)}")

        personRepository.runInTransaction {
            onIntegrateWithTransaction()
        }

        Log.i(LogConstants.WORKER_IMPORT, "${"-".repeat(50)} Finalizando Integração Health Connect ${javaClass.simpleName} ${"-".repeat(50)}")
    }

    /**
     * Sobrescrita do tratamento de erro. Em caso de falha na integração,
     * a exceção é enviada ao Firebase Crashlytics e o Worker
     * retorna [Result.retry] para que o WorkManager tente novamente mais tarde.
     */
    override suspend fun onError(e: Exception): Result {
        e.sendToFirebaseCrashlytics()
        return Result.retry()
    }
}
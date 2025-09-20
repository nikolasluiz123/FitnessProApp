package br.com.fitnesspro.workout.repository.sync.importation.integration

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.common.FitnessProRepository

/**
 * Repositório "Facade" que orquestra a execução de todos os repositórios
 * de integração de dados do Health Connect.
 *
 * Este repositório centraliza a lógica de importação (leitura do Health Connect
 * e salvamento no DB local), garantindo que calorias, frequência cardíaca,
 * sono e passos sejam integrados em uma única chamada.
 *
 * É tipicamente invocado por um Worker (ex: [br.com.fitnesspro.common.workers.common.AbstractHealthConnectIntegrationWorker]).
 *
 * @param context O contexto da aplicação.
 * @param personRepository Repositório para buscar o usuário logado.
 * @param caloriesIntegrationRepository Repositório de integração de calorias.
 * @param heartRateIntegrationRepository Repositório de integração de frequência cardíaca.
 * @param sleepIntegrationRepository Repositório de integração de sono.
 * @param stepsIntegrationRepository Repositório de integração de passos.
 *
 * @see br.com.fitnesspro.common.repository.sync.importation.health.AbstractHealthConnectIntegrationRepository
 * @see br.com.fitnesspro.common.workers.common.AbstractHealthConnectIntegrationWorker
 *
 * @author Nikolas Luiz Schmitt
 */
class HealthConnectIntegrationRepository(
    context: Context,
    private val personRepository: PersonRepository,
    private val caloriesIntegrationRepository: CaloriesIntegrationRepository,
    private val heartRateIntegrationRepository: HeartRateIntegrationRepository,
    private val sleepIntegrationRepository: SleepIntegrationRepository,
    private val stepsIntegrationRepository: StepsIntegrationRepository,
): FitnessProRepository(context) {

    /**
     * Executa a integração de todos os tipos de dados do Health Connect
     * para o usuário autenticado.
     *
     * Busca o ID da pessoa logada e o repassa para cada repositório
     * de integração individual ([runIntegration][AbstractHealthConnectIntegrationRepository.runIntegration]).
     */
    suspend fun runIntegration() {
        personRepository.getAuthenticatedTOPerson()?.id?.let {
            caloriesIntegrationRepository.runIntegration(it)
            heartRateIntegrationRepository.runIntegration(it)
            sleepIntegrationRepository.runIntegration(it)
            stepsIntegrationRepository.runIntegration(it)
        }
    }
}
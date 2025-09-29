package br.com.fitnesspro.health.connect.service

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import br.com.android.health.connect.toolkit.service.AbstractBaseHealthConnectService

/**
 * Serviço especializado para leitura de dados de [ActiveCaloriesBurnedRecord]
 * (Calorias Ativas Queimadas) do Health Connect.
 *
 * @see AbstractBaseHealthConnectService
 *
 * @author Nikolas Luiz Schmitt
 */
class CaloriesBurnedService(client: HealthConnectClient): AbstractBaseHealthConnectService<ActiveCaloriesBurnedRecord>(client) {
    override fun getRecordType() = ActiveCaloriesBurnedRecord::class
}
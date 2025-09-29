package br.com.fitnesspro.health.connect.service

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import br.com.android.health.connect.toolkit.service.AbstractBaseHealthConnectService

/**
 * Serviço especializado para leitura de dados de [StepsRecord] (Passos)
 * do Health Connect.
 *
 * @see AbstractBaseHealthConnectService
 *
 * @author Nikolas Luiz Schmitt
 */
class StepsService(client: HealthConnectClient): AbstractBaseHealthConnectService<StepsRecord>(client) {
    override fun getRecordType() = StepsRecord::class
}
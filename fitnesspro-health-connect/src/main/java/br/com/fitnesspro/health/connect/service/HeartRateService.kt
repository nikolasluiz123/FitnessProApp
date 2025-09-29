package br.com.fitnesspro.health.connect.service

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import br.com.android.health.connect.toolkit.service.AbstractBaseHealthConnectService

/**
 * Serviço especializado para leitura de dados de [HeartRateRecord]
 * do Health Connect.
 *
 * @see AbstractBaseHealthConnectService
 *
 * @author Nikolas Luiz Schmitt
 */
class HeartRateService(client: HealthConnectClient): AbstractBaseHealthConnectService<HeartRateRecord>(client) {
    override fun getRecordType() = HeartRateRecord::class
}
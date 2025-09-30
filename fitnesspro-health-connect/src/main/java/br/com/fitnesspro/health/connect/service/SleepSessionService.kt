package br.com.fitnesspro.health.connect.service

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.SleepSessionRecord
import br.com.android.health.connect.toolkit.service.AbstractBaseHealthConnectService

/**
 * Serviço especializado para leitura de dados de [SleepSessionRecord] (Sessão de Sono)
 * do Health Connect.
 *
 * @see AbstractBaseHealthConnectService
 *
 * @author Nikolas Luiz Schmitt
 */
class SleepSessionService(client: HealthConnectClient): AbstractBaseHealthConnectService<SleepSessionRecord>(client) {
    override fun getRecordType() = SleepSessionRecord::class
}
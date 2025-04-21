package br.com.fitnesspro.common.repository

import android.content.Context
import android.os.Build
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.DeviceDAO
import br.com.fitnesspro.mappers.DeviceModelMapper
import br.com.fitnesspro.shared.communication.dtos.serviceauth.DeviceDTO
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.tasks.await

class DeviceRepository(
    context: Context,
    private val deviceDAO: DeviceDAO,
    private val deviceModelMapper: DeviceModelMapper
) : FitnessProRepository(context) {

    suspend fun saveDeviceLocally(deviceDTO: DeviceDTO) {
        val device = deviceModelMapper.getDevice(deviceDTO)

        if (deviceDAO.findById(deviceDTO.id!!) == null) {
            deviceDAO.insert(device)
        } else {
            deviceDAO.update(device)
        }
    }

    suspend fun getDeviceDTO(): DeviceDTO {
        val deviceId = getDeviceIdFromFirebase()

        return deviceDAO.findById(deviceId)?.let(deviceModelMapper::getDeviceDTO) ?: DeviceDTO(
            id = deviceId,
            model = Build.MODEL,
            brand = Build.BRAND,
            androidVersion = Build.VERSION.RELEASE
        )
    }

    suspend fun getDeviceIdFromFirebase(): String {
        return FirebaseInstallations.getInstance().id.await()
    }
}
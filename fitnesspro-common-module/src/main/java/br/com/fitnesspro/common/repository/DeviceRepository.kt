package br.com.fitnesspro.common.repository

import android.content.Context
import android.os.Build
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.DeviceDAO
import br.com.fitnesspro.mappers.getDevice
import br.com.fitnesspro.mappers.getDeviceDTO
import br.com.fitnesspro.shared.communication.dtos.serviceauth.DeviceDTO
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.tasks.await

class DeviceRepository(
    context: Context,
    private val deviceDAO: DeviceDAO,
) : FitnessProRepository(context) {

    suspend fun saveDeviceLocally(deviceDTO: DeviceDTO) {
        val device = deviceDTO.getDevice()

        if (deviceDAO.findById(deviceDTO.id!!) == null) {
            deviceDAO.insert(device)
        } else {
            deviceDAO.update(device)
        }
    }

    suspend fun getDeviceDTO(): DeviceDTO {
        val deviceId = getDeviceIdFromFirebase()

        return deviceDAO.findById(deviceId)?.getDeviceDTO() ?: DeviceDTO(
            id = deviceId,
            model = Build.MODEL,
            brand = Build.BRAND,
            androidVersion = Build.VERSION.RELEASE,
            active = true,
        )
    }

    suspend fun getDeviceIdFromFirebase(): String {
        return FirebaseInstallations.getInstance().id.await()
    }
}
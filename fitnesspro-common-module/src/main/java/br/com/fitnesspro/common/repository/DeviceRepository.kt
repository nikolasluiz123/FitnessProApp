package br.com.fitnesspro.common.repository

import android.content.Context
import android.os.Build
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.DeviceDAO
import br.com.fitnesspro.mappers.getDevice
import br.com.fitnesspro.mappers.getDeviceDTO
import br.com.fitnesspro.mappers.getPersonDTO
import br.com.fitnesspro.shared.communication.dtos.serviceauth.DeviceDTO
import com.google.firebase.Firebase
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.tasks.await
import java.time.ZoneId

class DeviceRepository(
    context: Context,
    private val deviceDAO: DeviceDAO,
    private val personRepository: PersonRepository
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
        val personDTO = personRepository.getAuthenticatedTOPerson()!!.getPersonDTO()

        val deviceDTO = deviceDAO.findById(deviceId)?.getDeviceDTO(personDTO)?.copy(
            androidVersion = Build.VERSION.RELEASE,
            firebaseMessagingToken = Firebase.messaging.token.await(),
            zoneId = ZoneId.systemDefault().id,
        )

        return deviceDTO ?: DeviceDTO(
            id = deviceId,
            model = Build.MODEL,
            brand = Build.BRAND,
            androidVersion = Build.VERSION.RELEASE,
            active = true,
            firebaseMessagingToken = Firebase.messaging.token.await(),
            zoneId = ZoneId.systemDefault().id,
            person = personDTO
        )
    }

    suspend fun getDeviceIdFromFirebase(): String {
        return FirebaseInstallations.getInstance().id.await()
    }
}
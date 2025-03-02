package br.com.fitnesspro.local.data.access.dao.common

import androidx.room.Update
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState

abstract class IntegratedMaintenanceDAO<T: IntegratedModel>: MaintenanceDAO<T>() {

    open suspend fun update(model: T, writeTransmissionState: Boolean = false) {
        if (writeTransmissionState) {
            model.transmissionState = EnumTransmissionState.PENDING
        }

        internalUpdate(model)
    }

    open suspend fun updateBatch(models: List<T>, writeTransmissionState: Boolean = false) {
        if (writeTransmissionState) {
            models.forEach {
                it.transmissionState = EnumTransmissionState.PENDING
            }
        }

        internalUpdateBatch(models)
    }

    @Update
    protected abstract suspend fun internalUpdate(model: T)

    @Update
    protected abstract suspend fun internalUpdateBatch(models: List<T>)
}
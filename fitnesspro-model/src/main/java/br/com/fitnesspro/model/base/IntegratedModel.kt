package br.com.fitnesspro.model.base

import br.com.fitnesspro.model.enums.EnumTransmissionState

interface IntegratedModel: BaseModel {
    var transmissionState: EnumTransmissionState
}
package br.com.fitnesspro.model.base

import br.com.fitnesspro.model.enums.EnumTransmissionState
import java.time.LocalDateTime

interface StorageModel {
    var storageTransmissionDate: LocalDateTime?
    var storageTransmissionState: EnumTransmissionState
    var storageUrl: String?
}
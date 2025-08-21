package br.com.fitnesspro.model.base

import br.com.fitnesspro.model.enums.EnumTransmissionState
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

interface StorageModel {
    var storageTransmissionDate: LocalDateTime?
    var storageTransmissionState: EnumTransmissionState
    var storageUrl: String?
    var storageUrlExpiration: Long?
    var expirationUnit: TimeUnit?
}
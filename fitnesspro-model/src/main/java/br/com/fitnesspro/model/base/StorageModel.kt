package br.com.fitnesspro.model.base

import br.com.fitnesspro.model.enums.EnumTransmissionState
import java.util.concurrent.TimeUnit

interface StorageModel {
    var storageTransmissionState: EnumTransmissionState
    var storageUrl: String?
    var storageUrlExpiration: Long?
    var expirationUnit: TimeUnit?
}
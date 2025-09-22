package br.com.fitnesspro.model.base

import br.com.fitnesspro.model.enums.EnumDownloadState
import br.com.fitnesspro.model.enums.EnumTransmissionState
import java.time.LocalDateTime

interface StorageModel {
    var storageTransmissionDate: LocalDateTime?
    var storageTransmissionState: EnumTransmissionState
    var storageDownloadState: EnumDownloadState
    var storageUrl: String?
}
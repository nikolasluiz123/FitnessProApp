package br.com.fitnesspro.model.base

import java.time.LocalDateTime

abstract class AuditableModel: BaseModel() {
    abstract var creationDate: LocalDateTime
    abstract var updateDate: LocalDateTime
    abstract var creationUserId: String?
    abstract var updateUserId: String?
}
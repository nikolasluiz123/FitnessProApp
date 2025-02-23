package br.com.fitnesspro.model.base

import java.time.LocalDateTime

abstract class IntegratedModel: AuditableModel() {
    abstract var transmissionDate: LocalDateTime?
}
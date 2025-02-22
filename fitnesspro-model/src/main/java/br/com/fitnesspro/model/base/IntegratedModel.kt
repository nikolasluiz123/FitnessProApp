package br.com.fitnesspro.model.base

import java.time.LocalDateTime

abstract class IntegratedModel: BaseModel() {
    abstract var transmissionDate: LocalDateTime?
}
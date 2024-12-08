package br.com.fitnesspro.core.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class TransferObject(val modelClass: KClass<*>)

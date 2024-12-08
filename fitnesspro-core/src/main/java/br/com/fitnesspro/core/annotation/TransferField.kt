package br.com.fitnesspro.core.annotation

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FIELD)
annotation class TransferField(val fieldName: String)

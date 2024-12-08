package br.com.fitnesspro.helper

import android.content.Context
import br.com.fitnesspro.core.R
import br.com.fitnesspro.core.annotation.TransferField
import br.com.fitnesspro.core.annotation.TransferObject
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.to.BaseTO
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

class TransferObjectHelper(
    private val context: Context
) {

    fun <MODEL: BaseModel, TO: BaseTO> transferObjectToModel(to: TO, modelClass: KClass<MODEL>): MODEL {
        validateTransferObjectAnnotation(to)
        validateTransferFieldAnnotation(to)

        val transferFields = getTransferFields(to)
        val modelInstance = modelClass.createInstance()

        transferFields.forEach { field ->
            val transferFieldAnnotation = field.findAnnotation<TransferField>()!!

            when (val value = field.getter.call(to)) {
                is BaseTO -> {
                    val modelProperty = getModelMutableProperty(modelClass, transferFieldAnnotation)
                    modelProperty.setter.call(modelInstance, value.id)
                }

                else -> {
                    val modelProperty = getModelMutableProperty(modelClass, transferFieldAnnotation)
                    modelProperty.setter.call(modelInstance, value)
                }
            }
        }

        return modelInstance
    }

    private fun <TO: BaseTO> validateTransferObjectAnnotation(to: TO) {
        val isTransferObject = to::class.hasAnnotation<TransferObject>()

        if (!isTransferObject) {
            throw IllegalArgumentException(
                context.getString(
                    R.string.transfer_object_helper_msg_not_has_transfer_object_annotation,
                    to::class.simpleName,
                    TransferObject::class.simpleName
                )
            )
        }
    }

    private fun <TO: BaseTO> validateTransferFieldAnnotation(to: TO) {
        val hasTransferFields = to::class.memberProperties.any { it.hasAnnotation<TransferField>() }

        if (!hasTransferFields) {
            throw IllegalArgumentException(
                context.getString(
                    R.string.transfer_object_helper_msg_not_has_transfer_fields,
                    to::class.simpleName,
                    TransferField::class.simpleName
                )
            )
        }
    }

    private fun <TO : BaseTO> getTransferFields(to: TO): List<KProperty1<out TO, *>> {
        return to::class.memberProperties.filter { it.hasAnnotation<TransferField>() }
    }

    private fun <MODEL : BaseModel> getModelMutableProperty(
        modelClass: KClass<MODEL>,
        transferFieldAnnotation: TransferField
    ): KMutableProperty1<MODEL, *> {
        return modelClass.memberProperties
            .filterIsInstance<KMutableProperty1<MODEL, *>>()
            .first { it.name == transferFieldAnnotation.fieldName }
    }

}
package br.com.fitnesspro.common.repository.sync.importation.common

import br.com.android.room.toolkit.model.interfaces.BaseModel
import kotlin.reflect.KClass

data class ImportSegregationResult<T : BaseModel>(
    val insertionList: List<T>,
    val updateList: List<T>,
    val modelClass: KClass<out BaseModel>
)
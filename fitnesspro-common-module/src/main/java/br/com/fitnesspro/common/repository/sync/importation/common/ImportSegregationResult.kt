package br.com.fitnesspro.common.repository.sync.importation.common

import br.com.fitnesspro.model.base.BaseModel

data class ImportSegregationResult(
    val insertionList: List<BaseModel>,
    val updateList: List<BaseModel>
)
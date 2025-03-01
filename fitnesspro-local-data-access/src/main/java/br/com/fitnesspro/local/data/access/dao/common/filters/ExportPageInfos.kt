package br.com.fitnesspro.local.data.access.dao.common.filters

data class ExportPageInfos(
    val pageSize: Int = 200,
    var pageNumber: Int = 0,
)
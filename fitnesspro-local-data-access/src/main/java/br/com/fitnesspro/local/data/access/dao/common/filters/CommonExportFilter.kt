package br.com.fitnesspro.local.data.access.dao.common.filters

import java.time.LocalDateTime

open class CommonExportFilter(val authenticatedUserId: String, val lastUpdateDate: LocalDateTime? = null)
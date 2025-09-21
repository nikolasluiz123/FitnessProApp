package br.com.fitnesspro.common.repository.sync.importation.common

import java.time.LocalDateTime

data class CursorData(
    val cursorIdsMap: MutableMap<String, String?>,
    val cursorTimestampMap: MutableMap<String, LocalDateTime?>,
)

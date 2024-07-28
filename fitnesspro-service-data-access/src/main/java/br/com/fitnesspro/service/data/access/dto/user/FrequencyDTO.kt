package br.com.fitnesspro.service.data.access.dto.user

import com.google.gson.annotations.SerializedName
import java.time.LocalTime

data class FrequencyDTO(
    val id: Long? = null,
    @SerializedName("day_week")
    val dayWeek: String,
    val start: LocalTime?,
    val end: LocalTime?,
    val username: String,
    val academy: Long
)

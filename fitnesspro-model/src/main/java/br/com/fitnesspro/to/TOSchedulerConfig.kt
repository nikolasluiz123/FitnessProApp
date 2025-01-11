package br.com.fitnesspro.to

import java.time.LocalTime

data class TOSchedulerConfig(
    override var id: String? = null,
    var alarm: Boolean = false,
    var notification: Boolean = false,
    var minScheduleDensity: Int? = 1,
    var maxScheduleDensity: Int? = 2,
    var personId: String? = null,
    val startBreakTime: LocalTime? = LocalTime.of(12, 0),
    val endBreakTime: LocalTime? = LocalTime.of(13, 0),
    val startWorkTime: LocalTime? = LocalTime.of(8, 0),
    val endWorkTime: LocalTime? = LocalTime.of(17, 30),
): BaseTO()
package br.com.fitnesspro.to

import br.com.fitnesspro.core.menu.ITupleListItem
import java.time.DayOfWeek

data class TOWorkoutGroup(
    override var id: String? = null,
    var name: String? = null,
    var workoutId: String? = null,
    var dayWeek: DayOfWeek? = null,
    var active: Boolean = true
): BaseTO, ITupleListItem {

    override fun getLabel(): String = name ?: ""
}
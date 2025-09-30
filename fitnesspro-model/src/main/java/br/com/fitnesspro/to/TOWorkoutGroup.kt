package br.com.fitnesspro.to

import br.com.core.android.utils.interfaces.ISimpleListItem
import java.time.DayOfWeek

data class TOWorkoutGroup(
    override var id: String? = null,
    var name: String? = null,
    var workoutId: String? = null,
    var dayWeek: DayOfWeek? = null,
    var order: Int? = null,
    var active: Boolean = true
): BaseTO, ISimpleListItem {

    override fun getLabel(): String = name ?: ""
}
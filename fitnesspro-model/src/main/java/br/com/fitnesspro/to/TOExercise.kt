package br.com.fitnesspro.to

import br.com.fitnesspro.core.menu.ITupleListItem
import java.time.DayOfWeek
import java.time.temporal.ChronoUnit

data class TOExercise(
    override var id: String? = null,
    var name: String? = null,
    var duration: Long? = null,
    var unitDuration: ChronoUnit? = null,
    var repetitions: Int? = null,
    var sets: Int? = null,
    var rest: Long? = null,
    var unitRest: ChronoUnit? = null,
    var observation: String? = null,
    var workoutId: String? = null,
    var workoutGroupId: String? = null,
    var workoutGroupName: String? = null,
    var active: Boolean = true,
    var preDefinition: Boolean = false,
    var dayOfWeek: DayOfWeek? = null
): BaseTO, ITupleListItem {

    override fun getLabel(): String {
        return name!!
    }
}
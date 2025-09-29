package br.com.fitnesspro.to

import br.com.core.android.utils.interfaces.ISimpleListItem
import br.com.core.utils.extensions.bestChronoUnit
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
    var dayWeek: DayOfWeek? = null,
    var groupOrder: Int? = null,
    var exerciseOrder: Int? = null,
): BaseTO, ISimpleListItem {

    init {
        if (unitDuration == null) {
            unitDuration = duration?.bestChronoUnit()
        }

        if (unitRest == null) {
            unitRest = rest?.bestChronoUnit()
        }
    }

    override fun getLabel(): String {
        return name!!
    }
}
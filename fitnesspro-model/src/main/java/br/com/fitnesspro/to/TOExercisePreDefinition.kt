package br.com.fitnesspro.to

import br.com.core.android.utils.interfaces.ISimpleListItem
import java.time.temporal.ChronoUnit

data class TOExercisePreDefinition(
    override var id: String? = null,
    var name: String? = null,
    var duration: Long? = null,
    var unitDuration: ChronoUnit? = null,
    var repetitions: Int? = null,
    var sets: Int? = null,
    var rest: Long? = null,
    var unitRest: ChronoUnit? = null,
    var personalTrainerPersonId: String? = null,
    var workoutGroupPreDefinitionId: String? = null,
    var exerciseOrder: Int? = null,
    var active: Boolean = true
): BaseTO, ISimpleListItem {
    override fun getLabel(): String {
        return name ?: ""
    }
}
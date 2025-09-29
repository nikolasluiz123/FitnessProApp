package br.com.fitnesspro.workout.ui.screen.members.workout

import br.com.core.utils.extensions.dateNow
import br.com.fitnesspro.to.TOWorkout
import br.com.fitnesspro.workout.ui.state.MembersWorkoutUIState
import java.time.ZoneId

internal val defaultMemberWorkoutItem = TOWorkout(
    memberName = "Nikolas Luiz Schmitt",
    dateEnd = dateNow(ZoneId.systemDefault())
)

internal val emptyMembersWorkoutState = MembersWorkoutUIState()

internal val membersWorkoutState = MembersWorkoutUIState(
    workouts = listOf(
        defaultMemberWorkoutItem,
        defaultMemberWorkoutItem.copy(memberName = "João da Silva"),
        defaultMemberWorkoutItem.copy(
            memberName = "Maria Santos",
            dateEnd = dateNow(ZoneId.systemDefault()).plusDays(1)
        )
    )
)
package br.com.fitnesspro.workout.ui.screen.members.workout

import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.workout.ui.screen.members.workout.decorator.MemberWorkoutDecorator
import br.com.fitnesspro.workout.ui.state.MembersWorkoutUIState
import java.time.ZoneId

internal val defaultMemberWorkoutItem = MemberWorkoutDecorator(
    memberName = "Nikolas Luiz Schmitt",
    workoutEndDate = dateNow(ZoneId.systemDefault())
)

internal val emptyMembersWorkoutState = MembersWorkoutUIState()

internal val membersWorkoutState = MembersWorkoutUIState(
    members = listOf(
        defaultMemberWorkoutItem,
        defaultMemberWorkoutItem.copy(memberName = "João da Silva"),
        defaultMemberWorkoutItem.copy(
            memberName = "Maria Santos",
            workoutEndDate = dateNow(ZoneId.systemDefault()).plusDays(1)
        )
    )
)
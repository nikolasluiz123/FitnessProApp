package br.com.fitnesspro.workout.ui.screen.evolution

import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.tuple.PersonTuple
import br.com.fitnesspro.workout.ui.state.MembersEvolutionUIState
import kotlinx.coroutines.flow.flowOf

internal val defaultPersonTuple = PersonTuple(
    id = "1",
    name = "Nikolas Luiz Schmitt",
    userType = EnumUserType.PERSONAL_TRAINER
)

internal val defaultMembersEvolutionState = MembersEvolutionUIState(
    persons = flowOf(listOf(defaultPersonTuple, defaultPersonTuple, defaultPersonTuple))
)

internal val emptyMembersEvolutionState = MembersEvolutionUIState()
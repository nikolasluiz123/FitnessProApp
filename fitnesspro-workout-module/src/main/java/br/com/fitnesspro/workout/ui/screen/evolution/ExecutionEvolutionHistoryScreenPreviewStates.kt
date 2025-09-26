package br.com.fitnesspro.workout.ui.screen.evolution

import androidx.paging.PagingData
import br.com.fitnesspro.tuple.ExecutionEvolutionHistoryGroupedTuple
import br.com.fitnesspro.workout.ui.state.ExecutionEvolutionHistoryUIState
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

internal val defaultExecutionEvolutionHistoryState = ExecutionEvolutionHistoryUIState(
    history = flowOf(
        PagingData.from(
            listOf(
                ExecutionEvolutionHistoryGroupedTuple(
                    sortOrder = 0,
                    dateStart = LocalDate.now(),
                    dateEnd = LocalDate.now()
                ),
                ExecutionEvolutionHistoryGroupedTuple(
                    sortOrder = 1,
                    exerciseId = "1",
                    exerciseName = "Supino Reto com Barra"
                ),
                ExecutionEvolutionHistoryGroupedTuple(
                    sortOrder = 1,
                    exerciseId = "2",
                    exerciseName = "Agachamento Livre"
                ),
                ExecutionEvolutionHistoryGroupedTuple(
                    sortOrder = 0,
                    dateStart = LocalDate.now().minusDays(5),
                    dateEnd = LocalDate.now().minusDays(5)
                ),
                ExecutionEvolutionHistoryGroupedTuple(
                    sortOrder = 1,
                    exerciseId = "3",
                    exerciseName = "Levantamento Terra"
                ),
            )
        )
    )
)
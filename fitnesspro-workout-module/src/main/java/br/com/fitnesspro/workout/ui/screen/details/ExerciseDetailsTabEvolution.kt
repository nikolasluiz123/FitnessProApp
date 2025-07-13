package br.com.fitnesspro.workout.ui.screen.details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.ui.state.ExerciseDetailsUIState

@Composable
fun ExerciseDetailsTabEvolution(
    state: ExerciseDetailsUIState
) {
    ConstraintLayout(Modifier.fillMaxSize()) {

    }
}

@Preview
@Composable
private fun ExerciseDetailsTabEvolutionPreview() {
    Surface {
        FitnessProTheme {
            ExerciseDetailsTabEvolution(
                state = defaultExerciseDetailsUIState
            )
        }
    }
}

@Preview
@Composable
private fun ExerciseDetailsTabEvolutionPreviewDark() {
    Surface {
        FitnessProTheme(darkTheme = true) {
            ExerciseDetailsTabEvolution(
                state = defaultExerciseDetailsUIState
            )
        }
    }
}
package br.com.fitnesspro.workout.ui.screen.evolution

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.filter.SimpleFilter
import br.com.fitnesspro.compose.components.list.LazyVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.tuple.PersonTuple
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.state.MembersEvolutionUIState
import br.com.fitnesspro.workout.ui.viewmodel.MembersEvolutionViewModel

@Composable
fun MembersEvolutionScreen(
    viewModel: MembersEvolutionViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MembersEvolutionScreen(
        state = state,
        onBackClick = onBackClick,
        onExecuteLoad = viewModel::loadStateWithDatabaseInfos
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersEvolutionScreen(
    state: MembersEvolutionUIState = MembersEvolutionUIState(),
    onBackClick: () -> Unit = {},
    onExecuteLoad: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = stringResource(R.string.members_evolution_screen_title),
                onBackClick = onBackClick
            )
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .consumeWindowInsets(it)
                .fillMaxSize()
        ) {
            LaunchedEffect(state.executeLoad) {
                if (state.executeLoad) {
                    onExecuteLoad()
                }
            }

            FitnessProMessageDialog(state.messageDialogState)

            SimpleFilter(
                modifier = Modifier.fillMaxWidth(),
                state = state.simpleFilterState,
                placeholderResId = R.string.members_evolution_screen_simple_filter_placeholder
            ) {
                MembersList(
                    state = state,
                )
            }

            MembersList(
                state = state,
            )
        }
    }
}

@Composable
private fun MembersList(
    state: MembersEvolutionUIState,
    onMemberClick: (PersonTuple) -> Unit = {}
) {
    LazyVerticalList(
        modifier = Modifier.fillMaxSize(),
        itemsFlow = state.persons,
        emptyMessageResId = R.string.members_evolution_empty_message,
    ) { tuple ->
        MemberItem(
            personTuple = tuple,
            onClick = onMemberClick
        )
    }
}

@Preview
@Composable
private fun MembersEvolutionScreenLightPreview() {
    FitnessProTheme {
        Surface {
            MembersEvolutionScreen(
                state = defaultMembersEvolutionState
            )
        }
    }
}

@Preview
@Composable
private fun MembersEvolutionEmptyScreenLightPreview() {
    FitnessProTheme {
        Surface {
            MembersEvolutionScreen(
                state = emptyMembersEvolutionState
            )
        }
    }
}


@Preview
@Composable
private fun MembersEvolutionScreenDarkPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            MembersEvolutionScreen(
                state = defaultMembersEvolutionState
            )
        }
    }
}

@Preview
@Composable
private fun MembersEvolutionEmptyScreenDarkPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            MembersEvolutionScreen(
                state = emptyMembersEvolutionState
            )
        }
    }
}
package br.com.fitnesspro.workout.ui.screen.members.workout

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.filter.SimpleFilter
import br.com.fitnesspro.compose.components.list.LazyVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.state.MembersWorkoutUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersWorkoutScreen(
    state: MembersWorkoutUIState
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = stringResource(R.string.members_workout_screen_title),
            )
        }
    ) { paddingValues ->
        ConstraintLayout(
            Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize()
        ) {
            val (listRef, filterRef) = createRefs()

            FitnessProMessageDialog(state = state.messageDialogState)

            SimpleFilter(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(filterRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                onSimpleFilterChange = { },
                onExpandedChange = { },
                expanded = false,
                placeholderResId = R.string.members_workout_screen_simple_filter_placeholder
            ) {
                LazyVerticalList(
                    modifier = Modifier.fillMaxSize(),
                    items = state.members,
                    emptyMessageResId = R.string.members_workout_screen_empty_message
                ) {
                    MemberWorkoutItem(it)
                }
            }

            LazyVerticalList(
                modifier = Modifier
                    .constrainAs(listRef) {
                        top.linkTo(filterRef.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                items = state.members,
                emptyMessageResId = R.string.members_workout_screen_empty_message
            ) {
                MemberWorkoutItem(it)
            }
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun MembersWorkoutScreenEmptyPreview() {
    FitnessProTheme {
        Surface {
            MembersWorkoutScreen(
                state = emptyMembersWorkoutState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun MembersWorkoutScreenEmptyPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            MembersWorkoutScreen(
                state = emptyMembersWorkoutState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun MembersWorkoutScreenPreview() {
    FitnessProTheme {
        Surface {
            MembersWorkoutScreen(
                state = membersWorkoutState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun MembersWorkoutScreenPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            MembersWorkoutScreen(
                state = membersWorkoutState
            )
        }
    }
}
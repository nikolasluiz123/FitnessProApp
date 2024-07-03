package br.com.fitnesspro.ui.screen.registeruser

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.bottombar.FitnessProBottomAppBar
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonArrowDown
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonTime
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.ui.state.RegisterAcademyUIState
import br.com.fitnesspro.ui.viewmodel.RegisterAcademyViewModel
import br.com.market.market.compose.components.button.fab.FloatingActionButtonSave

@Composable
fun RegisterAcademyScreen(
    viewModel: RegisterAcademyViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    RegisterAcademyScreen(
        state = state,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAcademyScreen(
    state: RegisterAcademyUIState = RegisterAcademyUIState(),
    onBackClick: () -> Unit = { }
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                subtitle = state.subtitle,
                onBackClick = onBackClick,
                showMenuWithLogout = false
            )
        },
        bottomBar = {
            FitnessProBottomAppBar(
                floatingActionButton = {
                    FloatingActionButtonSave(
                        onClick = {

                        }
                    )
                },
                actions = {
                    IconButtonDelete(
                        onClick = {

                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .padding(paddingValues)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            val (gymRef, dayWeekRef, startRef, endRef) = createRefs()
            var academiesOpen by remember { mutableStateOf(false) }
            var dayWeeksOpen by remember { mutableStateOf(false) }
            val focusRequester = remember { FocusRequester() }
            val keyboard = LocalSoftwareKeyboardController.current

            ExposedDropdownMenuBox(
                modifier = Modifier.constrainAs(gymRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }.fillMaxWidth(),
                expanded = academiesOpen,
                onExpandedChange = { academiesOpen = false }
            ) {
                OutlinedTextFieldValidation(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .menuAnchor()
                        .fillMaxWidth(),
                    field = state.academy,
                    label = stringResource(R.string.register_user_screen_label_gym),
                    trailingIcon = {
                        IconButtonArrowDown {
                            academiesOpen = !academiesOpen
                            focusRequester.requestFocus()
                            keyboard?.show()
                        }
                    }
                )

                DropdownMenu(
                    modifier = Modifier
                        .exposedDropdownSize()
                        .fillMaxHeight(fraction = 0.5f),
                    expanded = academiesOpen,
                    onDismissRequest = { academiesOpen = false }
                ) {
                    state.filteredAcademies.forEach { academy ->
                        DropdownMenuItem(
                            text = {
                                Text(text = academy.label)
                            },
                            onClick = {
                                state.academy.onChange(academy.label)
                                academiesOpen = false
                            }
                        )
                    }
                }
            }

            OutlinedTextFieldValidation(
                modifier = Modifier.constrainAs(dayWeekRef) {
                    start.linkTo(parent.start)
                    top.linkTo(gymRef.bottom)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                },
                field = state.dayWeek,
                label = stringResource(R.string.register_user_screen_label_day_week),
                trailingIcon = {
                    IconButtonArrowDown {
                        dayWeeksOpen = !dayWeeksOpen
                    }
                }
            )

            createHorizontalChain(startRef, endRef)

            OutlinedTextFieldValidation(
                modifier = Modifier
                    .constrainAs(startRef) {
                        start.linkTo(parent.start)
                        top.linkTo(dayWeekRef.bottom)

                        width = Dimension.fillToConstraints
                        horizontalChainWeight = 0.5f
                    }
                    .padding(end = 8.dp),
                field = state.start,
                label = stringResource(R.string.register_user_screen_label_start),
                trailingIcon = {
                    IconButtonTime {

                    }
                }
            )

            OutlinedTextFieldValidation(
                modifier = Modifier.constrainAs(endRef) {
                    top.linkTo(dayWeekRef.bottom)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
                field = state.start,
                label = stringResource(R.string.register_user_screen_label_end),
                trailingIcon = {
                    IconButtonTime {

                    }
                }
            )
        }

    }

}

@Preview
@Composable
private fun RegisterAcademyPreview() {
    FitnessProTheme {
        Surface {
            RegisterAcademyScreen()
        }
    }
}
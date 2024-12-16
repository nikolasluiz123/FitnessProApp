package br.com.fitnesspro.ui.screen.registeruser

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.bottombar.FitnessProBottomAppBar
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonTime
import br.com.fitnesspro.compose.components.dialog.FitnessProDialog
import br.com.fitnesspro.compose.components.dialog.TimePickerInput
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.transformation.TimeVisualTransformation
import br.com.fitnesspro.compose.components.menu.DefaultExposedDropdownMenu
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.SnackBarTextStyle
import br.com.fitnesspro.ui.screen.registeruser.callback.OnSaveAcademyClick
import br.com.fitnesspro.ui.state.RegisterAcademyUIState
import br.com.fitnesspro.ui.viewmodel.RegisterAcademyViewModel
import br.com.market.market.compose.components.button.fab.FloatingActionButtonSave
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun RegisterAcademyScreen(
    viewModel: RegisterAcademyViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    RegisterAcademyScreen(
        state = state,
        onBackClick = onBackClick,
        onSaveAcademyClick = viewModel::saveAcademy
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAcademyScreen(
    state: RegisterAcademyUIState = RegisterAcademyUIState(),
    onBackClick: () -> Unit = { },
    onSaveAcademyClick: OnSaveAcademyClick? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

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
                            onSaveAcademyClick?.onExecute(
                                onSaved = {
                                    showSuccessMessage(coroutineScope, snackbarHostState, context)
                                }
                            )
                        }
                    )
                },
                actions = {
                    IconButtonDelete(
                        onClick = {

                        },
                        enabled = state.toPersonAcademyTime != null
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(modifier = Modifier.padding(8.dp)) {
                    Text(text = it.visuals.message, style = SnackBarTextStyle)
                }
            }
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
            var timePickerStartOpen by remember { mutableStateOf(false) }
            var timePickerEndOpen by remember { mutableStateOf(false) }

            FitnessProDialog(
                type = state.dialogType,
                show = state.showDialog,
                onDismissRequest = { state.onHideDialog() },
                message = state.dialogMessage
            )

            DefaultExposedDropdownMenu(
                modifier = Modifier
                    .constrainAs(gymRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .fillMaxWidth(),
                field = state.academy,
                labelResId = R.string.register_user_screen_label_gym,
                expanded = academiesOpen,
                onExpandedChange = { academiesOpen = !academiesOpen },
                onMenuDismissRequest = { academiesOpen = false },
                onItemClick = {
                    state.academy.onChange(it.label)
                    academiesOpen = false
                },
                items = state.academies
            )

            DefaultExposedDropdownMenu(
                modifier = Modifier.constrainAs(dayWeekRef) {
                    start.linkTo(parent.start)
                    top.linkTo(gymRef.bottom)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                },
                field = state.dayWeek,
                labelResId = R.string.register_user_screen_label_day_week,
                expanded = dayWeeksOpen,
                onExpandedChange = { dayWeeksOpen = !dayWeeksOpen },
                onMenuDismissRequest = { dayWeeksOpen = false },
                onItemClick = {
                    state.dayWeek.onChange(it.label)
                    dayWeeksOpen = false
                },
                items = state.dayWeeks
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
                    IconButtonTime { timePickerStartOpen = true }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                visualTransformation = TimeVisualTransformation(),
                maxLength = 4
            )

            if (timePickerStartOpen) {
                TimePickerInput(
                    title = stringResource(R.string.register_academy_label_start),
                    onConfirm = {
                        state.start.onChange(it.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS))
                    },
                    onDismiss = { timePickerStartOpen = false }
                )
            }

            OutlinedTextFieldValidation(
                modifier = Modifier.constrainAs(endRef) {
                    top.linkTo(dayWeekRef.bottom)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
                field = state.end,
                label = stringResource(R.string.register_user_screen_label_end),
                trailingIcon = {
                    IconButtonTime { timePickerEndOpen = true }
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSaveAcademyClick?.onExecute(
                            onSaved = {
                                showSuccessMessage(coroutineScope, snackbarHostState, context)
                            }
                        )
                    }
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                visualTransformation = TimeVisualTransformation(),
                maxLength = 4
            )

            if (timePickerEndOpen) {
                TimePickerInput(
                    title = stringResource(R.string.register_academy_label_end),
                    onConfirm = {
                        state.end.onChange(it.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS))
                    },
                    onDismiss = { timePickerEndOpen = false }
                )
            }
        }

    }

}

private fun showSuccessMessage(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    coroutineScope.launch {
        snackbarHostState.showSnackbar(
            message = context.getString(R.string.register_academy_screen_success_message)
        )
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
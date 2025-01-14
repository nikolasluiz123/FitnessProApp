package br.com.fitnesspro.common.ui.screen.registeruser

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.screen.registeruser.callback.OnSaveAcademyClick
import br.com.fitnesspro.common.ui.state.RegisterAcademyUIState
import br.com.fitnesspro.common.ui.viewmodel.RegisterAcademyViewModel
import br.com.fitnesspro.compose.components.bottombar.FitnessProBottomAppBar
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonSave
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.fields.PagedListDialogOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.TimePickerOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.menu.DefaultExposedDropdownMenu
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.SnackBarTextStyle
import br.com.fitnesspro.core.theme.ValueTextStyle
import br.com.fitnesspro.tuple.AcademyTuple
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
                        enabled = state.toPersonAcademyTime.id != null
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

            FitnessProMessageDialog(
                type = state.dialogType,
                show = state.showDialog,
                onDismissRequest = { state.onHideDialog() },
                message = state.dialogMessage
            )

            PagedListDialogOutlinedTextFieldValidation(
                field = state.academy,
                fieldLabel = stringResource(R.string.register_user_screen_label_gym),
                simpleFilterPlaceholderResId = R.string.register_user_screen_simple_filter_placeholder_gym_dialog_list,
                itemLayout = { academyTuple ->
                    DialogListItem(
                        academy = academyTuple,
                        onItemClick = state.academy.onDataListItemClick
                    )
                },
                modifier = Modifier.constrainAs(gymRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
            )

            DefaultExposedDropdownMenu(
                modifier = Modifier.constrainAs(dayWeekRef) {
                    start.linkTo(parent.start)
                    top.linkTo(gymRef.bottom, margin = 8.dp)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                },
                field = state.dayWeek,
                labelResId = R.string.register_user_screen_label_day_week,
            )


            TimePickerOutlinedTextFieldValidation(
                field = state.start,
                fieldLabel = stringResource(R.string.register_user_screen_label_start),
                timePickerTitle = stringResource(R.string.register_academy_label_start),
                modifier = Modifier
                    .constrainAs(startRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(dayWeekRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                    },
            )

            TimePickerOutlinedTextFieldValidation(
                field = state.end,
                fieldLabel = stringResource(R.string.register_user_screen_label_end),
                timePickerTitle = stringResource(R.string.register_academy_label_end),
                modifier = Modifier.constrainAs(endRef) {
                    top.linkTo(startRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                },
            )
        }
    }
}

@Composable
fun DialogListItem(academy: AcademyTuple, onItemClick: (AcademyTuple) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(academy) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(12.dp),
            text = academy.name,
            style = ValueTextStyle.copy(fontSize = 16.sp)
        )
    }

    HorizontalDivider()
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
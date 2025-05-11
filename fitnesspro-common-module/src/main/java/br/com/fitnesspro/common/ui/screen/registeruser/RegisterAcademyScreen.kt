package br.com.fitnesspro.common.ui.screen.registeruser

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.screen.registeruser.callback.OnInactivateAcademyClick
import br.com.fitnesspro.common.ui.screen.registeruser.callback.OnSaveAcademyClick
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_ACTION_BUTTON_DELETE
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_DIALOG_ACADEMIES_LIST_ITEM
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FAB_SAVE
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FIELD_ACADEMY
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FIELD_DAY_WEEK
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FIELD_END
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FIELD_START
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_KEYBOARD_SAVE
import br.com.fitnesspro.common.ui.state.RegisterAcademyUIState
import br.com.fitnesspro.common.ui.viewmodel.RegisterAcademyViewModel
import br.com.fitnesspro.compose.components.bottombar.FitnessProBottomAppBar
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonSave
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.fields.PagedListDialogOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.TimePickerOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.menu.DefaultExposedDropdownMenu
import br.com.fitnesspro.compose.components.loading.FitnessProLinearProgressIndicator
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.SnackBarTextStyle
import br.com.fitnesspro.core.theme.ValueTextStyle
import br.com.fitnesspro.firebase.api.analytics.logButtonClick
import br.com.fitnesspro.tuple.AcademyTuple
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
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
        onSaveAcademyClick = viewModel::saveAcademy,
        onInactivateAcademyClick = viewModel::inactivateAcademy
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAcademyScreen(
    state: RegisterAcademyUIState = RegisterAcademyUIState(),
    onBackClick: () -> Unit = { },
    onSaveAcademyClick: OnSaveAcademyClick? = null,
    onInactivateAcademyClick: OnInactivateAcademyClick? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                subtitle = state.subtitle,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            FitnessProBottomAppBar(
                modifier = Modifier.imePadding(),
                floatingActionButton = {
                    FloatingActionButtonSave(
                        modifier = Modifier.testTag(REGISTER_ACADEMY_SCREEN_FAB_SAVE.name),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        onClick = {
                            keyboardController?.hide()
                            state.onToggleLoading()

                            Firebase.analytics.logButtonClick(REGISTER_ACADEMY_SCREEN_FAB_SAVE)
                            onSaveAcademyClick?.onExecute(
                                onSaved = {
                                    state.onToggleLoading()
                                    showSaveSuccessMessage(coroutineScope, snackbarHostState, context)
                                }
                            )
                        }
                    )
                },
                actions = {
                    IconButtonDelete(
                        modifier = Modifier.testTag(REGISTER_ACADEMY_ACTION_BUTTON_DELETE.name),
                        onClick = {
                            state.onToggleLoading()

                            Firebase.analytics.logButtonClick(REGISTER_ACADEMY_ACTION_BUTTON_DELETE)
                            onInactivateAcademyClick?.onExecute {
                                state.onToggleLoading()
                                showInactivatedSuccessMessage(coroutineScope, snackbarHostState, context)
                            }
                        },
                        enabled = state.isEnabledInactivationButton
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
        val scrollState = rememberScrollState()
        val keyboardController = LocalSoftwareKeyboardController.current

        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            val (loadingRef, containerRef) = createRefs()

            ConstraintLayout(
                Modifier.fillMaxWidth()
            ) {
                FitnessProLinearProgressIndicator(
                    state.showLoading,
                    Modifier.constrainAs(loadingRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                )
            }

            ConstraintLayout(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .imePadding()
                    .constrainAs(containerRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(loadingRef.bottom)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                val (gymRef, dayWeekRef, startRef, endRef) = createRefs()

                FitnessProMessageDialog(state = state.messageDialogState)

                PagedListDialogOutlinedTextFieldValidation(
                    field = state.academy,
                    fieldLabel = stringResource(R.string.register_user_screen_label_gym),
                    simpleFilterPlaceholderResId = R.string.register_user_screen_simple_filter_placeholder_gym_dialog_list,
                    emptyMessage = R.string.register_user_screen_empty_message_gym_dialog_list,
                    itemLayout = { academyTuple ->
                        DialogListItem(
                            academy = academyTuple,
                            onItemClick = state.academy.dialogListState.onDataListItemClick
                        )
                    },
                    modifier = Modifier
                        .testTag(REGISTER_ACADEMY_SCREEN_FIELD_ACADEMY.name)
                        .constrainAs(gymRef) {
                            top.linkTo(parent.top, margin = 12.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)

                            width = Dimension.fillToConstraints
                        }
                )

                DefaultExposedDropdownMenu(
                    modifier = Modifier
                        .testTag(REGISTER_ACADEMY_SCREEN_FIELD_DAY_WEEK.name)
                        .constrainAs(dayWeekRef) {
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
                        .testTag(REGISTER_ACADEMY_SCREEN_FIELD_START.name)
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
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            state.onToggleLoading()

                            Firebase.analytics.logButtonClick(REGISTER_ACADEMY_SCREEN_KEYBOARD_SAVE)
                            onSaveAcademyClick?.onExecute(
                                onSaved = {
                                    state.onToggleLoading()
                                    showSaveSuccessMessage(coroutineScope, snackbarHostState, context)
                                }
                            )
                        }
                    ),
                    modifier = Modifier
                        .testTag(REGISTER_ACADEMY_SCREEN_FIELD_END.name)
                        .constrainAs(endRef) {
                            top.linkTo(startRef.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)

                            width = Dimension.fillToConstraints
                        },
                )
            }
        }
    }
}

fun showInactivatedSuccessMessage(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    coroutineScope.launch {
        snackbarHostState.showSnackbar(
            message = context.getString(R.string.register_academy_screen_inactivate_success_message)
        )
    }
}

@Composable
fun DialogListItem(academy: AcademyTuple, onItemClick: (AcademyTuple) -> Unit) {
    Row(
        Modifier
            .testTag(REGISTER_ACADEMY_DIALOG_ACADEMIES_LIST_ITEM.name)
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

private fun showSaveSuccessMessage(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    coroutineScope.launch {
        snackbarHostState.showSnackbar(
            message = context.getString(R.string.register_academy_screen_success_save_message)
        )
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun RegisterAcademyPreview() {
    FitnessProTheme {
        Surface {
            RegisterAcademyScreen()
        }
    }
}
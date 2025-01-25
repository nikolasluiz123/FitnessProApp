package br.com.fitnesspro.scheduler.ui.screen.scheduler

import android.content.Context
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.buttons.HorizontalLabeledSwitchButton
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonSave
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.state.SwitchButtonField
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_700
import br.com.fitnesspro.core.theme.GREY_800
import br.com.fitnesspro.core.theme.LabelFontWeightMediumTextStyle
import br.com.fitnesspro.core.theme.LabelTextStyle
import br.com.fitnesspro.core.theme.SnackBarTextStyle
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.screen.scheduler.callback.OnSaveSchedulerConfigClick
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_FAB_SAVE
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABELED_SWITCH_BUTTON_ALARM
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABELED_SWITCH_BUTTON_NOTIFICATION
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABEL_BREAK_TIME
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABEL_BREAK_TIME_EXPLANATION
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABEL_DENSITY_EVENTS
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABEL_DENSITY_EVENTS_EXPLANATION
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABEL_GENERAL
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABEL_WORK_TIME
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_LABEL_WORK_TIME_EXPLANATION
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_MAX_DENSITY_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTestTags.SCHEDULER_CONFIG_SCREEN_MIN_DENSITY_FIELD
import br.com.fitnesspro.scheduler.ui.state.SchedulerConfigUIState
import br.com.fitnesspro.scheduler.ui.viewmodel.SchedulerConfigViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SchedulerConfigScreen(
    viewModel: SchedulerConfigViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    SchedulerConfigScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onSaveClick = viewModel::save
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerConfigScreen(
    state: SchedulerConfigUIState,
    onNavigateBack: () -> Unit = { },
    onSaveClick: OnSaveSchedulerConfigClick? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = stringResource(R.string.scheduler_config_screen_title),
                onBackClick = onNavigateBack,
                showMenuWithLogout = false
            )
        },
        floatingActionButton = {
            FloatingActionButtonSave(
                modifier = Modifier.testTag(SCHEDULER_CONFIG_SCREEN_FAB_SAVE.name),
                onClick = {
                    onSaveClick?.onExecute {
                        showSuccessMessage(
                            coroutineScope = coroutineScope,
                            snackbarHostState = snackbarHostState,
                            context = context
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(modifier = Modifier.padding(8.dp)) {
                    Text(text = it.visuals.message, style = SnackBarTextStyle)
                }
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (state.userType) {
                EnumUserType.PERSONAL_TRAINER, EnumUserType.NUTRITIONIST -> {
                    ProfessionalSchedulerConfigScreen(state)
                }

                EnumUserType.ACADEMY_MEMBER -> {
                    MemberSchedulerConfigScreen(state)
                }

                else -> { }
            }
        }
    }
}

@Composable
fun ProfessionalSchedulerConfigScreen(state: SchedulerConfigUIState) {
    val scrollState = rememberScrollState()

    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
            .scrollable(scrollState, orientation = Orientation.Vertical)
    ) {
        val (
            generalRef, labeledCheckboxAlarmRef, labeledCheckboxNotificationRef,
            eventDensityRef, eventDensityExplanationRef, eventDensityMinRef, eventDensityMaxRef,
            workTimeRef, workTimeExplanationRef, workTimeStartRef, workTimeEndRef,
            breakTimeRef, breakTimeExplanationRef, breakTimeStartRef, breakTimeEndRef
        ) = createRefs()

        Text(
            text = stringResource(R.string.scheduler_config_screen_label_general),
            style = LabelFontWeightMediumTextStyle,
            color = GREY_800,
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_LABEL_GENERAL.name)
                .constrainAs(generalRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        HorizontalLabeledSwitchButton(
            field = state.alarm,
            label = stringResource(R.string.scheduler_config_screen_label_alarm),
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_LABELED_SWITCH_BUTTON_ALARM.name)
                .constrainAs(labeledCheckboxAlarmRef) {
                    top.linkTo(generalRef.bottom, 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )

        HorizontalLabeledSwitchButton(
            field = state.notification,
            label = stringResource(R.string.scheduler_config_screen_label_notification),
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_LABELED_SWITCH_BUTTON_NOTIFICATION.name)
                .constrainAs(labeledCheckboxNotificationRef) {
                    top.linkTo(labeledCheckboxAlarmRef.bottom, 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )

        Text(
            text = stringResource(R.string.scheduler_config_screen_label_event_density),
            style = LabelFontWeightMediumTextStyle,
            color = GREY_800,
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_LABEL_DENSITY_EVENTS.name)
                .constrainAs(eventDensityRef) {
                    top.linkTo(labeledCheckboxNotificationRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Text(
            text = stringResource(R.string.scheduler_config_screen_label_event_density_explanation),
            style = LabelTextStyle,
            color = GREY_700,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_LABEL_DENSITY_EVENTS_EXPLANATION.name)
                .constrainAs(eventDensityExplanationRef) {
                    top.linkTo(eventDensityRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        createHorizontalChain(eventDensityMinRef, eventDensityMaxRef)

        OutlinedTextFieldValidation(
            field = state.minEventDensity,
            label = stringResource(R.string.scheduler_config_screen_label_event_density_min),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_MIN_DENSITY_FIELD.name)
                .padding(end = 8.dp)
                .constrainAs(eventDensityMinRef) {
                    top.linkTo(eventDensityExplanationRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                }
        )

        OutlinedTextFieldValidation(
            field = state.maxEventDensity,
            label = stringResource(R.string.scheduler_config_screen_label_event_density_max),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_MAX_DENSITY_FIELD.name)
                .constrainAs(eventDensityMaxRef) {
                    top.linkTo(eventDensityExplanationRef.bottom, margin = 8.dp)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                }
        )

        Text(
            text = stringResource(R.string.scheduler_config_screen_label_work_time),
            style = LabelFontWeightMediumTextStyle,
            color = GREY_800,
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_LABEL_WORK_TIME.name)
                .constrainAs(workTimeRef) {
                    top.linkTo(eventDensityMinRef.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Text(
            text = stringResource(R.string.scheduler_config_screen_label_work_time_explanation),
            style = LabelTextStyle,
            color = GREY_700,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_LABEL_WORK_TIME_EXPLANATION.name)
                .constrainAs(workTimeExplanationRef) {
                    top.linkTo(workTimeRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Text(
            text = stringResource(R.string.scheduler_config_screen_label_break_time),
            style = LabelFontWeightMediumTextStyle,
            color = GREY_800,
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_LABEL_BREAK_TIME.name)
                .constrainAs(breakTimeRef) {
                top.linkTo(workTimeExplanationRef.bottom, margin = 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Text(
            text = stringResource(R.string.scheduler_config_screen_label_break_time_explanation),
            style = LabelTextStyle,
            color = GREY_700,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_LABEL_BREAK_TIME_EXPLANATION.name)
                .constrainAs(breakTimeExplanationRef) {
                top.linkTo(breakTimeRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}

@Composable
fun MemberSchedulerConfigScreen(state: SchedulerConfigUIState) {
    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        val (generalRef, labeledCheckboxAlarmRef, labeledCheckboxNotificationRef) = createRefs()

        Text(
            text = stringResource(R.string.scheduler_config_screen_label_general),
            style = LabelFontWeightMediumTextStyle,
            color = GREY_800,
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_LABEL_GENERAL.name)
                .constrainAs(generalRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        HorizontalLabeledSwitchButton(
            field = state.alarm,
            label = stringResource(R.string.scheduler_config_screen_label_alarm),
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_LABELED_SWITCH_BUTTON_ALARM.name)
                .constrainAs(labeledCheckboxAlarmRef) {
                    top.linkTo(generalRef.bottom, 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )

        HorizontalLabeledSwitchButton(
            field = state.notification,
            label = stringResource(R.string.scheduler_config_screen_label_notification),
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_LABELED_SWITCH_BUTTON_NOTIFICATION.name)
                .constrainAs(labeledCheckboxNotificationRef) {
                    top.linkTo(labeledCheckboxAlarmRef.bottom, 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )
    }
}

private fun showSuccessMessage(
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    coroutineScope.launch {
        snackbarHostState.showSnackbar(message = context.getString(R.string.scheduler_config_screen_message_success))
    }
}

@Preview
@Composable
private fun SchedulerConfigScreenMemberPreview() {
    FitnessProTheme {
        Surface {
            SchedulerConfigScreen(
                state = SchedulerConfigUIState(
                    userType = EnumUserType.ACADEMY_MEMBER
                ),
            )
        }
    }
}

@Preview
@Composable
private fun SchedulerConfigScreenProfessionalPreview() {
    FitnessProTheme {
        Surface {
            SchedulerConfigScreen(
                state = SchedulerConfigUIState(
                    userType = EnumUserType.PERSONAL_TRAINER
                ),
            )
        }
    }
}

@Preview
@Composable
private fun SchedulerConfigScreenProfessionalPopulatedPreview() {
    FitnessProTheme {
        Surface {
            SchedulerConfigScreen(
                state = SchedulerConfigUIState(
                    userType = EnumUserType.PERSONAL_TRAINER,
                    alarm = SwitchButtonField(checked = true),
                    notification = SwitchButtonField(checked = true)
                ),
            )
        }
    }
}
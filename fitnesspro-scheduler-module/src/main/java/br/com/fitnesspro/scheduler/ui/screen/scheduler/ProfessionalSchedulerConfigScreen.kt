package br.com.fitnesspro.scheduler.ui.screen.scheduler

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.buttons.HorizontalLabeledSwitchButton
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.LabelTextStyle
import br.com.fitnesspro.core.theme.ValueTextStyle
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_LABELED_SWITCH_BUTTON_NOTIFICATION
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_LABEL_BREAK_TIME
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_LABEL_BREAK_TIME_EXPLANATION
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_LABEL_DENSITY_EVENTS
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_LABEL_DENSITY_EVENTS_EXPLANATION
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_LABEL_GENERAL
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_LABEL_WORK_TIME
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_LABEL_WORK_TIME_EXPLANATION
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_MAX_DENSITY_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_MIN_DENSITY_FIELD
import br.com.fitnesspro.scheduler.ui.state.SchedulerConfigUIState

@Composable
internal fun ProfessionalSchedulerConfigScreen(
    state: SchedulerConfigUIState,
    onDone: () -> Unit = { }
) {
    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 64.dp)
    ) {
        val (
            generalRef, labeledCheckboxNotificationRef,
            eventDensityRef, eventDensityExplanationRef, eventDensityMinRef, eventDensityMaxRef,
            workTimeRef, workTimeExplanationRef, breakTimeRef, breakTimeExplanationRef
        ) = createRefs()

        Text(
            text = stringResource(R.string.scheduler_config_screen_label_general),
            style = LabelTextStyle.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_LABEL_GENERAL.name)
                .constrainAs(generalRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        HorizontalLabeledSwitchButton(
            field = state.notification,
            label = stringResource(R.string.scheduler_config_screen_label_notification),
            modifier = Modifier
                .testTag(SCHEDULER_CONFIG_SCREEN_LABELED_SWITCH_BUTTON_NOTIFICATION.name)
                .constrainAs(labeledCheckboxNotificationRef) {
                    top.linkTo(generalRef.bottom, 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )

        Text(
            text = stringResource(R.string.scheduler_config_screen_label_event_density),
            style = LabelTextStyle.copy(fontWeight = FontWeight.Medium),
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
            style = ValueTextStyle.copy(fontSize = 14.sp),
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            ),
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            ),
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
            style = LabelTextStyle.copy(fontWeight = FontWeight.Medium),
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
            style = ValueTextStyle.copy(fontSize = 14.sp),
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
            style = LabelTextStyle.copy(fontWeight = FontWeight.Medium),
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
            style = ValueTextStyle.copy(fontSize = 14.sp),
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

@Preview(device = "id:small_phone")
@Composable
private fun ProfessionalSchedulerConfigScreenPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ProfessionalSchedulerConfigScreen(
                state = schedulerConfigPersonalState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ProfessionalSchedulerConfigScreenPreviewLight() {
    FitnessProTheme {
        Surface {
            ProfessionalSchedulerConfigScreen(
                state = schedulerConfigPersonalState
            )
        }
    }
}
package br.com.fitnesspro.scheduler.ui.screen.scheduler

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.buttons.HorizontalLabeledSwitchButton
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.LabelTextStyle
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_LABELED_SWITCH_BUTTON_ALARM
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_LABELED_SWITCH_BUTTON_NOTIFICATION
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerConfigScreenTags.SCHEDULER_CONFIG_SCREEN_LABEL_GENERAL
import br.com.fitnesspro.scheduler.ui.state.SchedulerConfigUIState

@Composable
internal fun MemberSchedulerConfigScreen(state: SchedulerConfigUIState) {
    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        val (generalRef, labeledCheckboxAlarmRef, labeledCheckboxNotificationRef) = createRefs()

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

@Preview(device = "id:small_phone")
@Composable
private fun MemberSchedulerConfigScreenPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            MemberSchedulerConfigScreen(
                state = schedulerConfigPersonalState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun MemberSchedulerConfigScreenPreviewLight() {
    FitnessProTheme {
        Surface {
            MemberSchedulerConfigScreen(
                state = schedulerConfigPersonalState
            )
        }
    }
}
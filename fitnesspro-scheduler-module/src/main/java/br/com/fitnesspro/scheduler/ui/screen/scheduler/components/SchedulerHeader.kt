package br.com.fitnesspro.scheduler.ui.screen.scheduler.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.LabelTextStyle
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTags.SCHEDULER_SCREEN_BACK_MONTH_BUTTON
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTags.SCHEDULER_SCREEN_FORWARD_MONTH_BUTTON
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTags.SCHEDULER_SCREEN_LABEL_YEAR_MONTH
import java.time.YearMonth

@Composable
internal fun SchedulerHeader(
    selectedYearMonth: YearMonth,
    onBackClick: (newYearMonth: YearMonth) -> Unit,
    onForwardClick: (newYearMonth: YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier) {
        val (monthRef, backRef, forwardRef) = createRefs()

        IconButton(
            modifier = Modifier
                .testTag(SCHEDULER_SCREEN_BACK_MONTH_BUTTON.name)
                .constrainAs(backRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            onClick = {
                onBackClick(selectedYearMonth.minusMonths(1))
            }
        ) {
            Icon(
                painter = painterResource(br.com.fitnesspro.core.R.drawable.ic_navigate_back_16dp),
                contentDescription = stringResource(R.string.schedule_screen_navigate_back_description)
            )
        }

        Text(
            modifier = Modifier
                .testTag(SCHEDULER_SCREEN_LABEL_YEAR_MONTH.name)
                .constrainAs(monthRef) {
                    start.linkTo(backRef.end)
                    end.linkTo(forwardRef.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            text = selectedYearMonth.format(EnumDateTimePatterns.MONTH_YEAR),
            style = LabelTextStyle,
        )

        IconButton(
            modifier = Modifier
                .testTag(SCHEDULER_SCREEN_FORWARD_MONTH_BUTTON.name)
                .constrainAs(forwardRef) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            onClick = {
                onForwardClick(selectedYearMonth.plusMonths(1))
            }
        ) {
            Icon(
                painter = painterResource(br.com.fitnesspro.core.R.drawable.ic_navigate_forward_16dp),
                contentDescription = stringResource(R.string.schedule_screen_navigate_forward_description)
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerHeaderPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            SchedulerHeader(
                selectedYearMonth = YearMonth.now(),
                onBackClick = {},
                onForwardClick = {}
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun SchedulerHeaderPreviewLight() {
    FitnessProTheme {
        Surface {
            SchedulerHeader(
                selectedYearMonth = YearMonth.now(),
                onBackClick = {},
                onForwardClick = {}
            )
        }
    }
}
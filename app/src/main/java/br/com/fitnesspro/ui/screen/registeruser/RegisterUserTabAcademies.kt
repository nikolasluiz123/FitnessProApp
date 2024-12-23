package br.com.fitnesspro.ui.screen.registeruser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.LabeledText
import br.com.fitnesspro.compose.components.list.expandable.LazyExpandableVerticalList
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.to.TOPersonAcademyTime
import br.com.fitnesspro.ui.navigation.RegisterAcademyScreenArgs
import br.com.fitnesspro.ui.screen.registeruser.callback.OnAcademyItemClick
import br.com.fitnesspro.ui.screen.registeruser.decorator.AcademyGroupDecorator
import br.com.fitnesspro.ui.state.RegisterUserUIState
import java.time.DayOfWeek
import java.time.LocalTime

@Composable
fun RegisterUserTabAcademies(
    state: RegisterUserUIState = RegisterUserUIState(),
    onAcademyItemClick: OnAcademyItemClick? = null,
    onUpdateAcademies: () -> Unit = { }
) {
    ConstraintLayout(
        Modifier
            .padding(12.dp)
            .fillMaxSize()
    ) {
        val (listRef) = createRefs()

        LaunchedEffect(Unit) {
            onUpdateAcademies()
        }

        LazyExpandableVerticalList(
            modifier = Modifier.constrainAs(listRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)

                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            },
            groups = state.academies,
            emptyMessageResId = R.string.register_user_tab_academies_empty_message,
            itemLayout =  { to ->
                AcademyTimeItem(
                    item = to,
                    onClick = {
                        onAcademyItemClick?.onExecute(
                            args = RegisterAcademyScreenArgs(
                                personId = to.personId!!,
                                personAcademyTimeId = to.id!!,
                            )
                        )
                    }
                )
            }
        )

    }
}

@Composable
fun AcademyTimeItem(item: TOPersonAcademyTime, onClick: () -> Unit) {
    ConstraintLayout(Modifier.padding(8.dp).clickable { onClick() }) {
        val (dayWeekRef, timeRef, dividerRef) = createRefs()

        createHorizontalChain(dayWeekRef, timeRef)

        LabeledText(
            modifier = Modifier.constrainAs(dayWeekRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)

                width = Dimension.fillToConstraints
                horizontalChainWeight = 0.5f
            },
            label = stringResource(R.string.register_user_tab_academies_label_day_week),
            value = item.dayOfWeek!!.getFirstPartFullDisplayName()
        )

        LabeledText(
            modifier = Modifier.constrainAs(timeRef) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)

                width = Dimension.fillToConstraints
                horizontalChainWeight = 0.5f
            },
            label = stringResource(R.string.register_user_tab_academies_label_time),
            value = stringResource(
                R.string.register_user_tab_academies_value_time,
                item.timeStart!!.format(EnumDateTimePatterns.TIME),
                item.timeEnd!!.format(EnumDateTimePatterns.TIME)
            )
        )

        HorizontalDivider(
            modifier = Modifier.constrainAs(dividerRef) {
                bottom.linkTo(parent.bottom)
                top.linkTo(dayWeekRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}

@Preview
@Composable
private fun RegisterUserTabAcademiesPreview() {
    val state = RegisterUserUIState(
        academies = mutableListOf(
            AcademyGroupDecorator(
                id = "1",
                label = R.string.label_preview_academy,
                value = "Smart Fit",
                isExpanded = true,
                items = listOf(
                    TOPersonAcademyTime(
                        id = "1",
                        dayOfWeek = DayOfWeek.MONDAY,
                        timeStart = LocalTime.of(10, 0),
                        timeEnd = LocalTime.of(12, 0),
                        active = true
                    ),
                    TOPersonAcademyTime(
                        id = "2",
                        dayOfWeek = DayOfWeek.TUESDAY,
                        timeStart = LocalTime.of(10, 0),
                        timeEnd = LocalTime.of(12, 0),
                        active = true
                    ),
                    TOPersonAcademyTime(
                        id = "3",
                        dayOfWeek = DayOfWeek.WEDNESDAY,
                        timeStart = LocalTime.of(10, 0),
                        timeEnd = LocalTime.of(12, 0),
                        active = true
                    ),
                    TOPersonAcademyTime(
                        id = "4",
                        dayOfWeek = DayOfWeek.THURSDAY,
                        timeStart = LocalTime.of(10, 0),
                        timeEnd = LocalTime.of(12, 0),
                        active = true
                    )
                )
            )
        )
    )

    FitnessProTheme {
        Surface {
            RegisterUserTabAcademies(state)
        }
    }
}

@Preview
@Composable
private fun RegisterUserTabAcademiesEmptyPreview() {
    val state = RegisterUserUIState(academies = mutableListOf())

    FitnessProTheme {
        Surface {
            RegisterUserTabAcademies(state)
        }
    }
}
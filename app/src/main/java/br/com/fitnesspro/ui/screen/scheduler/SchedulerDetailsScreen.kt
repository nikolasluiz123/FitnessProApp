package br.com.fitnesspro.ui.screen.scheduler

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.LabeledText
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonAdd
import br.com.fitnesspro.compose.components.list.LazyVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.parseToLocalDate
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.ui.navigation.CompromiseScreenArgs
import br.com.fitnesspro.ui.screen.scheduler.callback.OnNavigateToCompromise
import br.com.fitnesspro.ui.state.SchedulerDetailsUIState
import br.com.fitnesspro.ui.viewmodel.SchedulerDetailsViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun SchedulerDetailsScreen(
    viewModel: SchedulerDetailsViewModel,
    onBackClick: () -> Unit,
    onNavigateToCompromise: OnNavigateToCompromise
) {
    val state by viewModel.uiState.collectAsState()

    SchedulerDetailsScreen(
        state = state,
        onBackClick = onBackClick,
        onUpdateSchedules = viewModel::updateSchedules,
        onNavigateToCompromise = onNavigateToCompromise
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulerDetailsScreen(
    state: SchedulerDetailsUIState,
    onBackClick: () -> Unit = { },
    onUpdateSchedules: () -> Unit = { },
    onNavigateToCompromise: OnNavigateToCompromise? = null
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                subtitle = state.subtitle,
                showMenuWithLogout = false,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            if (state.isVisibleFabAdd) {
                FloatingActionButtonAdd(
                    onClick = {
                        onNavigateToCompromise?.onExecute(
                            args = CompromiseScreenArgs(
                                date = state.subtitle.parseToLocalDate(EnumDateTimePatterns.DATE)!!,
                                recurrent = false,
                            )
                        )
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { padding ->

        LaunchedEffect(Unit) {
            onUpdateSchedules()
        }

        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val listRef = createRef()

            LazyVerticalList(
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(listRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                items = state.schedules,
                emptyMessageResId = getEmptyMessage(state),
            ) { toScheduler ->
                SchedulerDetailItem(
                    to = toScheduler,
                    state = state,
                    onNavigateToCompromise = onNavigateToCompromise
                )
            }
        }
    }
}

private fun getEmptyMessage(state: SchedulerDetailsUIState): Int {
    val date = state.subtitle.parseToLocalDate(EnumDateTimePatterns.DATE)!!

    return if (date < LocalDate.now()) {
        R.string.scheduler_details_empty_message_past_date
    } else {
        R.string.scheduler_details_empty_message
    }
}

@Composable
fun SchedulerDetailItem(
    to: TOScheduler,
    state: SchedulerDetailsUIState,
    onNavigateToCompromise: OnNavigateToCompromise? = null
) {
    val context = LocalContext.current

    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                onNavigateToCompromise?.onExecute(
                    args = CompromiseScreenArgs(
                        date = state.subtitle.parseToLocalDate(EnumDateTimePatterns.DATE)!!,
                        recurrent = false,
                        schedulerId = to.id
                    )
                )
            }
    ) {
        val (nameRef, hourRef, compromiseTypeRef, situationRef, professionalRef,
             dividerRef) = createRefs()

        createHorizontalChain(nameRef, hourRef)

        LabeledText(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .constrainAs(nameRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
            label = stringResource(R.string.scheduler_details_name_label),
            value = if (state.userType == EnumUserType.ACADEMY_MEMBER) to.professionalName!! else to.academyMemberName!!
        )

        LabeledText(
            modifier = Modifier
                .padding(end = 8.dp)
                .constrainAs(hourRef) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
            label = stringResource(R.string.scheduler_details_hour_label),
            value = stringResource(
                R.string.scheduler_details_hour_value,
                to.start!!.format(EnumDateTimePatterns.TIME),
                to.end!!.format(EnumDateTimePatterns.TIME)
            )
        )

        createHorizontalChain(compromiseTypeRef, situationRef)

        LabeledText(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .constrainAs(compromiseTypeRef) {
                    top.linkTo(nameRef.bottom, margin = 12.dp)
                    start.linkTo(parent.start)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
            label = stringResource(R.string.scheduler_details_compromisse_type_label),
            value = to.compromiseType?.getLabel(context)!!
        )

        LabeledText(
            modifier = Modifier
                .padding(end = 8.dp)
                .constrainAs(situationRef) {
                    top.linkTo(hourRef.bottom, margin = 12.dp)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
            label = stringResource(R.string.scheduler_details_situation_label),
            value = to.situation?.getLabel(context)!!
        )

        if (state.userType == EnumUserType.ACADEMY_MEMBER) {
            LabeledText(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .constrainAs(professionalRef) {
                        top.linkTo(compromiseTypeRef.bottom, margin = 12.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                },
                label = stringResource(R.string.scheduler_details_professional_label),
                value = to.professionalName!!
            )
        }

        HorizontalDivider(
            modifier = Modifier.constrainAs(dividerRef) {
                if (state.userType == EnumUserType.ACADEMY_MEMBER) {
                    top.linkTo(professionalRef.bottom, margin = 8.dp)
                } else {
                    top.linkTo(compromiseTypeRef.bottom, margin = 8.dp)
                }

                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
    }
}

@Preview
@Composable
private fun SchedulerDetailItemAcademyMemberPreview() {
    FitnessProTheme {
        Surface {
            SchedulerDetailItem(
                to = TOScheduler(
                    academyMemberName = "Josnei Cardoso Neto",
                    professionalName = "Gabriela da Silva",
                    scheduledDate = LocalDate.parse("2024-05-01"),
                    start = LocalTime.parse("08:00"),
                    end = LocalTime.parse("09:00"),
                    situation = EnumSchedulerSituation.SCHEDULED,
                    compromiseType = EnumCompromiseType.FIRST
                ),
                state = SchedulerDetailsUIState(
                    userType = EnumUserType.ACADEMY_MEMBER
                )
            )
        }
    }
}

@Preview
@Composable
private fun SchedulerDetailItemProfessionalPreview() {
    FitnessProTheme {
        Surface {
            SchedulerDetailItem(
                to = TOScheduler(
                    academyMemberName = "Josnei Cardoso Neto",
                    professionalName = "Gabriela da Silva",
                    scheduledDate = LocalDate.parse("2024-05-01"),
                    start = LocalTime.parse("08:00"),
                    end = LocalTime.parse("09:00"),
                    situation = EnumSchedulerSituation.SCHEDULED,
                    compromiseType = EnumCompromiseType.FIRST
                ),
                state = SchedulerDetailsUIState(
                    userType = EnumUserType.PERSONAL_TRAINER
                )
            )
        }
    }
}

@Preview
@Composable
private fun SchedulerDetailsPreview() {
    FitnessProTheme {
        Surface {
            SchedulerDetailsScreen(
                state = SchedulerDetailsUIState(
                    title = "Detalhes dos Compromissos",
                    subtitle = "01/05/2024",
                    schedules = listOf(
                        TOScheduler(
                            academyMemberName = "Josnei Cardoso Neto",
                            professionalName = "Gabriela da Silva",
                            scheduledDate = LocalDate.parse("2024-05-01"),
                            start = LocalTime.parse("08:00"),
                            end = LocalTime.parse("09:00"),
                            situation = EnumSchedulerSituation.SCHEDULED
                        ),
                        TOScheduler(
                            academyMemberName = "Josnei Cardoso Neto",
                            professionalName = "Gabriela da Silva",
                            scheduledDate = LocalDate.parse("2024-05-01"),
                            start = LocalTime.parse("12:00"),
                            end = LocalTime.parse("12:30"),
                            situation = EnumSchedulerSituation.CONFIRMED
                        ),
                        TOScheduler(
                            academyMemberName = "Josnei Cardoso Neto",
                            professionalName = "Gabriela da Silva",
                            scheduledDate = LocalDate.parse("2024-05-01"),
                            start = LocalTime.parse("15:00"),
                            end = LocalTime.parse("16:30"),
                            situation = EnumSchedulerSituation.CANCELLED,
                            canceledDate = LocalDateTime.parse("2024-05-01T10:15:30")
                        )
                    )
                )
            )
        }
    }
}

@Preview
@Composable
private fun SchedulerDetailsEmptyPreview() {
    FitnessProTheme {
        Surface {
            SchedulerDetailsScreen(
                state = SchedulerDetailsUIState(
                    title = "Detalhes dos Compromissos",
                    subtitle = "01/05/2024",
                )
            )
        }
    }
}
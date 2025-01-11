package br.com.fitnesspro.ui.screen.scheduler

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
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
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.LabeledText
import br.com.fitnesspro.compose.components.bottombar.FitnessProBottomAppBar
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonSave
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonCalendarCheck
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonMessage
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.fields.DatePickerOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.DayWeeksSelector
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.PagedListDialogOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.TimePickerOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.SnackBarTextStyle
import br.com.fitnesspro.core.theme.ValueTextStyle
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.tuple.PersonTuple
import br.com.fitnesspro.ui.screen.scheduler.callback.OnSaveCompromiseClick
import br.com.fitnesspro.ui.state.CompromiseUIState
import br.com.fitnesspro.ui.viewmodel.CompromiseViewModel
import br.com.fitnesspro.usecase.scheduler.enums.EnumSchedulerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalTime


@Composable
fun CompromiseScreen(
    viewModel: CompromiseViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    CompromiseScreen(
        state = state,
        onBackClick = onBackClick,
        onSaveCompromiseClick = viewModel::saveCompromise
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompromiseScreen(
    state: CompromiseUIState,
    onBackClick: () -> Unit = { },
    onSaveCompromiseClick: OnSaveCompromiseClick? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                subtitle = state.subtitle,
                showMenuWithLogout = false,
                onBackClick = onBackClick
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(modifier = Modifier.padding(8.dp)) {
                    Text(text = it.visuals.message, style = SnackBarTextStyle)
                }
            }
        },
        bottomBar = {
            FitnessProBottomAppBar(
                actions = {
                    if (!state.recurrent) {
                        IconButtonDelete(
                            enabled = state.isEnabledDeleteButton,
                            onClick = { }
                        )

                        IconButtonMessage(
                            hasMessagesToRead = state.hasNewMessages,
                            enabled = state.isEnabledMessageButton,
                            onClick = { }
                        )

                        if (state.userType != EnumUserType.ACADEMY_MEMBER) {
                            IconButtonCalendarCheck(
                                enabled = state.isEnabledConfirmButton,
                                onClick = { }
                            )
                        }
                    }

                },
                floatingActionButton = {
                    if (state.userType != EnumUserType.ACADEMY_MEMBER || state.toScheduler.id == null) {
                        FloatingActionButtonSave(
                            onClick = {
                                onSaveCompromiseClick?.onExecute {
                                    showSuccessMessage(
                                        enumSchedulerType = it,
                                        state = state,
                                        coroutineScope = coroutineScope,
                                        snackbarHostState = snackbarHostState,
                                        context = context
                                    )
                                }
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            FitnessProMessageDialog(
                type = state.dialogType,
                show = state.showDialog,
                onDismissRequest = { state.onHideDialog() },
                message = state.dialogMessage
            )

            when (state.userType) {
                EnumUserType.PERSONAL_TRAINER -> {
                    if (state.recurrent) {
                        RecurrentCompromise(state)
                    } else {
                        UniqueCompromise(state)
                    }
                }

                EnumUserType.NUTRITIONIST -> {
                    UniqueCompromise(state)
                }

                EnumUserType.ACADEMY_MEMBER -> {
                    UniqueCompromiseSuggestion(state)
                }

                else -> {}
            }
        }
    }
}

private fun showSuccessMessage(
    enumSchedulerType: EnumSchedulerType,
    state: CompromiseUIState,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    coroutineScope.launch {
        val message = when (enumSchedulerType) {
            EnumSchedulerType.SUGGESTION -> {
                context.getString(R.string.compromise_screen_message_success_suggestion)
            }

            EnumSchedulerType.UNIQUE -> {
                context.getString(
                    R.string.compromise_screen_message_success_unique,
                    state.toScheduler.scheduledDate!!.format(EnumDateTimePatterns.DATE)
                )
            }

            EnumSchedulerType.RECURRENT -> {
                context.getString(
                    R.string.compromise_screen_message_success_recurrent,
                    state.recurrentConfig.dateStart!!.format(EnumDateTimePatterns.DATE),
                    state.recurrentConfig.dateEnd!!.format(EnumDateTimePatterns.DATE)
                )
            }
        }

        snackbarHostState.showSnackbar(message = message)
    }
}

@Composable
fun RecurrentCompromise(state: CompromiseUIState) {
    val scrollState = rememberScrollState()

    ConstraintLayout(
        Modifier
            .padding(8.dp)
            .fillMaxSize()
            .scrollable(scrollState, orientation = Orientation.Vertical)
    ) {
        val (memberRef, startDateRef, endDateRef, startHourRef, endHourRef,
            observationRef, dayWeeksRef) = createRefs()

        PagedListDialogOutlinedTextFieldValidation(
            field = state.member,
            fieldLabel = stringResource(R.string.compromise_screen_label_member),
            simpleFilterPlaceholderResId = R.string.compromise_screen_simple_filter_placeholder_member_dialog_list,
            itemLayout = { personTuple ->
                DialogListItem(
                    person = personTuple,
                    onItemClick = state.member.onDataListItemClick
                )
            },
            modifier = Modifier.constrainAs(memberRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            }
        )

        DatePickerOutlinedTextFieldValidation(
            field = state.dateStart,
            fieldLabel = stringResource(R.string.compromise_screen_label_start_date),
            modifier = Modifier
                .constrainAs(startDateRef) {
                    top.linkTo(memberRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )

        DatePickerOutlinedTextFieldValidation(
            field = state.dateEnd,
            fieldLabel = stringResource(R.string.compromise_screen_label_end_date),
            modifier = Modifier.constrainAs(endDateRef) {
                top.linkTo(startDateRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            }
        )


        TimePickerOutlinedTextFieldValidation(
            field = state.hourStart,
            fieldLabel = stringResource(R.string.compromise_screen_label_start_hour),
            timePickerTitle = stringResource(R.string.compromise_screen_time_picker_title_start_hour),
            modifier = Modifier
                .constrainAs(startHourRef) {
                    top.linkTo(endDateRef.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )

        TimePickerOutlinedTextFieldValidation(
            field = state.hourEnd,
            fieldLabel = stringResource(R.string.compromise_screen_label_end_hour),
            timePickerTitle = stringResource(R.string.compromise_screen_time_picker_title_end_hour),
            modifier = Modifier.constrainAs(endHourRef) {
                top.linkTo(startHourRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            }
        )

        OutlinedTextFieldValidation(
            field = state.observation,
            label = stringResource(R.string.compromise_screen_label_observation),
            modifier = Modifier.constrainAs(observationRef) {
                top.linkTo(endHourRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            }
        )

        DayWeeksSelector(
            selectorField = state.dayWeeksSelectorField,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(dayWeeksRef) {
                    top.linkTo(observationRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )
    }
}

@Composable
fun UniqueCompromise(state: CompromiseUIState) {
    val scrollState = rememberScrollState()

    ConstraintLayout(
        Modifier
            .padding(8.dp)
            .fillMaxSize()
            .scrollable(scrollState, orientation = Orientation.Vertical)
    ) {
        val (memberRef, startRef, endRef, observationRef) = createRefs()

        PagedListDialogOutlinedTextFieldValidation(
            modifier = Modifier
                .constrainAs(memberRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth(),
            field = state.member,
            fieldLabel = stringResource(R.string.compromise_screen_label_member),
            simpleFilterPlaceholderResId = R.string.compromise_screen_simple_filter_placeholder_member_dialog_list,
            itemLayout = { personTuple ->
                DialogListItem(
                    person = personTuple,
                    onItemClick = state.member.onDataListItemClick
                )
            }
        )

        TimePickerOutlinedTextFieldValidation(
            modifier = Modifier
                .constrainAs(startRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(memberRef.bottom, margin = 8.dp)

                    width = Dimension.fillToConstraints
                },
            field = state.hourStart,
            fieldLabel = stringResource(R.string.compromise_screen_label_time_start),
            timePickerTitle = stringResource(R.string.compromise_screen_time_picker_title_start_hour)
        )

        TimePickerOutlinedTextFieldValidation(
            modifier = Modifier.constrainAs(endRef) {
                top.linkTo(startRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            field = state.hourEnd,
            fieldLabel = stringResource(R.string.compromise_screen_label_time_end),
            timePickerTitle = stringResource(R.string.compromise_screen_time_picker_title_end_hour),
        )

        OutlinedTextFieldValidation(
            modifier = Modifier.constrainAs(observationRef) {
                top.linkTo(endRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            field = state.observation,
            label = stringResource(R.string.compromise_screen_label_observation),
            keyboardActions = KeyboardActions(
                onDone = {

                }
            )
        )
    }
}

@Composable
fun DialogListItem(
    person: PersonTuple,
    onItemClick: (PersonTuple) -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onItemClick(person) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val text = if (person.userType == EnumUserType.ACADEMY_MEMBER) {
            person.name
        } else {
            stringResource(
                R.string.compromise_screen_label_professional_name_and_type,
                person.name,
                person.userType.getLabel(LocalContext.current)!!
            )
        }

        Text(
            modifier = Modifier.padding(12.dp),
            text = text,
            style = ValueTextStyle.copy(fontSize = 16.sp)
        )
    }

    HorizontalDivider()
}

@Composable
fun UniqueCompromiseSuggestion(state: CompromiseUIState) {
    if (state.toScheduler.id != null) {
        UniqueCompromiseSuggestionReadOnly(state)
    } else {
        UniqueCompromiseSuggestionEditable(state)
    }
}

@Composable
fun UniqueCompromiseSuggestionReadOnly(state: CompromiseUIState) {
    val context = LocalContext.current

    ConstraintLayout(
        Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        val (nameRef, hourRef, professionalRef, situationRef, observationRef) = createRefs()

        createHorizontalChain(nameRef, professionalRef)

        LabeledText(
            modifier = Modifier.constrainAs(nameRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)

                width = Dimension.fillToConstraints
                horizontalChainWeight = 0.5f
            },
            label = stringResource(R.string.compromise_screen_label_name),
            value = state.toScheduler.professionalName!!
        )

        LabeledText(
            modifier = Modifier.constrainAs(professionalRef) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
                horizontalChainWeight = 0.5f
            },
            label = stringResource(R.string.compromise_screen_label_professional_type),
            value = state.toScheduler.professionalType?.getLabel(context)!!
        )

        createHorizontalChain(hourRef, situationRef)

        LabeledText(
            modifier = Modifier.constrainAs(hourRef) {
                top.linkTo(nameRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)

                width = Dimension.fillToConstraints
                horizontalChainWeight = 0.5f
            },
            label = stringResource(R.string.compromise_screen_label_hour),
            value = stringResource(
                R.string.compromise_screen_label_hour_value,
                state.toScheduler.start!!.format(EnumDateTimePatterns.TIME),
                state.toScheduler.end!!.format(EnumDateTimePatterns.TIME)
            )
        )

        LabeledText(
            modifier = Modifier.constrainAs(situationRef) {
                top.linkTo(professionalRef.bottom, margin = 8.dp)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
                horizontalChainWeight = 0.5f
            },
            label = stringResource(R.string.compromise_screen_label_situation),
            value = state.toScheduler.situation?.getLabel(context)!!
        )

        state.toScheduler.observation?.let {
            LabeledText(
                modifier = Modifier.constrainAs(observationRef) {
                    top.linkTo(hourRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                },
                label = stringResource(R.string.compromise_screen_label_observation),
                value = it
            )
        }
    }
}

@Composable
private fun UniqueCompromiseSuggestionEditable(state: CompromiseUIState) {
    val scrollState = rememberScrollState()
    ConstraintLayout(
        Modifier
            .padding(8.dp)
            .fillMaxSize()
            .scrollable(scrollState, orientation = Orientation.Vertical)
    ) {
        val (professionalRef, startRef, endRef, observationRef) = createRefs()

        PagedListDialogOutlinedTextFieldValidation(
            modifier = Modifier
                .constrainAs(professionalRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth(),
            field = state.professional,
            fieldLabel = stringResource(R.string.compromise_screen_label_professional),
            simpleFilterPlaceholderResId = R.string.compromise_screen_simple_filter_placeholder_professional_dialog_list,
            itemLayout = { personTuple ->
                DialogListItem(
                    person = personTuple,
                    onItemClick = state.professional.onDataListItemClick
                )
            }
        )

        TimePickerOutlinedTextFieldValidation(
            modifier = Modifier
                .constrainAs(startRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(professionalRef.bottom, margin = 8.dp)

                    width = Dimension.fillToConstraints
                },
            field = state.hourStart,
            fieldLabel = stringResource(R.string.compromise_screen_label_time_start),
            timePickerTitle = stringResource(R.string.compromise_screen_time_picker_title_start_hour),
        )

        TimePickerOutlinedTextFieldValidation(
            modifier = Modifier.constrainAs(endRef) {
                top.linkTo(startRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            field = state.hourEnd,
            fieldLabel = stringResource(R.string.compromise_screen_label_time_end),
            timePickerTitle = stringResource(R.string.compromise_screen_time_picker_title_end_hour),
            keyboardActions = KeyboardActions(
                onDone = {

                }
            )
        )

        OutlinedTextFieldValidation(
            modifier = Modifier.constrainAs(observationRef) {
                top.linkTo(endRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            field = state.observation,
            label = stringResource(R.string.compromise_screen_label_observation),
            keyboardActions = KeyboardActions(
                onDone = {

                }
            )
        )
    }
}

@Preview
@Composable
private fun MemberDialogListItemPreview() {
    FitnessProTheme {
        Surface {
            DialogListItem(
                person = PersonTuple(
                    id = "1",
                    name = "Nikolas Luiz Schmitt",
                    userType = EnumUserType.ACADEMY_MEMBER
                )
            )
        }
    }
}

@Preview
@Composable
private fun CompromiseScreenInclusionMemberPreview() {
    FitnessProTheme {
        Surface {
            CompromiseScreen(
                state = CompromiseUIState(
                    title = "Sugestão de Compromisso",
                    subtitle = "01/05/2024",
                    userType = EnumUserType.ACADEMY_MEMBER
                )
            )
        }
    }
}

@Preview
@Composable
private fun CompromiseScreenEditionMemberPreview() {
    FitnessProTheme {
        Surface {
            CompromiseScreen(
                state = CompromiseUIState(
                    title = "Compromisso",
                    subtitle = "01/05/2024 08:00 às 09:00",
                    userType = EnumUserType.ACADEMY_MEMBER,
                    isEnabledDeleteButton = true,
                    isEnabledMessageButton = true,
                    toScheduler = TOScheduler(
                        professionalName = "Gabriela da Silva",
                        professionalType = EnumUserType.NUTRITIONIST,
                        start = LocalTime.parse("08:00"),
                        end = LocalTime.parse("09:00"),
                        situation = EnumSchedulerSituation.CONFIRMED,
                        observation = "Muito bem observado"
                    )
                )
            )
        }
    }
}

@Preview
@Composable
private fun CompromiseScreenInclusionPersonalTrainerPreview() {
    FitnessProTheme {
        Surface {
            CompromiseScreen(
                state = CompromiseUIState(
                    title = "Novo Compromisso",
                    subtitle = "01/05/2024",
                    userType = EnumUserType.PERSONAL_TRAINER,
                    toScheduler = TOScheduler(
                        professionalName = "Gabriela da Silva",
                        professionalType = EnumUserType.PERSONAL_TRAINER,
                        start = LocalTime.parse("08:00"),
                        end = LocalTime.parse("09:00"),
                        situation = EnumSchedulerSituation.CONFIRMED
                    )
                )
            )
        }
    }
}

@Preview
@Composable
private fun CompromiseScreenRecurrentInclusionPersonalTrainerPreview() {
    FitnessProTheme {
        Surface {
            CompromiseScreen(
                state = CompromiseUIState(
                    title = "Novo Compromisso Recorrente",
                    subtitle = null,
                    userType = EnumUserType.PERSONAL_TRAINER,
                    recurrent = true
                )
            )
        }
    }
}
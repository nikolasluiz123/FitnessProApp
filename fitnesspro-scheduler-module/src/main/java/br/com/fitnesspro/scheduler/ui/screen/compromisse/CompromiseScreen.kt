package br.com.fitnesspro.scheduler.ui.screen.compromisse

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.bottombar.FitnessProBottomAppBar
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonSave
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonCalendarCheck
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonMessage
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.DATE
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.SnackBarTextStyle
import br.com.fitnesspro.firebase.api.analytics.logButtonClick
import br.com.fitnesspro.model.enums.EnumSchedulerSituation.CONFIRMED
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.screen.compromisse.callbacks.OnInactivateCompromiseClick
import br.com.fitnesspro.scheduler.ui.screen.compromisse.callbacks.OnSaveCompromiseClick
import br.com.fitnesspro.scheduler.ui.screen.compromisse.callbacks.OnScheduleConfirmClick
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_ACTION_DELETE
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_ACTION_MESSAGE
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_ACTION_SCHEDULE_CONFIRM
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_FAB_SAVE
import br.com.fitnesspro.scheduler.ui.state.CompromiseUIState
import br.com.fitnesspro.scheduler.ui.viewmodel.CompromiseViewModel
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun CompromiseScreen(
    viewModel: CompromiseViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    CompromiseScreen(
        state = state,
        onBackClick = onBackClick,
        onSaveCompromiseClick = viewModel::saveCompromise,
        onInactivateCompromiseClick = viewModel::onInactivateCompromiseClick,
        onScheduleConfirmClick = viewModel::onScheduleConfirmClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompromiseScreen(
    state: CompromiseUIState,
    onBackClick: () -> Unit = { },
    onSaveCompromiseClick: OnSaveCompromiseClick? = null,
    onInactivateCompromiseClick: OnInactivateCompromiseClick? = null,
    onScheduleConfirmClick: OnScheduleConfirmClick? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                subtitle = state.subtitle,
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
                            modifier = Modifier.testTag(COMPROMISE_SCREEN_ACTION_DELETE.name),
                            enabled = state.isEnabledDeleteButton,
                            onClick = {
                                Firebase.analytics.logButtonClick(COMPROMISE_SCREEN_ACTION_DELETE)
                                onInactivateCompromiseClick?.onExecute {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.compromise_screen_message_inactivated)
                                        )
                                    }
                                }
                            }
                        )

                        IconButtonMessage(
                            modifier = Modifier.testTag(COMPROMISE_SCREEN_ACTION_MESSAGE.name),
                            hasMessagesToRead = state.hasNewMessages,
                            enabled = state.isEnabledMessageButton,
                            onClick = {
                                Firebase.analytics.logButtonClick(COMPROMISE_SCREEN_ACTION_MESSAGE)
                            }
                        )

                        if (state.userType != EnumUserType.ACADEMY_MEMBER) {
                            IconButtonCalendarCheck(
                                modifier = Modifier.testTag(COMPROMISE_SCREEN_ACTION_SCHEDULE_CONFIRM.name),
                                enabled = state.isEnabledConfirmButton,
                                onClick = {
                                    Firebase.analytics.logButtonClick(COMPROMISE_SCREEN_ACTION_SCHEDULE_CONFIRM)
                                    onScheduleConfirmClick?.onExecute {
                                        coroutineScope.launch {
                                            val message = if (state.toScheduler.situation == CONFIRMED) {
                                                context.getString(R.string.compromise_screen_message_success_confirmed)
                                            } else {
                                                context.getString(R.string.compromise_screen_message_success_completed)
                                            }

                                            snackbarHostState.showSnackbar(message = message)
                                        }
                                    }
                                }
                            )
                        }
                    }

                },
                floatingActionButton = {
                    if (state.userType != EnumUserType.ACADEMY_MEMBER || state.toScheduler.id == null) {
                        FloatingActionButtonSave(
                            modifier = Modifier.testTag(COMPROMISE_SCREEN_FAB_SAVE.name),
                            iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            onClick = {
                                Firebase.analytics.logButtonClick(COMPROMISE_SCREEN_FAB_SAVE)
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
            FitnessProMessageDialog(state = state.messageDialogState)

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
                    state.toScheduler.scheduledDate!!.format(DATE)
                )
            }

            EnumSchedulerType.RECURRENT -> {
                context.getString(
                    R.string.compromise_screen_message_success_recurrent,
                    state.recurrentConfig.dateStart!!.format(DATE),
                    state.recurrentConfig.dateEnd!!.format(DATE)
                )
            }
        }

        snackbarHostState.showSnackbar(message = message)
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CompromiseScreenInclusionMemberPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            CompromiseScreen(
                state = defaultCompromiseAcademyMemberState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CompromiseScreenEditionMemberPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            CompromiseScreen(
                state = compromiseAcademyMemberEditionState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CompromiseScreenEditionCancelatedMemberPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            CompromiseScreen(
                state = compromiseAcademyMemberCancelatedState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CompromiseScreenInclusionPersonalTrainerPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            CompromiseScreen(
                state = compromisePersonalInclusionState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CompromiseScreenRecurrentInclusionPersonalTrainerPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            CompromiseScreen(
                state = compromisePersonalRecurrentState
            )
        }
    }
}


@Preview(device = "id:small_phone")
@Composable
private fun CompromiseScreenInclusionMemberPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            CompromiseScreen(
                state = defaultCompromiseAcademyMemberState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CompromiseScreenEditionMemberPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            CompromiseScreen(
                state = compromiseAcademyMemberEditionState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CompromiseScreenEditionCancelatedMemberPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            CompromiseScreen(
                state = compromiseAcademyMemberCancelatedState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CompromiseScreenInclusionPersonalTrainerPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            CompromiseScreen(
                state = compromisePersonalInclusionState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun CompromiseScreenRecurrentInclusionPersonalTrainerPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            CompromiseScreen(
                state = compromisePersonalRecurrentState
            )
        }
    }
}
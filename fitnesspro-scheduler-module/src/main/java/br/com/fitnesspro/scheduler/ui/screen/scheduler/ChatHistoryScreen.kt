package br.com.fitnesspro.scheduler.ui.screen.scheduler

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.LabeledText
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonAdd
import br.com.fitnesspro.compose.components.dialog.FitnessProPagedListDialog
import br.com.fitnesspro.compose.components.list.LazyVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_700
import br.com.fitnesspro.core.theme.LabelTextStyle
import br.com.fitnesspro.core.theme.RED_500
import br.com.fitnesspro.core.theme.ValueTextStyle
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.enums.EnumUserType.NUTRITIONIST
import br.com.fitnesspro.model.enums.EnumUserType.PERSONAL_TRAINER
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.screen.scheduler.decorator.ChatHistoryDecorator
import br.com.fitnesspro.scheduler.ui.state.ChatHistoryUIState
import br.com.fitnesspro.scheduler.ui.viewmodel.ChatHistoryViewModel
import br.com.fitnesspro.tuple.PersonTuple
import java.time.LocalDateTime

@Composable
fun ChatHistoryScreen(
    viewModel: ChatHistoryViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    ChatHistoryScreen(
        state = state,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistoryScreen(
    state: ChatHistoryUIState,
    onBackClick: () -> Unit = { }
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                onBackClick = onBackClick,
                showMenuWithLogout = false
            )
        },
        floatingActionButton = {
            FloatingActionButtonAdd(
                onClick = {
                    val professionals = listOf(NUTRITIONIST, PERSONAL_TRAINER)

                    if (state.userType in professionals) {
                        state.membersDialogState.onShow()
                    } else {
                        state.professionalsDialogState.onShow()
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        ConstraintLayout(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (state.membersDialogState.show) {
                FitnessProPagedListDialog(
                    state = state.membersDialogState,
                    simpleFilterPlaceholderResId = R.string.chat_history_screen_placeholder_search_members,
                    emptyMessage = R.string.chat_history_screen_empty_message_members
                ) {
                    PersonDialogListItem(
                        person = it,
                        onItemClick = state.membersDialogState.onDataListItemClick
                    )
                }
            }

            if (state.professionalsDialogState.show) {
                FitnessProPagedListDialog(
                    state = state.professionalsDialogState,
                    simpleFilterPlaceholderResId = R.string.chat_history_screen_placeholder_search_professionals,
                    emptyMessage = R.string.chat_history_screen_empty_message_professionals
                ) {
                    PersonDialogListItem(
                        person = it,
                        onItemClick = state.professionalsDialogState.onDataListItemClick
                    )
                }
            }

            LazyVerticalList(
                modifier = Modifier.fillMaxSize(),
                items = state.history,
                emptyMessageResId = R.string.chat_history_empty_message,
            ) { decorator ->
                ChatHistoryItem(
                    context = context,
                    item = decorator,
                    onClick = {

                    }
                )
            }
        }
    }
}

@Composable
private fun PersonDialogListItem(
    person: PersonTuple,
    onItemClick: (PersonTuple) -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick(person)
            },
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
            modifier = Modifier
                .padding(12.dp),
            text = text,
            style = ValueTextStyle.copy(fontSize = 16.sp)
        )
    }

    HorizontalDivider()
}

@Composable
fun ChatHistoryItem(
    context: Context,
    item: ChatHistoryDecorator,
    onClick: (ChatHistoryDecorator) -> Unit = { }
) {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .clickable { onClick(item) }
    ) {
        val (userMessageRef, notReadMessagesCount, lastMessageDate, dividerRef) = createRefs()

        LabeledText(
            label = item.userName,
            value = item.lastMessage,
            maxLinesValue = 1,
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(userMessageRef) {
                    val endConstraint = if (item.notReadMessagesCount > 0) notReadMessagesCount.start else parent.end

                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(endConstraint)

                    width = Dimension.fillToConstraints
                }
        )

        if (item.notReadMessagesCount > 0) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(color = RED_500, shape = CircleShape)
                    .constrainAs(notReadMessagesCount) {
                        top.linkTo(userMessageRef.top, margin = 8.dp)
                        end.linkTo(parent.end, margin = 8.dp)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.notReadMessagesCount.toString(),
                    textAlign = TextAlign.Center,
                    style = LabelTextStyle,
                    color = Color.White
                )
            }
        }

        Text(
            text = getFormatedMessageDate(context, item),
            style = ValueTextStyle,
            color = GREY_700,
            modifier = Modifier.constrainAs(lastMessageDate) {
                top.linkTo(userMessageRef.bottom)
                end.linkTo(parent.end, margin = 8.dp)
                bottom.linkTo(parent.bottom, margin = 8.dp)
            }
        )

        HorizontalDivider(
            modifier = Modifier.constrainAs(dividerRef) {
                top.linkTo(lastMessageDate.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
            thickness = 0.5.dp
        )
    }
}

private fun getFormatedMessageDate(context: Context, item: ChatHistoryDecorator): String {
    return when {
        item.lastMessageDate.toLocalDate() == dateNow() -> {
            context.getString(
                R.string.chat_message_date_now,
                item.lastMessageDate.format(EnumDateTimePatterns.TIME)
            )
        }

        item.lastMessageDate.toLocalDate() == dateTimeNow().minusDays(1).toLocalDate() -> {
            context.getString(
                R.string.chat_message_date_yesterday,
                item.lastMessageDate.format(EnumDateTimePatterns.TIME)
            )
        }

        else -> {
            item.lastMessageDate.format(EnumDateTimePatterns.DATE_TIME_SHORT)
        }
    }
}

@Preview
@Composable
private fun ChatHistoryItemPreview() {
    FitnessProTheme {
        Surface {
            ChatHistoryItem(
                context = LocalContext.current,
                item = ChatHistoryDecorator(
                    id = "1",
                    userName = "João",
                    notReadMessagesCount = 2,
                    lastMessage = "Olá, tudo bem?",
                    lastMessageDate = LocalDateTime.now()
                )
            )
        }
    }
}

@Preview(device = "spec:width=411dp,height=891dp")
@Composable
private fun ChatHistoryScreenEmptyPreviewMediumPhone() {
    FitnessProTheme {
        Surface {
            ChatHistoryScreen(
                state = ChatHistoryUIState(
                    title = "Histórico de Conversas"
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ChatHistoryScreenEmptySmallPhone() {
    FitnessProTheme {
        Surface {
            ChatHistoryScreen(
                state = ChatHistoryUIState(
                    title = "Histórico de Conversas"
                )
            )
        }
    }
}

@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun ChatHistoryScreenEmptyPreviewTablet() {
    FitnessProTheme {
        Surface {
            ChatHistoryScreen(
                state = ChatHistoryUIState(
                    title = "Histórico de Conversas"
                )
            )
        }
    }
}

@Preview(device = "spec:width=411dp,height=891dp")
@Composable
private fun ChatHistoryScreenPreviewMediumPhone() {
    FitnessProTheme {
        Surface {
            ChatHistoryScreen(
                state = ChatHistoryUIState(
                    title = "Histórico de Conversas",
                    history = listOf(
                        ChatHistoryDecorator(
                            id = "1",
                            userName = "João",
                            notReadMessagesCount = 2,
                            lastMessage = "Olá, tudo bem?",
                            lastMessageDate = LocalDateTime.now()
                        ),
                        ChatHistoryDecorator(
                            id = "2",
                            userName = "Maria",
                            notReadMessagesCount = 0,
                            lastMessage = "Tudo bem, e você?",
                            lastMessageDate = LocalDateTime.now().minusDays(1)
                        ),
                        ChatHistoryDecorator(
                            id = "3",
                            userName = "Pedro",
                            notReadMessagesCount = 1,
                            lastMessage = "Estou bem, obrigado!",
                            lastMessageDate = LocalDateTime.now().minusDays(2)
                        )
                    )
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ChatHistoryScreenSmallPhone() {
    FitnessProTheme {
        Surface {
            ChatHistoryScreen(
                state = ChatHistoryUIState(
                    title = "Histórico de Conversas",
                    history = listOf(
                        ChatHistoryDecorator(
                            id = "1",
                            userName = "João",
                            notReadMessagesCount = 2,
                            lastMessage = "Olá, tudo bem?",
                            lastMessageDate = LocalDateTime.now()
                        ),
                        ChatHistoryDecorator(
                            id = "2",
                            userName = "Maria",
                            notReadMessagesCount = 0,
                            lastMessage = "Tudo bem, e você?",
                            lastMessageDate = LocalDateTime.now().minusDays(1)
                        ),
                        ChatHistoryDecorator(
                            id = "3",
                            userName = "Pedro",
                            notReadMessagesCount = 1,
                            lastMessage = "Estou bem, obrigado!",
                            lastMessageDate = LocalDateTime.now().minusDays(2)
                        )
                    )
                )
            )
        }
    }
}

@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun ChatHistoryScreenPreviewTablet() {
    FitnessProTheme {
        Surface {
            ChatHistoryScreen(
                state = ChatHistoryUIState(
                    title = "Histórico de Conversas",
                    history = listOf(
                        ChatHistoryDecorator(
                            id = "1",
                            userName = "João",
                            notReadMessagesCount = 2,
                            lastMessage = "Olá, tudo bem?",
                            lastMessageDate = LocalDateTime.now()
                        ),
                        ChatHistoryDecorator(
                            id = "2",
                            userName = "Maria",
                            notReadMessagesCount = 0,
                            lastMessage = "Tudo bem, e você?",
                            lastMessageDate = LocalDateTime.now().minusDays(1)
                        ),
                        ChatHistoryDecorator(
                            id = "3",
                            userName = "Pedro",
                            notReadMessagesCount = 1,
                            lastMessage = "Estou bem, obrigado!",
                            lastMessageDate = LocalDateTime.now().minusDays(2)
                        )
                    )
                )
            )
        }
    }
}

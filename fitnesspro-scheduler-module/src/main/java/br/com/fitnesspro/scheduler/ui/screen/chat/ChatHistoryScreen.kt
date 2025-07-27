package br.com.fitnesspro.scheduler.ui.screen.chat

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonAdd
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.dialog.FitnessProPagedListDialog
import br.com.fitnesspro.compose.components.list.LazyVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.firebase.api.analytics.logButtonClick
import br.com.fitnesspro.firebase.api.analytics.logListItemClick
import br.com.fitnesspro.model.enums.EnumUserType.NUTRITIONIST
import br.com.fitnesspro.model.enums.EnumUserType.PERSONAL_TRAINER
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.screen.chat.callbacks.OnNavigateToChat
import br.com.fitnesspro.scheduler.ui.screen.chat.enums.EnumChatHistoryScreenTags.CHAT_HISTORY_SCREEN_DIALOG_MEMBER_LIST_ITEM
import br.com.fitnesspro.scheduler.ui.screen.chat.enums.EnumChatHistoryScreenTags.CHAT_HISTORY_SCREEN_DIALOG_PROFESSIONAL_LIST_ITEM
import br.com.fitnesspro.scheduler.ui.screen.chat.enums.EnumChatHistoryScreenTags.CHAT_HISTORY_SCREEN_FAB_ADD
import br.com.fitnesspro.scheduler.ui.state.ChatHistoryUIState
import br.com.fitnesspro.scheduler.ui.viewmodel.ChatHistoryViewModel
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

@Composable
fun ChatHistoryScreen(
    viewModel: ChatHistoryViewModel,
    onBackClick: () -> Unit,
    onNavigateToChat: OnNavigateToChat
) {
    val state by viewModel.uiState.collectAsState()

    ChatHistoryScreen(
        state = state,
        onBackClick = onBackClick,
        onNavigateToChat = onNavigateToChat
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistoryScreen(
    state: ChatHistoryUIState,
    onBackClick: () -> Unit = { },
    onNavigateToChat: OnNavigateToChat? = null
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButtonAdd(
                onClick = {
                    Firebase.analytics.logButtonClick(CHAT_HISTORY_SCREEN_FAB_ADD)

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
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val listRef = createRef()

            FitnessProMessageDialog(state = state.messageDialogState)

            if (state.membersDialogState.show) {
                FitnessProPagedListDialog(
                    state = state.membersDialogState,
                    simpleFilterPlaceholderResId = R.string.chat_history_screen_placeholder_search_members,
                    emptyMessage = R.string.chat_history_screen_empty_message_members
                ) {
                    PersonDialogListItem(
                        person = it,
                        onItemClick = {
                            Firebase.analytics.logListItemClick(CHAT_HISTORY_SCREEN_DIALOG_MEMBER_LIST_ITEM)
                            state.membersDialogState.onDataListItemClick(it)
                        }
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
                        onItemClick = {
                            Firebase.analytics.logListItemClick(CHAT_HISTORY_SCREEN_DIALOG_PROFESSIONAL_LIST_ITEM)
                            state.professionalsDialogState.onDataListItemClick(it)
                        }
                    )
                }
            }

            LazyVerticalList(
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(listRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                items = state.history,
                emptyMessageResId = R.string.chat_history_empty_message,
            ) { decorator ->
                ChatHistoryItem(
                    context = context,
                    item = decorator,
                    onNavigateToChat = onNavigateToChat
                )
            }
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ChatHistoryScreenEmptyDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ChatHistoryScreen(
                state = chatHistoryEmptyState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ChatHistoryScreenPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ChatHistoryScreen(
                state = chatHistoryState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ChatHistoryScreenEmpty() {
    FitnessProTheme {
        Surface {
            ChatHistoryScreen(
                state = chatHistoryEmptyState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ChatHistoryScreenPreview() {
    FitnessProTheme {
        Surface {
            ChatHistoryScreen(
                state = chatHistoryState
            )
        }
    }
}

package br.com.fitnesspro.scheduler.ui.screen.chat

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.list.LazyVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.scheduler.ui.state.ChatUIState
import br.com.fitnesspro.scheduler.ui.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    ChatScreen(
        state = state,
        onBackClick = onBackClick,
        onSendMessageClick = viewModel::sendMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    state: ChatUIState,
    onBackClick: () -> Unit = { },
    onSendMessageClick: () -> Unit = { }
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                subtitle = state.subtitle,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            BottomAppBarMessageInput(
                modifier = Modifier.imePadding(),
                state = state,
                onSendMessageClick = onSendMessageClick
            )
        }
    ) { paddingValues ->
        ConstraintLayout(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            val (messageListRef) = createRefs()

            FitnessProMessageDialog(state = state.messageDialogState)

            LazyVerticalList(
                modifier = Modifier.constrainAs(messageListRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)

                    height = Dimension.fillToConstraints
                },
                items = state.messages,
                emptyMessageResId = null,
                reverseLayout = true
            ) { messageDocument ->
                ChatMessageItem(item = messageDocument, state = state)
            }
        }
    }
}



@Preview(device = "id:small_phone")
@Composable
private fun ChatScreenPreviewLight() {
    FitnessProTheme {
        Surface {
            ChatScreen(
                state = chatWithMessagesState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ChatScreenPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ChatScreen(
                state = chatWithMessagesState
            )
        }
    }
}

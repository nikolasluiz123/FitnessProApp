package br.com.fitnesspro.scheduler.ui.screen.scheduler

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonSendMessage
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.list.LazyVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.toLocalDateTime
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_100
import br.com.fitnesspro.core.theme.GREY_300
import br.com.fitnesspro.core.theme.GREY_400
import br.com.fitnesspro.core.theme.GREY_700
import br.com.fitnesspro.core.theme.GREY_800
import br.com.fitnesspro.core.theme.ValueTextStyle
import br.com.fitnesspro.firebase.api.firestore.documents.MessageDocument
import br.com.fitnesspro.firebase.api.firestore.enums.EnumMessageState.READ
import br.com.fitnesspro.firebase.api.firestore.enums.EnumMessageState.SENDING
import br.com.fitnesspro.firebase.api.firestore.enums.EnumMessageState.SENT
import br.com.fitnesspro.scheduler.R
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

@OptIn(ExperimentalMaterial3Api::class)
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
                onBackClick = onBackClick,
                showMenuWithLogout = false
            )
        },
        bottomBar = {
            BottomAppBarMessageInput(
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

@Composable
fun ChatMessageItem(item: MessageDocument, state: ChatUIState) {
    val isSender = item.personSenderId == state.authenticatedPersonId
    val containerColor = if (isSender) GREY_700 else GREY_300
    val contentColor = if (isSender) GREY_300 else GREY_800

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            val (messageRef, timeRef) = createRefs()

            Text(
                modifier = Modifier.constrainAs(messageRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                },
                text = item.text!!,
                style = ValueTextStyle,
            )

            Row(
                Modifier
                    .padding(top = 8.dp)
                    .constrainAs(timeRef) {
                        top.linkTo(messageRef.bottom, margin = 8.dp)
                        end.linkTo(parent.end)
                    }
            ) {
                Text(
                    text = item.date!!.toLocalDateTime().format(EnumDateTimePatterns.TIME),
                    style = ValueTextStyle,
                )

                if (isSender) {
                    Icon(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(14.dp),
                        painter = painterResource(id = getDrawableStateMessage(item)!!),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
fun BottomAppBarMessageInput(
    state: ChatUIState,
    onSendMessageClick: () -> Unit
) {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(color = GREY_400)
    ) {
        val (inputRef, sendBtnRef) = createRefs()

        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .constrainAs(inputRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(sendBtnRef.start)
                    bottom.linkTo(parent.bottom)

                    width = Dimension.fillToConstraints
                },
            value = state.messageTextField.value,
            onValueChange = state.messageTextField.onChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.chat_screen_message_input_placeholder),
                    style = ValueTextStyle,
                    color = GREY_700
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = GREY_800,
                focusedBorderColor = GREY_300,
                unfocusedBorderColor = GREY_300,
                unfocusedTextColor = GREY_700,
                focusedTextColor = GREY_700,
                focusedContainerColor = GREY_300,
                disabledContainerColor = GREY_100,
                errorContainerColor = GREY_300,
                unfocusedContainerColor = GREY_300,
                errorPlaceholderColor = GREY_700,
                disabledPlaceholderColor = GREY_700,
                focusedPlaceholderColor = GREY_700,
                unfocusedPlaceholderColor = GREY_700
            )
        )

        IconButtonSendMessage(
            modifier = Modifier.constrainAs(sendBtnRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            },
            onClick = onSendMessageClick
        )
    }
}

private fun getDrawableStateMessage(item: MessageDocument): Int? {
    return when (item.state) {
        SENDING.name -> R.drawable.ic_sending_24dp
        SENT.name -> R.drawable.ic_sent_14dp
        READ.name -> R.drawable.ic_read_14dp
        else -> null
    }
}

@Preview(device = "spec:width=411dp,height=891dp")
@Composable
private fun ChatScreenPreviewMediumPhone() {
    val messages = listOf(
        MessageDocument(
            id = "1",
            text = "Olá! Como você está?",
            personSenderId = "user_1",
            date = 1738551600000,
            state = READ.name,
        ),
        MessageDocument(
            id = "2",
            text = "Oi! Estou bem, e você?",
            personSenderId = "user_2",
            date = 1738551600000,
            state = READ.name,
        ),
        MessageDocument(
            id = "3",
            text = "Também estou bem, obrigado por perguntar!",
            personSenderId = "user_1",
            date = 1743901821970,
            state = READ.name,
        ),
        MessageDocument(
            id = "4",
            text = "Que bom! O que você tem feito ultimamente?",
            personSenderId = "user_2",
            date = 1743901821970,
            state = READ.name,
        ),
        MessageDocument(
            id = "5",
            text = "Tenho trabalhado bastante, mas está sendo produtivo!",
            personSenderId = "user_1",
            date = 1743901821970,
            state = SENDING.name,
        )
    )

    FitnessProTheme {
        Surface {
            ChatScreen(
                state = ChatUIState(
                    title = "Mensagens",
                    subtitle = "Nikolas Luiz Schmitt",
                    messages = messages
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ChatScreenSmallPhone() {
    val messages = listOf(
        MessageDocument(
            id = "1",
            text = "Olá! Como você está?",
            personSenderId = "user_1",
            date = 1743901821970,
            state = READ.name,
        ),
        MessageDocument(
            id = "2",
            text = "Oi! Estou bem, e você?",
            personSenderId = "user_2",
            date = 1743901821970,
            state = READ.name,
        ),
        MessageDocument(
            id = "3",
            text = "Também estou bem, obrigado por perguntar!",
            personSenderId = "user_1",
            date = 1743901821970,
            state = READ.name,
        ),
        MessageDocument(
            id = "4",
            text = "Que bom! O que você tem feito ultimamente?",
            personSenderId = "user_2",
            date = 1743901821970,
            state = READ.name,
        ),
        MessageDocument(
            id = "5",
            text = "Tenho trabalhado bastante, mas está sendo produtivo!",
            personSenderId = "user_1",
            date = 1743901821970,
            state = SENT.name,
        )
    )

    FitnessProTheme {
        Surface {
            ChatScreen(
                state = ChatUIState(
                    title = "Mensagens",
                    subtitle = "Nikolas Luiz Schmitt",
                    messages = messages
                )
            )
        }
    }
}

@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun ChatScreenPreviewTablet() {
    val messages = listOf(
        MessageDocument(
            id = "1",
            text = "Olá! Como você está?",
            personSenderId = "user_1",
            date = 1743901821970,
            state = READ.name,
        ),
        MessageDocument(
            id = "2",
            text = "Oi! Estou bem, e você?",
            personSenderId = "user_2",
            date = 1743901821970,
            state = READ.name,
        ),
        MessageDocument(
            id = "3",
            text = "Também estou bem, obrigado por perguntar!",
            personSenderId = "user_1",
            date = 1743901821970,
            state = READ.name,
        ),
        MessageDocument(
            id = "4",
            text = "Que bom! O que você tem feito ultimamente?",
            personSenderId = "user_2",
            date = 1743901821970,
            state = READ.name,
        ),
        MessageDocument(
            id = "5",
            text = "Tenho trabalhado bastante, mas está sendo produtivo!",
            personSenderId = "user_1",
            date = 1743901821970,
            state = READ.name,
        )
    )

    FitnessProTheme {
        Surface {
            ChatScreen(
                state = ChatUIState(
                    title = "Mensagens",
                    subtitle = "Nikolas Luiz Schmitt",
                    messages = messages
                )
            )
        }
    }
}

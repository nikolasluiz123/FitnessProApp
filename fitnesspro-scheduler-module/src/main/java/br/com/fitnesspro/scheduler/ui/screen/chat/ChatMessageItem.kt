package br.com.fitnesspro.scheduler.ui.screen.chat

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.core.extensions.toEpochMillis
import br.com.fitnesspro.core.theme.ChatMessageTextStyle
import br.com.fitnesspro.core.theme.ChatMessageTimeTextStyle
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.firebase.api.firestore.documents.MessageDocument
import br.com.fitnesspro.firebase.api.firestore.enums.EnumMessageState.READ
import br.com.fitnesspro.firebase.api.firestore.enums.EnumMessageState.SENDING
import br.com.fitnesspro.firebase.api.firestore.enums.EnumMessageState.SENT
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.state.ChatUIState
import java.time.LocalDateTime

@Composable
internal fun ChatMessageItem(item: MessageDocument, state: ChatUIState) {
    val isSender = item.personSenderId == state.authenticatedPersonId
    val containerColor = if (isSender) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSender) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onSurfaceVariant
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(4.dp),
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
                style = ChatMessageTextStyle,
            )

            Row(
                Modifier
                    .padding(top = 8.dp)
                    .constrainAs(timeRef) {
                        top.linkTo(messageRef.bottom, margin = 8.dp)
                        end.linkTo(parent.end)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getFormatedMessageDate(context, item.date!!),
                    style = ChatMessageTimeTextStyle,
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

private fun getDrawableStateMessage(item: MessageDocument): Int? {
    return when (item.state) {
        SENDING.name -> R.drawable.ic_sending_24dp
        SENT.name -> R.drawable.ic_sent_14dp
        READ.name -> R.drawable.ic_read_14dp
        else -> null
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ChatMessageItemPreviewLight() {
    FitnessProTheme {
        Surface {
            ChatMessageItem(
                item = chatWithMessagesState.messages.first(),
                state = chatWithMessagesState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ChatMessageItemPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ChatMessageItem(
                item = chatWithMessagesState.messages.first(),
                state = chatWithMessagesState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ChatMessageItemOtherUserPreviewLight() {
    FitnessProTheme {
        Surface {
            ChatMessageItem(
                item = chatWithMessagesState.messages.first().copy(
                    date = LocalDateTime.now().minusDays(7).toEpochMillis()
                ),
                state = ChatUIState(
                    authenticatedPersonId = "user_2"
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ChatMessageItemOtherUserPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ChatMessageItem(
                item = chatWithMessagesState.messages.first().copy(
                    date = LocalDateTime.now().minusDays(7).toEpochMillis()
                ),
                state = ChatUIState(
                    authenticatedPersonId = "user_2"
                )
            )
        }
    }
}
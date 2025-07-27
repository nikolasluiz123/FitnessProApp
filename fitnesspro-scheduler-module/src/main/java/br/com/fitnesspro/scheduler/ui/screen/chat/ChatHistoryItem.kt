package br.com.fitnesspro.scheduler.ui.screen.chat

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.divider.FitnessProHorizontalDivider
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.toLocalDate
import br.com.fitnesspro.core.extensions.toLocalDateTime
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.LabelTextStyle
import br.com.fitnesspro.core.theme.RED_500
import br.com.fitnesspro.core.theme.ValueTextStyle
import br.com.fitnesspro.firebase.api.analytics.logListItemClick
import br.com.fitnesspro.firebase.api.firestore.documents.ChatDocument
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.navigation.ChatArgs
import br.com.fitnesspro.scheduler.ui.screen.chat.callbacks.OnNavigateToChat
import br.com.fitnesspro.scheduler.ui.screen.chat.enums.EnumChatHistoryScreenTags
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import java.time.ZoneId

@Composable
fun ChatHistoryItem(
    context: Context,
    item: ChatDocument,
    onNavigateToChat: OnNavigateToChat? = null
) {
    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .clickable {
                Firebase.analytics.logListItemClick(EnumChatHistoryScreenTags.CHAT_HISTORY_SCREEN_LIST_ITEM)
                onNavigateToChat?.onExecute(ChatArgs(chatId = item.id))
            }
    ) {
        val (userNameRef, lastMessageRef, notReadMessagesCount, lastMessageDate, dividerRef) = createRefs()

        Text(
            text = item.receiverPersonName,
            style = LabelTextStyle,
            modifier = Modifier
                .padding(top = 16.dp, start = 8.dp, end = 8.dp)
                .constrainAs(userNameRef) {
                    val endConstraint = if (item.notReadMessagesCount > 0) notReadMessagesCount.start else parent.end

                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(endConstraint)

                    width = Dimension.fillToConstraints
                }
        )

        item.lastMessage?.let { message ->
            Text(
                text = message,
                style = ValueTextStyle,
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(lastMessageRef) {
                        val endConstraint = if (item.notReadMessagesCount > 0) notReadMessagesCount.start else parent.end

                        top.linkTo(userNameRef.bottom, margin = 4.dp)
                        start.linkTo(parent.start)
                        end.linkTo(endConstraint)

                        width = Dimension.fillToConstraints
                    }
            )
        }

        if (item.notReadMessagesCount > 0) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(color = RED_500, shape = CircleShape)
                    .constrainAs(notReadMessagesCount) {
                        top.linkTo(userNameRef.top, margin = 8.dp)
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

        item.lastMessageDate?.let { date ->
            Text(
                text = getFormatedMessageDate(context, date),
                style = ValueTextStyle,
                modifier = Modifier.constrainAs(lastMessageDate) {
                    top.linkTo(userNameRef.bottom)
                    end.linkTo(parent.end, margin = 8.dp)
                    bottom.linkTo(parent.bottom, margin = 8.dp)
                }
            )
        }

        FitnessProHorizontalDivider(
            modifier = Modifier.constrainAs(dividerRef) {
                val topConstraint = if (item.lastMessageDate != null) lastMessageDate.bottom else userNameRef.bottom

                top.linkTo(topConstraint, margin = 12.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
        )
    }
}

internal fun getFormatedMessageDate(context: Context, date: Long): String {
    return when {
        date.toLocalDate() == dateNow(ZoneId.systemDefault()) -> {
            context.getString(
                R.string.chat_message_date_now,
                date.toLocalDateTime().format(EnumDateTimePatterns.TIME)
            )
        }

        date.toLocalDate() == dateTimeNow(ZoneId.systemDefault()).minusDays(1).toLocalDate() -> {
            context.getString(
                R.string.chat_message_date_yesterday,
                date.toLocalDateTime().format(EnumDateTimePatterns.TIME)
            )
        }

        date.toLocalDate().year == dateNow(ZoneId.systemDefault()).year -> {
            date.toLocalDateTime().format(EnumDateTimePatterns.DAY_MONTH_DATE_TIME_SHORT)
        }

        else -> {
            date.toLocalDateTime().format(EnumDateTimePatterns.DATE_TIME_SHORT)
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ChatHistoryItemPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ChatHistoryItem(
                context = LocalContext.current,
                item = chatHistoryItemState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ChatHistoryItemPreviewLight() {
    FitnessProTheme {
        Surface {
            ChatHistoryItem(
                context = LocalContext.current,
                item = chatHistoryItemState
            )
        }
    }
}
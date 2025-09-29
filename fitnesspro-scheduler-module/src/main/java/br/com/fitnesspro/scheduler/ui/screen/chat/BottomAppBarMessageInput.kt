package br.com.fitnesspro.scheduler.ui.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.android.ui.compose.components.styles.ValueTextStyle
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonSendMessage
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.state.ChatUIState

@Composable
internal fun BottomAppBarMessageInput(
    state: ChatUIState,
    onSendMessageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        val (inputRef, sendBtnRef) = createRefs()

        OutlinedTextField(
            modifier = Modifier
                .padding(8.dp)
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
                )
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    onSendMessageClick()
                }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = MaterialTheme.colorScheme.onSecondaryContainer,
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSecondaryContainer
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

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun BottomAppBarMessageInputPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            BottomAppBarMessageInput(
                state = ChatUIState(),
                onSendMessageClick = {}
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun BottomAppBarMessageInputPreviewLight() {
    FitnessProTheme {
        Surface {
            BottomAppBarMessageInput(
                state = ChatUIState(),
                onSendMessageClick = {}
            )
        }
    }
}
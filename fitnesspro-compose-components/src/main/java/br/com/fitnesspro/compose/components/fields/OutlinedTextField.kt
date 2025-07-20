package br.com.fitnesspro.compose.components.fields

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonCalendar
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonSearch
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonTime
import br.com.fitnesspro.compose.components.dialog.FitnessProDatePickerDialog
import br.com.fitnesspro.compose.components.dialog.FitnessProListDialog
import br.com.fitnesspro.compose.components.dialog.FitnessProPagedListDialog
import br.com.fitnesspro.compose.components.dialog.TimePickerDialog
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_ERROR_MESSAGE
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_ERROR_TRAILING_ICON
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_TRAILING_ICON
import br.com.fitnesspro.compose.components.fields.state.DatePickerTextField
import br.com.fitnesspro.compose.components.fields.state.DialogListTextField
import br.com.fitnesspro.compose.components.fields.state.ITextField
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.fields.state.TimePickerTextField
import br.com.fitnesspro.compose.components.fields.transformation.DateVisualTransformation
import br.com.fitnesspro.compose.components.fields.transformation.TimeVisualTransformation
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.parseToLocalTime
import br.com.fitnesspro.core.menu.ITupleListItem
import br.com.fitnesspro.core.theme.FieldErrorTextStyle
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.InputTextStyle
import kotlin.properties.Delegates

@Composable
fun OutlinedTextFieldValidation(
    field: ITextField,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = field.errorMessage.isNotEmpty(),
    trailingIcon: @Composable (() -> Unit)? = {
        if (field.errorMessage.isNotEmpty())
            Icon(
                modifier = Modifier.testTag(OUTLINED_TEXT_FIELD_ERROR_TRAILING_ICON.name),
                imageVector = Icons.Default.Warning,
                contentDescription = LocalContext.current.getString(R.string.outlined_textfield_validation_error_icon_description),
                tint = MaterialTheme.colorScheme.error
            )
    },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    colors: TextFieldColors = getDefaultOutlinedTextFieldColors(),
    maxLength: Int? = null,
) {
    OutlinedTextFieldValidation(
        value = field.value,
        onValueChange = field.onChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = { Text(text = label, style = InputTextStyle) },
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        error = field.errorMessage,
        isError = isError,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
        maxLength = maxLength,
    )
}

@Composable
fun OutlinedTextFieldValidation(
    field: TextField,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = field.errorMessage.isNotEmpty(),
    trailingIcon: @Composable (() -> Unit)? = {
        if (field.errorMessage.isNotEmpty())
            Icon(
                Icons.Default.Warning,
                "error",
                tint = MaterialTheme.colorScheme.error
            )
    },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    colors: TextFieldColors = getDefaultOutlinedTextFieldColors(),
) {
    OutlinedTextFieldValidation(
        value = field.value,
        onValueChange = field.onChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        error = field.errorMessage,
        isError = isError,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}

/**
 * Campo de texto com comportamentos necessários para feedback de validações.
 *
 * @param value Valor digitado.
 * @param onValueChange Listener executado ao alterar o valor.
 * @param modifier Modificadores do componente.
 * @param enabled Flag para habilitar ou desabilitar o campo.
 * @param readOnly Flag para bloquear a escrita.
 * @param textStyle Estilo do texto.
 * @param label Label do campo.
 * @param placeholder Texto exibido como dica.
 * @param leadingIcon Ícone a direita.
 * @param error Mensagem de erro de alguma validação.
 * @param isError Flag que indica se o campo está com erro.
 * @param trailingIcon Ícone a esquerda do campo.
 * @param visualTransformation Alterações visuais da saída do texto digitado no campo.
 * @param keyboardOptions Opções de teclado.
 * @param keyboardActions Ações específicas que deverão ser exibidas no teclado.
 * @param singleLine Flag que indica se o campo se expandirá ao digitar mais que uma linha.
 * @param maxLines Número máximo de linhas que o campo poderá se expandir. Caso single line seja false.
 * @param interactionSource Personalização da aparência ou comportamento do campo dependendo do estado.
 * @param shape Forma do campo.
 * @param colors Cores do campo.
 *
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun OutlinedTextFieldValidation(
    value: String?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = InputTextStyle,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    error: String = "",
    isError: Boolean = error.isNotEmpty(),
    trailingIcon: @Composable (() -> Unit)? = {
        if (error.isNotEmpty())
            Icon(
                modifier = Modifier.testTag(OUTLINED_TEXT_FIELD_ERROR_TRAILING_ICON.name),
                imageVector = Icons.Default.Warning,
                contentDescription = "error",
                tint = MaterialTheme.colorScheme.error
            )
    },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    colors: TextFieldColors = getDefaultOutlinedTextFieldColors(),
    maxLength: Int? = null,
    writeCondition: Boolean = true
) {
    OutlinedTextField(
        enabled = enabled,
        readOnly = readOnly,
        modifier = modifier,
        value = value ?: "",
        onValueChange = {
            if(maxLength == null || it.length <= maxLength) {
                onValueChange(it)
            }
        },
        singleLine = singleLine,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = {
            if (error.isNotEmpty()) {
                Text(
                    modifier = Modifier.testTag(OUTLINED_TEXT_FIELD_ERROR_MESSAGE.name),
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = FieldErrorTextStyle
                )
            }
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        maxLines = maxLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}

@Composable
fun getDefaultOutlinedTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        cursorColor = MaterialTheme.colorScheme.secondary,
        unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
        unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
        unfocusedTextColor = MaterialTheme.colorScheme.secondary,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.secondary,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
        focusedTextColor = MaterialTheme.colorScheme.secondary,
    )
}

@Composable
fun OutlinedTextFieldPasswordValidation(
    field: TextField,
    label: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions,
    maxLength: Int? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextFieldPasswordValidation(
        value = field.value,
        onValueChange = field.onChange,
        error = field.errorMessage,
        modifier = modifier,
        label = { Text(text = label, style = InputTextStyle) },
        keyboardOptions = keyboardOptions,
        maxLength = maxLength,
        keyboardActions = keyboardActions
    )
}

@Composable
fun OutlinedTextFieldPasswordValidation(
    field: TextField,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions,
    label: @Composable (() -> Unit)? = null,
    maxLength: Int? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextFieldPasswordValidation(
        value = field.value,
        onValueChange = field.onChange,
        error = field.errorMessage,
        modifier = modifier,
        label = label,
        keyboardOptions = keyboardOptions,
        maxLength = maxLength,
        keyboardActions = keyboardActions
    )
}

/**
 * Campo de texto com comportamento padrão para manipulação de senhas.
 *
 * @param value Valor digitado.
 * @param onValueChange Listener executado ao alterar o valor.
 * @param modifier Modificadores do componente.
 * @param label Label do campo.
 * @param error Mensagem de erro de alguma validação.
 * @param keyboardOptions Opções de teclado.
 *
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun OutlinedTextFieldPasswordValidation(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    error: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
    maxLength: Int? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextFieldValidation(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        error = error,
        label = label,
        keyboardOptions = keyboardOptions,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            lateinit var description: String
            var resource by Delegates.notNull<Int>()

            if (passwordVisible) {
                resource = br.com.fitnesspro.core.R.drawable.ic_visibility_24dp
                description = stringResource(R.string.description_icon_hide_password)
            } else {
                resource = br.com.fitnesspro.core.R.drawable.ic_visibility_off_24dp
                description = stringResource(R.string.description_icon_show_password)
            }

            IconButton(
                modifier = Modifier.testTag(OUTLINED_TEXT_FIELD_TRAILING_ICON.name),
                onClick = { passwordVisible = !passwordVisible }
            ) {
                Icon(painter = painterResource(resource), description, tint = MaterialTheme.colorScheme.onBackground)
            }
        },
        maxLength = maxLength,
        keyboardActions = keyboardActions
    )
}

@Composable
fun TimePickerOutlinedTextFieldValidation(
    field: TimePickerTextField,
    fieldLabel: String,
    timePickerTitle: String,
    modifier: Modifier = Modifier,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextFieldValidation(
        field = field,
        label = fieldLabel,
        modifier = modifier,
        trailingIcon = {
            IconButtonTime(
                modifier = Modifier.testTag(OUTLINED_TEXT_FIELD_TRAILING_ICON.name),
                iconColor = MaterialTheme.colorScheme.secondary
            ) { field.onTimePickerOpenChange(true) }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        visualTransformation = TimeVisualTransformation(),
        maxLength = 4
    )

    if (field.timePickerOpen) {
        TimePickerDialog(
            title = timePickerTitle,
            value = field.value.parseToLocalTime(EnumDateTimePatterns.TIME_ONLY_NUMBERS),
            onConfirm = field.onTimeChange,
            onDismiss = field.onTimeDismiss
        )
    }
}

@Composable
fun DatePickerOutlinedTextFieldValidation(
    field: DatePickerTextField,
    fieldLabel: String,
    modifier: Modifier = Modifier,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    imeAction: ImeAction = ImeAction.Next
) {
    OutlinedTextFieldValidation(
        field = field,
        label = fieldLabel,
        modifier = modifier,
        trailingIcon = {
            IconButtonCalendar(
                modifier = Modifier.testTag(OUTLINED_TEXT_FIELD_TRAILING_ICON.name),
                iconColor = MaterialTheme.colorScheme.secondary
            ) { field.onDatePickerOpenChange(true) }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        visualTransformation = DateVisualTransformation(),
        maxLength = 8
    )

    if (field.datePickerOpen) {
        FitnessProDatePickerDialog(
            onDismissRequest = field.onDatePickerDismiss,
            onConfirm = field.onDateChange,
            onCancel = field.onDatePickerDismiss
        )
    }
}

@Composable
fun <T: ITupleListItem> PagedListDialogOutlinedTextFieldValidation(
    field: PagedDialogListTextField<T>,
    fieldLabel: String,
    simpleFilterPlaceholderResId: Int,
    emptyMessage: Int,
    itemLayout: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
) {
    OutlinedTextFieldValidation(
        field = field,
        label = fieldLabel,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        trailingIcon = {
            if (field.dialogListState.showTrailingIcon) {
                IconButtonSearch(
                    modifier = Modifier.testTag(OUTLINED_TEXT_FIELD_TRAILING_ICON.name),
                    onClick = field.dialogListState.onShow,
                    iconColor = MaterialTheme.colorScheme.secondary
                )
            }
        }
    )

    if (field.dialogListState.show) {
        FitnessProPagedListDialog(
            state = field.dialogListState,
            simpleFilterPlaceholderResId = simpleFilterPlaceholderResId,
            emptyMessage = emptyMessage,
            itemLayout = itemLayout
        )
    }
}

@Composable
fun <T: ITupleListItem> ListDialogOutlinedTextFieldValidation(
    field: DialogListTextField<T>,
    fieldLabel: String,
    simpleFilterPlaceholderResId: Int,
    emptyMessage: Int,
    itemLayout: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
) {
    OutlinedTextFieldValidation(
        field = field,
        label = fieldLabel,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        trailingIcon = {
            IconButtonSearch(
                onClick = field.dialogListState.onShow,
                iconColor = MaterialTheme.colorScheme.secondary
            )
        }
    )

    if (field.dialogListState.show) {
        FitnessProListDialog(
            state = field.dialogListState,
            simpleFilterPlaceholderResId = simpleFilterPlaceholderResId,
            emptyMessage = emptyMessage,
            itemLayout = itemLayout
        )
    }
}

@Preview(device = "id:small_phone")
@Composable
fun OutlinedTextFieldValidationEmptyPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            OutlinedTextFieldValidation(
                label = {
                    Text("Label")
                },
                value = "",
                onValueChange = { },
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
fun OutlinedTextFieldValidationPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            OutlinedTextFieldValidation(
                label = {
                    Text("Label")
                },
                value = "Valor digitado pelo usuário",
                onValueChange = { },
                error = "Mensagem de erro."
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
fun OutlinedTextFieldValidationWithoutErrorPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            OutlinedTextFieldValidation(
                label = {
                    Text("Label")
                },
                value = "Valor digitado pelo usuário",
                onValueChange = { }
            )
        }
    }
}


@Preview(device = "id:small_phone")
@Composable
fun OutlinedTextFieldValidationEmptyPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            OutlinedTextFieldValidation(
                label = {
                    Text("Label")
                },
                value = "",
                onValueChange = { },
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
fun OutlinedTextFieldValidationPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            OutlinedTextFieldValidation(
                label = {
                    Text("Label")
                },
                value = "Valor digitado pelo usuário",
                onValueChange = { },
                error = "Mensagem de erro."
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
fun OutlinedTextFieldValidationWithoutErrorPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            OutlinedTextFieldValidation(
                label = {
                    Text("Label")
                },
                value = "Valor digitado pelo usuário",
                onValueChange = { }
            )
        }
    }
}
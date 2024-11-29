package br.com.fitnesspro.ui.screen.registeruser

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonCalendar
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldPasswordValidation
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.transformation.PhoneVisualTransformation
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.keyboard.EmailKeyboardOptions
import br.com.fitnesspro.core.keyboard.LastPasswordKeyboardOptions
import br.com.fitnesspro.core.keyboard.PersonNameKeyboardOptions
import br.com.fitnesspro.core.keyboard.PhoneKeyboardOptions
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.ui.state.RegisterUserUIState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUserTabGeneral(state: RegisterUserUIState, onDone: () -> Unit) {
    val scrollState = rememberScrollState()

    ConstraintLayout(
        Modifier
            .padding(12.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        val (nameRef, emailRef, passwordRef, birthDayDatePickerRef, phoneRef) = createRefs()

        var birthDayDatePickerShow by remember { mutableStateOf(false) }
        val birthDayDatePickerState = rememberDatePickerState()

        OutlinedTextFieldValidation(
            field = state.name,
            label = stringResource(R.string.register_user_screen_label_name),
            modifier = Modifier.constrainAs(nameRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = PersonNameKeyboardOptions,
        )

        OutlinedTextFieldValidation(
            field = state.email,
            label = stringResource(R.string.register_user_screen_label_email),
            modifier = Modifier.constrainAs(emailRef) {
                start.linkTo(parent.start)
                top.linkTo(nameRef.bottom)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = EmailKeyboardOptions,
        )

        OutlinedTextFieldPasswordValidation(
            field = state.password,
            label = stringResource(R.string.register_user_screen_label_password),
            modifier = Modifier.constrainAs(passwordRef) {
                start.linkTo(parent.start)
                top.linkTo(emailRef.bottom)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = LastPasswordKeyboardOptions,
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            )
        )

        OutlinedTextFieldValidation(
            modifier = Modifier
                .constrainAs(birthDayDatePickerRef) {
                    start.linkTo(parent.start)
                    top.linkTo(passwordRef.bottom)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                },
            field = state.birthDate,
            label = { Text(text = stringResource(R.string.register_user_screen_label_birth_date)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Words
            ),
            enabled = true,
            readOnly = true,
            keyboardActions = KeyboardActions(
                onDone = {
                    defaultKeyboardAction(ImeAction.Done)
                }
            ),
            trailingIcon = {
                IconButtonCalendar { birthDayDatePickerShow = true }
            }
        )

        if (birthDayDatePickerShow) {
            DatePickerDialog(
                onDismissRequest = {
                    birthDayDatePickerShow = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            birthDayDatePickerState.selectedDateMillis?.let {
                                val localDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                                val formatedDate = localDate.format(
                                    DateTimeFormatter.ofPattern(EnumDateTimePatterns.DATE.pattern)
                                )

                                state.birthDate.onChange(formatedDate)
                            }

                            birthDayDatePickerShow = false
                        },
                    ) {
                        Text(text = stringResource(id = br.com.fitnesspro.compose.components.R.string.label_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { birthDayDatePickerShow = false }) {
                        Text(text = stringResource(id = br.com.fitnesspro.compose.components.R.string.label_cancel))
                    }
                }
            ) {
                DatePicker(state = birthDayDatePickerState)
            }
        }

        OutlinedTextFieldValidation(
            field = state.phone,
            label = stringResource(R.string.register_user_screen_label_phone),
            modifier = Modifier.constrainAs(phoneRef) {
                start.linkTo(parent.start)
                top.linkTo(birthDayDatePickerRef.bottom)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = PhoneKeyboardOptions,
            visualTransformation = PhoneVisualTransformation(),
        )
    }
}

@Preview
@Composable
private fun RegisterUserTabGeneralPreview() {
    FitnessProTheme {
        Surface {
            RegisterUserTabGeneral(
                state = RegisterUserUIState(),
                onDone = {}
            )
        }
    }
}
package br.com.fitnesspro.ui.screen.registeruser

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldPasswordValidation
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.core.keyboard.EmailKeyboardOptions
import br.com.fitnesspro.core.keyboard.LastPasswordKeyboardOptions
import br.com.fitnesspro.core.keyboard.NormalTextKeyboardOptions
import br.com.fitnesspro.core.keyboard.PersonNameKeyboardOptions
import br.com.fitnesspro.service.data.access.dto.user.enums.EnumUserDTOValidationFields
import br.com.fitnesspro.ui.state.RegisterUserUIState

@Composable
fun RegisterUserTabGeneral(state: RegisterUserUIState, onDone: () -> Unit) {
    val scrollState = rememberScrollState()

    ConstraintLayout(
        Modifier
            .padding(12.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        val (firstNameRef, lastNameRef, usernameRef, emailRef, passwordRef) = createRefs()

        OutlinedTextFieldValidation(
            field = state.firstName,
            label = stringResource(R.string.register_user_screen_label_first_name),
            modifier = Modifier.constrainAs(firstNameRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = PersonNameKeyboardOptions,
            maxLength = EnumUserDTOValidationFields.FIRST_NAME.maxLength
        )

        OutlinedTextFieldValidation(
            field = state.lastName,
            label = stringResource(R.string.register_user_screen_label_last_name),
            modifier = Modifier.constrainAs(lastNameRef) {
                start.linkTo(parent.start)
                top.linkTo(firstNameRef.bottom)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = PersonNameKeyboardOptions,
            maxLength = EnumUserDTOValidationFields.LAST_NAME.maxLength
        )

        OutlinedTextFieldValidation(
            field = state.username,
            label = stringResource(R.string.register_user_screen_label_username),
            modifier = Modifier.constrainAs(usernameRef) {
                start.linkTo(parent.start)
                top.linkTo(lastNameRef.bottom)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = NormalTextKeyboardOptions,
            maxLength = EnumUserDTOValidationFields.USERNAME.maxLength
        )

        OutlinedTextFieldValidation(
            field = state.email,
            label = stringResource(R.string.register_user_screen_label_email),
            modifier = Modifier.constrainAs(emailRef) {
                start.linkTo(parent.start)
                top.linkTo(usernameRef.bottom)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = EmailKeyboardOptions,
            maxLength = EnumUserDTOValidationFields.EMAIL.maxLength
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
            maxLength = EnumUserDTOValidationFields.PASSWORD.maxLength,
            keyboardOptions = LastPasswordKeyboardOptions,
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            )
        )
    }
}
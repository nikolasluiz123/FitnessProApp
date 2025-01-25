package br.com.fitnesspro.common.ui.screen.registeruser

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_BIRTH_DATE
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_EMAIL
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_NAME
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PASSWORD
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PHONE
import br.com.fitnesspro.common.ui.state.RegisterUserUIState
import br.com.fitnesspro.compose.components.fields.DatePickerOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldPasswordValidation
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.transformation.PhoneVisualTransformation
import br.com.fitnesspro.core.keyboard.EmailKeyboardOptions
import br.com.fitnesspro.core.keyboard.LastPhoneKeyboardOptions
import br.com.fitnesspro.core.keyboard.PasswordKeyboardOptions
import br.com.fitnesspro.core.keyboard.PersonNameKeyboardOptions
import br.com.fitnesspro.core.theme.FitnessProTheme

@Composable
fun RegisterUserTabGeneral(state: RegisterUserUIState, onDone: () -> Unit) {
    val scrollState = rememberScrollState()

    ConstraintLayout(
        Modifier
            .testTag(REGISTER_USER_SCREEN_TAB_GENERAL.name)
            .padding(12.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        val (nameRef, emailRef, passwordRef, birthDayDatePickerRef, phoneRef) = createRefs()

        OutlinedTextFieldValidation(
            field = state.name,
            label = stringResource(R.string.register_user_screen_label_name),
            modifier = Modifier
                .testTag(REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_NAME.name)
                .constrainAs(nameRef) {
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
            modifier = Modifier
                .testTag(REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_EMAIL.name)
                .constrainAs(emailRef) {
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
            modifier = Modifier
                .testTag(REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PASSWORD.name)
                .constrainAs(passwordRef) {
                start.linkTo(parent.start)
                top.linkTo(emailRef.bottom)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
            },
            keyboardOptions = PasswordKeyboardOptions
        )

        DatePickerOutlinedTextFieldValidation(
            modifier = Modifier
                .testTag(REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_BIRTH_DATE.name)
                .constrainAs(birthDayDatePickerRef) {
                    start.linkTo(parent.start)
                    top.linkTo(passwordRef.bottom)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                },
            field = state.birthDate,
            fieldLabel = stringResource(R.string.register_user_screen_label_birth_date),
        )

        if (state.isVisibleFieldPhone) {
            OutlinedTextFieldValidation(
                field = state.phone,
                label = stringResource(R.string.register_user_screen_label_phone),
                modifier = Modifier
                    .testTag(REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PHONE.name)
                    .constrainAs(phoneRef) {
                    start.linkTo(parent.start)
                    top.linkTo(birthDayDatePickerRef.bottom)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                },
                keyboardOptions = LastPhoneKeyboardOptions,
                visualTransformation = PhoneVisualTransformation(),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onDone()
                    }
                )
            )
        }
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

@Preview
@Composable
private fun RegisterUserTabGeneralProfessionalPreview() {
    FitnessProTheme {
        Surface {
            RegisterUserTabGeneral(
                state = RegisterUserUIState(
                    isVisibleFieldPhone = true
                ),
                onDone = {}
            )
        }
    }
}
package br.com.fitnesspro.common.ui.screen.registeruser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
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
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_USER_TYPE
import br.com.fitnesspro.common.ui.state.RegisterUserUIState
import br.com.fitnesspro.compose.components.fields.DatePickerOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldPasswordValidation
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.menu.DefaultExposedDropdownMenu
import br.com.fitnesspro.compose.components.fields.transformation.PhoneVisualTransformation
import br.com.fitnesspro.compose.components.loading.FitnessProLinearProgressIndicator
import br.com.fitnesspro.core.keyboard.EmailKeyboardOptions
import br.com.fitnesspro.core.keyboard.LastPhoneKeyboardOptions
import br.com.fitnesspro.core.keyboard.PasswordKeyboardOptions
import br.com.fitnesspro.core.keyboard.PersonNameKeyboardOptions
import br.com.fitnesspro.core.theme.FitnessProTheme

@Composable
fun RegisterUserTabGeneral(state: RegisterUserUIState, onDone: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(Modifier.fillMaxSize()) {
        FitnessProLinearProgressIndicator(
            modifier = Modifier.padding(top = 4.dp),
            show = state.showLoading
        )

        ConstraintLayout(
            Modifier
                .testTag(REGISTER_USER_SCREEN_TAB_GENERAL.name)
                .padding(horizontal = 12.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
        ) {
            val (nameRef, emailRef, passwordRef, birthDayDatePickerRef, phoneRef, userTypeRef) = createRefs()

            if (state.isRegisterServiceAuth) {
                DefaultExposedDropdownMenu(
                    field = state.userType,
                    labelResId = R.string.register_user_screen_label_user_type,
                    modifier = Modifier
                        .testTag(REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_USER_TYPE.name)
                        .constrainAs(userTypeRef) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top, margin = 12.dp)
                            end.linkTo(parent.end)

                            width = Dimension.fillToConstraints
                        }
                )
            }

            OutlinedTextFieldValidation(
                field = state.name,
                label = stringResource(R.string.register_user_screen_label_name),
                modifier = Modifier
                    .testTag(REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_NAME.name)
                    .constrainAs(nameRef) {
                        val constraintTop = if (state.isRegisterServiceAuth) userTypeRef.bottom else parent.top
                        val marginTop = if (state.isRegisterServiceAuth) 8.dp else 12.dp

                        start.linkTo(parent.start)
                        top.linkTo(constraintTop, marginTop)
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
                        top.linkTo(nameRef.bottom, margin = 8.dp)
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
                        top.linkTo(emailRef.bottom, margin = 8.dp)
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
                        top.linkTo(passwordRef.bottom, margin = 8.dp)
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
                            top.linkTo(birthDayDatePickerRef.bottom, margin = 8.dp)
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

}

@Preview(device = "id:small_phone")
@Composable
private fun RegisterUserTabGeneralPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            RegisterUserTabGeneral(
                state = RegisterUserUIState(),
                onDone = {}
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun RegisterUserTabGeneralProfessionalPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            RegisterUserTabGeneral(
                state = registerUserWithFoneState,
                onDone = {}
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun RegisterUserTabGeneralAuthenticatedServicePreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            RegisterUserTabGeneral(
                state = registerUserServiceState,
                onDone = {}
            )
        }
    }
}


@Preview(device = "id:small_phone")
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

@Preview(device = "id:small_phone")
@Composable
private fun RegisterUserTabGeneralProfessionalPreview() {
    FitnessProTheme {
        Surface {
            RegisterUserTabGeneral(
                state = registerUserWithFoneState,
                onDone = {}
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun RegisterUserTabGeneralAuthenticatedServicePreview() {
    FitnessProTheme {
        Surface {
            RegisterUserTabGeneral(
                state = registerUserServiceState,
                onDone = {}
            )
        }
    }
}
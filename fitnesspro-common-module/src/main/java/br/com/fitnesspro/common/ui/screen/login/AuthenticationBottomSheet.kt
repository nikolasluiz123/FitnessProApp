package br.com.fitnesspro.common.ui.screen.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.android.firebase.toolkit.analytics.logButtonClick
import br.com.android.ui.compose.components.buttons.BaseButton
import br.com.android.ui.compose.components.fields.text.OutlinedTextFieldPasswordValidation
import br.com.android.ui.compose.components.fields.text.OutlinedTextFieldValidation
import br.com.android.ui.compose.components.keyboard.EmailKeyboardOptions
import br.com.android.ui.compose.components.keyboard.LastPasswordKeyboardOptions
import br.com.android.ui.compose.components.loading.BaseCircularBlockUIProgressIndicator
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.screen.login.callback.OnLoginClick
import br.com.fitnesspro.common.ui.screen.login.callback.OnLoginWithGoogle
import br.com.fitnesspro.common.ui.screen.login.enums.EnumAuthenticationBottomSheetTags
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_DONE_BUTTON
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_LOGIN_BUTTON
import br.com.fitnesspro.common.ui.state.BottomSheetAuthenticationUIState
import br.com.fitnesspro.common.ui.viewmodel.BottomSheetAuthenticationViewModel
import br.com.fitnesspro.compose.components.buttons.rounded.RoundedGoogleButton
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.core.theme.FitnessProTheme
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

@Composable
fun AuthenticationBottomSheet(viewModel: BottomSheetAuthenticationViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AuthenticationBottomSheet(
        state = state,
        onLoginWithGoogleClick = { _, _ ->
            viewModel.loginWithGoogle()
        },
        onLoginClick = viewModel::login
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationBottomSheet(
    modifier: Modifier = Modifier,
    state: BottomSheetAuthenticationUIState = BottomSheetAuthenticationUIState(),
    onLoginWithGoogleClick: OnLoginWithGoogle? = null,
    onLoginClick: OnLoginClick? = null,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = if (state.showBottomSheet) Expanded else Hidden,
        skipHiddenState = false
    )

    if (state.showBottomSheet) {
        ModalBottomSheet(
            modifier = modifier,
            onDismissRequest = state.onDismissRequest,
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            ConstraintLayout(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                val (emailRef, passwordRef, loginButtonRef, loginGoogleRef, loginFacebookRef,
                    loadingRef) = createRefs()

                BaseCircularBlockUIProgressIndicator(
                    show = state.showLoading,
                    label = stringResource(R.string.auth_bottom_sheet_label_loading),
                    modifier = Modifier.constrainAs(loadingRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                )

                FitnessProMessageDialog(state = state.messageDialogState)

                OutlinedTextFieldValidation(
                    modifier = Modifier
                        .testTag(EnumAuthenticationBottomSheetTags.BOTTOM_SHEET_AUTH_EMAIL_FIELD.name)
                        .constrainAs(emailRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)

                            width = Dimension.fillToConstraints
                        },
                    field = state.email,
                    label = stringResource(R.string.login_screen_label_user),
                    keyboardOptions = EmailKeyboardOptions,
                )

                OutlinedTextFieldPasswordValidation(
                    modifier = Modifier
                        .testTag(EnumAuthenticationBottomSheetTags.BOTTOM_SHEET_AUTH_PASSWORD_FIELD.name)
                        .constrainAs(passwordRef) {
                            start.linkTo(emailRef.start)
                            end.linkTo(emailRef.end)
                            top.linkTo(emailRef.bottom, margin = 8.dp)

                            width = Dimension.fillToConstraints
                        },
                    field = state.password,
                    label = stringResource(R.string.login_screen_label_password),
                    keyboardOptions = LastPasswordKeyboardOptions,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            Firebase.analytics.logButtonClick(LOGIN_SCREEN_DONE_BUTTON)
                            onLoginClick?.onExecute(onSuccess = { })
                        }
                    )
                )

                BaseButton(
                    modifier = Modifier
                        .testTag(EnumAuthenticationBottomSheetTags.BOTTOM_SHEET_AUTH_LOGIN_BUTTON.name)
                        .constrainAs(loginButtonRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(passwordRef.bottom, margin = 8.dp)

                            width = Dimension.fillToConstraints
                        }
                        .padding(start = 8.dp),
                    label = stringResource(R.string.login_screen_label_button_login),
                    onClickListener = {
                        Firebase.analytics.logButtonClick(LOGIN_SCREEN_LOGIN_BUTTON)
                        onLoginClick?.onExecute(onSuccess = { })
                    }
                )

                RoundedGoogleButton(
                    modifier = Modifier
                        .testTag(EnumAuthenticationBottomSheetTags.BOTTOM_SHEET_AUTH_GOOGLE_BUTTON.name)
                        .constrainAs(loginGoogleRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                            top.linkTo(loginButtonRef.bottom)
                        }
                        .padding(end = 8.dp, top = 8.dp, bottom = 16.dp),
                    onClick = {
                        Firebase.analytics.logButtonClick(EnumAuthenticationBottomSheetTags.BOTTOM_SHEET_AUTH_GOOGLE_BUTTON)

                        onLoginWithGoogleClick?.onExecute(
                            onSuccess = { },
                            onUserNotExistsLocal = { }
                        )
                    }
                )
            }
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun AuthenticationBottomSheetPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            AuthenticationBottomSheet(
                state = BottomSheetAuthenticationUIState(
                    showBottomSheet = true,
                )
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun AuthenticationBottomSheetPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            AuthenticationBottomSheet(
                state = BottomSheetAuthenticationUIState(
                    showBottomSheet = true,
                )
            )
        }
    }
}
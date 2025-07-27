package br.com.fitnesspro.common.ui.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.bottomsheet.registeruser.BottomSheetRegisterUser
import br.com.fitnesspro.common.ui.bottomsheet.registeruser.OnNavigateToRegisterUser
import br.com.fitnesspro.common.ui.navigation.RegisterUserScreenArgs
import br.com.fitnesspro.common.ui.screen.login.callback.OnLoginClick
import br.com.fitnesspro.common.ui.screen.login.callback.OnLoginWithGoogle
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_EMAIL_FIELD
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_GOOGLE_BUTTON
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_LOGIN_BUTTON
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_PASSWORD_FIELD
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_REGISTER_BUTTON
import br.com.fitnesspro.common.ui.state.LoginUIState
import br.com.fitnesspro.common.ui.viewmodel.LoginViewModel
import br.com.fitnesspro.compose.components.buttons.FitnessProButton
import br.com.fitnesspro.compose.components.buttons.FitnessProOutlinedButton
import br.com.fitnesspro.compose.components.buttons.RoundedGoogleButton
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldPasswordValidation
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.loading.FitnessProLinearProgressIndicator
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.keyboard.EmailKeyboardOptions
import br.com.fitnesspro.core.keyboard.LastPasswordKeyboardOptions
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.firebase.api.analytics.logButtonClick
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegisterUser: OnNavigateToRegisterUser,
    onNavigateToHome: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    RequestAllPermissions(context)

    LoginScreen(
        state = state,
        onNavigateToRegisterUser = onNavigateToRegisterUser,
        onLoginClick = viewModel::login,
        onNavigateToHome = onNavigateToHome,
        onLoginWithGoogleClick = viewModel::loginWithGoogle
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    state: LoginUIState = LoginUIState(),
    onNavigateToRegisterUser: OnNavigateToRegisterUser? = null,
    onLoginClick: OnLoginClick? = null,
    onNavigateToHome: () -> Unit = { },
    onLoginWithGoogleClick: OnLoginWithGoogle? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = stringResource(R.string.login_screen_title),
                showNavigationIcon = false,
            )
        }
    ) { padding ->
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val (loadingRef, containerRef) = createRefs()
            Row(
                Modifier
                    .fillMaxWidth()
                    .constrainAs(loadingRef) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
            ) {
                FitnessProLinearProgressIndicator(state.showLoading)
            }

            Column(
                modifier = Modifier
                    .constrainAs(containerRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(loadingRef.bottom, margin = 16.dp)
                        bottom.linkTo(parent.bottom)
                    }
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
            ) {

                ConstraintLayout(Modifier.fillMaxWidth()) {
                    val (emailRef, passwordRef, loginButtonRef, registerButtonRef,
                        googleButtonRef) = createRefs()

                    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

                    FitnessProMessageDialog(state = state.messageDialogState)

                    OutlinedTextFieldValidation(
                        modifier = Modifier
                            .testTag(LOGIN_SCREEN_EMAIL_FIELD.name)
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
                            .testTag(LOGIN_SCREEN_PASSWORD_FIELD.name)
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
                                Firebase.analytics.logButtonClick(EnumLoginScreenTags.LOGIN_SCREEN_DONE_BUTTON)
                                onLoginClick?.onExecute(onNavigateToHome)
                            }
                        )
                    )

                    createHorizontalChain(registerButtonRef, loginButtonRef)

                    FitnessProButton(
                        modifier = Modifier
                            .testTag(LOGIN_SCREEN_LOGIN_BUTTON.name)
                            .constrainAs(loginButtonRef) {
                                start.linkTo(parent.start)
                                top.linkTo(passwordRef.bottom, margin = 8.dp)

                                horizontalChainWeight = 0.45F

                                width = Dimension.fillToConstraints
                            }
                            .padding(start = 8.dp),
                        label = stringResource(R.string.login_screen_label_button_login),
                        enabled = state.showLoading.not(),
                        onClickListener = {
                            Firebase.analytics.logButtonClick(LOGIN_SCREEN_LOGIN_BUTTON)
                            onLoginClick?.onExecute(onNavigateToHome)
                        }
                    )

                    FitnessProOutlinedButton(
                        modifier = Modifier
                            .testTag(LOGIN_SCREEN_REGISTER_BUTTON.name)
                            .constrainAs(registerButtonRef) {
                                end.linkTo(parent.end)
                                top.linkTo(passwordRef.bottom, margin = 8.dp)

                                horizontalChainWeight = 0.45F

                                width = Dimension.fillToConstraints
                            },
                        label = stringResource(R.string.login_screen_label_button_register),
                        enabled = state.showLoading.not(),
                        onClickListener = {
                            Firebase.analytics.logButtonClick(LOGIN_SCREEN_REGISTER_BUTTON)
                            openBottomSheet = true
                        }
                    )

                    RoundedGoogleButton(
                        modifier = Modifier
                            .testTag(LOGIN_SCREEN_GOOGLE_BUTTON.name)
                            .constrainAs(googleButtonRef) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                top.linkTo(loginButtonRef.bottom)
                            }
                            .padding(end = 8.dp, top = 8.dp),
                        enabled = state.showLoading.not(),
                        onClick = {
                            Firebase.analytics.logButtonClick(LOGIN_SCREEN_GOOGLE_BUTTON)

                            onLoginWithGoogleClick?.onExecute(
                                onUserNotExistsLocal = {
                                    onNavigateToRegisterUser?.onNavigate(
                                        args = RegisterUserScreenArgs(
                                            toPersonAuthService = it
                                        )
                                    )
                                },
                                onSuccess = {
                                    onNavigateToHome()
                                }
                            )
                        }
                    )

                    if (openBottomSheet) {
                        BottomSheetRegisterUser(
                            onDismissRequest = { openBottomSheet = false },
                            onItemClickListener = onNavigateToRegisterUser
                        )
                    }
                }
            }
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun LoginScreenPreview() {
    FitnessProTheme {
        Surface {
            LoginScreen(state = LoginUIState())
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun LoginScreenDarkPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            LoginScreen(state = LoginUIState())
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun LoginScreenDarkDisabledPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            LoginScreen(
                state = LoginUIState(
                    showLoading = true
                )
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun LoginScreenDisabledPreview() {
    FitnessProTheme() {
        Surface {
            LoginScreen(
                state = LoginUIState(
                    showLoading = true
                )
            )
        }
    }
}
package br.com.fitnesspro.common.ui.screen.login

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.bottomsheet.registeruser.BottomSheetRegisterUser
import br.com.fitnesspro.common.ui.bottomsheet.registeruser.OnNavigateToRegisterUser
import br.com.fitnesspro.common.ui.navigation.RegisterUserScreenArgs
import br.com.fitnesspro.common.ui.screen.login.callback.OnLoginClick
import br.com.fitnesspro.common.ui.screen.login.callback.OnLoginWithGoogle
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTestTags.LOGIN_SCREEN_EMAIL_FIELD
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTestTags.LOGIN_SCREEN_FACEBOOK_BUTTON
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTestTags.LOGIN_SCREEN_GOOGLE_BUTTON
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTestTags.LOGIN_SCREEN_LOGIN_BUTTON
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTestTags.LOGIN_SCREEN_PASSWORD_FIELD
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTestTags.LOGIN_SCREEN_REGISTER_BUTTON
import br.com.fitnesspro.common.ui.state.LoginUIState
import br.com.fitnesspro.common.ui.viewmodel.LoginViewModel
import br.com.fitnesspro.compose.components.buttons.FitnessProButton
import br.com.fitnesspro.compose.components.buttons.FitnessProOutlinedButton
import br.com.fitnesspro.compose.components.buttons.RoundedFacebookButton
import br.com.fitnesspro.compose.components.buttons.RoundedGoogleButton
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldPasswordValidation
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.loading.FitnessProLinearProgressIndicator
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.callback.showInformationDialog
import br.com.fitnesspro.core.extensions.verifyPermissionGranted
import br.com.fitnesspro.core.keyboard.EmailKeyboardOptions
import br.com.fitnesspro.core.keyboard.LastPasswordKeyboardOptions
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_800
import br.com.fitnesspro.core.theme.LabelTextStyle
import br.com.fitnesspro.core.utils.PermissionUtils.requestMultiplePermissionsLauncher
import br.com.fitnesspro.firebase.api.analytics.logButtonClick
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegisterUser: OnNavigateToRegisterUser,
    onNavigateToHome: () -> Unit,
    onNavigateToMockScreen: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LoginScreen(
        state = state,
        onNavigateToRegisterUser = onNavigateToRegisterUser,
        onLoginClick = viewModel::login,
        onNavigateToHome = onNavigateToHome,
        onNavigateToMockScreen = onNavigateToMockScreen,
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
    onNavigateToMockScreen: () -> Unit = { },
    onLoginWithGoogleClick: OnLoginWithGoogle? = null
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = stringResource(R.string.login_screen_title),
                showNavigationIcon = false,
                showMenuWithLogout = false,
                showMenu = true,
                menuItems = {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.login_screen_menu_item_mocks),
                                style = LabelTextStyle,
                                color = GREY_800
                            )
                        },
                        onClick = onNavigateToMockScreen
                    )
                }
            )
        }
    ) { padding ->
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val (loadingRef, containerRef) = createRefs()

            RequestAllPermissions(context)

            ConstraintLayout(
                Modifier.fillMaxWidth()
            ) {
                FitnessProLinearProgressIndicator(
                    state.showLoading,
                    Modifier
                        .constrainAs(loadingRef) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        }
                )
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
                        googleButtonRef, facebookButtonRef) = createRefs()

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
                        onClickListener = {
                            Firebase.analytics.logButtonClick(LOGIN_SCREEN_REGISTER_BUTTON)
                            openBottomSheet = true
                        }
                    )

                    createHorizontalChain(
                        googleButtonRef,
                        facebookButtonRef,
                        chainStyle = ChainStyle.Packed
                    )

                    RoundedGoogleButton(
                        modifier = Modifier
                            .testTag(LOGIN_SCREEN_GOOGLE_BUTTON.name)
                            .constrainAs(googleButtonRef) {
                                start.linkTo(parent.start)
                                top.linkTo(loginButtonRef.bottom)
                            }
                            .padding(end = 8.dp, top = 8.dp),
                        onClick = {
                            Firebase.analytics.logButtonClick(LOGIN_SCREEN_GOOGLE_BUTTON)

                            state.onToggleLoading()

                            onLoginWithGoogleClick?.onExecute(
                                onUserNotExistsLocal = {
                                    state.onToggleLoading()

                                    onNavigateToRegisterUser?.onNavigate(
                                        args = RegisterUserScreenArgs(
                                            toPersonAuthService = it
                                        )
                                    )
                                },
                                onSuccess = {
                                    state.onToggleLoading()
                                    onNavigateToHome()
                                },
                                onFailure = {
                                    state.onToggleLoading()
                                }
                            )
                        }
                    )

                    RoundedFacebookButton(
                        modifier = Modifier
                            .constrainAs(facebookButtonRef) {
                                end.linkTo(parent.end)
                                top.linkTo(loginButtonRef.bottom)
                            }
                            .padding(start = 8.dp, top = 8.dp),
                        onClick = {
                            Firebase.analytics.logButtonClick(LOGIN_SCREEN_FACEBOOK_BUTTON)

                            state.messageDialogState.onShowDialog?.showInformationDialog(
                                message = context.getString(R.string.login_screen_facebook_button_message)
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

@Composable
private fun RequestAllPermissions(context: Context) {
    val requestPermissionLauncher = requestMultiplePermissionsLauncher()

    LaunchedEffect(Unit) {
        val permissions = mutableListOf<String>()

        if (!context.verifyPermissionGranted(Manifest.permission.POST_NOTIFICATIONS) &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        ) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissions.isNotEmpty()) {
            requestPermissionLauncher.launch(permissions.toTypedArray())
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    FitnessProTheme {
        Surface {
            LoginScreen(state = LoginUIState())
        }
    }
}
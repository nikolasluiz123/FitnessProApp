package br.com.fitnesspro.common.ui.screen.login

import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.android.firebase.toolkit.analytics.logButtonClick
import br.com.android.ui.compose.components.buttons.BaseButton
import br.com.android.ui.compose.components.buttons.BaseOutlinedButton
import br.com.android.ui.compose.components.dialog.message.showConfirmationDialog
import br.com.android.ui.compose.components.dialog.message.showInformationDialog
import br.com.android.ui.compose.components.fields.text.OutlinedTextFieldPasswordValidation
import br.com.android.ui.compose.components.fields.text.OutlinedTextFieldValidation
import br.com.android.ui.compose.components.keyboard.EmailKeyboardOptions
import br.com.android.ui.compose.components.keyboard.LastPasswordKeyboardOptions
import br.com.android.ui.compose.components.loading.BaseLinearProgressIndicator
import br.com.android.ui.compose.components.topbar.SimpleTopAppBar
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
import br.com.fitnesspro.compose.components.buttons.rounded.RoundedGoogleButton
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.core.theme.FitnessProTheme
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegisterUser: OnNavigateToRegisterUser,
    onNavigateToHome: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
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
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    val healthConnectInstall = rememberInstallHealthConnectLauncher()
    val healthPermissionLauncher = rememberLauncherForActivityResult(PermissionController.createRequestPermissionResultContract()) { result ->
        if (!result.containsAll(getRequestedPermissionSet())) {
            state.messageDialogState.onShowDialog?.showConfirmationDialog(
                message = context.getString(R.string.not_acepted_all_requested_permission_health_connect),
                onConfirm = {
                    onNavigateToHome()
                }
            )
        }
    }

    Scaffold(
        topBar = {
            SimpleTopAppBar(
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
                BaseLinearProgressIndicator(state.showLoading)
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

                                requestHealthConnectPermissions(
                                    state = state,
                                    context = context,
                                    healthConnectInstall = healthConnectInstall,
                                    coroutine = coroutine,
                                    healthPermissionLauncher = healthPermissionLauncher,
                                    onPermissionsGranted = {
                                        onLoginClick?.onExecute(
                                            onSuccess = {
                                                onNavigateToHome()
                                            }
                                        )
                                    }
                                )
                            }
                        )
                    )

                    createHorizontalChain(registerButtonRef, loginButtonRef)

                    BaseButton(
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

                            requestHealthConnectPermissions(
                                state = state,
                                context = context,
                                healthConnectInstall = healthConnectInstall,
                                coroutine = coroutine,
                                healthPermissionLauncher = healthPermissionLauncher,
                                onPermissionsGranted = {
                                    onLoginClick?.onExecute(
                                        onSuccess = {
                                            onNavigateToHome()
                                        }
                                    )
                                }
                            )
                        }
                    )

                    BaseOutlinedButton(
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

                            requestHealthConnectPermissions(
                                state = state,
                                context = context,
                                healthConnectInstall = healthConnectInstall,
                                coroutine = coroutine,
                                healthPermissionLauncher = healthPermissionLauncher,
                                onPermissionsGranted = {
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

private fun requestHealthConnectPermissions(
    state: LoginUIState,
    context: Context,
    healthConnectInstall: () -> Unit,
    coroutine: CoroutineScope,
    healthPermissionLauncher: ManagedActivityResultLauncher<Set<String>, Set<String>>,
    onPermissionsGranted: () -> Unit,
) {
    val availabilityStatus = HealthConnectClient.getSdkStatus(context)

    when (availabilityStatus) {
        HealthConnectClient.SDK_UNAVAILABLE -> {
            state.messageDialogState.onShowDialog?.showInformationDialog(context.getString(R.string.request_permission_health_connect_sdk_unavailable))
        }

        HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
            healthConnectInstall()
        }

        HealthConnectClient.SDK_AVAILABLE -> {
            coroutine.launch {
                val healthConnectClient = HealthConnectClient.getOrCreate(context)

                val healthPermissionsSet = getRequestedPermissionSet()

                val grantedHealthPermissions = healthConnectClient.permissionController.getGrantedPermissions()
                val permissionsToRequest = healthPermissionsSet.filterNot { it in grantedHealthPermissions }

                if (permissionsToRequest.isNotEmpty()) {
                    healthPermissionLauncher.launch(permissionsToRequest.toSet())
                } else {
                    onPermissionsGranted()
                }
            }
        }
    }
}

private fun getRequestedPermissionSet(): Set<String> = setOf(
    HealthPermission.getReadPermission(HeartRateRecord::class),
    HealthPermission.getReadPermission(StepsRecord::class),
    HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
    HealthPermission.getReadPermission(SleepSessionRecord::class)
)

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun LoginScreenPreview() {
    FitnessProTheme {
        Surface {
            LoginScreen(state = LoginUIState())
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun LoginScreenDarkPreview() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            LoginScreen(state = LoginUIState())
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
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

@Preview(device = "id:small_phone", apiLevel = 35)
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
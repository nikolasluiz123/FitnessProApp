package br.com.fitnesspro.ui.screen.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.buttons.FitnessProButton
import br.com.fitnesspro.compose.components.buttons.FitnessProOutlinedButton
import br.com.fitnesspro.compose.components.buttons.RoundedFacebookButton
import br.com.fitnesspro.compose.components.buttons.RoundedGoogleButton
import br.com.fitnesspro.compose.components.dialog.FitnessProDialog
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldPasswordValidation
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.keyboard.EmailKeyboardOptions
import br.com.fitnesspro.core.keyboard.LastPasswordKeyboardOptions
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.ui.bottomsheet.BottomSheetRegisterUser
import br.com.fitnesspro.ui.screen.login.callback.OnBottomSheetRegisterUserItemClick
import br.com.fitnesspro.ui.state.LoginUIState
import br.com.fitnesspro.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onBottomSheetRegisterUserItemClick: OnBottomSheetRegisterUserItemClick
) {
    val state by viewModel.uiState.collectAsState()

    LoginScreen(
        state = state,
        onBottomSheetRegisterUserItemClick = onBottomSheetRegisterUserItemClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    state: LoginUIState = LoginUIState(),
    onBottomSheetRegisterUserItemClick: OnBottomSheetRegisterUserItemClick? = null
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = stringResource(R.string.login_screen_title),
                showNavigationIcon = false,
                showMenuWithLogout = false
            )
        }
    ) { padding ->
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val (loadingRef, containerRef) = createRefs()

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

                    FitnessProDialog(
                        type = state.dialogType,
                        show = state.showDialog,
                        onDismissRequest = { state.onHideDialog() },
                        message = state.dialogMessage
                    )

                    OutlinedTextFieldValidation(
                        modifier = Modifier.constrainAs(emailRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)

                            width = Dimension.fillToConstraints
                        },
                        field = state.email,
                        label = stringResource(R.string.login_screen_label_email),
                        keyboardOptions = EmailKeyboardOptions
                    )

                    OutlinedTextFieldPasswordValidation(
                        modifier = Modifier.constrainAs(passwordRef) {
                            start.linkTo(emailRef.start)
                            end.linkTo(emailRef.end)
                            top.linkTo(emailRef.bottom, margin = 8.dp)

                            width = Dimension.fillToConstraints
                        },
                        field = state.password,
                        label = stringResource(R.string.login_screen_label_password),
                        keyboardOptions = LastPasswordKeyboardOptions
                    )

                    createHorizontalChain(registerButtonRef, loginButtonRef)

                    FitnessProButton(
                        modifier = Modifier.constrainAs(loginButtonRef) {
                                start.linkTo(parent.start)
                                top.linkTo(passwordRef.bottom, margin = 8.dp)

                                horizontalChainWeight = 0.45F

                                width = Dimension.fillToConstraints
                            }
                            .padding(start = 8.dp),
                        label = stringResource(R.string.login_screen_label_button_login),
                        enabled = !state.showLoading,
                        onClickListener = { }
                    )

                    FitnessProOutlinedButton(
                        modifier = Modifier.constrainAs(registerButtonRef) {
                            end.linkTo(parent.end)
                            top.linkTo(passwordRef.bottom, margin = 8.dp)

                            horizontalChainWeight = 0.45F

                            width = Dimension.fillToConstraints
                        },
                        label = stringResource(R.string.login_screen_label_button_register),
                        onClickListener = { openBottomSheet = true }
                    )

                    createHorizontalChain(
                        googleButtonRef,
                        facebookButtonRef,
                        chainStyle = ChainStyle.Packed
                    )

                    RoundedGoogleButton(
                        modifier = Modifier
                            .constrainAs(googleButtonRef) {
                                start.linkTo(parent.start)
                                top.linkTo(loginButtonRef.bottom)
                            }
                            .padding(end = 8.dp, top = 8.dp)
                    )

                    RoundedFacebookButton(
                        modifier = Modifier
                            .constrainAs(facebookButtonRef) {
                                end.linkTo(parent.end)
                                top.linkTo(loginButtonRef.bottom)
                            }
                            .padding(start = 8.dp, top = 8.dp)
                    )


                    if (openBottomSheet) {
                        BottomSheetRegisterUser(
                            onDismissRequest = { openBottomSheet = false },
                            onItemClickListener = onBottomSheetRegisterUserItemClick
                        )
                    }
                }

            }
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
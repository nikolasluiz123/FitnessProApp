package br.com.fitnesspro.tests.ui.isolated.login

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.navigation.loginScreen
import br.com.fitnesspro.common.ui.navigation.loginScreenRoute
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterUserScreen
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_EMAIL_FIELD
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_LOGIN_BUTTON
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_PASSWORD_FIELD
import br.com.fitnesspro.common.usecase.login.enums.EnumValidatedLoginFields
import br.com.fitnesspro.compose.components.dialog.enums.EnumFitnessProMessageDialogTestTags.FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertRequiredTextFieldValidation
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.writeTextField
import br.com.fitnesspro.ui.navigation.navigateToHomeScreen
import br.com.fitnesspro.ui.navigation.navigateToMockScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class LoginScreenFieldsValidationUITests: BaseAuthenticatedUITest() {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<AndroidTestsActivity>()

    override fun getHiltAndroidRule() = hiltRule

    override fun getAndroidComposeRule() = composeTestRule

    override fun getTag() = TAG

    override fun getStartingDestination() = loginScreenRoute

    override fun NavGraphBuilder.testNavGraph(navController: NavHostController) {
        loginScreen(
            onNavigateToRegisterUser = navController::navigateToRegisterUserScreen,
            onNavigateToHome = {
                navController.navigateToHomeScreen(
                    navOptions = navOptions {
                        popUpTo(loginScreenRoute) { inclusive = true }
                    }
                )
            },
            onNavigateToMockScreen = navController::navigateToMockScreen
        )
    }

    @Test
    fun should_show_error_when_email_is_empty() {
        setNavHostContent()

        composeTestRule.apply {
            assertRequiredTextFieldValidation(
                LOGIN_SCREEN_EMAIL_FIELD,
                LOGIN_SCREEN_LOGIN_BUTTON,
                activity.getString(
                    R.string.validation_msg_required_field,
                    activity.getString(EnumValidatedLoginFields.EMAIL.labelResId)
                )
            )
        }
    }

    @Test
    fun should_show_error_when_password_is_empty() {
        setNavHostContent()

        composeTestRule.apply {
            assertRequiredTextFieldValidation(
                LOGIN_SCREEN_PASSWORD_FIELD,
                LOGIN_SCREEN_LOGIN_BUTTON,
                activity.getString(
                    R.string.validation_msg_required_field,
                    activity.getString(EnumValidatedLoginFields.PASSWORD.labelResId)
                )
            )
        }
    }

    @Test
    fun should_show_dialog_with_error_message_when_login_email_is_wrong() = runTest {
        prepareDatabaseWithPersons()
        setNavHostContent()

        composeTestRule.apply {
            writeTextField(LOGIN_SCREEN_EMAIL_FIELD, "personal1213@gmail.com")
            writeTextField(LOGIN_SCREEN_PASSWORD_FIELD, DEFAULT_PASSWORD)
            onClick(LOGIN_SCREEN_LOGIN_BUTTON)
            assertWithText(
                activity.getString(R.string.validation_msg_invalid_credetials_login),
                FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
            )
        }
    }

    @Test
    fun should_show_dialog_with_error_message_when_login_password_is_wrong() = runTest {
        prepareDatabaseWithPersons()
        setNavHostContent()

        composeTestRule.apply {
            writeTextField(LOGIN_SCREEN_EMAIL_FIELD, PERSONAL_EMAIL)
            writeTextField(LOGIN_SCREEN_PASSWORD_FIELD, "1231231231231")
            onClick(LOGIN_SCREEN_LOGIN_BUTTON)
            assertWithText(
                activity.getString(R.string.validation_msg_invalid_credetials_login),
                FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
            )
        }
    }

    companion object {
        private const val TAG = "LoginScreenFieldsValidationUITests"
    }
}
package br.com.fitnesspro.tests.ui.isolated.login

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.common.ui.navigation.loginScreen
import br.com.fitnesspro.common.ui.navigation.loginScreenRoute
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterUserScreen
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTestTags.LOGIN_SCREEN_EMAIL_FIELD
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTestTags.LOGIN_SCREEN_PASSWORD_FIELD
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_TRAILING_ICON
import br.com.fitnesspro.tests.ui.common.BaseUITests
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.assertWriteTextField
import br.com.fitnesspro.tests.ui.extensions.onClickWithParent
import br.com.fitnesspro.tests.ui.extensions.writeTextField
import br.com.fitnesspro.ui.navigation.navigateToHomeScreen
import br.com.fitnesspro.ui.navigation.navigateToMockScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class LoginScreenWriteFieldsUITests: BaseUITests() {

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
    fun should_show_email_when_user_write() {
        setNavHostContent()

        val email = "nikolas@gmail.com"

        composeTestRule.apply {
            assertWriteTextField(email, LOGIN_SCREEN_EMAIL_FIELD)
        }
    }

    @Test
    fun should_show_password_when_user_write() {
        setNavHostContent()

        val password = "123456"

        composeTestRule.apply {
            writeTextField(LOGIN_SCREEN_PASSWORD_FIELD, password)
            onNodeWithTag(LOGIN_SCREEN_PASSWORD_FIELD.name).assert(!hasText(password))
            onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, LOGIN_SCREEN_PASSWORD_FIELD)
            assertWithText(password, LOGIN_SCREEN_PASSWORD_FIELD)
        }
    }

    companion object {
        private const val TAG = "LoginScreenWriteFieldsUITests"
    }
}
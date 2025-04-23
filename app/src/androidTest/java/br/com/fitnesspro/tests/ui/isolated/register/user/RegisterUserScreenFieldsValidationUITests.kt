package br.com.fitnesspro.tests.ui.isolated.register.user

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.navigation.loginScreen
import br.com.fitnesspro.common.ui.navigation.loginScreenRoute
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterAcademyScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterUserScreen
import br.com.fitnesspro.common.ui.navigation.registerUserScreen
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTags.LOGIN_SCREEN_REGISTER_BUTTON
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_FAB_SAVE
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_EMAIL
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_NAME
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PASSWORD
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields.EMAIL
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields.NAME
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields.PASSWORD
import br.com.fitnesspro.tests.ui.common.BaseUITests
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickWithText
import br.com.fitnesspro.ui.navigation.navigateToHomeScreen
import br.com.fitnesspro.ui.navigation.navigateToMockScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class RegisterUserScreenFieldsValidationUITests: BaseUITests() {

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

        registerUserScreen(
            onBackClick = navController::popBackStack,
            onAddAcademyClick = navController::navigateToRegisterAcademyScreen,
            onAcademyItemClick = navController::navigateToRegisterAcademyScreen
        )
    }

    @Test
    fun should_show_error_when_name_is_empty() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(R.string.label_member_bottom_sheet_register_user))
            onClick(REGISTER_USER_SCREEN_FAB_SAVE)
            assertWithText(
                activity.getString(
                    R.string.validation_msg_required_field,
                    activity.getString(NAME.labelResId)
                ),
                REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_NAME
            )
        }
    }

    @Test
    fun should_show_error_when_email_is_empty() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(R.string.label_member_bottom_sheet_register_user))
            onClick(REGISTER_USER_SCREEN_FAB_SAVE)
            assertWithText(
                activity.getString(
                    R.string.validation_msg_required_field,
                    activity.getString(EMAIL.labelResId)
                ),
                REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_EMAIL
            )
        }
    }

    @Test
    fun should_show_error_when_password_is_empty() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(R.string.label_member_bottom_sheet_register_user))
            onClick(REGISTER_USER_SCREEN_FAB_SAVE)
            assertWithText(
                activity.getString(
                    R.string.validation_msg_required_field,
                    activity.getString(PASSWORD.labelResId)
                ),
                REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PASSWORD
            )
        }
    }

    companion object {
        private const val TAG = "RegisterUserScreenFieldsValidationUITests"
    }
}
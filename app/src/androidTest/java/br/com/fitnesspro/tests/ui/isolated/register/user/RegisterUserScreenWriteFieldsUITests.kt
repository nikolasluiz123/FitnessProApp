package br.com.fitnesspro.tests.ui.isolated.register.user

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.navigation.loginScreen
import br.com.fitnesspro.common.ui.navigation.loginScreenRoute
import br.com.fitnesspro.common.ui.navigation.navigateToLoginScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterAcademyScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterUserScreen
import br.com.fitnesspro.common.ui.navigation.registerUserScreen
import br.com.fitnesspro.common.ui.screen.login.enums.EnumLoginScreenTestTags.LOGIN_SCREEN_REGISTER_BUTTON
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_BIRTH_DATE
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_EMAIL
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_NAME
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PASSWORD
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PHONE
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_TRAILING_ICON
import br.com.fitnesspro.scheduler.ui.navigation.navigateToScheduleScreen
import br.com.fitnesspro.tests.ui.common.BaseUITests
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.assertWriteTextField
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickWithParent
import br.com.fitnesspro.tests.ui.extensions.onClickWithText
import br.com.fitnesspro.tests.ui.extensions.writeTextField
import br.com.fitnesspro.ui.navigation.homeScreen
import br.com.fitnesspro.ui.navigation.homeScreenRoute
import br.com.fitnesspro.ui.navigation.navigateToHomeScreen
import br.com.fitnesspro.ui.navigation.navigateToMockScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class RegisterUserScreenWriteFieldsUITests: BaseUITests() {

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

        homeScreen(
            onNavigateToAccountInformation = navController::navigateToRegisterUserScreen,
            onNavigateToSchedule = navController::navigateToScheduleScreen,
            onNavigateToLogin = {
                navController.navigateToLoginScreen(
                    navOptions = navOptions {
                        popUpTo(homeScreenRoute) { inclusive = true }
                    }
                )
            }
        )
    }

    @Test
    fun should_show_text_on_name_field_when_write() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(R.string.label_member_bottom_sheet_register_user))
            assertWriteTextField("aaaa", REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_NAME)
        }
    }

    @Test
    fun should_show_text_on_email_field_when_write() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(R.string.label_member_bottom_sheet_register_user))
            assertWriteTextField("aaaa", REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_EMAIL)
        }
    }

    @Test
    fun should_show_text_on_password_field_when_write() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(R.string.label_member_bottom_sheet_register_user))
            writeTextField(REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PASSWORD, "aaaa")
            onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PASSWORD)
            assertWithText("aaaa", REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PASSWORD)
        }
    }

    @Test
    fun should_show_text_on_birth_date_field_when_write() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(R.string.label_member_bottom_sheet_register_user))
            assertWriteTextField(
                "31052000",
                REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_BIRTH_DATE,
                "31/05/2000"
            )
        }
    }

    @Test
    fun should_show_text_on_phone_field_when_write() {
        setNavHostContent()

        composeTestRule.apply {
            onClick(LOGIN_SCREEN_REGISTER_BUTTON)
            onClickWithText(activity.getString(R.string.label_trainer_bottom_sheet_register_user))
            assertWriteTextField("123321", REGISTER_USER_SCREEN_TAB_GENERAL_FIELD_PHONE)
        }
    }

    companion object {
        const val TAG = "RegisterUserScreenWriteFieldsUITests"
    }
}
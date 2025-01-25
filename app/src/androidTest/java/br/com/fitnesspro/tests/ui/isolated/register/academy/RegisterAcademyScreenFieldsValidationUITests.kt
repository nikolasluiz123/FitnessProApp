package br.com.fitnesspro.tests.ui.isolated.register.academy

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import androidx.test.ext.junit.rules.ActivityScenarioRule
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.common.ui.navigation.navigateToLoginScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterAcademyScreen
import br.com.fitnesspro.common.ui.navigation.navigateToRegisterUserScreen
import br.com.fitnesspro.common.ui.navigation.registerAcademyScreen
import br.com.fitnesspro.common.ui.navigation.registerUserScreen
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FAB_SAVE
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FIELD_ACADEMY
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FIELD_DAY_WEEK
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FIELD_END
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FIELD_START
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_FAB_ADD
import br.com.fitnesspro.common.usecase.academy.EnumValidatedAcademyFields.ACADEMY
import br.com.fitnesspro.common.usecase.academy.EnumValidatedAcademyFields.DAY_OF_WEEK
import br.com.fitnesspro.common.usecase.academy.EnumValidatedAcademyFields.TIME_END
import br.com.fitnesspro.common.usecase.academy.EnumValidatedAcademyFields.TIME_START
import br.com.fitnesspro.compose.components.fields.enums.EnumDropdownMenuTestTags.DROP_DOWN_MENU_OUTLINED_TEXT_FIELD
import br.com.fitnesspro.compose.components.tabs.EnumTabTestTags.TAB
import br.com.fitnesspro.scheduler.ui.navigation.navigateToScheduleScreen
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertWriteTextField
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onPosition
import br.com.fitnesspro.tests.ui.extensions.onTagWithParent
import br.com.fitnesspro.ui.navigation.homeScreen
import br.com.fitnesspro.ui.navigation.homeScreenRoute
import br.com.fitnesspro.ui.screen.home.enums.EnumHomeScreenTestTags.HOME_SCREEN_ACCOUNT_BUTTON
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class RegisterAcademyScreenFieldsValidationUITests: BaseAuthenticatedUITest() {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<AndroidTestsActivity>()

    override fun getHiltAndroidRule() = hiltRule

    override fun getAndroidComposeRule() = composeTestRule

    override fun getTag() = TAG

    override fun getStartingDestination() = homeScreenRoute

    override fun NavGraphBuilder.testNavGraph(navController: NavHostController) {
        registerUserScreen(
            onBackClick = navController::popBackStack,
            onAddAcademyClick = navController::navigateToRegisterAcademyScreen,
            onAcademyItemClick = navController::navigateToRegisterAcademyScreen
        )

        registerAcademyScreen(
            onBackClick = navController::popBackStack,
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
    fun should_show_error_when_academy_is_empty() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            navigateToRegisterAcademy()
            onClick(REGISTER_ACADEMY_SCREEN_FAB_SAVE)
            assertWriteTextField(
                activity.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    activity.getString(ACADEMY.labelResId)
                ),
                REGISTER_ACADEMY_SCREEN_FIELD_ACADEMY
            )
        }
    }

    @Test
    fun should_show_error_when_day_of_weer_is_empty() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            navigateToRegisterAcademy()
            onClick(REGISTER_ACADEMY_SCREEN_FAB_SAVE)
            onTagWithParent(DROP_DOWN_MENU_OUTLINED_TEXT_FIELD, REGISTER_ACADEMY_SCREEN_FIELD_DAY_WEEK)
                .assert(
                    hasText(
                        activity.getString(
                            br.com.fitnesspro.common.R.string.validation_msg_required_field,
                            activity.getString(DAY_OF_WEEK.labelResId)
                        )
                    )
                )
        }
    }

    @Test
    fun should_show_error_when_hour_start_is_empty() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            navigateToRegisterAcademy()
            onClick(REGISTER_ACADEMY_SCREEN_FAB_SAVE)
            assertWriteTextField(
                activity.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    activity.getString(TIME_START.labelResId)
                ),
                REGISTER_ACADEMY_SCREEN_FIELD_START
            )
        }
    }

    @Test
    fun should_show_error_when_hour_end_is_empty() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            navigateToRegisterAcademy()
            onClick(REGISTER_ACADEMY_SCREEN_FAB_SAVE)
            assertWriteTextField(
                activity.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    activity.getString(TIME_END.labelResId)
                ),
                REGISTER_ACADEMY_SCREEN_FIELD_END
            )
        }
    }

    private fun AndroidComposeTestRule<ActivityScenarioRule<AndroidTestsActivity>, AndroidTestsActivity>.navigateToRegisterAcademy() {
        onClick(HOME_SCREEN_ACCOUNT_BUTTON)
        onPosition(1, TAB).performClick()
        onClick(REGISTER_USER_SCREEN_FAB_ADD)
    }


    companion object {
        private const val TAG = "RegisterAcademyScreenFieldsValidationUITests"
    }
}
package br.com.fitnesspro.tests.ui.isolated.register.academy

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
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_DIALOG_ACADEMIES_LIST_ITEM
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FIELD_ACADEMY
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FIELD_DAY_WEEK
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FIELD_END
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterAcademyTestTags.REGISTER_ACADEMY_SCREEN_FIELD_START
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumRegisterUserScreenTestTags.REGISTER_USER_SCREEN_FAB_ADD
import br.com.fitnesspro.compose.components.dialog.enums.EnumTimePickerInputTestTags.TIME_PICKER_DIALOG_BUTTON_CONFIRM
import br.com.fitnesspro.compose.components.fields.enums.EnumDropdownMenuTestTags.DROP_DOWN_MENU_ITEM
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_TRAILING_ICON
import br.com.fitnesspro.compose.components.tabs.EnumTabTestTags.TAB
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.TIME
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.core.extensions.timeNow
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.scheduler.ui.navigation.navigateToScheduleScreen
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertDropDownMenuWithText
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.assertWriteTextField
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickFirst
import br.com.fitnesspro.tests.ui.extensions.onClickWithParent
import br.com.fitnesspro.tests.ui.extensions.onPosition
import br.com.fitnesspro.ui.navigation.homeScreen
import br.com.fitnesspro.ui.navigation.homeScreenRoute
import br.com.fitnesspro.ui.screen.home.enums.EnumHomeScreenTestTags.HOME_SCREEN_ACCOUNT_BUTTON
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek.MONDAY

@HiltAndroidTest
class RegisterAcademyScreenWriteFieldsUITests: BaseAuthenticatedUITest() {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<AndroidTestsActivity>()

    @Inject
    lateinit var academyDAO: AcademyDAO

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
    fun should_show_text_on_academy_field_when_select() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        createAcademy()
        setNavHostContent()

        composeTestRule.apply {
            navigateToRegisterAcademy()
            onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, REGISTER_ACADEMY_SCREEN_FIELD_ACADEMY)
            onClickFirst(REGISTER_ACADEMY_DIALOG_ACADEMIES_LIST_ITEM)
            assertWithText("academy", REGISTER_ACADEMY_SCREEN_FIELD_ACADEMY)
        }
    }

    @Test
    fun should_show_text_on_day_of_week_field_when_select() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            navigateToRegisterAcademy()
            onPosition(1, OUTLINED_TEXT_FIELD_TRAILING_ICON).performClick()
            onPosition(0, DROP_DOWN_MENU_ITEM).performClick()
            assertDropDownMenuWithText(
                REGISTER_ACADEMY_SCREEN_FIELD_DAY_WEEK,
                MONDAY.getFirstPartFullDisplayName()
            )
        }
    }

    @Test
    fun should_show_text_on_start_hour_when_write() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            navigateToRegisterAcademy()
            assertWriteTextField("0900", REGISTER_ACADEMY_SCREEN_FIELD_START, "09:00")
        }
    }

    @Test
    fun should_show_text_on_start_hour_when_confirm_dialog() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            navigateToRegisterAcademy()
            onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, REGISTER_ACADEMY_SCREEN_FIELD_START)
            onClick(TIME_PICKER_DIALOG_BUTTON_CONFIRM)
            assertWithText(timeNow().format(TIME), REGISTER_ACADEMY_SCREEN_FIELD_START)
        }
    }

    @Test
    fun should_show_text_on_end_hour_when_write() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            navigateToRegisterAcademy()
            assertWriteTextField("0900", REGISTER_ACADEMY_SCREEN_FIELD_END, "09:00")
        }
    }

    @Test
    fun should_show_text_on_end_hour_when_confirm_dialog() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            navigateToRegisterAcademy()
            onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, REGISTER_ACADEMY_SCREEN_FIELD_END)
            onClick(TIME_PICKER_DIALOG_BUTTON_CONFIRM)
            assertWithText(timeNow().format(TIME), REGISTER_ACADEMY_SCREEN_FIELD_END)
        }
    }

    private fun AndroidComposeTestRule<ActivityScenarioRule<AndroidTestsActivity>, AndroidTestsActivity>.navigateToRegisterAcademy() {
        onClick(HOME_SCREEN_ACCOUNT_BUTTON)
        onPosition(1, TAB).performClick()
        onClick(REGISTER_USER_SCREEN_FAB_ADD)
    }

    private suspend fun createAcademy(): Academy {
        val academy = Academy(id = "1", name = "academy")
        academyDAO.saveAcademiesBatch(listOf(academy))

        return academy
    }

    companion object {
        const val TAG = "RegisterAcademyScreenWriteFieldsUITests"
    }
}
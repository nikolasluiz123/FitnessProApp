package br.com.fitnesspro.tests.ui.isolated.scheduler.compromise.member

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.compose.components.dialog.enums.EnumTimePickerInputTestTags.TIME_PICKER_DIALOG_BUTTON_CONFIRM
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_TRAILING_ICON
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.TIME
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.timeNow
import br.com.fitnesspro.scheduler.ui.navigation.compromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToCompromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreenRoute
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_DIALOG_LIST_ITEM
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_END_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_OBSERVATION_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_PROFESSIONAL_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_START_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerDetailsTestTags.SCHEDULER_DETAILS_SCREEN_FAB_ADD
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertOnlyDigitsFieldValidation
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.assertWriteTextField
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickFirst
import br.com.fitnesspro.tests.ui.extensions.onClickWithParent
import br.com.fitnesspro.tests.ui.extensions.onClickWithText
import br.com.fitnesspro.tests.ui.extensions.writeTextField
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MemberCompromiseSuggestionScreenWriteFieldsUITests: BaseAuthenticatedUITest() {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<AndroidTestsActivity>()

    override fun getHiltAndroidRule() = hiltRule

    override fun getAndroidComposeRule() = composeTestRule

    override fun getTag(): String = TAG

    override fun getStartingDestination(): String = schedulerScreenRoute

    override fun NavGraphBuilder.testNavGraph(navController: NavHostController) {
        schedulerScreen(
            onBackClick = navController::popBackStack,
            onDayClick = navController::navigateToSchedulerDetailsScreen,
            onNavigateToCompromise = navController::navigateToCompromiseScreen,
            onNavigateToConfig = navController::navigateToSchedulerConfigScreen
        )

        schedulerDetailsScreen(
            onBackClick = navController::popBackStack,
            onNavigateToCompromise = navController::navigateToCompromiseScreen
        )

        compromiseScreen(
            onBackClick = navController::popBackStack
        )
    }

    @Test
    fun should_write_name_of_selected_professional_on_input_when_click_on_professional_item_in_compromise_suggestion() = runTest {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_PROFESSIONAL_FIELD)
            onClickFirst(COMPROMISE_SCREEN_DIALOG_LIST_ITEM)
            assertWithText(toPersons[0].name!!, COMPROMISE_SCREEN_PROFESSIONAL_FIELD)
        }
    }

    @Test
    fun should_show_time_on_start_input_when_write_on_start_time_field_in_compromise_suggestion() = runTest {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            assertWriteTextField("1400", COMPROMISE_SCREEN_START_HOUR_FIELD, "14:00")
        }
    }

    @Test
    fun should_show_time_on_start_input_when_click_on_time_button_and_confirm_on_time_dialog_in_compromise_suggestion() = runTest {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_START_HOUR_FIELD)
            onClick(TIME_PICKER_DIALOG_BUTTON_CONFIRM)
            assertWithText(timeNow().format(TIME), COMPROMISE_SCREEN_START_HOUR_FIELD)
        }
    }

    @Test
    fun should_show_time_on_end_input_when_click_on_time_button_and_confirm_on_time_dialog_in_compromise_suggestion() = runTest {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_END_HOUR_FIELD)
            onClick(TIME_PICKER_DIALOG_BUTTON_CONFIRM)
            assertWithText(timeNow().format(TIME), COMPROMISE_SCREEN_END_HOUR_FIELD)
        }
    }

    @Test
    fun should_show_observation_on_field_when_write_on_compromise_suggestion() = runTest {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            writeTextField(COMPROMISE_SCREEN_OBSERVATION_FIELD, "Observação")
            assertWithText("Observação", COMPROMISE_SCREEN_OBSERVATION_FIELD)
        }
    }

    @Test
    fun should_not_write_in_time_start_when_not_write_digits_in_compromise_suggestion() = runTest {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            assertOnlyDigitsFieldValidation(COMPROMISE_SCREEN_START_HOUR_FIELD, "aaaa")
        }
    }

    @Test
    fun should_not_write_in_time_end_when_not_write_digits_in_compromise_suggestion() = runTest {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            assertOnlyDigitsFieldValidation(COMPROMISE_SCREEN_END_HOUR_FIELD, "aaaa")
        }
    }

    private suspend fun executeDefaultPrepareForTest() {
        prepareDatabaseWithPersons()
        authenticateMember()
    }

    private fun AndroidComposeTestRule<*, *>.navigateToCompromise() {
        onClickWithText("21")
        onClick(SCHEDULER_DETAILS_SCREEN_FAB_ADD)
    }

    companion object {
        const val TAG = "MemberCompromiseSuggestionScreenWriteFieldsUITests"
    }
}
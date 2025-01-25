package br.com.fitnesspro.tests.ui.isolated.scheduler.compromise.personal

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.compose.components.dialog.enums.EnumDatePickerDialogTestTags.DATE_PICKER_DIALOG
import br.com.fitnesspro.compose.components.dialog.enums.EnumFitnessProPagedListDialogTestTags.FITNESS_PRO_PAGED_LIST_DIALOG
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_TRAILING_ICON
import br.com.fitnesspro.compose.components.filter.EnumSimpleFilterTestTags.SIMPLE_FILTER_SEARCH_BAR_INPUT_FIELD
import br.com.fitnesspro.compose.components.topbar.enums.EnumFitnessProTopAppBarTestTags.FITNESS_PRO_TOP_APP_BAR_TITLE
import br.com.fitnesspro.core.extensions.getShortDisplayName
import br.com.fitnesspro.scheduler.R.string
import br.com.fitnesspro.scheduler.ui.navigation.compromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToCompromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreenRoute
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_DIALOG_LIST_ITEM
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_END_DATE_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_END_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_FAB_SAVE
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_MEMBER_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_START_DATE_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_START_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTestTags.SCHEDULER_SCREEN_RECURRENT_SCHEDULE_FAB
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseUseCase
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertDisplayed
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickFirst
import br.com.fitnesspro.tests.ui.extensions.onClickWithParent
import br.com.fitnesspro.tests.ui.extensions.onClickWithText
import br.com.fitnesspro.tests.ui.extensions.writeTextField
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.WEDNESDAY
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@HiltAndroidTest
class PersonalRecurrentCompromiseScreenActionsUITests : BaseAuthenticatedUITest() {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<AndroidTestsActivity>()

    @Inject
    lateinit var saveCompromiseUseCase: SaveCompromiseUseCase

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
    fun should_navigate_to_when_click_on_recurrent_fab() {
        runTest {
            prepareDatabaseWithPersons()
            authenticatePersonal()
            setNavHostContent()

            composeTestRule.apply {
                waitRecurrentScheduleFABVisibleAndClick()
                assertWithText(
                    activity.getString(string.compromise_screen_title_new_recurrent_compromise),
                    FITNESS_PRO_TOP_APP_BAR_TITLE
                )
            }
        }
    }

    @Test
    fun should_show_member_dialog_list_when_click_on_search_button_of_member_field() {
        runTest {
            prepareDatabaseWithPersons()
            authenticatePersonal()
            setNavHostContent()

            composeTestRule.apply {
                waitRecurrentScheduleFABVisibleAndClick()
                onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_MEMBER_FIELD)
                assertDisplayed(FITNESS_PRO_PAGED_LIST_DIALOG)
            }
        }
    }

    @Test
    fun should_show_only_members_with_filtered_name_when_write_on_search_dialog_list_simple_filter() {
        runTest {
            prepareDatabaseWithPersons()
            authenticatePersonal()
            setNavHostContent()

            composeTestRule.apply {
                waitRecurrentScheduleFABVisibleAndClick()
                onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_MEMBER_FIELD)
                writeTextField(SIMPLE_FILTER_SEARCH_BAR_INPUT_FIELD, "member")
                onAllNodesWithText(toPersons[2].name!!).assertCountEquals(2)
            }
        }
    }

    @Test
    fun should_show_date_dialog_when_click_on_calendar_button_of_start_date_field() {
        runTest {
            prepareDatabaseWithPersons()
            authenticatePersonal()
            setNavHostContent()

            composeTestRule.apply {
                waitRecurrentScheduleFABVisibleAndClick()
                onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_START_DATE_FIELD)
                assertDisplayed(DATE_PICKER_DIALOG)
            }
        }
    }

    @Test
    fun should_show_date_dialog_when_click_on_calendar_button_of_end_date_field() {
        runTest {
            prepareDatabaseWithPersons()
            authenticatePersonal()
            setNavHostContent()

            composeTestRule.apply {
                waitRecurrentScheduleFABVisibleAndClick()
                onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_END_DATE_FIELD)
                assertDisplayed(DATE_PICKER_DIALOG)
            }
        }
    }

    @Test
    fun should_success_when_all_fields_informed_except_observation_and_save() {
        runTest(timeout = 30.toDuration(DurationUnit.SECONDS)) {
            prepareDatabaseWithPersons()
            authenticatePersonal()
            setNavHostContent()

            composeTestRule.apply {
                waitRecurrentScheduleFABVisibleAndClick()

                onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_MEMBER_FIELD)
                onClickFirst(COMPROMISE_SCREEN_DIALOG_LIST_ITEM)

                writeTextField(COMPROMISE_SCREEN_START_DATE_FIELD, "21052025")
                writeTextField(COMPROMISE_SCREEN_END_DATE_FIELD, "21082025")

                writeTextField(COMPROMISE_SCREEN_START_HOUR_FIELD, "0800")
                writeTextField(COMPROMISE_SCREEN_END_HOUR_FIELD, "0900")

                onClickWithText(MONDAY.getShortDisplayName())
                onClickWithText(WEDNESDAY.getShortDisplayName())
                onClickWithText(FRIDAY.getShortDisplayName())

                onClick(COMPROMISE_SCREEN_FAB_SAVE)

                waitUntil(5000) {
                    onNodeWithText(
                        activity.getString(
                            string.compromise_screen_message_success_recurrent,
                            "21/05/2025",
                            "21/08/2025"
                        )
                    ).isDisplayed()
                }
            }
        }
    }

    private fun AndroidComposeTestRule<*, *>.waitRecurrentScheduleFABVisibleAndClick() {
        waitUntil(2000) {
            onNodeWithTag(SCHEDULER_SCREEN_RECURRENT_SCHEDULE_FAB.name).isDisplayed()
        }

        onClick(SCHEDULER_SCREEN_RECURRENT_SCHEDULE_FAB)
    }

    companion object {
        const val TAG = "PersonalRecurrentCompromiseScreenActionsUITests"
    }
}
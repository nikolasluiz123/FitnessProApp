package br.com.fitnesspro.tests.ui.isolated.scheduler.compromise.personal

import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.common.R.string
import br.com.fitnesspro.compose.components.dialog.enums.EnumFitnessProMessageDialogTestTags.FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_TRAILING_ICON
import br.com.fitnesspro.scheduler.ui.navigation.compromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToChatHistoryScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToCompromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreenRoute
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_DIALOG_LIST_ITEM
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_END_DATE_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_END_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_FAB_SAVE
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_MEMBER_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_OBSERVATION_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_START_DATE_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_START_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTags.SCHEDULER_SCREEN_RECURRENT_SCHEDULE_FAB
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.DAY_OF_WEEKS
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertOnlyDigitsFieldValidation
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.assertWriteTextField
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickFirst
import br.com.fitnesspro.tests.ui.extensions.onClickWithParent
import br.com.fitnesspro.tests.ui.extensions.writeTextField
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class PersonalRecurrentCompromiseScreenWriteFieldsUITests : BaseAuthenticatedUITest() {

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
            onNavigateToConfig = navController::navigateToSchedulerConfigScreen,
            onNavigateToChatHistory = navController::navigateToChatHistoryScreen
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
    fun should_show_name_of_selected_member_when_click_on_member_dialog_list_item() {
        runTest {
            prepareDatabaseWithPersons()
            authenticatePersonal()
            setNavHostContent()

            composeTestRule.apply {
                waitRecurrentScheduleFABVisibleAndClick()
                onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_MEMBER_FIELD)
                onClickFirst(COMPROMISE_SCREEN_DIALOG_LIST_ITEM)
                assertWithText(toPersons[2].name!!, COMPROMISE_SCREEN_MEMBER_FIELD)
            }
        }
    }

    @Test
    fun should_show_date_on_start_input_when_write_on_input() {
        runTest {
            prepareDatabaseWithPersons()
            authenticatePersonal()
            setNavHostContent()

            composeTestRule.apply {
                waitRecurrentScheduleFABVisibleAndClick()
                assertWriteTextField("20052025", COMPROMISE_SCREEN_START_DATE_FIELD, "20/05/2025")
            }
        }
    }

    @Test
    fun should_show_date_on_end_input_when_write_on_input() {
        runTest {
            prepareDatabaseWithPersons()
            authenticatePersonal()
            setNavHostContent()

            composeTestRule.apply {
                waitRecurrentScheduleFABVisibleAndClick()
                assertWriteTextField("20052025", COMPROMISE_SCREEN_END_DATE_FIELD, "20/05/2025")
            }
        }
    }

    @Test
    fun should_show_observation_on_field_when_write() {
        runTest {
            prepareDatabaseWithPersons()
            authenticatePersonal()
            setNavHostContent()

            composeTestRule.apply {
                waitRecurrentScheduleFABVisibleAndClick()
                assertWriteTextField("Observacao", COMPROMISE_SCREEN_OBSERVATION_FIELD)
            }
        }

    }

    @Test
    fun should_not_write_in_time_start_when_not_write_digits() {
        runTest {
            prepareDatabaseWithPersons()
            authenticatePersonal()
            setNavHostContent()

            composeTestRule.apply {
                waitRecurrentScheduleFABVisibleAndClick()
                assertOnlyDigitsFieldValidation(COMPROMISE_SCREEN_START_HOUR_FIELD, "aaa")
            }
        }
    }

    @Test
    fun should_not_write_in_time_end_when_not_write_digits() {
        runTest {
            prepareDatabaseWithPersons()
            authenticatePersonal()
            setNavHostContent()

            composeTestRule.apply {
                waitRecurrentScheduleFABVisibleAndClick()
                assertOnlyDigitsFieldValidation(COMPROMISE_SCREEN_END_HOUR_FIELD, "aaa")
            }
        }

    }

    @Test
    fun should_not_write_in_date_start_when_not_write_digits() {
        runTest {
            prepareDatabaseWithPersons()
            authenticatePersonal()
            setNavHostContent()

            composeTestRule.apply {
                waitRecurrentScheduleFABVisibleAndClick()
                assertOnlyDigitsFieldValidation(COMPROMISE_SCREEN_START_DATE_FIELD, "aaa")
            }
        }
    }

    @Test
    fun should_not_write_in_date_end_when_not_write_digits() {
        runTest {
            prepareDatabaseWithPersons()
            authenticatePersonal()
            setNavHostContent()

            composeTestRule.apply {
                waitRecurrentScheduleFABVisibleAndClick()
                assertOnlyDigitsFieldValidation(COMPROMISE_SCREEN_END_DATE_FIELD, "aaa")
            }
        }
    }

    @Test
    fun should_error_when_not_select_day_of_week() {
        runTest {
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

                onClick(COMPROMISE_SCREEN_FAB_SAVE)
                assertWithText(
                    activity.getString(
                        string.validation_msg_required_field,
                        activity.getString(DAY_OF_WEEKS.labelResId)
                    ),
                    FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
                )
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
        const val TAG = "PersonalRecurrentCompromiseScreenWriteFieldsUITests"
    }
}
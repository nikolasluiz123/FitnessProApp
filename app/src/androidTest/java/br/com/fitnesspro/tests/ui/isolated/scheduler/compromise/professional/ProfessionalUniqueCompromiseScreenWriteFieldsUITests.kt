package br.com.fitnesspro.tests.ui.isolated.scheduler.compromise.professional

import android.util.Log
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.compose.components.dialog.enums.EnumFitnessProMessageDialogTestTags.FITNESS_PRO_MESSAGE_DIALOG
import br.com.fitnesspro.compose.components.dialog.enums.EnumTimePickerInputTestTags.TIME_PICKER_DIALOG_BUTTON_CONFIRM
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_TRAILING_ICON
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.TIME
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.timeNow
import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.scheduler.ui.navigation.compromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToChatHistoryScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToCompromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreenRoute
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_DIALOG_LIST_ITEM
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_END_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_FAB_SAVE
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_MEMBER_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_OBSERVATION_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_START_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.details.enums.EnumSchedulerDetailsTags.SCHEDULER_DETAILS_SCREEN_FAB_ADD
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertOnlyDigitsFieldValidation
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.assertWriteTextField
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickFirst
import br.com.fitnesspro.tests.ui.extensions.onClickWithParent
import br.com.fitnesspro.tests.ui.extensions.onClickWithText
import br.com.fitnesspro.tests.ui.extensions.writeTextField
import br.com.fitnesspro.to.TOScheduler
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

@HiltAndroidTest
class ProfessionalUniqueCompromiseScreenWriteFieldsUITests : BaseAuthenticatedUITest() {

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
    fun should_write_name_of_selected_member_on_input_when_is_in_unique_compromise_and_click_on_member_item() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_MEMBER_FIELD)
                onClickFirst(COMPROMISE_SCREEN_DIALOG_LIST_ITEM)
                assertWithText(toPersons[2].name!!, COMPROMISE_SCREEN_MEMBER_FIELD)
            }
        }
    }

    @Test
    fun should_show_time_on_start_input_when_write_on_start_time_field_in_unique_compromise() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                assertWriteTextField("1300", COMPROMISE_SCREEN_START_HOUR_FIELD, "13:00")
            }
        }
    }

    @Test
    fun should_show_time_on_start_input_when_click_on_time_button_and_confirm_on_time_dialog_in_unique_compromise() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_START_HOUR_FIELD)
                onClick(TIME_PICKER_DIALOG_BUTTON_CONFIRM)
                assertWithText(timeNow().format(TIME), COMPROMISE_SCREEN_START_HOUR_FIELD)
            }
        }
    }

    @Test
    fun should_show_time_on_end_input_when_click_on_time_button_and_confirm_on_time_dialog_in_unique_compromise() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_END_HOUR_FIELD)
                onClick(TIME_PICKER_DIALOG_BUTTON_CONFIRM)
                assertWithText(timeNow().format(TIME), COMPROMISE_SCREEN_END_HOUR_FIELD)
            }
        }
    }

    @Test
    fun should_show_observation_on_field_when_write_on_unique_compromise() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                assertWriteTextField("Observação", COMPROMISE_SCREEN_OBSERVATION_FIELD)
            }
        }
    }

    @Test
    fun should_not_write_in_time_start_when_not_write_digits_in_unique_compromise() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                assertOnlyDigitsFieldValidation(COMPROMISE_SCREEN_START_HOUR_FIELD, "aaaa")
            }
        }
    }

    @Test
    fun should_not_write_in_time_end_when_not_write_digits_in_unique_compromise() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                assertOnlyDigitsFieldValidation(COMPROMISE_SCREEN_END_HOUR_FIELD, "aaaa")
            }
        }

    }

    @Test
    fun should_error_when_has_conflict_in_unique_compromise() {
        runTest {
            executeDefaultPrepareForTest()
            createFirstUniqueCompromiseScheduled()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                populateCompromiseFieldsAndSave(
                    start = "1330",
                    end = "1500"
                )

                waitUntil(3000) {
                    onNodeWithTag(FITNESS_PRO_MESSAGE_DIALOG.name).isDisplayed()
                }
            }
        }
    }

    private fun AndroidComposeTestRule<*, *>.populateCompromiseFieldsAndSave(
        start: String,
        end: String,
    ) {
        onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_MEMBER_FIELD)
        onClickFirst(COMPROMISE_SCREEN_DIALOG_LIST_ITEM)
        writeTextField(COMPROMISE_SCREEN_START_HOUR_FIELD, start)
        writeTextField(COMPROMISE_SCREEN_END_HOUR_FIELD, end)
        onClick(COMPROMISE_SCREEN_FAB_SAVE)
    }

    private suspend fun createFirstUniqueCompromiseScheduled() {
        val toScheduler = TOScheduler(
            academyMemberPersonId = toPersons[2].id!!,
            professionalPersonId = toPersons[0].id!!,
            professionalType = toPersons[0].user?.type!!,
            scheduledDate = dateNow().plusDays(1),
            timeStart = LocalTime.of(13, 0),
            timeEnd = LocalTime.of(14, 0),
            situation = EnumSchedulerSituation.SCHEDULED,
            compromiseType = EnumCompromiseType.FIRST,
            observation = "Conversa sobre os treinos"
        )

        val result = saveCompromiseUseCase.execute(toScheduler, EnumSchedulerType.SUGGESTION)

        if (result.isNotEmpty()) {
            Log.e(TAG, "Error creating first compromise suggestion: ${result.map { it.validationType }}")
        }
    }

    private suspend fun executeDefaultPrepareForTest() {
        prepareDatabaseWithPersons()
        authenticatePersonal()
    }

    private fun AndroidComposeTestRule<*, *>.navigateToCompromise() {
        onClickWithText("21")
        onClick(SCHEDULER_DETAILS_SCREEN_FAB_ADD)
    }

    companion object {
        const val TAG = "ProfessionalUniqueCompromiseScreenWriteFieldsUITests"
    }
}
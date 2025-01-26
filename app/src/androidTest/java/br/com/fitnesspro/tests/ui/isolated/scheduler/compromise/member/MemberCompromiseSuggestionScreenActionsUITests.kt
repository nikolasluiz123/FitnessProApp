package br.com.fitnesspro.tests.ui.isolated.scheduler.compromise.member

import android.util.Log
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.compose.components.dialog.enums.EnumFitnessProMessageDialogTestTags.FITNESS_PRO_MESSAGE_DIALOG_CONFIRM_BUTTON
import br.com.fitnesspro.compose.components.dialog.enums.EnumFitnessProMessageDialogTestTags.FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
import br.com.fitnesspro.compose.components.dialog.enums.EnumFitnessProPagedListDialogTestTags.FITNESS_PRO_PAGED_LIST_DIALOG
import br.com.fitnesspro.compose.components.dialog.enums.EnumFitnessProPagedListDialogTestTags.FITNESS_PRO_PAGED_LIST_DIALOG_TITLE
import br.com.fitnesspro.compose.components.dialog.enums.EnumTimePickerInputTestTags.TIME_PICKER_DIALOG
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_TRAILING_ICON
import br.com.fitnesspro.compose.components.filter.EnumSimpleFilterTestTags.SIMPLE_FILTER_SEARCH_BAR_INPUT_FIELD
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.DATE
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.TIME
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.navigation.compromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToCompromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreenRoute
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_ACTION_DELETE
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_DIALOG_LIST_ITEM
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_END_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_FAB_SAVE
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_LABELED_TEXT_DATA_CANCEL
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_LABELED_TEXT_HOUR
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_LABELED_TEXT_NAME
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_LABELED_TEXT_PROFESSIONAL
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_LABELED_TEXT_SITUATION
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_PROFESSIONAL_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumCompromiseScreenTestTags.COMPROMISE_SCREEN_START_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerDetailsTestTags.SCHEDULER_DETAILS_SCREEN_FAB_ADD
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerDetailsTestTags.SCHEDULER_DETAILS_SCREEN_ITEM_LIST
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertDisplayed
import br.com.fitnesspro.tests.ui.extensions.assertDisplayedWithText
import br.com.fitnesspro.tests.ui.extensions.assertNotDisplayed
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickFirst
import br.com.fitnesspro.tests.ui.extensions.onClickWithParent
import br.com.fitnesspro.tests.ui.extensions.onClickWithText
import br.com.fitnesspro.tests.ui.extensions.onFirst
import br.com.fitnesspro.tests.ui.extensions.onPosition
import br.com.fitnesspro.tests.ui.extensions.writeTextField
import br.com.fitnesspro.to.TOScheduler
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.DurationUnit.SECONDS
import kotlin.time.toDuration

@HiltAndroidTest
class MemberCompromiseSuggestionScreenActionsUITests: BaseAuthenticatedUITest() {

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
    fun should_change_compromise_suggestion_to_read_only_when_save_with_success() = runTest(timeout = TIMEOUT) {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            populateCompromiseSuggestionFieldsAndSave(
                start = "0700",
                end = "0800"
            )

            waitUntil(5000) {
                onNodeWithText(
                    activity.getString(R.string.compromise_screen_message_success_suggestion)
                ).isDisplayed()
            }

            assertCompromiseSuggestionReadOnly()
        }
    }

    @Test
    fun should_show_professional_dialog_list_when_click_on_search_button_of_professional_field_in_compromise_suggestion() = runTest(timeout = TIMEOUT) {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_PROFESSIONAL_FIELD)
            assertDisplayed(FITNESS_PRO_PAGED_LIST_DIALOG)
            assertWithText(
                text = activity.getString(R.string.compromise_screen_label_professional_list),
                testTag = FITNESS_PRO_PAGED_LIST_DIALOG_TITLE
            )
        }
    }

    @Test
    fun should_show_only_professionals_with_filtered_name_when_write_on_search_dialog_list_simple_filter_in_compromise_suggestion() = runTest(timeout = TIMEOUT) {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_PROFESSIONAL_FIELD)
            writeTextField(SIMPLE_FILTER_SEARCH_BAR_INPUT_FIELD, "Personal")
            onFirst(COMPROMISE_SCREEN_DIALOG_LIST_ITEM).assert(
                hasText(
                    activity.getString(
                        R.string.compromise_screen_label_professional_name_and_type,
                        toPersons[0].name!!,
                        toPersons[0].toUser?.type!!.getLabel(activity)
                    )
                )
            )
        }
    }

    @Test
    fun should_show_time_dialog_when_click_on_time_button_of_start_time_field_in_compromise_suggestion() = runTest(timeout = TIMEOUT) {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_START_HOUR_FIELD)
            assertDisplayed(TIME_PICKER_DIALOG)
        }
    }

    @Test
    fun should_show_time_dialog_when_click_on_time_button_of_end_time_field_in_compromise_suggestion() = runTest(timeout = TIMEOUT) {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_END_HOUR_FIELD)
            assertDisplayed(TIME_PICKER_DIALOG)
        }
    }

    @Test
    fun should_show_confirmation_dialog_message_when_click_on_inactivation_button() = runTest(timeout = TIMEOUT) {
        executeDefaultPrepareForTest()
        createFirstCompromiseSuggestionMember()
        setNavHostContent()

        composeTestRule.apply {
            onClickWithText("21")
            onClickFirst(SCHEDULER_DETAILS_SCREEN_ITEM_LIST)
            onClick(COMPROMISE_SCREEN_ACTION_DELETE)
            assertWithText(
                activity.getString(
                    R.string.compromise_screen_dialog_inactivation_message,
                    LocalDate.of(2025, 1, 21).format(DATE),
                    LocalTime.of(7, 0).format(TIME),
                    LocalTime.of(8, 0).format(TIME),
                ),
                FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
            )
        }
    }

    @Test
    fun show_success_message_when_cancel_is_successful() = runTest(timeout = TIMEOUT) {
        executeDefaultPrepareForTest()
        createFirstCompromiseSuggestionMember()
        setNavHostContent()

        composeTestRule.apply {
            onClickWithText("21")
            onClickFirst(SCHEDULER_DETAILS_SCREEN_ITEM_LIST)
            onClick(COMPROMISE_SCREEN_ACTION_DELETE)
            assertWithText(
                activity.getString(
                    R.string.compromise_screen_dialog_inactivation_message,
                    LocalDate.of(2025, 1, 21).format(DATE),
                    LocalTime.of(7, 0).format(TIME),
                    LocalTime.of(8, 0).format(TIME),
                ),
                FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
            )
            onClick(FITNESS_PRO_MESSAGE_DIALOG_CONFIRM_BUTTON)

            waitUntil {
                onNodeWithText(
                    activity.getString(R.string.compromise_screen_message_inactivated)
                ).isDisplayed()
            }

            assertDisplayedWithText(EnumSchedulerSituation.CANCELLED.getLabel(activity)!!)
            assertDisplayed(COMPROMISE_SCREEN_LABELED_TEXT_DATA_CANCEL)
        }
    }

    private suspend fun executeDefaultPrepareForTest() {
        prepareDatabaseWithPersons()
        authenticateMember()
    }

    private suspend fun createFirstCompromiseSuggestionMember() {
        val toScheduler = TOScheduler(
            academyMemberPersonId = toPersons[2].id!!,
            professionalPersonId = toPersons[0].id!!,
            professionalType = toPersons[0].toUser?.type!!,
            scheduledDate = dateNow().plusDays(1),
            start = LocalTime.of(7, 0),
            end = LocalTime.of(8, 0),
            situation = EnumSchedulerSituation.SCHEDULED,
            compromiseType = EnumCompromiseType.FIRST
        )

        val result = saveCompromiseUseCase.execute(toScheduler, EnumSchedulerType.SUGGESTION)

        if (result.isNotEmpty()) {
            Log.e(TAG, "Error creating first compromise suggestion: ${result.map { it.validationType }}")
        }
    }

    private fun AndroidComposeTestRule<*, *>.assertCompromiseSuggestionReadOnly() {
        assertDisplayed(COMPROMISE_SCREEN_LABELED_TEXT_NAME)
        assertDisplayed(COMPROMISE_SCREEN_LABELED_TEXT_PROFESSIONAL)
        assertDisplayed(COMPROMISE_SCREEN_LABELED_TEXT_HOUR)
        assertDisplayed(COMPROMISE_SCREEN_LABELED_TEXT_SITUATION)
        assertNotDisplayed(COMPROMISE_SCREEN_FAB_SAVE)
    }

    private fun AndroidComposeTestRule<*, *>.navigateToCompromise() {
        onClickWithText("21")
        onClick(SCHEDULER_DETAILS_SCREEN_FAB_ADD)
    }

    private fun AndroidComposeTestRule<*, *>.populateCompromiseSuggestionFieldsAndSave(
        start: String,
        end: String,
        professionalName: String? = null
    ) {
        onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_PROFESSIONAL_FIELD)

        if (professionalName.isNullOrEmpty()) {
            onClickFirst(COMPROMISE_SCREEN_DIALOG_LIST_ITEM)
        } else {
            writeTextField(SIMPLE_FILTER_SEARCH_BAR_INPUT_FIELD, professionalName)
            onPosition(1, COMPROMISE_SCREEN_DIALOG_LIST_ITEM).performClick()
        }

        writeTextField(COMPROMISE_SCREEN_START_HOUR_FIELD, start)
        writeTextField(COMPROMISE_SCREEN_END_HOUR_FIELD, end)

        onClick(COMPROMISE_SCREEN_FAB_SAVE)
    }

    companion object {
        const val TAG = "MemberCompromiseSuggestionScreenActionsUITests"
        val TIMEOUT = 30.toDuration(SECONDS)
    }
}
package br.com.fitnesspro.tests.ui.isolated.scheduler.compromise.professional

import android.util.Log
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
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
import br.com.fitnesspro.scheduler.ui.navigation.navigateToChatHistoryScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToCompromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreenRoute
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_ACTION_SCHEDULE_CONFIRM
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_DIALOG_LIST_ITEM
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_FAB_SAVE
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_MEMBER_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_START_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.details.enums.EnumSchedulerDetailsTags.SCHEDULER_DETAILS_SCREEN_FAB_ADD
import br.com.fitnesspro.scheduler.ui.screen.details.enums.EnumSchedulerDetailsTags.SCHEDULER_DETAILS_SCREEN_ITEM_LIST
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertDisplayed
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickFirst
import br.com.fitnesspro.tests.ui.extensions.onClickWithParent
import br.com.fitnesspro.tests.ui.extensions.onClickWithText
import br.com.fitnesspro.tests.ui.extensions.onFirst
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
class ProfessionalUniqueCompromiseScreenActionsUITests: BaseAuthenticatedUITest() {

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
    fun should_show_member_dialog_list_when_is_in_unique_compromise_and_click_on_search_button_of_member_field() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_MEMBER_FIELD)
                assertDisplayed(FITNESS_PRO_PAGED_LIST_DIALOG)
                assertWithText(
                    text = activity.getString(R.string.compromise_screen_label_member_list),
                    testTag = FITNESS_PRO_PAGED_LIST_DIALOG_TITLE
                )
            }
        }

    }

    @Test
    fun should_show_only_members_with_filtered_name_when_is_in_unique_compromise_and_write_on_search_dialog_list_simple_filter() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_MEMBER_FIELD)
                writeTextField(SIMPLE_FILTER_SEARCH_BAR_INPUT_FIELD, "Member")
                onFirst(COMPROMISE_SCREEN_DIALOG_LIST_ITEM).assert(hasText(toPersons[2].name!!))
            }
        }
    }

    @Test
    fun should_show_time_dialog_when_click_on_time_button_of_start_time_field_in_unique_compromise() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_START_HOUR_FIELD)
                assertDisplayed(TIME_PICKER_DIALOG)
            }
        }
    }

    @Test
    fun should_show_dialog_message_when_click_on_confirm_button_on_unique_compromise_and_situation_is_scheduled() {
        runTest {
            executeDefaultPrepareForTest()
            createFirstUniqueCompromiseScheduled()
            setNavHostContent()

            composeTestRule.apply {
                onClickWithText("21")
                onClickFirst(SCHEDULER_DETAILS_SCREEN_ITEM_LIST)
                onClick(COMPROMISE_SCREEN_ACTION_SCHEDULE_CONFIRM)
                assertWithText(
                    activity.getString(
                        R.string.compromise_screen_message_question_confirmation,
                        dateNow().plusDays(1).format(DATE),
                        LocalTime.of(13, 0).format(TIME),
                        LocalTime.of(14, 0).format(TIME)
                    ),
                    FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
                )
            }
        }
    }

    @Test
    fun should_show_dialog_message_when_click_on_confirm_button_on_unique_compromise_and_situation_is_confirmed() {
        runTest {
            executeDefaultPrepareForTest()
            createFirstUniqueCompromiseConfirmed()
            setNavHostContent()

            composeTestRule.apply {
                onClickWithText("21")
                onClickFirst(SCHEDULER_DETAILS_SCREEN_ITEM_LIST)
                onClick(COMPROMISE_SCREEN_ACTION_SCHEDULE_CONFIRM)
                assertWithText(
                    activity.getString(
                        R.string.compromise_screen_message_question_finalization,
                        dateNow().plusDays(1).format(DATE),
                        LocalTime.of(13, 0).format(TIME),
                        LocalTime.of(14, 0).format(TIME)
                    ),
                    FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
                )
            }
        }
    }

    @Test
    fun should_show_success_message_when_click_on_fab_save_and_all_fields_are_valid_in_unique_compromise() {
        runTest {
            executeDefaultPrepareForTest()
            createFirstUniqueCompromiseConfirmed()
            setNavHostContent()

            composeTestRule.apply {
                onClickWithText("21")
                onClickFirst(SCHEDULER_DETAILS_SCREEN_ITEM_LIST)
                onClick(COMPROMISE_SCREEN_FAB_SAVE)

                waitUntil(2000) {
                    onNodeWithText(
                        activity.getString(
                            R.string.compromise_screen_message_success_unique,
                            dateNow().plusDays(1).format(DATE)
                        )
                    ).isDisplayed()
                }
            }
        }
    }

    private suspend fun createFirstUniqueCompromiseScheduled() {
        val toScheduler = TOScheduler(
            academyMemberPersonId = toPersons[2].id!!,
            professionalPersonId = toPersons[0].id!!,
            professionalType = toPersons[0].toUser?.type!!,
            scheduledDate = dateNow().plusDays(1),
            start = LocalTime.of(13, 0),
            end = LocalTime.of(14, 0),
            situation = EnumSchedulerSituation.SCHEDULED,
            compromiseType = EnumCompromiseType.FIRST,
            observation = "Conversa sobre os treinos"
        )

        val result = saveCompromiseUseCase.execute(toScheduler, EnumSchedulerType.SUGGESTION)

        if (result.isNotEmpty()) {
            Log.e(TAG, "Error creating first compromise suggestion: ${result.map { it.validationType }}")
        }
    }

    private suspend fun createFirstUniqueCompromiseConfirmed() {
        val toScheduler = TOScheduler(
            academyMemberPersonId = toPersons[2].id!!,
            professionalPersonId = toPersons[0].id!!,
            professionalType = toPersons[0].toUser?.type!!,
            scheduledDate = dateNow().plusDays(1),
            start = LocalTime.of(13, 0),
            end = LocalTime.of(14, 0),
            situation = EnumSchedulerSituation.CONFIRMED,
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
        const val TAG = "ProfessionalUniqueCompromiseScreenActionsUITests"
    }

}
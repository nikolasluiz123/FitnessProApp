package br.com.fitnesspro.tests.ui.isolated.scheduler.compromise.member

import android.util.Log
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.compose.components.dialog.enums.EnumFitnessProMessageDialogTestTags.FITNESS_PRO_MESSAGE_DIALOG
import br.com.fitnesspro.compose.components.dialog.enums.EnumFitnessProMessageDialogTestTags.FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_TRAILING_ICON
import br.com.fitnesspro.compose.components.filter.EnumSimpleFilterTestTags.SIMPLE_FILTER_SEARCH_BAR_INPUT_FIELD
import br.com.fitnesspro.core.extensions.dateNow
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
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_DIALOG_LIST_ITEM
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_END_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_FAB_SAVE
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_PROFESSIONAL_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_START_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.details.enums.EnumSchedulerDetailsTags.SCHEDULER_DETAILS_SCREEN_FAB_ADD
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.HOUR_START
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.PROFESSIONAL
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertRequiredTextFieldValidation
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickFirst
import br.com.fitnesspro.tests.ui.extensions.onClickWithParent
import br.com.fitnesspro.tests.ui.extensions.onClickWithText
import br.com.fitnesspro.tests.ui.extensions.onPosition
import br.com.fitnesspro.tests.ui.extensions.writeTextField
import br.com.fitnesspro.to.TOScheduler
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@HiltAndroidTest
class MemberCompromiseSuggestionScreenFieldsValidationUITests: BaseAuthenticatedUITest() {

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
    fun should_show_error_when_professional_is_not_informed_in_compromise_suggestion() = runTest {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            assertRequiredTextFieldValidation(
                fieldTag = COMPROMISE_SCREEN_PROFESSIONAL_FIELD,
                buttonTag = COMPROMISE_SCREEN_FAB_SAVE,
                message = activity.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    activity.getString(PROFESSIONAL.labelResId)
                )
            )
        }
    }

    @Test
    fun should_show_error_when_start_time_is_not_informed_in_compromise_suggestion() = runTest {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            assertRequiredTextFieldValidation(
                fieldTag = COMPROMISE_SCREEN_START_HOUR_FIELD,
                buttonTag = COMPROMISE_SCREEN_FAB_SAVE,
                message = activity.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    activity.getString(HOUR_START.labelResId)
                )
            )
        }
    }

    @Test
    fun should_show_error_when_start_time_is_after_end_time_in_compromise_suggestion() = runTest(timeout = 30.toDuration(
        DurationUnit.SECONDS)) {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            writeTextField(COMPROMISE_SCREEN_START_HOUR_FIELD, "1500")
            writeTextField(COMPROMISE_SCREEN_END_HOUR_FIELD, "1400")
            onClick(COMPROMISE_SCREEN_FAB_SAVE)
            assertWithText(
                text = activity.getString(R.string.save_compromise_invalid_hour_period),
                testTag = FITNESS_PRO_MESSAGE_DIALOG_MESSAGE
            )
        }
    }

    @Test
    fun should_show_error_when_end_time_is_not_informed_in_compromise_suggestion() = runTest {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            assertRequiredTextFieldValidation(
                fieldTag = COMPROMISE_SCREEN_END_HOUR_FIELD,
                buttonTag = COMPROMISE_SCREEN_FAB_SAVE,
                message = activity.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    activity.getString(HOUR_START.labelResId)
                )
            )
        }
    }

    @Test
    fun should_error_when_has_conflict_in_compromise_suggestion() = runTest(timeout = 30.toDuration(DurationUnit.SECONDS)) {
        executeDefaultPrepareForTest()
        createFirstCompromiseSuggestionMember()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            populateCompromiseSuggestionFieldsAndSave(
                professionalName = "Personal",
                start = "1330",
                end = "1500"
            )

            waitUntil(3000) {
                onNodeWithTag(FITNESS_PRO_MESSAGE_DIALOG.name).isDisplayed()
            }
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
            start = LocalTime.of(13, 0),
            end = LocalTime.of(14, 0),
            situation = EnumSchedulerSituation.SCHEDULED,
            compromiseType = EnumCompromiseType.FIRST
        )

        val result = saveCompromiseUseCase.execute(toScheduler, EnumSchedulerType.SUGGESTION)

        if (result.isNotEmpty()) {
            Log.e(TAG, "Error creating first compromise suggestion: ${result.map { it.validationType }}")
        }
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
        const val TAG = "MemberCompromiseSuggestionScreenFieldsValidationUITests"
    }
}
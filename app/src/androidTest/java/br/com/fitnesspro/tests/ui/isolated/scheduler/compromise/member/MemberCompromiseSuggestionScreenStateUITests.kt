package br.com.fitnesspro.tests.ui.isolated.scheduler.compromise.member

import android.util.Log
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.compose.components.topbar.enums.EnumFitnessProTopAppBarTestTags.FITNESS_PRO_TOP_APP_BAR_SUBTITLE
import br.com.fitnesspro.compose.components.topbar.enums.EnumFitnessProTopAppBarTestTags.FITNESS_PRO_TOP_APP_BAR_TITLE
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
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_ACTION_DELETE
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_ACTION_MESSAGE
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_FAB_SAVE
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_HOUR
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_NAME
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_PROFESSIONAL
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_SITUATION
import br.com.fitnesspro.scheduler.ui.screen.details.enums.EnumSchedulerDetailsTags.SCHEDULER_DETAILS_SCREEN_FAB_ADD
import br.com.fitnesspro.scheduler.ui.screen.details.enums.EnumSchedulerDetailsTags.SCHEDULER_DETAILS_SCREEN_ITEM_LIST
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertDisplayed
import br.com.fitnesspro.tests.ui.extensions.assertEnabled
import br.com.fitnesspro.tests.ui.extensions.assertNotDisplayed
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickFirst
import br.com.fitnesspro.tests.ui.extensions.onClickWithText
import br.com.fitnesspro.to.TOScheduler
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@HiltAndroidTest
class MemberCompromiseSuggestionScreenStateUITests: BaseAuthenticatedUITest() {

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
    fun should_show_specific_title_when_navigate_to_compromise_suggestion() = runTest {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()

            assertWithText(
                text = activity.getString(R.string.compromise_screen_title_new_sugestion),
                testTag = FITNESS_PRO_TOP_APP_BAR_TITLE
            )
        }
    }

    @Test
    fun should_show_clicked_date_in_subtitle_when_navigate_to_compromise_suggestion() = runTest {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()

            assertWithText(
                text = LocalDate.of(2025, 1, 21).format(DATE),
                testTag = FITNESS_PRO_TOP_APP_BAR_SUBTITLE
            )
        }
    }

    @Test
    fun should_show_clicked_date_in_subtitle_and_time_range_when_navigate_to_compromise_suggestion_read_only() = runTest {
        executeDefaultPrepareForTest()
        createFirstCompromiseSuggestionMember()
        setNavHostContent()

        val date = LocalDate.of(2025, 1, 21)

        composeTestRule.apply {
            onClickWithText("21")
            onClickFirst(SCHEDULER_DETAILS_SCREEN_ITEM_LIST)
            assertWithText(
                text = activity.getString(
                    R.string.compromise_screen_subtitle,
                    date.format(DATE),
                    LocalTime.of(13, 0).format(TIME),
                    LocalTime.of(14, 0).format(TIME)
                ),
                testTag = FITNESS_PRO_TOP_APP_BAR_SUBTITLE
            )
            assertCompromiseSuggestionReadOnly()
        }
    }

    @Test
    fun should_disable_inactivation_button_when_is_compromise_creation() = runTest {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            assertEnabled(COMPROMISE_SCREEN_ACTION_DELETE, false)
        }
    }

    @Test
    fun should_disable_message_button_when_is_compromise_creation() = runTest {
        executeDefaultPrepareForTest()
        setNavHostContent()

        composeTestRule.apply {
            navigateToCompromise()
            assertEnabled(COMPROMISE_SCREEN_ACTION_MESSAGE, false)
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
            professionalType = toPersons[0].user?.type!!,
            scheduledDate = dateNow().plusDays(1),
            dateTimeStart = LocalTime.of(13, 0),
            dateTimeEnd = LocalTime.of(14, 0),
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

    companion object {
        const val TAG = "MemberCompromiseSuggestionScreenStateUITests"
    }
}
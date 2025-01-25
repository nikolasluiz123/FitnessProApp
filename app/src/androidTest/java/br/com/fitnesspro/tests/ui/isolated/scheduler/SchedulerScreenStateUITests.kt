package br.com.fitnesspro.tests.ui.isolated.scheduler

import android.util.Log
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.MONTH_YEAR
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.yearMonthNow
import br.com.fitnesspro.core.theme.RED_200
import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.scheduler.ui.navigation.navigateToCompromiseScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.navigation.navigateToSchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreen
import br.com.fitnesspro.scheduler.ui.navigation.schedulerScreenRoute
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTestTags.SCHEDULER_SCREEN_LABEL_YEAR_MONTH
import br.com.fitnesspro.scheduler.ui.screen.scheduler.enums.EnumSchedulerScreenTestTags.SCHEDULER_SCREEN_RECURRENT_SCHEDULE_FAB
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertColorWithText
import br.com.fitnesspro.tests.ui.extensions.assertNotDisplayed
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.to.TOScheduler
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

@HiltAndroidTest
class SchedulerScreenStateUITests: BaseAuthenticatedUITest() {

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<AndroidTestsActivity>()

    @Inject
    lateinit var schedulerConfigUseCase: SaveSchedulerConfigUseCase

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
    }

    @Test
    fun should_init_with_now_month_and_year_when_open_screen() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        setNavHostContent()

        composeTestRule.apply {
            assertWithText(yearMonthNow().format(MONTH_YEAR), SCHEDULER_SCREEN_LABEL_YEAR_MONTH)
        }
    }

    @Test
    fun should_apply_tint_on_days_when_have_schedules() = runTest {
        prepareDatabaseWithPersons()
        authenticateMember()
        createSchedulerConfigMember()
        createFirstCompromiseSuggestionMember()
        setNavHostContent()

        composeTestRule.apply {
            assertColorWithText("20", RED_200)
        }
    }

    @Test
    fun should_show_recurrent_fab_when_personal_is_authenticated() = runTest {
        prepareDatabaseWithPersons()
        authenticatePersonal()
        setNavHostContent()

        composeTestRule.apply {
            waitUntil {
                onNodeWithTag(SCHEDULER_SCREEN_RECURRENT_SCHEDULE_FAB.name).isDisplayed()
            }
        }
    }

    @Test
    fun should_hide_recurrent_fab_when_nutritionist_is_authenticated() = runTest {
        prepareDatabaseWithPersons()
        authenticateNutritionist()
        setNavHostContent()

        composeTestRule.apply {
            assertNotDisplayed(SCHEDULER_SCREEN_RECURRENT_SCHEDULE_FAB)
        }
    }

    private suspend fun createSchedulerConfigMember() {
        val personId = toPersons[2].id!!

        val result = schedulerConfigUseCase.saveConfig(personId)

        if (result.isNotEmpty()) {
            Log.e(TAG, "Error creating scheduler config: ${result.first().message}")
        }
    }

    private suspend fun createFirstCompromiseSuggestionMember() {
        val toScheduler = TOScheduler(
            academyMemberPersonId = toPersons[2].id!!,
            professionalPersonId = toPersons[0].id!!,
            professionalType = toPersons[0].toUser?.type!!,
            scheduledDate = dateNow(),
            start = LocalTime.of(13, 0),
            end = LocalTime.of(14, 0),
            situation = EnumSchedulerSituation.SCHEDULED,
            compromiseType = EnumCompromiseType.FIRST
        )

        Log.i("Teste", "createFirstCompromiseSuggestionMember")
        val result = saveCompromiseUseCase.execute(toScheduler, EnumSchedulerType.SUGGESTION)

        if (result.isNotEmpty()) {
            Log.e(TAG, "Error creating first compromise suggestion: ${result.map { it.validationType }}")
        }
    }

    companion object {
        private const val TAG = "SchedulerScreenStateUITests"
    }

}
package br.com.fitnesspro.tests.ui.isolated.scheduler.compromise.professional

import android.util.Log
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.compose.components.dialog.enums.EnumTimePickerInputTestTags.TIME_PICKER_DIALOG
import br.com.fitnesspro.compose.components.fields.enums.EnumOutlinedTextFieldTestTags.OUTLINED_TEXT_FIELD_TRAILING_ICON
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
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_ACTION_SCHEDULE_CONFIRM
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_END_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_MEMBER_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_OBSERVATION_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_START_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.details.enums.EnumSchedulerDetailsTags.SCHEDULER_DETAILS_SCREEN_FAB_ADD
import br.com.fitnesspro.scheduler.ui.screen.details.enums.EnumSchedulerDetailsTags.SCHEDULER_DETAILS_SCREEN_ITEM_LIST
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.tests.ui.common.BaseAuthenticatedUITest
import br.com.fitnesspro.tests.ui.extensions.assertDisplayed
import br.com.fitnesspro.tests.ui.extensions.assertEnabled
import br.com.fitnesspro.tests.ui.extensions.assertWithText
import br.com.fitnesspro.tests.ui.extensions.onClick
import br.com.fitnesspro.tests.ui.extensions.onClickFirst
import br.com.fitnesspro.tests.ui.extensions.onClickWithParent
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
class ProfessionalUniqueCompromiseScreenStateUITests : BaseAuthenticatedUITest() {

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
            onNavigateToChatHistory = navController::navigateToChatHistoryScreen,
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
    fun should_show_new_compromise_title_when_fab_add_click() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                assertWithText(
                    activity.getString(R.string.compromise_screen_title_new_compromise),
                    FITNESS_PRO_TOP_APP_BAR_TITLE
                )
            }
        }
    }

    @Test
    fun should_show_compromise_title_when_details_item_click() {
        runTest {
            executeDefaultPrepareForTest()
            createFirstUniqueCompromiseScheduled()
            setNavHostContent()

            composeTestRule.apply {
                onClickWithText("21")

                waitUntil(4000) {
                    onNodeWithText(activity.getString(R.string.scheduler_details_screen_title))
                        .isDisplayed()
                }

                onClickFirst(SCHEDULER_DETAILS_SCREEN_ITEM_LIST)

                waitUntil(4000) {
                    onNodeWithText(
                        activity.getString(
                            R.string.compromise_screen_title_compromise_with_situation,
                            EnumSchedulerSituation.SCHEDULED.getLabel(activity)!!
                        )
                    ).isDisplayed()
                }
            }
        }
    }

    @Test
    fun should_populate_fields_when_details_item_click() {
        runTest {
            executeDefaultPrepareForTest()
            createFirstUniqueCompromiseScheduled()
            setNavHostContent()

            composeTestRule.apply {
                onClickWithText("21")
                onClickFirst(SCHEDULER_DETAILS_SCREEN_ITEM_LIST)

                assertWithText(toPersons[2].name!!, COMPROMISE_SCREEN_MEMBER_FIELD)
                assertWithText("13:00", COMPROMISE_SCREEN_START_HOUR_FIELD)
                assertWithText("14:00", COMPROMISE_SCREEN_END_HOUR_FIELD)
                assertWithText("Conversa sobre os treinos", COMPROMISE_SCREEN_OBSERVATION_FIELD)
            }
        }
    }

    @Test
    fun should_disable_confirmation_button_when_is_unique_compromise_creation() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                assertEnabled(COMPROMISE_SCREEN_ACTION_SCHEDULE_CONFIRM, false)
            }
        }
    }

    @Test
    fun should_show_clicked_date_in_subtitle_when_navigate_to_unique_compromise_for_creation() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                assertWithText(
                    LocalDate.of(2025, 1, 21).format(DATE),
                    FITNESS_PRO_TOP_APP_BAR_SUBTITLE
                )
            }
        }
    }

    @Test
    fun should_show_clicked_date_in_subtitle_and_time_range_when_navigate_to_unique_compromise_for_edition() {
        runTest {
            executeDefaultPrepareForTest()
            createFirstUniqueCompromiseScheduled()
            setNavHostContent()

            val date = LocalDate.of(2025, 1, 21)
            val start = LocalTime.of(13, 0)
            val end = LocalTime.of(14, 0)

            composeTestRule.apply {
                onClickWithText("21")
                onClickFirst(SCHEDULER_DETAILS_SCREEN_ITEM_LIST)
                assertWithText(
                    activity.getString(
                        R.string.compromise_screen_subtitle,
                        date.format(DATE),
                        start.format(TIME),
                        end.format(TIME)
                    ),
                    FITNESS_PRO_TOP_APP_BAR_SUBTITLE
                )
            }
        }
    }

    @Test
    fun should_show_time_dialog_when_click_on_time_button_of_end_time_field_in_unique_compromise() {
        runTest {
            executeDefaultPrepareForTest()
            setNavHostContent()

            composeTestRule.apply {
                navigateToCompromise()
                onClickWithParent(OUTLINED_TEXT_FIELD_TRAILING_ICON, COMPROMISE_SCREEN_END_HOUR_FIELD)
                assertDisplayed(TIME_PICKER_DIALOG)
            }
        }
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
        const val TAG = "ProfessionalUniqueCompromiseScreenStateUITests"
    }
}
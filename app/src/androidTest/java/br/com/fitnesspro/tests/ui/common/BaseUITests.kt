package br.com.fitnesspro.tests.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.rules.ActivityScenarioRule
import br.com.fitnesspro.AndroidTestsActivity
import br.com.fitnesspro.App
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.timeNow
import br.com.fitnesspro.core.extensions.yearMonthNow
import br.com.fitnesspro.core.theme.FitnessProTheme
import dagger.hilt.android.testing.HiltAndroidRule
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.BeforeClass
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

abstract class BaseUITests {

    abstract fun getHiltAndroidRule(): HiltAndroidRule

    abstract fun getAndroidComposeRule(): AndroidComposeTestRule<ActivityScenarioRule<AndroidTestsActivity>, AndroidTestsActivity>

    abstract fun getTag(): String

    abstract fun getStartingDestination(): String

    abstract fun NavGraphBuilder.testNavGraph(navController: NavHostController)

    @Before
    fun setup() {
        getHiltAndroidRule().inject()
    }

    protected fun setNavHostContent() {
        getAndroidComposeRule().setContent {
            DefaultTestNavHost()
        }
    }

    @Composable
    private fun DefaultTestNavHost() {
        FitnessProTheme {
            App {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = getStartingDestination(),
                ) {
                    testNavGraph(navController)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun setupClass() {
            mockkStatic(LocalTime::class, YearMonth::class, LocalDate::class)

            mockLocalTimeNow()
            mockYearMonthNow()
            mockLocalDateNow()
        }

        @JvmStatic
        protected fun mockLocalTimeNow() {
            every { timeNow() } returns LocalTime.of(10, 0)
        }

        @JvmStatic
        protected fun mockYearMonthNow() {
            every { yearMonthNow() } returns YearMonth.of(2025, 1)
        }

        @JvmStatic
        protected fun mockLocalDateNow() {
            every { dateNow() } returns LocalDate.of(2025, 1, 20)
        }
    }
}
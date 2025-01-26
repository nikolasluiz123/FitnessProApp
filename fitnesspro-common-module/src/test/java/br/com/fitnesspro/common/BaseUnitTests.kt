package br.com.fitnesspro.common

import android.content.Context
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.extensions.timeNow
import br.com.fitnesspro.core.extensions.yearMonthNow
import com.github.javafaker.Faker
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.util.Locale

abstract class BaseUnitTests {

    protected lateinit var context: Context
    protected lateinit var faker: Faker

    @BeforeEach
    open fun setUp() {
        context = mockk(relaxed = true)
        faker = Faker(Locale.getDefault())
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setupClassTest() {
            Locale.setDefault(Locale("pt", "BR"))

            mockkStatic(LocalTime::class, LocalDate::class, LocalDateTime::class, YearMonth::class)

            mockLocalTimeNow()
            mockLocalDateNow()
            mockLocalDateTimeNow()
            mockYearMonthNow()
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

        @JvmStatic
        protected fun mockLocalDateTimeNow() {
            every { dateTimeNow() } returns LocalDateTime.of(2025, 1, 20, 10, 0)
        }

    }
}
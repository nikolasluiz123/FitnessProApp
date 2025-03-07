package br.com.fitnesspro.common

import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.usecase.academy.EnumAcademyValidationTypes
import br.com.fitnesspro.common.usecase.academy.SavePersonAcademyTimeUseCase
import br.com.fitnesspro.core.validation.getValidations
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.to.TOAcademy
import br.com.fitnesspro.to.TOPersonAcademyTime
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainOnly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalTime

class SavePersonAcademyTimeUseCaseTests: BaseUnitTests() {

    private lateinit var academyRepository: AcademyRepository
    private lateinit var savePersonAcademyTimeUseCase: SavePersonAcademyTimeUseCase

    @BeforeEach
    override fun setUp() {
        super.setUp()

        academyRepository = mockk(relaxed = true)
        savePersonAcademyTimeUseCase = SavePersonAcademyTimeUseCase(context, academyRepository)
    }

    @Test
    fun should_fail_when_academy_is_null(): Unit = runTest {
        val toPersonAcademyTime = TOPersonAcademyTime(
            toAcademy = null,
            timeStart = LocalTime.of(12, 0),
            timeEnd = LocalTime.of(14, 0),
            dayOfWeek = DayOfWeek.FRIDAY
        )

        coEvery { academyRepository.getConflictPersonAcademyTime(any()) } returns null

        val validationResults = savePersonAcademyTimeUseCase.execute(toPersonAcademyTime).getValidations()

        validationResults.shouldContainOnly(EnumAcademyValidationTypes.REQUIRED_ACADEMY)
    }

    @Test
    fun should_fail_when_timeStart_is_null(): Unit = runTest {

        val toPersonAcademyTime = TOPersonAcademyTime(
            toAcademy = getFakeAcademy(),
            timeStart = null,
            timeEnd = LocalTime.of(12, 0),
            dayOfWeek = DayOfWeek.FRIDAY
        )

        coEvery { academyRepository.getConflictPersonAcademyTime(any()) } returns null

        val validationResults = savePersonAcademyTimeUseCase.execute(toPersonAcademyTime).getValidations()

        validationResults.shouldContainOnly(EnumAcademyValidationTypes.REQUIRED_TIME_START)
    }

    @Test
    fun should_fail_when_timeEnd_is_null(): Unit = runTest {

        val toPersonAcademyTime = TOPersonAcademyTime(
            toAcademy = getFakeAcademy(),
            timeStart = LocalTime.of(12, 0),
            timeEnd = null,
            dayOfWeek = DayOfWeek.FRIDAY
        )

        coEvery { academyRepository.getConflictPersonAcademyTime(any()) } returns null

        val validationResults = savePersonAcademyTimeUseCase.execute(toPersonAcademyTime).getValidations()

        validationResults.shouldContainOnly(EnumAcademyValidationTypes.REQUIRED_TIME_END)
    }

    @Test
    fun should_fail_when_timeStart_is_after_timeEnd(): Unit = runTest {

        val toPersonAcademyTime = TOPersonAcademyTime(
            toAcademy = getFakeAcademy(),
            timeStart = LocalTime.of(12, 0),
            timeEnd = LocalTime.of(10,0),
            dayOfWeek = DayOfWeek.FRIDAY
        )

        coEvery { academyRepository.getConflictPersonAcademyTime(any()) } returns null

        val validationResults = savePersonAcademyTimeUseCase.execute(toPersonAcademyTime).getValidations()

        validationResults.shouldContainOnly(EnumAcademyValidationTypes.INVALID_TIME_PERIOD)
    }

    @Test
    fun should_fail_when_dayOfWeek_is_null(): Unit = runTest {

        val toPersonAcademyTime = TOPersonAcademyTime(
            toAcademy = getFakeAcademy(),
            timeStart = LocalTime.of(12,0),
            timeEnd = LocalTime.of(13,0),
            dayOfWeek = null
        )

        coEvery { academyRepository.getConflictPersonAcademyTime(any()) } returns null

        val validationResults = savePersonAcademyTimeUseCase.execute(toPersonAcademyTime).getValidations()

        validationResults.shouldContainOnly(EnumAcademyValidationTypes.REQUIRED_DAY_OF_WEEK)
    }

    @Test
    fun should_fail_when_there_is_a_conflict(): Unit = runTest {
        val toPersonAcademyTime = TOPersonAcademyTime(
            toAcademy = getFakeAcademy(),
            timeStart = LocalTime.of(12,0),
            timeEnd = LocalTime.of(13,0),
            dayOfWeek = DayOfWeek.FRIDAY
        )

        coEvery { academyRepository.getConflictPersonAcademyTime(any()) } returns PersonAcademyTime()

        val validationResults = savePersonAcademyTimeUseCase.execute(toPersonAcademyTime).getValidations()

        validationResults.shouldContainOnly(EnumAcademyValidationTypes.CONFLICT_TIME_PERIOD)
    }

    @Test
    fun should_pass_when_all_fields_are_valid(): Unit = runTest {
        val toPersonAcademyTime = TOPersonAcademyTime(
            toAcademy = getFakeAcademy(),
            timeStart = LocalTime.of(12,0),
            timeEnd = LocalTime.of(13,0),
            dayOfWeek = DayOfWeek.FRIDAY
        )

        coEvery { academyRepository.getConflictPersonAcademyTime(any()) } returns null

        val validationResults = savePersonAcademyTimeUseCase.execute(toPersonAcademyTime).getValidations()

        validationResults.shouldBeEmpty()
    }

    private fun getFakeAcademy() = TOAcademy(id = "1", name = "Academy")
}
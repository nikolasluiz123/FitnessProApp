package br.com.fitnesspro

import android.content.Context
import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.usecase.academy.EnumValidatedAcademyFields
import br.com.fitnesspro.common.usecase.academy.SavePersonAcademyTimeUseCase
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.to.TOAcademy
import br.com.fitnesspro.to.TOPersonAcademyTime
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainOnly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalTime

class SavePersonAcademyTimeUseCaseTests {

    private lateinit var context: Context
    private lateinit var academyRepository: AcademyRepository
    private lateinit var savePersonAcademyTimeUseCase: SavePersonAcademyTimeUseCase

    @BeforeEach
    fun setUp() {
        context = mockk(relaxed = true)
        academyRepository = mockk(relaxed = true)
        savePersonAcademyTimeUseCase = SavePersonAcademyTimeUseCase(context, academyRepository)
    }

    @Test
    fun should_fail_when_academy_is_null(): Unit = runBlocking {
        val toPersonAcademyTime = TOPersonAcademyTime(
            toAcademy = null,
            timeStart = LocalTime.now(),
            timeEnd = LocalTime.now().plusHours(1),
            dayOfWeek = DayOfWeek.FRIDAY
        )

        coEvery { academyRepository.getConflictPersonAcademyTime(any()) } returns null

        val validationResults = savePersonAcademyTimeUseCase.execute(toPersonAcademyTime).map { it.first }

        validationResults.shouldContainOnly(EnumValidatedAcademyFields.ACADEMY)
    }

    @Test
    fun should_fail_when_timeStart_is_null(): Unit = runBlocking {

        val toPersonAcademyTime = TOPersonAcademyTime(
            toAcademy = getFakeAcademy(),
            timeStart = null,
            timeEnd = LocalTime.now().plusHours(1),
            dayOfWeek = DayOfWeek.FRIDAY
        )

        coEvery { academyRepository.getConflictPersonAcademyTime(any()) } returns null

        val validationResults = savePersonAcademyTimeUseCase.execute(toPersonAcademyTime).map { it.first }

        validationResults.shouldContainOnly(EnumValidatedAcademyFields.DATE_TIME_START)
    }

    @Test
    fun should_fail_when_timeEnd_is_null(): Unit = runBlocking {

        val toPersonAcademyTime = TOPersonAcademyTime(
            toAcademy = getFakeAcademy(),
            timeStart = LocalTime.now(),
            timeEnd = null,
            dayOfWeek = DayOfWeek.FRIDAY
        )

        coEvery { academyRepository.getConflictPersonAcademyTime(any()) } returns null

        val validationResults = savePersonAcademyTimeUseCase.execute(toPersonAcademyTime).map { it.first }

        validationResults.shouldContainOnly(EnumValidatedAcademyFields.DATE_TIME_END)
    }

    @Test
    fun should_fail_when_timeStart_is_after_timeEnd(): Unit = runBlocking {

        val toPersonAcademyTime = TOPersonAcademyTime(
            toAcademy = getFakeAcademy(),
            timeStart = LocalTime.now().plusHours(2),
            timeEnd = LocalTime.now(),
            dayOfWeek = DayOfWeek.FRIDAY
        )

        coEvery { academyRepository.getConflictPersonAcademyTime(any()) } returns null

        val validationResults = savePersonAcademyTimeUseCase.execute(toPersonAcademyTime).map { it.first }

        validationResults.shouldContainOnly(null)
    }

    @Test
    fun should_fail_when_dayOfWeek_is_null(): Unit = runBlocking {

        val toPersonAcademyTime = TOPersonAcademyTime(
            toAcademy = getFakeAcademy(),
            timeStart = LocalTime.now(),
            timeEnd = LocalTime.now().plusHours(1),
            dayOfWeek = null
        )

        coEvery { academyRepository.getConflictPersonAcademyTime(any()) } returns null

        val validationResults = savePersonAcademyTimeUseCase.execute(toPersonAcademyTime).map { it.first }

        validationResults.shouldContainOnly(EnumValidatedAcademyFields.DAY_OF_WEEK)
    }

    @Test
    fun should_fail_when_there_is_a_conflict(): Unit = runBlocking {
        val toPersonAcademyTime = TOPersonAcademyTime(
            toAcademy = getFakeAcademy(),
            timeStart = LocalTime.now(),
            timeEnd = LocalTime.now().plusHours(1),
            dayOfWeek = DayOfWeek.FRIDAY
        )

        coEvery { academyRepository.getConflictPersonAcademyTime(any()) } returns PersonAcademyTime()

        val validationResults = savePersonAcademyTimeUseCase.execute(toPersonAcademyTime).map { it.first }

        validationResults.shouldContainOnly(null)
    }

    @Test
    fun should_pass_when_all_fields_are_valid(): Unit = runBlocking {
        val toPersonAcademyTime = TOPersonAcademyTime(
            toAcademy = getFakeAcademy(),
            timeStart = LocalTime.now(),
            timeEnd = LocalTime.now().plusHours(1),
            dayOfWeek = DayOfWeek.FRIDAY
        )

        coEvery { academyRepository.getConflictPersonAcademyTime(any()) } returns null

        val validationResults = savePersonAcademyTimeUseCase.execute(toPersonAcademyTime).map { it.first }

        validationResults.shouldBeEmpty()
    }

    private fun getFakeAcademy() = TOAcademy(id = "1", name = "Academy")
}
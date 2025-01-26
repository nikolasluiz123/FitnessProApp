package br.com.fitnesspro.scheduler

import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.timeNow
import br.com.fitnesspro.core.validation.getValidations
import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseSuggestionUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.END_HOUR_OUT_OF_WORK_TIME_RANGE
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.MAX_LENGTH_OBSERVATION
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.REQUIRED_HOUR_END
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.REQUIRED_HOUR_START
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.START_HOUR_IN_PAST_TODAY
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.START_HOUR_OUT_OF_WORK_TIME_RANGE
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes.START_HOUR_WITHOUT_ONE_HOUR_ANTECEDENCE_TODAY
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.to.TOUser
import io.kotest.matchers.collections.shouldContainOnly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class SaveCompromiseSuggestionTests: BaseUnitTests() {

    private lateinit var schedulerRepository: SchedulerRepository
    private lateinit var userRepository: UserRepository
    private lateinit var academyRepository: AcademyRepository
    private lateinit var saveCompromiseSuggestionUseCase: SaveCompromiseSuggestionUseCase

    @BeforeEach
    override fun setUp() {
        super.setUp()

        schedulerRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        academyRepository = mockk(relaxed = true)
        saveCompromiseSuggestionUseCase = SaveCompromiseSuggestionUseCase(
            context,
            schedulerRepository,
            userRepository,
            academyRepository
        )
    }

    @Test
    fun should_fail_when_hour_start_is_null(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()
        mockAcademyTimeList()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = LocalDate.of(2025, 1, 15),
            start = null,
            end = LocalTime.of(8, 0),
            compromiseType = EnumCompromiseType.FIRST
        )

        val result = saveCompromiseSuggestionUseCase.saveCompromiseSuggestion(scheduler).getValidations()

        result.shouldContainOnly(REQUIRED_HOUR_START)
    }

    @Test
    fun should_fail_when_hour_start_is_in_the_past_and_today(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()
        mockAcademyTimeList()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = dateNow(),
            start = timeNow().minusHours(1),
            end = timeNow().plusHours(2),
            compromiseType = EnumCompromiseType.FIRST
        )

        val result = saveCompromiseSuggestionUseCase.saveCompromiseSuggestion(scheduler).getValidations()

        result.shouldContainOnly(START_HOUR_IN_PAST_TODAY)
    }

    @Test
    fun should_fail_when_hour_start_without_one_hour_antecedence(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()
        mockAcademyTimeList()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = dateNow(),
            start = timeNow(),
            end = timeNow().plusHours(1),
            compromiseType = EnumCompromiseType.FIRST
        )

        val result = saveCompromiseSuggestionUseCase.saveCompromiseSuggestion(scheduler).getValidations()

        result.shouldContainOnly(START_HOUR_WITHOUT_ONE_HOUR_ANTECEDENCE_TODAY)
    }

    @Test
    fun should_fail_when_hour_start_is_out_of_work_time_range(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()
        mockAcademyTimeList()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = LocalDate.of(2025, 1, 16),
            start = LocalTime.of(7, 30),
            end = LocalTime.of(8, 30),
            compromiseType = EnumCompromiseType.FIRST
        )

        val result = saveCompromiseSuggestionUseCase.saveCompromiseSuggestion(scheduler).getValidations()

        result.shouldContainOnly(START_HOUR_OUT_OF_WORK_TIME_RANGE)
    }

    @Test
    fun should_fail_when_hour_end_is_null(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()
        mockAcademyTimeList()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = LocalDate.of(2025, 1, 15),
            start = LocalTime.of(13, 0),
            end = null,
            compromiseType = EnumCompromiseType.FIRST
        )

        val result = saveCompromiseSuggestionUseCase.saveCompromiseSuggestion(scheduler).getValidations()

        result.shouldContainOnly(REQUIRED_HOUR_END)
    }

    @Test
    fun should_fail_when_hour_end_is_out_of_work_time_range(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()
        mockAcademyTimeList()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = LocalDate.of(2025, 1, 16),
            start = LocalTime.of(17, 0),
            end = LocalTime.of(18, 30),
            compromiseType = EnumCompromiseType.FIRST
        )

        val result = saveCompromiseSuggestionUseCase.saveCompromiseSuggestion(scheduler).getValidations()

        result.shouldContainOnly(END_HOUR_OUT_OF_WORK_TIME_RANGE)
    }

    @Test
    fun should_fail_when_hour_start_and_end_is_out_of_work_time_range(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()
        mockAcademyTimeList()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = LocalDate.of(2025, 1, 16),
            start = LocalTime.of(20, 0),
            end = LocalTime.of(21, 0),
            compromiseType = EnumCompromiseType.FIRST
        )

        val result = saveCompromiseSuggestionUseCase.saveCompromiseSuggestion(scheduler).getValidations()

        result.shouldContainOnly(START_HOUR_OUT_OF_WORK_TIME_RANGE, END_HOUR_OUT_OF_WORK_TIME_RANGE)
    }

    @Test
    fun should_fail_when_observation_is_greater_than_4096_characters(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()
        mockAcademyTimeList()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = dateNow(),
            start = LocalTime.of(13, 30),
            end = LocalTime.of(14, 0),
            compromiseType = EnumCompromiseType.FIRST,
            observation = "a".repeat(4097)
        )

        val result = saveCompromiseSuggestionUseCase.saveCompromiseSuggestion(scheduler).getValidations()

        result.shouldContainOnly(MAX_LENGTH_OBSERVATION)
    }

    private fun prepareMockGetTOPersonByIdPersonalTrainer() {
        coEvery { userRepository.getTOPersonById(any()) } returns TOPerson(
            toUser = TOUser(type = EnumUserType.PERSONAL_TRAINER)
        )
    }

    private fun prepareMockHasConflictFalse() {
        coEvery {
            schedulerRepository.getHasSchedulerConflict(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns false
    }

    private fun mockAcademyTimeList() {
        coEvery {
            academyRepository.getAcademyTimes(any(), any(), any())
        } returns listOf(
            PersonAcademyTime(
                personId = UUID.randomUUID().toString(),
                academyId = UUID.randomUUID().toString(),
                timeStart = LocalTime.of(8, 0),
                timeEnd = LocalTime.of(17, 0),
                dayOfWeek = LocalDate.of(2025, 1, 15).dayOfWeek
            )
        )
    }
}
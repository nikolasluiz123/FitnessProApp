package br.com.fitnesspro.scheduler

import android.content.Context
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.timeNow
import br.com.fitnesspro.core.validation.getValidations
import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumUserType
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
import br.com.fitnesspro.to.TOSchedulerConfig
import br.com.fitnesspro.to.TOUser
import io.kotest.matchers.collections.shouldContainOnly
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class SaveCompromiseSuggestionTests {

    private lateinit var context: Context
    private lateinit var schedulerRepository: SchedulerRepository
    private lateinit var schedulerConfigRepository: SchedulerConfigRepository
    private lateinit var userRepository: UserRepository
    private lateinit var saveCompromiseSuggestionUseCase: SaveCompromiseSuggestionUseCase

    @BeforeEach
    fun setUp() {
        context = mockk(relaxed = true)
        schedulerRepository = mockk(relaxed = true)
        schedulerConfigRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        saveCompromiseSuggestionUseCase = SaveCompromiseSuggestionUseCase(
            context,
            schedulerRepository,
            userRepository,
            schedulerConfigRepository
        )

        mockkStatic(::dateNow, ::timeNow)

        every { dateNow() } returns LocalDate.of(2025, 1, 15)
        every { timeNow() } returns LocalTime.of(10, 0)

        coEvery {
            schedulerConfigRepository.getTOSchedulerConfigByPersonId(any())
        } returns TOSchedulerConfig()
    }

    @Test
    fun should_fail_when_hour_start_is_null(): Unit = runBlocking {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()

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
    fun should_fail_when_hour_start_is_in_the_past_and_today(): Unit = runBlocking {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()

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
    fun should_fail_when_hour_start_without_one_hour_antecedence(): Unit = runBlocking {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()

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
    fun should_fail_when_hour_start_is_out_of_work_time_range(): Unit = runBlocking {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()

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
    fun should_fail_when_hour_end_is_null(): Unit = runBlocking {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()

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
    fun should_fail_when_hour_end_is_out_of_work_time_range(): Unit = runBlocking {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()

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
    fun should_fail_when_hour_start_and_end_is_out_of_work_time_range(): Unit = runBlocking {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()

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
    fun should_fail_when_observation_is_greater_than_4096_characters(): Unit = runBlocking {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdPersonalTrainer()

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

    @Test
    fun should_fail_when_has_conflict(): Unit = runBlocking {
        prepareMockHasConflictTrue()
        prepareMockGetTOPersonByIdPersonalTrainer()

        // TODO: Implementar testes com conflito
    }

    @Test
    fun should_cancel_compromise_when_situation_is_valid_and_is_edition() {
        // TODO - Implementar o teste do novo caso de uso
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

    private fun prepareMockHasConflictTrue() {
        coEvery {
            schedulerRepository.getHasSchedulerConflict(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns true
    }
}
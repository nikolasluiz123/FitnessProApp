package br.com.fitnesspro.scheduler

import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.timeNow
import br.com.fitnesspro.core.validation.getValidations
import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.usecase.scheduler.CompromiseRecurrentConfig
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveRecurrentCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.to.TOUser
import io.kotest.matchers.collections.shouldContainOnly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class SaveRecurrentCompromiseTests: BaseUnitTests() {

    private lateinit var schedulerRepository: SchedulerRepository
    private lateinit var userRepository: UserRepository
    private lateinit var saveRecurrentCompromiseUseCase: SaveRecurrentCompromiseUseCase

    @BeforeEach
    override fun setUp() {
        super.setUp()

        context = mockk(relaxed = true)
        schedulerRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        saveRecurrentCompromiseUseCase = SaveRecurrentCompromiseUseCase(
            context,
            schedulerRepository,
            userRepository,
        )
    }

    @Test
    fun should_fail_when_member_is_null(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdAcademyMember()

        val scheduler = TOScheduler(
            academyMemberPersonId = null,
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = dateNow(),
            start = LocalTime.of(13, 0),
            end = LocalTime.of(14, 0),
            compromiseType = EnumCompromiseType.FIRST
        )

        val config = CompromiseRecurrentConfig(
            dateStart = LocalDate.of(2025, 1, 20),
            dateEnd = LocalDate.of(2025, 2, 20),
            dayWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY)
        )

        val result = saveRecurrentCompromiseUseCase.saveRecurrentCompromise(scheduler, config).getValidations()

        result.shouldContainOnly(EnumCompromiseValidationTypes.REQUIRED_MEMBER)
    }

    @Test
    fun should_fail_when_hour_start_is_null(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdAcademyMember()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = LocalDate.of(2025, 1, 15),
            start = null,
            end = LocalTime.of(8, 0),
            compromiseType = EnumCompromiseType.FIRST
        )

        val config = CompromiseRecurrentConfig(
            dateStart = LocalDate.of(2025, 1, 20),
            dateEnd = LocalDate.of(2025, 2, 20),
            dayWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY)
        )

        val result = saveRecurrentCompromiseUseCase.saveRecurrentCompromise(scheduler, config).getValidations()

        result.shouldContainOnly(EnumCompromiseValidationTypes.REQUIRED_HOUR_START)
    }

    @Test
    fun should_fail_when_hour_start_is_in_the_past_and_today(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdAcademyMember()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = dateNow(),
            start = timeNow().minusHours(1),
            end = timeNow().plusHours(2),
            compromiseType = EnumCompromiseType.FIRST
        )

        val config = CompromiseRecurrentConfig(
            dateStart = LocalDate.of(2025, 1, 20),
            dateEnd = LocalDate.of(2025, 2, 20),
            dayWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY)
        )

        val result = saveRecurrentCompromiseUseCase.saveRecurrentCompromise(scheduler, config).getValidations()

        result.shouldContainOnly(EnumCompromiseValidationTypes.START_HOUR_IN_PAST_TODAY)
    }

    @Test
    fun should_fail_when_hour_start_without_one_hour_antecedence(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdAcademyMember()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = dateNow(),
            start = timeNow(),
            end = timeNow().plusHours(1),
            compromiseType = EnumCompromiseType.FIRST
        )

        val config = CompromiseRecurrentConfig(
            dateStart = LocalDate.of(2025, 1, 20),
            dateEnd = LocalDate.of(2025, 2, 20),
            dayWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY)
        )

        val result = saveRecurrentCompromiseUseCase.saveRecurrentCompromise(scheduler, config).getValidations()

        result.shouldContainOnly(EnumCompromiseValidationTypes.START_HOUR_WITHOUT_ONE_HOUR_ANTECEDENCE_TODAY)
    }

    @Test
    fun should_fail_when_hour_end_is_null(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdAcademyMember()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = LocalDate.of(2025, 1, 15),
            start = LocalTime.of(13, 0),
            end = null,
            compromiseType = EnumCompromiseType.FIRST
        )

        val config = CompromiseRecurrentConfig(
            dateStart = LocalDate.of(2025, 1, 20),
            dateEnd = LocalDate.of(2025, 2, 20),
            dayWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY)
        )

        val result = saveRecurrentCompromiseUseCase.saveRecurrentCompromise(scheduler, config).getValidations()

        result.shouldContainOnly(EnumCompromiseValidationTypes.REQUIRED_HOUR_END)
    }

    @Test
    fun should_fail_when_observation_is_greater_than_4096_characters(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdAcademyMember()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = dateNow(),
            start = LocalTime.of(13, 0),
            end = LocalTime.of(14, 0),
            compromiseType = EnumCompromiseType.FIRST,
            observation = "a".repeat(4097)
        )

        val config = CompromiseRecurrentConfig(
            dateStart = LocalDate.of(2025, 1, 20),
            dateEnd = LocalDate.of(2025, 2, 20),
            dayWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY)
        )

        val result = saveRecurrentCompromiseUseCase.saveRecurrentCompromise(scheduler, config).getValidations()

        result.shouldContainOnly(EnumCompromiseValidationTypes.MAX_LENGTH_OBSERVATION)
    }

    @Test
    fun should_fail_when_has_conflict(): Unit = runTest {
        prepareMockHasConflictTrue()
        prepareMockGetTOPersonByIdAcademyMember()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = dateNow(),
            start = LocalTime.of(13, 0),
            end = LocalTime.of(14, 0),
            compromiseType = EnumCompromiseType.FIRST
        )

        val config = CompromiseRecurrentConfig(
            dateStart = LocalDate.of(2025, 1, 20),
            dateEnd = LocalDate.of(2025, 2, 20),
            dayWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY)
        )

        val result = saveRecurrentCompromiseUseCase.saveRecurrentCompromise(scheduler, config).getValidations()

        result.shouldContainOnly(EnumCompromiseValidationTypes.RECURRENT_SCHEDULER_CONFLICT)
    }

    @Test
    fun should_fail_when_date_start_is_null(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdAcademyMember()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = dateNow(),
            start = LocalTime.of(13, 0),
            end = LocalTime.of(14, 0),
            compromiseType = EnumCompromiseType.FIRST
        )

        val config = CompromiseRecurrentConfig(
            dateStart = null,
            dateEnd = LocalDate.of(2025, 2, 20),
            dayWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY)
        )

        val result = saveRecurrentCompromiseUseCase.saveRecurrentCompromise(scheduler, config).getValidations()

        result.shouldContainOnly(EnumCompromiseValidationTypes.REQUIRED_DATE_START)
    }

    @Test
    fun should_fail_when_date_end_is_null(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdAcademyMember()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = dateNow(),
            start = LocalTime.of(13, 0),
            end = LocalTime.of(14, 0),
            compromiseType = EnumCompromiseType.FIRST
        )

        val config = CompromiseRecurrentConfig(
            dateStart = LocalDate.of(2025, 1, 20),
            dateEnd = null,
            dayWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY)
        )

        val result = saveRecurrentCompromiseUseCase.saveRecurrentCompromise(scheduler, config).getValidations()

        result.shouldContainOnly(EnumCompromiseValidationTypes.REQUIRED_DATE_END)
    }

    @Test
    fun should_fail_when_date_start_is_after_date_end_or_equals(): Unit = runTest {
        prepareMockHasConflictFalse()
        prepareMockGetTOPersonByIdAcademyMember()

        val scheduler = TOScheduler(
            academyMemberPersonId = UUID.randomUUID().toString(),
            professionalPersonId = UUID.randomUUID().toString(),
            professionalType = EnumUserType.PERSONAL_TRAINER,
            scheduledDate = dateNow(),
            start = LocalTime.of(13, 0),
            end = LocalTime.of(14, 0),
            compromiseType = EnumCompromiseType.FIRST
        )

        val configEquals = CompromiseRecurrentConfig(
            dateStart = LocalDate.of(2025, 1, 20),
            dateEnd = LocalDate.of(2025, 1, 20),
            dayWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY)
        )

        val configStartAfterEnd = CompromiseRecurrentConfig(
            dateStart = LocalDate.of(2025, 1, 20),
            dateEnd = LocalDate.of(2025, 1, 10),
            dayWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY)
        )

        val result = saveRecurrentCompromiseUseCase.saveRecurrentCompromise(scheduler, configEquals).getValidations()
        val result2 = saveRecurrentCompromiseUseCase.saveRecurrentCompromise(scheduler, configStartAfterEnd).getValidations()

        result.shouldContainOnly(EnumCompromiseValidationTypes.INVALID_DATE_PERIOD)
        result2.shouldContainOnly(EnumCompromiseValidationTypes.INVALID_DATE_PERIOD)
    }

    private fun prepareMockGetTOPersonByIdAcademyMember() {
        coEvery { userRepository.getTOPersonById(any()) } returns TOPerson(
            toUser = TOUser(type = EnumUserType.ACADEMY_MEMBER)
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
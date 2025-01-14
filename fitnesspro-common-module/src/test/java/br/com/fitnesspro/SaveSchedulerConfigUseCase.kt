package br.com.fitnesspro

import android.content.Context
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.END_BREAK_TIME
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.END_WORK_TIME
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.MAX_SCHEDULE_DENSITY
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.MIN_SCHEDULE_DENSITY
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.START_BREAK_TIME
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.START_WORK_TIME
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOSchedulerConfig
import br.com.fitnesspro.to.TOUser
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainOnly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalTime
import java.util.UUID

class SaveSchedulerConfigUseCase {

    private lateinit var context: Context
    private lateinit var schedulerConfigRepository: SchedulerConfigRepository
    private lateinit var userRepository: UserRepository
    private lateinit var saveSchedulerConfigUseCase: SaveSchedulerConfigUseCase

    @BeforeEach
    fun setUp() {
        context = mockk(relaxed = true)
        schedulerConfigRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)

        saveSchedulerConfigUseCase = SaveSchedulerConfigUseCase(
            context,
            schedulerConfigRepository,
            userRepository
        )

    }

    @Test
    fun should_save_personal_trainer_config_when_all_fields_are_valid(): Unit = runBlocking {
        prepareMockGetTOPersonByIdWithPersonal()

        val result = saveSchedulerConfigUseCase.saveConfig("", TOSchedulerConfig(personId = UUID.randomUUID().toString()))

        result.shouldBeEmpty()
    }

    @Test
    fun should_save_academy_member_config_when_all_fields_are_valid(): Unit = runBlocking {
        prepareMockGetTOPersonByIdWithMember()

        val to = TOSchedulerConfig(personId = UUID.randomUUID().toString())
        val result = saveSchedulerConfigUseCase.saveConfig("", to)

        result.shouldBeEmpty()
    }

    @Test
    fun should_fail_when_min_schedule_density_is_null(): Unit = runBlocking {
        prepareMockGetTOPersonByIdWithPersonal()

        val to = TOSchedulerConfig(personId = UUID.randomUUID().toString(), minScheduleDensity = null)

        val result = saveSchedulerConfigUseCase.saveConfig("", to).map { it.first }

        result.shouldContainOnly(MIN_SCHEDULE_DENSITY)
    }

    @Test
    fun should_fail_when_min_schedule_density_is_less_than_1(): Unit = runBlocking {
        prepareMockGetTOPersonByIdWithPersonal()

        val to = TOSchedulerConfig(personId = UUID.randomUUID().toString(), minScheduleDensity = 0)

        val result = saveSchedulerConfigUseCase.saveConfig("", to).map { it.first }

        result.shouldContainOnly(MIN_SCHEDULE_DENSITY)
    }

    @Test
    fun should_fail_when_max_schedule_density_is_null(): Unit = runBlocking {
        prepareMockGetTOPersonByIdWithPersonal()

        val to = TOSchedulerConfig(personId = UUID.randomUUID().toString(),maxScheduleDensity = null)

        val result = saveSchedulerConfigUseCase.saveConfig("", to).map { it.first }

        result.shouldContainOnly(MAX_SCHEDULE_DENSITY)
    }

    @Test
    fun should_fail_when_max_schedule_density_is_less_than_1(): Unit = runBlocking {
        prepareMockGetTOPersonByIdWithPersonal()

        val to = TOSchedulerConfig(
            personId = UUID.randomUUID().toString(),
            maxScheduleDensity = 0
        )

        val result = saveSchedulerConfigUseCase.saveConfig("", to).map { it.first }

        result.shouldContainOnly(MAX_SCHEDULE_DENSITY, null)
    }

    @Test
    fun should_fail_when_min_schedule_density_is_greater_than_or_equal_to_max_schedule_density(): Unit = runBlocking {
        prepareMockGetTOPersonByIdWithPersonal()

        val toEquals = TOSchedulerConfig(
            personId = UUID.randomUUID().toString(),
            minScheduleDensity = 2,
            maxScheduleDensity = 2
        )
        val toGreater = TOSchedulerConfig(
            personId = UUID.randomUUID().toString(),
            minScheduleDensity = 9,
            maxScheduleDensity = 2
        )

        val resultEquals = saveSchedulerConfigUseCase.saveConfig("", toEquals).map { it.first }
        val resultGreater = saveSchedulerConfigUseCase.saveConfig("", toGreater).map { it.first }

        resultEquals.shouldContainOnly(null)
        resultGreater.shouldContainOnly(null)
    }

    @Test
    fun should_fail_when_start_work_time_is_null(): Unit = runBlocking {
        prepareMockGetTOPersonByIdWithPersonal()

        val to = TOSchedulerConfig(personId = UUID.randomUUID().toString(), startWorkTime = null)

        val result = saveSchedulerConfigUseCase.saveConfig("", to).map { it.first }

        result.shouldContainOnly(START_WORK_TIME)
    }

    @Test
    fun should_fail_when_end_work_time_is_null(): Unit = runBlocking {
        prepareMockGetTOPersonByIdWithPersonal()

        val to = TOSchedulerConfig(personId = UUID.randomUUID().toString(), endWorkTime = null)

        val result = saveSchedulerConfigUseCase.saveConfig("", to).map { it.first }

        result.shouldContainOnly(END_WORK_TIME)
    }

    @Test
    fun should_fail_when_start_work_time_is_after_or_equal_to_end_work_time(): Unit = runBlocking {
        prepareMockGetTOPersonByIdWithPersonal()

        val toEquals = TOSchedulerConfig(
            personId = UUID.randomUUID().toString(),
            startWorkTime = LocalTime.of(10, 0),
            endWorkTime = LocalTime.of(10, 0)
        )
        val toGreater = TOSchedulerConfig(
            personId = UUID.randomUUID().toString(),
            startWorkTime = LocalTime.of(10, 0),
            endWorkTime = LocalTime.of(9, 0)
        )

        val resultEquals = saveSchedulerConfigUseCase.saveConfig("", toEquals).map { it.first }
        val resultGreater = saveSchedulerConfigUseCase.saveConfig("", toGreater).map { it.first }

        resultEquals.shouldContainOnly(null)
        resultGreater.shouldContainOnly(null)
    }

    @Test
    fun should_fail_when_start_break_time_is_null(): Unit = runBlocking {
        prepareMockGetTOPersonByIdWithPersonal()

        val to = TOSchedulerConfig(personId = UUID.randomUUID().toString(), startBreakTime = null)

        val result = saveSchedulerConfigUseCase.saveConfig("", to).map { it.first }

        result.shouldContainOnly(START_BREAK_TIME)
    }

    @Test
    fun should_fail_when_end_break_time_is_null(): Unit = runBlocking {
        prepareMockGetTOPersonByIdWithPersonal()

        val to = TOSchedulerConfig(personId = UUID.randomUUID().toString(), endBreakTime = null)

        val result = saveSchedulerConfigUseCase.saveConfig("", to).map { it.first }

        result.shouldContainOnly(END_BREAK_TIME)
    }

    @Test
    fun should_fail_when_start_break_time_is_after_or_equal_to_end_break_time(): Unit = runBlocking {
        prepareMockGetTOPersonByIdWithPersonal()

        val toEquals = TOSchedulerConfig(
            personId = UUID.randomUUID().toString(),
            startBreakTime = LocalTime.of(13, 0),
            endBreakTime = LocalTime.of(13, 0)
        )
        val toGreater = TOSchedulerConfig(
            personId = UUID.randomUUID().toString(),
            startBreakTime = LocalTime.of(14, 0),
            endBreakTime = LocalTime.of(13, 0)
        )

        val resultEquals = saveSchedulerConfigUseCase.saveConfig("", toEquals).map { it.first }
        val resultGreater = saveSchedulerConfigUseCase.saveConfig("", toGreater).map { it.first }

        resultEquals.shouldContainOnly(null)
        resultGreater.shouldContainOnly(null)
    }

    private fun prepareMockGetTOPersonByIdWithPersonal() {
        coEvery { userRepository.getTOPersonById(any()) } returns TOPerson(
            toUser = TOUser(type = EnumUserType.PERSONAL_TRAINER)
        )
    }

    private fun prepareMockGetTOPersonByIdWithMember() {
        coEvery { userRepository.getTOPersonById(any()) } returns TOPerson(
            toUser = TOUser(type = EnumUserType.ACADEMY_MEMBER)
        )
    }
}
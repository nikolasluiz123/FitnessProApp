package br.com.fitnesspro.common

import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumSchedulerConfigValidationTypes.INVALID_DENSITY_RANGE
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumSchedulerConfigValidationTypes.INVALID_MAX_SCHEDULE_DENSITY
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumSchedulerConfigValidationTypes.INVALID_MIN_SCHEDULE_DENSITY
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumSchedulerConfigValidationTypes.REQUIRED_MAX_SCHEDULE_DENSITY
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumSchedulerConfigValidationTypes.REQUIRED_MIN_SCHEDULE_DENSITY
import br.com.fitnesspro.core.validation.getValidations
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOSchedulerConfig
import br.com.fitnesspro.to.TOUser
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainOnly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class SaveSchedulerConfigUseCaseTests: BaseUnitTests() {

    private lateinit var schedulerConfigRepository: SchedulerConfigRepository
    private lateinit var userRepository: UserRepository
    private lateinit var personRepository: PersonRepository
    private lateinit var saveSchedulerConfigUseCase: SaveSchedulerConfigUseCase

    @BeforeEach
    override fun setUp() {
        super.setUp()
        
        schedulerConfigRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)

        saveSchedulerConfigUseCase = SaveSchedulerConfigUseCase(
            context,
            schedulerConfigRepository
        )

    }

    @Test
    fun should_save_personal_trainer_config_when_all_fields_are_valid(): Unit = runTest {
        prepareMockGetTOPersonByIdWithPersonal()

        val result = saveSchedulerConfigUseCase.saveConfig("", TOSchedulerConfig(personId = UUID.randomUUID().toString()))

        result.shouldBeEmpty()
    }

    @Test
    fun should_save_academy_member_config_when_all_fields_are_valid(): Unit = runTest {
        prepareMockGetTOPersonByIdWithMember()

        val to = TOSchedulerConfig(personId = UUID.randomUUID().toString())
        val result = saveSchedulerConfigUseCase.saveConfig("", to)

        result.shouldBeEmpty()
    }

    @Test
    fun should_fail_when_min_schedule_density_is_null(): Unit = runTest {
        prepareMockGetTOPersonByIdWithPersonal()

        val to = TOSchedulerConfig(personId = UUID.randomUUID().toString(), minScheduleDensity = null)

        val result = saveSchedulerConfigUseCase.saveConfig("", to).getValidations()

        result.shouldContainOnly(REQUIRED_MIN_SCHEDULE_DENSITY)
    }

    @Test
    fun should_fail_when_min_schedule_density_is_less_than_1(): Unit = runTest {
        prepareMockGetTOPersonByIdWithPersonal()

        val to = TOSchedulerConfig(personId = UUID.randomUUID().toString(), minScheduleDensity = 0)

        val result = saveSchedulerConfigUseCase.saveConfig("", to).getValidations()

        result.shouldContainOnly(INVALID_MIN_SCHEDULE_DENSITY)
    }

    @Test
    fun should_fail_when_max_schedule_density_is_null(): Unit = runTest {
        prepareMockGetTOPersonByIdWithPersonal()

        val to = TOSchedulerConfig(personId = UUID.randomUUID().toString(),maxScheduleDensity = null)

        val result = saveSchedulerConfigUseCase.saveConfig("", to).getValidations()

        result.shouldContainOnly(REQUIRED_MAX_SCHEDULE_DENSITY)
    }

    @Test
    fun should_fail_when_max_schedule_density_is_less_than_1(): Unit = runTest {
        prepareMockGetTOPersonByIdWithPersonal()

        val to = TOSchedulerConfig(
            personId = UUID.randomUUID().toString(),
            maxScheduleDensity = 0,
        )

        val result = saveSchedulerConfigUseCase.saveConfig("", to).getValidations()

        result.shouldContainOnly(INVALID_MAX_SCHEDULE_DENSITY, INVALID_DENSITY_RANGE)
    }

    @Test
    fun should_fail_when_min_schedule_density_is_greater_than_or_equal_to_max_schedule_density(): Unit = runTest {
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

        val resultEquals = saveSchedulerConfigUseCase.saveConfig("", toEquals).getValidations()
        val resultGreater = saveSchedulerConfigUseCase.saveConfig("", toGreater).getValidations()

        resultEquals.shouldContainOnly(INVALID_DENSITY_RANGE)
        resultGreater.shouldContainOnly(INVALID_DENSITY_RANGE)
    }

    private fun prepareMockGetTOPersonByIdWithPersonal() {
        coEvery { personRepository.getTOPersonById(any()) } returns TOPerson(
            user = TOUser(type = EnumUserType.PERSONAL_TRAINER)
        )
    }

    private fun prepareMockGetTOPersonByIdWithMember() {
        coEvery { personRepository.getTOPersonById(any()) } returns TOPerson(
            user = TOUser(type = EnumUserType.ACADEMY_MEMBER)
        )
    }
}
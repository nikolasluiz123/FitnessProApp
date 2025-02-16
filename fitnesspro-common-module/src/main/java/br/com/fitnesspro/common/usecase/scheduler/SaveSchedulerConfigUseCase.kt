package br.com.fitnesspro.common.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumSchedulerConfigValidationTypes
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.MAX_SCHEDULE_DENSITY
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.MIN_SCHEDULE_DENSITY
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOSchedulerConfig

class SaveSchedulerConfigUseCase(
    private val context: Context,
    private val schedulerConfigRepository: SchedulerConfigRepository,
    private val userRepository: UserRepository,
    private val personRepository: PersonRepository
) {

    suspend fun saveConfig(
        personId: String,
        toSchedulerConfig: TOSchedulerConfig? = null
    ): MutableList<FieldValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>> {
        val config = toSchedulerConfig ?: TOSchedulerConfig(personId = personId)

        val validationResults = validateSchedulerConfig(config)

        if (validationResults.isEmpty()) {
            schedulerConfigRepository.saveSchedulerConfig(config)
        }

        return validationResults
    }

    private suspend fun validateSchedulerConfig(config: TOSchedulerConfig): MutableList<FieldValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>> {
        val userType = personRepository.getTOPersonById(config.personId!!).toUser?.type!!
        if (userType == EnumUserType.ACADEMY_MEMBER) return mutableListOf()

        val validationResults = mutableListOf(
            validateMinScheduleDensity(config),
            validateMaxScheduleDensity(config),
            validateDensityRange(config),
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private fun validateMinScheduleDensity(config: TOSchedulerConfig): FieldValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>? {
        val validationPair = when {
            config.minScheduleDensity == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(MIN_SCHEDULE_DENSITY.labelResId)
                )

                FieldValidationError(
                    field = MIN_SCHEDULE_DENSITY,
                    validationType = EnumSchedulerConfigValidationTypes.REQUIRED_MIN_SCHEDULE_DENSITY,
                    message = message
                )
            }

            config.minScheduleDensity!! < 1 -> {
                val message = context.getString(
                    R.string.validation_msg_invalid_field,
                    context.getString(MIN_SCHEDULE_DENSITY.labelResId)
                )

                FieldValidationError(
                    field = MIN_SCHEDULE_DENSITY,
                    validationType = EnumSchedulerConfigValidationTypes.INVALID_MIN_SCHEDULE_DENSITY,
                    message = message
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validateMaxScheduleDensity(config: TOSchedulerConfig): FieldValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>? {
        val validationPair = when {
            config.maxScheduleDensity == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(MAX_SCHEDULE_DENSITY.labelResId)
                )

                FieldValidationError(
                    field = MAX_SCHEDULE_DENSITY,
                    validationType = EnumSchedulerConfigValidationTypes.REQUIRED_MAX_SCHEDULE_DENSITY,
                    message = message
                )
            }

            config.maxScheduleDensity!! < 1 -> {
                val message = context.getString(
                    R.string.validation_msg_invalid_field,
                    context.getString(MAX_SCHEDULE_DENSITY.labelResId)
                )

                FieldValidationError(
                    field = MAX_SCHEDULE_DENSITY,
                    validationType = EnumSchedulerConfigValidationTypes.INVALID_MAX_SCHEDULE_DENSITY,
                    message = message
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validateDensityRange(config: TOSchedulerConfig): FieldValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>? {
        if (config.minScheduleDensity == null || config.maxScheduleDensity == null) return null

        return when {
            config.minScheduleDensity!! > config.maxScheduleDensity!! ||
            config.minScheduleDensity!! == config.maxScheduleDensity!! -> {
                val message = context.getString(R.string.save_scheduler_config_msg_invalid_density_range)
                FieldValidationError(
                    field = null,
                    validationType = EnumSchedulerConfigValidationTypes.INVALID_DENSITY_RANGE,
                    message = message
                )
            }

            else -> null
        }
    }

    suspend fun createConfigBatch(personIds: List<String>): List<FieldValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>> {
        val configs = personIds.map {
            TOSchedulerConfig(personId = it)
        }

        val validationResults = configs.flatMap { validateSchedulerConfig(it) }

        if (validationResults.isEmpty()) {
            schedulerConfigRepository.saveSchedulerConfigBatch(configs)
        }

        return validationResults
    }
}
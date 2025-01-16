package br.com.fitnesspro.common.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumSchedulerConfigValidationTypes
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.END_BREAK_TIME
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.END_WORK_TIME
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.MAX_SCHEDULE_DENSITY
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.MIN_SCHEDULE_DENSITY
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.START_BREAK_TIME
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.START_WORK_TIME
import br.com.fitnesspro.core.validation.ValidationError
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOSchedulerConfig

class SaveSchedulerConfigUseCase(
    private val context: Context,
    private val schedulerConfigRepository: SchedulerConfigRepository,
    private val userRepository: UserRepository
) {

    suspend fun saveConfig(
        personId: String,
        toSchedulerConfig: TOSchedulerConfig? = null
    ): MutableList<ValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>> {
        val config = toSchedulerConfig ?: TOSchedulerConfig(personId = personId)

        val validationResults = validateSchedulerConfig(config)

        if (validationResults.isEmpty()) {
            schedulerConfigRepository.saveSchedulerConfig(config)
        }

        return validationResults
    }

    private suspend fun validateSchedulerConfig(config: TOSchedulerConfig): MutableList<ValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>> {
        val userType = userRepository.getTOPersonById(config.personId!!).toUser?.type!!
        if (userType == EnumUserType.ACADEMY_MEMBER) return mutableListOf()

        val validationResults = mutableListOf(
            validateMinScheduleDensity(config),
            validateMaxScheduleDensity(config),
            validateDensityRange(config),
            validateStartWorkTime(config),
            validateEndWorkTime(config),
            validateWorkoutPeriod(config),
            validateStartBreakTime(config),
            validateEndBreakTime(config),
            validateBreakPeriod(config)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private fun validateMinScheduleDensity(config: TOSchedulerConfig): ValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>? {
        val validationPair = when {
            config.minScheduleDensity == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(MIN_SCHEDULE_DENSITY.labelResId)
                )

                ValidationError(
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

                ValidationError(
                    field = MIN_SCHEDULE_DENSITY,
                    validationType = EnumSchedulerConfigValidationTypes.INVALID_MIN_SCHEDULE_DENSITY,
                    message = message
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validateMaxScheduleDensity(config: TOSchedulerConfig): ValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>? {
        val validationPair = when {
            config.maxScheduleDensity == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(MAX_SCHEDULE_DENSITY.labelResId)
                )

                ValidationError(
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

                ValidationError(
                    field = MAX_SCHEDULE_DENSITY,
                    validationType = EnumSchedulerConfigValidationTypes.INVALID_MAX_SCHEDULE_DENSITY,
                    message = message
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validateDensityRange(config: TOSchedulerConfig): ValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>? {
        if (config.minScheduleDensity == null || config.maxScheduleDensity == null) return null

        return when {
            config.minScheduleDensity!! > config.maxScheduleDensity!! ||
            config.minScheduleDensity!! == config.maxScheduleDensity!! -> {
                val message = context.getString(R.string.save_scheduler_config_msg_invalid_density_range)
                ValidationError(
                    field = null,
                    validationType = EnumSchedulerConfigValidationTypes.INVALID_DENSITY_RANGE,
                    message = message
                )
            }

            else -> null
        }
    }

    private fun validateStartWorkTime(config: TOSchedulerConfig): ValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>? {
        val validationPair = when {
            config.startWorkTime == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(START_WORK_TIME.labelResId)
                )

                ValidationError(
                    field = START_WORK_TIME,
                    validationType = EnumSchedulerConfigValidationTypes.REQUIRED_START_WORK_TIME,
                    message = message
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validateEndWorkTime(config: TOSchedulerConfig): ValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>? {
        val validationPair = when {
            config.endWorkTime == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(END_WORK_TIME.labelResId)
                )

                ValidationError(
                    field = END_WORK_TIME,
                    validationType = EnumSchedulerConfigValidationTypes.REQUIRED_END_WORK_TIME,
                    message = message
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validateWorkoutPeriod(config: TOSchedulerConfig): ValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>? {
        if (config.startWorkTime == null || config.endWorkTime == null) return null

        return when {
            config.startWorkTime!!.isAfter(config.endWorkTime!!) ||
            config.startWorkTime == config.endWorkTime -> {
                val message = context.getString(R.string.save_scheduler_config_msg_invalid_workout_period)

                ValidationError(
                    field = null,
                    validationType = EnumSchedulerConfigValidationTypes.INVALID_WORK_PERIOD,
                    message = message
                )
            }

            else -> null
        }
    }

    private fun validateStartBreakTime(config: TOSchedulerConfig): ValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>? {
        val validationPair = when {
            config.startBreakTime == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(START_BREAK_TIME.labelResId)
                )

                ValidationError(
                    field = START_BREAK_TIME,
                    validationType = EnumSchedulerConfigValidationTypes.REQUIRED_START_BREAK_TIME,
                    message = message
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validateEndBreakTime(config: TOSchedulerConfig): ValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>? {
        val validationPair = when {
            config.endBreakTime == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(END_BREAK_TIME.labelResId)
                )

                ValidationError(
                    field = END_BREAK_TIME,
                    validationType = EnumSchedulerConfigValidationTypes.REQUIRED_END_BREAK_TIME,
                    message = message
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validateBreakPeriod(config: TOSchedulerConfig): ValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>? {
        if (config.startBreakTime == null || config.endBreakTime == null) return null

        return when {
            config.startBreakTime!!.isAfter(config.endBreakTime!!) ||
            config.startBreakTime == config.endBreakTime -> {
                val message = context.getString(R.string.save_scheduler_config_msg_invalid_break_period)

                ValidationError(
                    field = null,
                    validationType = EnumSchedulerConfigValidationTypes.INVALID_BREAK_PERIOD,
                    message = message
                )
            }

            else -> null
        }
    }

    suspend fun createConfigBatch(personIds: List<String>): List<ValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>> {
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
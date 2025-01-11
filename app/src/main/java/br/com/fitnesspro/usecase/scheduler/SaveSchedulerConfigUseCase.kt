package br.com.fitnesspro.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.R
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.repository.SchedulerRepository
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.to.TOSchedulerConfig
import br.com.fitnesspro.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields

class SaveSchedulerConfigUseCase(
    private val context: Context,
    private val schedulerRepository: SchedulerRepository,
    private val userRepository: UserRepository
) {

    suspend fun saveConfig(personId: String, toSchedulerConfig: TOSchedulerConfig? = null): MutableList<Pair<EnumValidatedSchedulerConfigFields?, String>> {
        val config = if (toSchedulerConfig != null) {
            schedulerRepository.getTOSchedulerConfigByPersonId(personId)!!.copy(
                alarm = toSchedulerConfig.alarm,
                notification = toSchedulerConfig.notification,
                minScheduleDensity = toSchedulerConfig.minScheduleDensity,
                maxScheduleDensity = toSchedulerConfig.maxScheduleDensity
            )
        } else {
            TOSchedulerConfig(personId = personId)
        }

        val validationResults = validateSchedulerConfig(config)

        if (validationResults.isEmpty()) {
            schedulerRepository.saveSchedulerConfig(config)
        }

        return validationResults
    }

    private suspend fun validateSchedulerConfig(config: TOSchedulerConfig): MutableList<Pair<EnumValidatedSchedulerConfigFields?, String>> {
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

    private fun validateMinScheduleDensity(config: TOSchedulerConfig): Pair<EnumValidatedSchedulerConfigFields, String>? {
        val validationPair = when {
            config.minScheduleDensity == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedSchedulerConfigFields.MIN_EVENT_DENSITY.labelResId)
                )

                Pair(EnumValidatedSchedulerConfigFields.MIN_EVENT_DENSITY, message)
            }

            config.minScheduleDensity!! < 1 -> {
                val message = context.getString(
                    R.string.validation_msg_invalid_field,
                    context.getString(EnumValidatedSchedulerConfigFields.MIN_EVENT_DENSITY.labelResId)
                )

                Pair(EnumValidatedSchedulerConfigFields.MIN_EVENT_DENSITY, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateMaxScheduleDensity(config: TOSchedulerConfig): Pair<EnumValidatedSchedulerConfigFields, String>? {
        val validationPair = when {
            config.maxScheduleDensity == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedSchedulerConfigFields.MAX_EVENT_DENSITY.labelResId)
                )

                Pair(EnumValidatedSchedulerConfigFields.MAX_EVENT_DENSITY, message)
            }

            config.maxScheduleDensity!! < 1 -> {
                val message = context.getString(
                    R.string.validation_msg_invalid_field,
                    context.getString(EnumValidatedSchedulerConfigFields.MAX_EVENT_DENSITY.labelResId)
                )

                Pair(EnumValidatedSchedulerConfigFields.MAX_EVENT_DENSITY, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateDensityRange(config: TOSchedulerConfig): Pair<EnumValidatedSchedulerConfigFields?, String>? {
        val validationPair = when {
            config.minScheduleDensity!! > config.maxScheduleDensity!! ||
            config.minScheduleDensity!! == config.maxScheduleDensity!! -> {
                val message = context.getString(R.string.save_scheduler_config_msg_invalid_density_range)
                Pair(null, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateStartWorkTime(config: TOSchedulerConfig): Pair<EnumValidatedSchedulerConfigFields, String>? {
        val validationPair = when {
            config.startWorkTime == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedSchedulerConfigFields.START_WORK_TIME.labelResId)
                )

                Pair(EnumValidatedSchedulerConfigFields.START_WORK_TIME, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateEndWorkTime(config: TOSchedulerConfig): Pair<EnumValidatedSchedulerConfigFields, String>? {
        val validationPair = when {
            config.endWorkTime == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedSchedulerConfigFields.END_WORK_TIME.labelResId)
                )

                Pair(EnumValidatedSchedulerConfigFields.END_WORK_TIME, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateWorkoutPeriod(config: TOSchedulerConfig): Pair<EnumValidatedSchedulerConfigFields?, String>? {
        val validationPair = when {
            config.startWorkTime!!.isBefore(config.endWorkTime!!) ||
                    config.startWorkTime == config.endWorkTime ||
                    config.endWorkTime!!.isAfter(config.startWorkTime!!) -> {
                val message = context.getString(R.string.save_scheduler_config_msg_invalid_workout_period)

                Pair(null, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateStartBreakTime(config: TOSchedulerConfig): Pair<EnumValidatedSchedulerConfigFields, String>? {
        val validationPair = when {
            config.startBreakTime == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedSchedulerConfigFields.START_BREAK_TIME.labelResId)
                )

                Pair(EnumValidatedSchedulerConfigFields.START_BREAK_TIME, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateEndBreakTime(config: TOSchedulerConfig): Pair<EnumValidatedSchedulerConfigFields, String>? {
        val validationPair = when {
            config.endBreakTime == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedSchedulerConfigFields.END_BREAK_TIME.labelResId)
                )

                Pair(EnumValidatedSchedulerConfigFields.END_BREAK_TIME, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validateBreakPeriod(config: TOSchedulerConfig): Pair<EnumValidatedSchedulerConfigFields?, String>? {
        val validationPair = when {
            config.startBreakTime!!.isBefore(config.endBreakTime!!) ||
                    config.startBreakTime == config.endBreakTime ||
                    config.endBreakTime!!.isAfter(config.startBreakTime!!) -> {
                val message = context.getString(R.string.save_scheduler_config_msg_invalid_break_period)

                Pair(null, message)
            }

            else -> null
        }

        return validationPair
    }

    suspend fun createConfigBatch(personIds: List<String>): List<Pair<EnumValidatedSchedulerConfigFields?, String>> {
        val configs = personIds.map {
            TOSchedulerConfig(personId = it)
        }

        val validationResults = configs.flatMap { validateSchedulerConfig(it) }

        if (validationResults.isEmpty()) {
            schedulerRepository.saveSchedulerConfigBatch(configs)
        }

        return validationResults
    }
}
package br.com.fitnesspro.common.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.MAX_SCHEDULE_DENSITY
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.MIN_SCHEDULE_DENSITY
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields.NOTIFICATION_ANTECEDENCE_TIME
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOSchedulerConfig
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class SaveSchedulerConfigUseCase(
    private val context: Context,
    private val schedulerConfigRepository: SchedulerConfigRepository
) {

    suspend fun saveConfig(
        toPerson: TOPerson,
        toSchedulerConfig: TOSchedulerConfig? = null
    ): MutableList<FieldValidationError<EnumValidatedSchedulerConfigFields>> = withContext(IO) {
        val personConfig = schedulerConfigRepository.getTOSchedulerConfigByPersonId(toPerson.id!!)

        val config = when {
            personConfig != null && toSchedulerConfig != null -> {
                personConfig.apply {
                    notification = toSchedulerConfig.notification
                    notificationAntecedenceTime = toSchedulerConfig.notificationAntecedenceTime
                    minScheduleDensity = toSchedulerConfig.minScheduleDensity
                    maxScheduleDensity = toSchedulerConfig.maxScheduleDensity
                }
            }

            personConfig == null && toSchedulerConfig == null -> {
                TOSchedulerConfig(personId = toPerson.id)
            }

            personConfig != null && toSchedulerConfig == null -> {
                personConfig
            }

            else -> null
        }

        val validationResults = validateSchedulerConfig(config!!, toPerson.user?.type!!)

        if (validationResults.isEmpty()) {
            schedulerConfigRepository.saveSchedulerConfig(config)
        }

        validationResults
    }

    private fun validateSchedulerConfig(config: TOSchedulerConfig, userType: EnumUserType): MutableList<FieldValidationError<EnumValidatedSchedulerConfigFields>> {
        if (userType == EnumUserType.ACADEMY_MEMBER) return mutableListOf()

        val validationResults = mutableListOf(
            validateMinScheduleDensity(config),
            validateMaxScheduleDensity(config),
            validateDensityRange(config),
            validateNotificationAntecedenceTime(config)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private fun validateMinScheduleDensity(config: TOSchedulerConfig): FieldValidationError<EnumValidatedSchedulerConfigFields>? {
        val validationPair = when {
            config.minScheduleDensity == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(MIN_SCHEDULE_DENSITY.labelResId)
                )

                FieldValidationError(
                    field = MIN_SCHEDULE_DENSITY,
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
                    message = message
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validateMaxScheduleDensity(config: TOSchedulerConfig): FieldValidationError<EnumValidatedSchedulerConfigFields>? {
        val validationPair = when {
            config.maxScheduleDensity == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(MAX_SCHEDULE_DENSITY.labelResId)
                )

                FieldValidationError(
                    field = MAX_SCHEDULE_DENSITY,
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
                    message = message
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validateDensityRange(config: TOSchedulerConfig): FieldValidationError<EnumValidatedSchedulerConfigFields>? {
        if (config.minScheduleDensity == null || config.maxScheduleDensity == null) return null

        return when {
            config.minScheduleDensity!! > config.maxScheduleDensity!! ||
            config.minScheduleDensity!! == config.maxScheduleDensity!! -> {
                val message = context.getString(R.string.save_scheduler_config_msg_invalid_density_range)
                FieldValidationError(
                    field = null,
                    message = message
                )
            }

            else -> null
        }
    }

    private fun validateNotificationAntecedenceTime(config: TOSchedulerConfig): FieldValidationError<EnumValidatedSchedulerConfigFields>? {
        val validationPair = when {
            config.notificationAntecedenceTime == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(MIN_SCHEDULE_DENSITY.labelResId)
                )

                FieldValidationError(
                    field = NOTIFICATION_ANTECEDENCE_TIME,
                    message = message
                )
            }

            config.notificationAntecedenceTime!! < 30 -> {
                val message = context.getString(
                    R.string.validation_msg_invalid_field,
                    context.getString(MIN_SCHEDULE_DENSITY.labelResId)
                )

                FieldValidationError(
                    field = NOTIFICATION_ANTECEDENCE_TIME,
                    message = message
                )
            }

            else -> null
        }

        return validationPair
    }
}
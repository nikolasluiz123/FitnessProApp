package br.com.fitnesspro.scheduler.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields
import br.com.fitnesspro.to.TOScheduler

class SaveUniqueCompromiseUseCase(
    context: Context,
    schedulerRepository: SchedulerRepository,
    userRepository: UserRepository,
    private val personRepository: PersonRepository
): SaveCompromiseCommonUseCase(context, schedulerRepository, userRepository) {

    suspend fun saveUniqueCompromise(toScheduler: TOScheduler): MutableList<FieldValidationError<EnumValidatedCompromiseFields>> {
        val validationResults = validateUniqueCompromise(toScheduler)

        if (validationResults.isEmpty()) {
            schedulerRepository.saveScheduler(toScheduler, EnumSchedulerType.UNIQUE)
        }

        return validationResults
    }

    private suspend fun validateUniqueCompromise(toScheduler: TOScheduler): MutableList<FieldValidationError<EnumValidatedCompromiseFields>> {
        val validationResults = mutableListOf(
            validateMember(toScheduler),
            validateHourStart(toScheduler, false),
            validateHourEnd(toScheduler),
            validateObservation(toScheduler),
            validateHourPeriod(toScheduler),
            validateSchedulerConflictMember(toScheduler)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private suspend fun validateSchedulerConflictMember(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields>? {
        val requiredFields = listOf(
            scheduler.dateTimeStart,
            scheduler.dateTimeEnd,
            scheduler.academyMemberPersonId
        )

        if (requiredFields.any { it == null }) return null

        val member = personRepository.getTOPersonById(scheduler.academyMemberPersonId!!)

        val hasConflict = schedulerRepository.getHasSchedulerConflict(
            schedulerId = scheduler.id,
            personId = scheduler.academyMemberPersonId!!,
            userType = member.user?.type!!,
            start = scheduler.dateTimeStart!!,
            end = scheduler.dateTimeEnd!!
        )

        return when {
            hasConflict -> {
                val message = context.getString(
                    R.string.save_compromise_scheduler_conflict,
                    scheduler.dateTimeStart!!.format(EnumDateTimePatterns.DATE),
                    scheduler.dateTimeStart!!.format(EnumDateTimePatterns.TIME),
                    scheduler.dateTimeEnd!!.format(EnumDateTimePatterns.TIME),
                    member.name
                )

                FieldValidationError(
                    field = null,
                    message = message
                )
            }

            else -> null
        }
    }
}
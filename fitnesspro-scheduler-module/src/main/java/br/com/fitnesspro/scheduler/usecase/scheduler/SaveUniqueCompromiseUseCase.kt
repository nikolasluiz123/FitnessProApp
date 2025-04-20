package br.com.fitnesspro.scheduler.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields
import br.com.fitnesspro.to.TOScheduler

class SaveUniqueCompromiseUseCase(
    context: Context,
    schedulerRepository: SchedulerRepository,
    userRepository: UserRepository,
    private val personRepository: PersonRepository
): SaveCompromiseCommonUseCase(context, schedulerRepository, userRepository) {

    suspend fun saveUniqueCompromise(toScheduler: TOScheduler): MutableList<FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>> {
        val validationResults = validateUniqueCompromise(toScheduler)

        if (validationResults.isEmpty()) {
            schedulerRepository.saveScheduler(toScheduler, EnumSchedulerType.UNIQUE)
        }

        return validationResults
    }

    private suspend fun validateUniqueCompromise(toScheduler: TOScheduler): MutableList<FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>> {
        val validationResults = mutableListOf(
            validateMember(toScheduler),
            validateHourStart(toScheduler),
            validateHourEnd(toScheduler),
            validateObservation(toScheduler),
            validateHourPeriod(toScheduler),
            validateSchedulerConflictMember(toScheduler)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private suspend fun validateSchedulerConflictMember(scheduler: TOScheduler): FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>? {
        val requiredFields = listOf(
            scheduler.timeStart,
            scheduler.timeEnd,
            scheduler.academyMemberPersonId
        )

        if (requiredFields.any { it == null }) return null

        val member = personRepository.getTOPersonById(scheduler.academyMemberPersonId!!)

        val hasConflict = schedulerRepository.getHasSchedulerConflict(
            schedulerId = scheduler.id,
            personId = scheduler.academyMemberPersonId!!,
            userType = member.user?.type!!,
            scheduledDate = scheduler.scheduledDate!!,
            start = scheduler.timeStart!!,
            end = scheduler.timeEnd!!
        )

        return when {
            hasConflict -> {
                val message = context.getString(
                    R.string.save_compromise_scheduler_conflict,
                    scheduler.scheduledDate!!.format(EnumDateTimePatterns.DATE),
                    scheduler.timeStart!!.format(EnumDateTimePatterns.TIME),
                    scheduler.timeEnd!!.format(EnumDateTimePatterns.TIME),
                    member.name
                )

                FieldValidationError(
                    field = null,
                    validationType = EnumCompromiseValidationTypes.SCHEDULER_CONFLICT_MEMBER,
                    message = message
                )
            }

            else -> null
        }
    }
}
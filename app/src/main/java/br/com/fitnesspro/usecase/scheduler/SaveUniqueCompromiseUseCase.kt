package br.com.fitnesspro.usecase.scheduler

import android.content.Context
import br.com.fitnesspro.R
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.repository.SchedulerRepository
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.usecase.scheduler.enums.EnumValidatedCompromiseFields

class SaveUniqueCompromiseUseCase(
    context: Context,
    schedulerRepository: SchedulerRepository,
    userRepository: UserRepository
): SaveCompromiseCommonUseCase(context, schedulerRepository, userRepository) {

    suspend fun saveUniqueCompromise(toScheduler: TOScheduler): MutableList<Pair<EnumValidatedCompromiseFields?, String>> {
        val validationResults = validateUniqueCompromise(toScheduler)

        if (validationResults.isEmpty()) {
            schedulerRepository.saveScheduler(toScheduler)
        }

        return validationResults
    }

    private suspend fun validateUniqueCompromise(toScheduler: TOScheduler): MutableList<Pair<EnumValidatedCompromiseFields?, String>> {
        val validationResults = mutableListOf(
            validateMember(toScheduler),
            validateHourStart(toScheduler),
            validateHourEnd(toScheduler),
            validateHourPeriod(toScheduler),
            validateSchedulerConflictMember(toScheduler)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private suspend fun validateSchedulerConflictMember(scheduler: TOScheduler): Pair<EnumValidatedCompromiseFields?, String>? {
        val member = userRepository.getTOPersonById(scheduler.academyMemberPersonId!!)

        val hasConflict = schedulerRepository.getHasSchedulerConflict(
            schedulerId = scheduler.id,
            personId = scheduler.academyMemberPersonId!!,
            userType = member.toUser?.type!!,
            scheduledDate = scheduler.scheduledDate!!,
            start = scheduler.start!!,
            end = scheduler.end!!
        )

        val validationPair = when {
            hasConflict -> {
                val message = context.getString(
                    R.string.save_compromise_scheduler_conflict_member,
                    scheduler.scheduledDate!!.format(EnumDateTimePatterns.DATE),
                    scheduler.start!!.format(EnumDateTimePatterns.TIME),
                    scheduler.end!!.format(EnumDateTimePatterns.TIME),
                    member.name
                )

                Pair(null, message)
            }

            else -> null
        }

        return validationPair
    }
}
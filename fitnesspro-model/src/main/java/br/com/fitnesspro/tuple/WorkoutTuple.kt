package br.com.fitnesspro.tuple

import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.menu.ITupleListItem
import java.time.LocalDate

data class WorkoutTuple(
    val id: String,
    val dateStart: LocalDate,
    val dateEnd: LocalDate
): ITupleListItem {

    override fun getLabel(): String {
        return "${dateStart.format(EnumDateTimePatterns.DATE)} - ${dateEnd.format(EnumDateTimePatterns.DATE)}"
    }
}
package br.com.fitnesspro.tuple

import br.com.core.android.utils.interfaces.ISimpleListItem
import br.com.core.utils.enums.EnumDateTimePatterns
import br.com.core.utils.extensions.format
import java.time.LocalDate

data class WorkoutTuple(
    val id: String,
    val dateStart: LocalDate,
    val dateEnd: LocalDate
): ISimpleListItem {

    override fun getLabel(): String {
        return "${dateStart.format(EnumDateTimePatterns.DATE)} - ${dateEnd.format(EnumDateTimePatterns.DATE)}"
    }
}
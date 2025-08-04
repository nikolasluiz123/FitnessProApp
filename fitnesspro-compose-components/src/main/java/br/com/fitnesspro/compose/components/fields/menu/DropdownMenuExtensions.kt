package br.com.fitnesspro.compose.components.fields.menu

import android.content.Context
import br.com.fitnesspro.core.extensions.getLabelFromChronoUnit
import java.time.temporal.ChronoUnit
import kotlin.enums.EnumEntries

fun EnumEntries<ChronoUnit>.getChronoUnitMenuItems(context: Context): List<MenuItem<ChronoUnit?>> {
    val units = slice(ChronoUnit.SECONDS.ordinal..ChronoUnit.HOURS.ordinal)

    return units.map { unit ->
        MenuItem(
            label = unit.getLabelFromChronoUnit(context),
            value = unit
        )
    }
}


package br.com.fitnesspro.to

import br.com.fitnesspro.model.enums.EnumReportContext

data class TOWorkoutReport(
    override var id: String? = null,
    var personId: String? = null,
    var workoutId: String? = null,
    var reportId: String? = null,
    var context: EnumReportContext? = null,
    var active: Boolean = true
): BaseTO
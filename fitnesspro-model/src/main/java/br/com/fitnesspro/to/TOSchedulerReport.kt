package br.com.fitnesspro.to

import br.com.fitnesspro.model.enums.EnumReportContext

data class TOSchedulerReport(
    override var id: String? = null,
    var personId: String? = null,
    var reportId: String? = null,
    var context: EnumReportContext? = null
): BaseTO
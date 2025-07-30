package br.com.fitnesspro.common.ui.screen.report.callback

import br.com.fitnesspro.to.TOReport

fun interface OnInactivateReportClick {
    fun onExecute(report: TOReport, onSuccess: () -> Unit)
}
package br.com.fitnesspro.scheduler.ui.screen.scheduler.callback

fun interface OnGenerateSchedulerReportClick {
    fun onExecute(onSuccess: (filePath: String) -> Unit)
}
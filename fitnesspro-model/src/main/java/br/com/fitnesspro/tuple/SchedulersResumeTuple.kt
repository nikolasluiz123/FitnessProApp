package br.com.fitnesspro.tuple

data class SchedulersResumeTuple(
    var personName: String? = null,
    var countPending: Int = 0,
    var countConfirmed: Int = 0,
    var countCancelled: Int = 0,
    var countCompleted: Int = 0
)
package br.com.fitnesspro.to

data class TOSchedulerConfig(
    override var id: String? = null,
    var notification: Boolean = false,
    var minScheduleDensity: Int? = 1,
    var maxScheduleDensity: Int? = 2,
    var personId: String? = null,
): BaseTO
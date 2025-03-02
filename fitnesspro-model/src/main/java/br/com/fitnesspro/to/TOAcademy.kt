package br.com.fitnesspro.to

data class TOAcademy(
    override var id: String? = null,
    val name: String? = null,
    var address: String? = null,
    var phone: String? = null,
    var active: Boolean = true,
): BaseTO
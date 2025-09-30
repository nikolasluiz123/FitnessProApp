package br.com.fitnesspro.to

import br.com.core.android.utils.interfaces.ISimpleListItem

data class TOWorkoutGroupPreDefinition(
    override var id: String? = null,
    var name: String? = null,
    var personalTrainerPersonId: String? = null,
    var active: Boolean = true
): BaseTO, ISimpleListItem {
    override fun getLabel(): String {
        return name ?: ""
    }
}
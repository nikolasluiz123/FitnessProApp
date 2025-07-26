package br.com.fitnesspro.to

import br.com.fitnesspro.core.menu.ITupleListItem

data class TOWorkoutGroupPreDefinition(
    override var id: String? = null,
    var name: String? = null,
    var personalTrainerPersonId: String? = null,
    var active: Boolean = true
): BaseTO, ITupleListItem {
    override fun getLabel(): String {
        return name ?: ""
    }
}
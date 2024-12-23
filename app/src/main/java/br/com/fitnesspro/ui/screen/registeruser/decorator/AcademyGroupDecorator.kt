package br.com.fitnesspro.ui.screen.registeruser.decorator

import br.com.fitnesspro.compose.components.list.expandable.IBasicExpandableGroup
import br.com.fitnesspro.to.TOPersonAcademyTime

data class AcademyGroupDecorator(
    override val id: String,
    override val label: Int,
    override val value: String,
    override var isExpanded: Boolean,
    override val items: List<TOPersonAcademyTime>
): IBasicExpandableGroup<TOPersonAcademyTime>
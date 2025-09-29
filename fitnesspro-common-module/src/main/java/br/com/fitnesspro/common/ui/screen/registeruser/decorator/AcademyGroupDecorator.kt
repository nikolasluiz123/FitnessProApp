package br.com.fitnesspro.common.ui.screen.registeruser.decorator

import br.com.android.ui.compose.components.list.grouped.expandable.IBasicExpandableGroup
import br.com.fitnesspro.to.TOPersonAcademyTime

data class AcademyGroupDecorator(
    override val id: String,
    override val label: Int,
    override val value: String,
    override var isExpanded: Boolean,
    override val items: List<TOPersonAcademyTime>
): IBasicExpandableGroup<TOPersonAcademyTime>
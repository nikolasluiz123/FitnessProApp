package br.com.fitnesspro.ui.screen.registeruser.decorator

import br.com.fitnesspro.compose.components.list.expandable.IBasicExpandableGroup
import br.com.fitnesspro.to.TOPersonAcademyTime
import java.util.UUID

data class AcademyGroupDecorator(
    override val label: Int,
    override val value: String,
    override var isExpanded: Boolean,
    override val items: List<TOPersonAcademyTime>,
    override val id: String = UUID.randomUUID().toString()
): IBasicExpandableGroup<TOPersonAcademyTime>
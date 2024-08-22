package br.com.fitnesspro.ui.decorator

import br.com.fitnesspro.compose.components.list.expandable.IBasicExpandableGroup
import br.com.fitnesspro.model.Frequency

class AcademyFrequencyGroupDecorator(
    override val label: Int,
    override val value: String,
    override var isExpanded: Boolean,
    override val items: List<Frequency>
) : IBasicExpandableGroup<Frequency> {
}
package br.com.fitnesspro.pdf.generator.components

import br.com.fitnesspro.pdf.generator.common.IDrawable
import br.com.fitnesspro.pdf.generator.common.IPreparable

/**
 * Interface para representar um Componente exibido no relatório. Nesse contexto, um componente é
 * qualquer elemento visual que seja repetitivo, o mesmo conceito de componente encontrado em implementação
 * de telas e afins.
 *
 * @author Nikolas Luiz Schmitt
 */
interface IReportComponent<FILTER: Any>: IPreparable<FILTER>, IDrawable
package br.com.fitnesspro.pdf.generator.components

import br.com.fitnesspro.pdf.generator.common.IDrawable
import br.com.fitnesspro.pdf.generator.common.IPreparable

interface IReportComponent<FILTER: Any>: IPreparable<FILTER>, IDrawable
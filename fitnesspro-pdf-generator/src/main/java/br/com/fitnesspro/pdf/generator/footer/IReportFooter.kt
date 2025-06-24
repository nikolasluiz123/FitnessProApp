package br.com.fitnesspro.pdf.generator.footer

import br.com.fitnesspro.pdf.generator.common.IDrawable
import br.com.fitnesspro.pdf.generator.common.IPreparable

interface IReportFooter<FILTER: Any>: IPreparable<FILTER>, IDrawable
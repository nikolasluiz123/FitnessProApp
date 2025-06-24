package br.com.fitnesspro.pdf.generator.header

import br.com.fitnesspro.pdf.generator.common.IDrawable
import br.com.fitnesspro.pdf.generator.common.IPreparable

interface IReportHeader<FILTER: Any>: IPreparable<FILTER>, IDrawable
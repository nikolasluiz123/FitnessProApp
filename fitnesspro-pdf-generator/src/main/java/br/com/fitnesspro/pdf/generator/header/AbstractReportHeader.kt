package br.com.fitnesspro.pdf.generator.header

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument

abstract class AbstractReportHeader<FILTER: Any>: IReportHeader<FILTER> {

    protected lateinit var title: String
    protected lateinit var bitmap: Bitmap

    override fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, pageNumbers: Int) {
        val pageWidth = pageInfo.pageWidth
        val padding = 32f // margem lateral e superior

        // Configurações do bitmap (logo)
        val logoWidth = 80f
        val logoHeight = 80f
        val logoLeft = padding
        val logoTop = padding
        val logoRight = logoLeft + logoWidth
        val logoBottom = logoTop + logoHeight

        // Desenhar logo
        val logoRect = RectF(logoLeft, logoTop, logoRight, logoBottom)
        canvas.drawBitmap(bitmap, null, logoRect, null)

        // Configurações do texto (título)
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 18f * (canvas.density)
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // Medir altura do texto
        val textBounds = Rect()
        textPaint.getTextBounds(title, 0, title.length, textBounds)
        val textHeight = textBounds.height()

        // Posição do texto
        val textStartX = logoRight + 16f // espaçamento após logo
        val textStartY = logoTop + textHeight

        // Desenhar título
        canvas.drawText(title, textStartX, textStartY, textPaint)

        // Desenhar linha abaixo do título
        val lineStartX = textStartX
        val lineY = textStartY + 8f // pequeno espaçamento abaixo do texto
        val lineEndX = pageWidth - padding

        val linePaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 2f
            isAntiAlias = true
        }

        canvas.drawLine(lineStartX, lineY, lineEndX, lineY, linePaint)
    }

}
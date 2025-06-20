package br.com.fitnesspro.pdf.generator.extensions

import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import br.com.fitnesspro.pdf.generator.utils.Margins
import br.com.fitnesspro.pdf.generator.utils.Paints
import br.com.fitnesspro.pdf.generator.utils.Position
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

suspend fun Canvas.drawTableCellValue(value: String?, axisX: Float, axisY: Float, onMoveAxisY:() -> Unit) = withContext(IO) {
    drawText(value ?: "", axisX, axisY, Paints.defaultValuePaint)
    onMoveAxisY()
}

suspend fun Canvas.drawTextInPosition(text: String, position: Position, paint: TextPaint) = withContext(IO) {
    drawText(text, position.axisX, position.axisY, paint)
}

suspend fun Canvas.drawTextInPosition(
    text: String,
    position: Position,
    paint: TextPaint,
    columnWidth: Float
): Float = withContext(IO) {
    val lines = text.splitText(paint, columnWidth)
    var axisY = position.axisY

    lines.forEach { line ->
        drawText(line, position.axisX, axisY, paint)
        axisY += paint.textSize + Margins.MARGIN_4
    }

    return@withContext (axisY - position.axisY) - Margins.MARGIN_4
}

suspend fun Canvas.drawLineInPosition(startPosition: Position, endPosition: Position, paint: Paint) = withContext(IO) {
    drawLine(startPosition.axisX, startPosition.axisY, endPosition.axisX, endPosition.axisY, paint)
}
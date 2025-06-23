package br.com.fitnesspro.pdf.generator.extensions

import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint

fun String.splitText(paint: Paint, maxWidth: Float): List<String> {
    val words = this.split(" ")
    val currentLine = StringBuilder()
    val wrappedText = StringBuilder()

    for (word in words) {
        if (paint.measureText(currentLine.toString() + word) <= maxWidth) {
            currentLine.append(word).append(" ")
        } else {
            wrappedText.append(currentLine.toString()).append("\n")
            currentLine.setLength(0)
            currentLine.append(word).append(" ")
        }
    }
    wrappedText.append(currentLine.toString())
    return wrappedText.toString().split("\n")
}

fun String.createStaticLayout(
    paint: TextPaint,
    width: Int,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
    includePad: Boolean = true
): StaticLayout {
    return StaticLayout.Builder.obtain(this, 0, length, paint, width)
        .setAlignment(alignment)
        .setLineSpacing(0f, 1.0f)
        .setIncludePad(includePad)
        .build()
}
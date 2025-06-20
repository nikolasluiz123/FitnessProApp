package br.com.fitnesspro.pdf.generator.extensions

import android.graphics.Paint

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


package br.com.fitnesspro.pdf.generator.extensions

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint

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
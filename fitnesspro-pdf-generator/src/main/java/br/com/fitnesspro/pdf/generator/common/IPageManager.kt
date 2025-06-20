package br.com.fitnesspro.pdf.generator.common

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument

interface IPageManager {
    val canvas: Canvas
    val pageInfo: PdfDocument.PageInfo
    var currentY: Float

    suspend fun ensureSpace(currentY: Float, heightNeeded: Float): Float
    fun checkSpace(currentY: Float, heightNeeded: Float): Boolean
    fun finish()
}
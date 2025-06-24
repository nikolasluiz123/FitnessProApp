package br.com.fitnesspro.pdf.generator.common

interface IDrawable {
    suspend fun measureHeight(pageManager: IPageManager): Float
    suspend fun draw(pageManager: IPageManager, yStart: Float): Float
}
package br.com.fitnesspro.core.media3

import java.io.File

data class CompressionParams(
    val file: File,
    val targetMaxSizeMb: Int = 10,
    val resolutionHeight: Int? = null
)
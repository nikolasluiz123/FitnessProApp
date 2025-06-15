package br.com.fitnesspro.core.media3

import androidx.media3.common.util.UnstableApi
import androidx.media3.transformer.Composition
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import kotlinx.coroutines.CancellableContinuation
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@UnstableApi
class DefaultVideoCompressorTransformerListener(
    private val continuation: CancellableContinuation<File>,
    private val outputFile: File
): Transformer.Listener {
    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
        continuation.resume(outputFile)
    }

    override fun onError(composition: Composition, exportResult: ExportResult, exportException: ExportException) {
        outputFile.delete()
        continuation.resumeWithException(exportException)
    }
}
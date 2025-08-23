package br.com.fitnesspro.firebase.api.storage

import android.content.Context
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.firebase.api.R
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.tasks.await
import java.io.File

class StorageBucketService(private val context: Context) {

    suspend fun downloadAllByUrl(urls: List<String>, files: List<File>) {
        require(urls.size == files.size) { context.getString(R.string.storage_bucket_service_batch_download_invalid_params) }

        if (urls.size <= MAX_SEQUENTIAL_DOWNLOADS) {
            downloadAllSequentially(urls, files)
        } else {
            downloadAllParallel(urls, files)
        }
    }

    private suspend fun downloadAllSequentially(urls: List<String>, files: List<File>) {
        try {
            urls.zip(files).forEach { (url, file) ->
                downloadByUrl(url, file)
            }
        } catch (e: Exception) {
            FileUtils.deleteFiles(files)
            throw e
        }
    }

    private suspend fun downloadAllParallel(urls: List<String>, files: List<File>) {
        val semaphore = Semaphore(MAX_PARALLEL_DOWNLOADS)

        try {
            coroutineScope {
                urls.zip(files).map { (url, file) ->
                    async {
                        semaphore.withPermit {
                            downloadByUrl(url, file)
                        }
                    }
                }.awaitAll()
            }
        } catch (e: Exception) {
            FileUtils.deleteFiles(files)
            throw e
        }
    }

    suspend fun downloadByUrl(url: String, file: File) {
        Firebase.storage.getReferenceFromUrl(url).getFile(file).await()
    }

    companion object {
        private const val MAX_SEQUENTIAL_DOWNLOADS = 10
        private const val MAX_PARALLEL_DOWNLOADS = 3
    }
}
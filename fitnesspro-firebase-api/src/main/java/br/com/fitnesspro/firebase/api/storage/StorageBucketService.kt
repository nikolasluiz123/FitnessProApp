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

    suspend fun downloadAllByUrl(storageBucket: String, urls: List<String>, files: List<File>) {
        require(urls.size == files.size) { context.getString(R.string.storage_bucket_service_batch_download_invalid_params) }

        if (urls.size <= MAX_SEQUENTIAL_DOWNLOADS) {
            downloadAllSequentially(storageBucket, urls, files)
        } else {
            downloadAllParallel(storageBucket, urls, files)
        }
    }

    private suspend fun downloadAllSequentially(storageBucket: String, urls: List<String>, files: List<File>) {
        try {
            urls.zip(files).forEach { (url, file) ->
                downloadByUrl(storageBucket, url, file)
            }
        } catch (e: Exception) {
            FileUtils.deleteFiles(files)
            throw e
        }
    }

    private suspend fun downloadAllParallel(storageBucket: String, urls: List<String>, files: List<File>) {
        val semaphore = Semaphore(MAX_PARALLEL_DOWNLOADS)

        try {
            coroutineScope {
                urls.zip(files).map { (url, file) ->
                    async {
                        semaphore.withPermit {
                            downloadByUrl(storageBucket, url, file)
                        }
                    }
                }.awaitAll()
            }
        } catch (e: Exception) {
            FileUtils.deleteFiles(files)
            throw e
        }
    }

    suspend fun downloadByUrl(storageBucket: String, url: String, file: File) {
        val fullBucketName = "$STORAGE_PREFIX$storageBucket"

        Firebase.storage(fullBucketName)
            .getReference(url.substringAfterLast("/"))
            .getFile(file)
            .await()
    }

    companion object {
        private const val STORAGE_PREFIX = "gs://"
        private const val MAX_SEQUENTIAL_DOWNLOADS = 10
        private const val MAX_PARALLEL_DOWNLOADS = 3
    }
}
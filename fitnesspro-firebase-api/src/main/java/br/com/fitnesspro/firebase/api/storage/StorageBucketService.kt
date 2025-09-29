package br.com.fitnesspro.firebase.api.storage

import android.content.Context
import br.com.android.firebase.toolkit.storage.AbstractStorageBucketService

class StorageBucketService(context: Context): AbstractStorageBucketService(context) {

    override fun getMaximumParallelDownloads() = 3

    override fun getMaximumSequentialDownloads() = 10
}
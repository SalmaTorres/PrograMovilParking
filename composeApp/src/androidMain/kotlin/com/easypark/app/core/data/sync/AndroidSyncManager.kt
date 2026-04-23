package com.easypark.app.core.data.sync

import android.content.Context
import androidx.work.*
import com.easypark.app.core.work.OfflineSyncWorker

class AndroidSyncManager(private val context: Context) : SyncManager {
    override fun enqueueOfflineSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<OfflineSyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
    }
}
package com.easypark.app.core.lifecycle

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.easypark.app.core.domain.repository.AppEventRepository
import com.easypark.app.core.sync.EventSyncWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class AppLifecycleObserver(private val context: Context) : DefaultLifecycleObserver {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        logAndSyncEvent("APP_OPENED")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        logAndSyncEvent("APP_CLOSED")
    }

    private fun logAndSyncEvent(eventType: String) {
        coroutineScope.launch {
            try {
                val repository: AppEventRepository = GlobalContext.get().get()
                repository.logEvent(eventType, System.currentTimeMillis())

                val syncWorkRequest = OneTimeWorkRequestBuilder<EventSyncWorker>().build()
                WorkManager.getInstance(context).enqueue(syncWorkRequest)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

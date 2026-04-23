package com.easypark.app.core.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easypark.app.core.domain.repository.AppEventRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import org.koin.core.context.GlobalContext

class EventSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val repository: AppEventRepository = GlobalContext.get().get()
            val unsyncedEvents = repository.getUnsyncedEvents()

            if (unsyncedEvents.isEmpty()) {
                return Result.success()
            }

            Log.d("EventSyncWorker", "Sincronizando ${unsyncedEvents.size} eventos...")
            val database = FirebaseDatabase.getInstance().reference.child("app_events")

            val updates = mutableMapOf<String, Any>()
            for (event in unsyncedEvents) {
                val key = "${event.timestamp}_${event.id}"
                val eventData = mapOf(
                    "eventType" to event.eventType,
                    "timestamp" to event.timestamp
                )
                updates[key] = eventData
            }

            database.updateChildren(updates).await()

            repository.markAsSynced(unsyncedEvents)
            Log.d("EventSyncWorker", "Eventos sincronizados exitosamente en Firebase")

            Result.success()
        } catch (e: Exception) {
            Log.e("EventSyncWorker", "Error sincronizando eventos", e)
            Result.retry()
        }
    }
}

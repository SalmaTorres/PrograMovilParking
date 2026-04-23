package com.easypark.app.core.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easypark.app.core.data.db.AppDatabase
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class EventSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params), KoinComponent {

    override suspend fun doWork(): Result {
        val database: AppDatabase = get()

        try {
            // 1. Guardar el evento en Room si viene en los parámetros
            val eventType = inputData.getString("eventType")
            val timestamp = inputData.getLong("timestamp", 0L)
            
            if (eventType != null) {
                database.appEventDao().insert(
                    com.easypark.app.core.data.entity.AppEventEntity(
                        type = eventType,
                        timestamp = timestamp
                    )
                )
            }

            // 2. Subir todos los eventos pendientes a Firebase
            val events = database.appEventDao().getAll()
            val firebaseRef = FirebaseDatabase.getInstance().getReference("logs")

            events.forEach { event ->
                firebaseRef.push().setValue(event).await()
                database.appEventDao().deleteById(event.id)
            }
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }
}
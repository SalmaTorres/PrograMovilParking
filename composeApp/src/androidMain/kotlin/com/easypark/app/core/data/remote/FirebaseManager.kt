package com.easypark.app.core.data.remote

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

actual class FirebaseManager actual constructor() {
    private val database = FirebaseDatabase.getInstance().reference

    actual suspend fun saveData(path: String, value: String) {
        try {
            database.child(path).setValue(value).await()
            println("Firebase Android: Guardado exitoso en $path")
        } catch (e: Exception) {
            println("Firebase Android: Error - ${e.message}")
        }
    }
}
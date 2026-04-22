package com.easypark.app.core.data.remote

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

actual class FirebaseManager actual constructor() {
    private val database = FirebaseDatabase.getInstance().reference

    actual suspend fun saveData(path: String, value: String) {
        try {
            val jsonElement = Json.parseToJsonElement(value)
            val jsonObject = jsonElement.jsonObject

            val mapToSave = mutableMapOf<String, Any?>()
            jsonObject.forEach { (key, element) ->
                val content = element.toString().replace("\"", "")
                val doubleValue = content.toDoubleOrNull()
                val boolValue = content.toBooleanStrictOrNull()

                mapToSave[key] = doubleValue ?: boolValue ?: content
            }

            database.child(path).setValue(mapToSave).await()
        } catch (e: Exception) {
            database.child(path).setValue(value).await()
        }
    }

    actual fun observeData(path: String) = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.value?.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        database.child(path).addValueEventListener(listener)
        awaitClose { database.child(path).removeEventListener(listener) }
    }
}
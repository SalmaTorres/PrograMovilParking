package com.easypark.app.core.data.remote

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.json.*

actual class FirebaseManager actual constructor() {
    private val database = FirebaseDatabase.getInstance().reference

    actual suspend fun saveData(path: String, value: String) {
        try {
            val jsonElement = Json.parseToJsonElement(value)
            val dataToSave = jsonElement.toAny()
            database.child(path).setValue(dataToSave).await()
        } catch (e: Exception) {
            database.child(path).setValue(value).await()
        }
    }

    actual suspend fun getFCMToken(): String? {
        return try {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            null
        }
    }

    actual fun observeData(path: String) = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val jsonElement = snapshot.value.toJsonElement()
                    trySend(jsonElement.toString())
                } catch (e: Exception) {
                    trySend(null)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        database.child(path).addValueEventListener(listener)
        awaitClose { database.child(path).removeEventListener(listener) }
    }

    private fun JsonElement.toAny(): Any? {
        return when (this) {
            is JsonNull -> null
            is JsonPrimitive -> {
                if (this.isString) this.content
                else this.booleanOrNull ?: this.intOrNull ?: this.longOrNull ?: this.doubleOrNull ?: this.content
            }
            is JsonObject -> {
                this.mapValues { it.value.toAny() }
            }
            is JsonArray -> {
                this.map { it.toAny() }
            }
        }
    }

    private fun Any?.toJsonElement(): JsonElement {
        return when (this) {
            null -> JsonNull
            is String -> JsonPrimitive(this)
            is Number -> JsonPrimitive(this)
            is Boolean -> JsonPrimitive(this)
            is Map<*, *> -> {
                val jsonObject = buildJsonObject {
                    this@toJsonElement.forEach { (key, value) ->
                        if (key is String) {
                            put(key, value.toJsonElement())
                        }
                    }
                }
                jsonObject
            }
            is List<*> -> {
                val jsonArray = buildJsonArray {
                    this@toJsonElement.forEach { value ->
                        add(value.toJsonElement())
                    }
                }
                jsonArray
            }
            else -> JsonPrimitive(this.toString())
        }
    }
}
package com.easypark.app.core.data.remote

expect class FirebaseManager() {
    suspend fun saveData(path: String, value: String)
}
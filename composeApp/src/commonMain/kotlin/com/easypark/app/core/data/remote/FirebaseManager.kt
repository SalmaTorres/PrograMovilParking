package com.easypark.app.core.data.remote

expect class FirebaseManager() {
    suspend fun saveData(path: String, value: String)
    fun observeData(path: String): kotlinx.coroutines.flow.Flow<String?>
}
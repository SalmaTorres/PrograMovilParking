package com.easypark.app.core.data.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

actual class FirebaseManager actual constructor() {
    actual suspend fun saveData(path: String, value: String) {
        // Stub para iOS
    }

    actual fun observeData(path: String): Flow<String?> {
        return flowOf(null)
    }

    actual suspend fun getFCMToken(): String? {
        return null
    }
}

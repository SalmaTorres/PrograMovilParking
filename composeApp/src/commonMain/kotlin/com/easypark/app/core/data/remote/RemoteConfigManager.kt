package com.easypark.app.core.data.remote

expect class RemoteConfigManager() {
    suspend fun initialize()
    fun getBoolean(key: String): Boolean
    fun getString(key: String): String
}
package com.easypark.app.navigation

import androidx.navigation.NavType
import com.easypark.app.core.domain.model.UserModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

val UserModelType = object : NavType<UserModel>(isNullableAllowed = false) {
    override fun get(bundle: android.os.Bundle, key: String): UserModel? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): UserModel {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: android.os.Bundle, key: String, value: UserModel) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun serializeAsValue(value: UserModel): String {
        return Json.encodeToString(value)
    }
}
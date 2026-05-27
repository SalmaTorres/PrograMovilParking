package com.easypark.app.onboarding.data

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class OnboardingPreferences : KoinComponent {
    private val context: Context by inject()

    private val prefs by lazy {
        context.getSharedPreferences("easypark_onboarding", Context.MODE_PRIVATE)
    }

    actual fun isOnboardingCompleted(): Boolean =
        prefs.getBoolean(KEY_COMPLETED, false)

    actual fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_COMPLETED, completed).apply()
    }

    companion object {
        private const val KEY_COMPLETED = "onboarding_completed"
    }
}

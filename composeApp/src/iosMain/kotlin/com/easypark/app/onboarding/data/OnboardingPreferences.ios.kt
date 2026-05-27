package com.easypark.app.onboarding.data

import platform.Foundation.NSUserDefaults

actual class OnboardingPreferences {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun isOnboardingCompleted(): Boolean =
        defaults.boolForKey(KEY_COMPLETED)

    actual fun setOnboardingCompleted(completed: Boolean) {
        defaults.setBool(completed, forKey = KEY_COMPLETED)
        defaults.synchronize()
    }

    companion object {
        private const val KEY_COMPLETED = "onboarding_completed"
    }
}

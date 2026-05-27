package com.easypark.app.di

import com.easypark.app.core.data.db.AppDatabase
import com.easypark.app.core.data.db.createDatabase
import com.easypark.app.core.data.db.getDatabaseBuilder
import com.easypark.app.onboarding.data.OnboardingPreferences
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<AppDatabase> {
        createDatabase(getDatabaseBuilder())
    }
    single { OnboardingPreferences() }
}

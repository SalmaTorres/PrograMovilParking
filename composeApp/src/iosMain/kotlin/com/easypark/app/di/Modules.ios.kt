package com.easypark.app.di

import org.koin.core.module.Module
import org.koin.dsl.module
import com.easypark.app.core.data.db.getDatabaseBuilder

actual val platformModule: Module = module {
    single { getDatabaseBuilder() }
}
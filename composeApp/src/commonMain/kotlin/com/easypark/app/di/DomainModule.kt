package com.easypark.app.di

import com.easypark.app.spacemanagement.domain.usecase.GetSpaceDataUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetSpaceDataUseCase)
}
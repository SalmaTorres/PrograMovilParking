package com.easypark.app.di

import com.easypark.app.spacemanagement.data.SpaceManagementMockRepository
import com.easypark.app.spacemanagement.domain.repository.SpaceManagementRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::SpaceManagementMockRepository) { bind<SpaceManagementRepository>() }
}
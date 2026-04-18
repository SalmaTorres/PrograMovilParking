package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.UserEntity
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.model.UserType

fun UserModel.toEntity() = UserEntity(
    name,
    type = type.name,
    email,
    cellphone,
    password
)

fun UserEntity.toModel() =  UserModel(
    id,
    name,
    type = UserType.valueOf(type),
    email,
    cellphone,
    password
)
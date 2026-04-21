package com.easypark.app.core.data.mapper

import com.easypark.app.core.data.entity.UserEntity
import com.easypark.app.core.domain.model.UserModel
import com.easypark.app.core.domain.model.status.UserType

fun UserModel.toEntity() = UserEntity(
    name = name,
    email = email,
    cellphone = cellphone.toString(),
    password = password,
    type = type.name
)

fun UserEntity.toModel() =  UserModel(
    id = id,
    name = name,
    email = email,
    cellphone = cellphone.toIntOrNull() ?: 0,
    password = password,
    type = UserType.valueOf(type)
)
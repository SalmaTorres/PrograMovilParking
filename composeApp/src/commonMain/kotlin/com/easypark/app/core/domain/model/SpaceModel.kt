package com.easypark.app.core.domain.model

class SpaceModel (
    val id: Int,
    val parkingId: Int,
    val number: Int,
    val state: String = "LIBRE"
)
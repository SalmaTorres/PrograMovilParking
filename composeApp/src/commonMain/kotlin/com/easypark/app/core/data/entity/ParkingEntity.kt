package com.easypark.app.core.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parking")
data class ParkingEntity(
    @ColumnInfo(name = "ownerId")
    val ownerId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @ColumnInfo(name = "pricePerHour")
    val pricePerHour: Double,

    @ColumnInfo(name = "rating")
    val rating: Float? = 0.0f,

    @ColumnInfo(name = "totalSpaces")
    val totalSpaces: Int,

    @ColumnInfo(name = "schedule")
    val schedule: String? = "08:00 - 22:00",
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}
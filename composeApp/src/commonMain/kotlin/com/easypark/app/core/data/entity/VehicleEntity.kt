package com.easypark.app.core.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle")
data class VehicleEntity(
    @ColumnInfo(name = "driverId")
    val driverId: Int,

    @ColumnInfo(name = "plate")
    val plate: String,

    @ColumnInfo(name = "model")
    val model: String,

    @ColumnInfo(name = "color")
    val color: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}
package com.easypark.app.core.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservation")
data class ReservationEntity(
    @ColumnInfo(name = "spaceId")
    val spaceId: Int,

    @ColumnInfo(name = "driverId")
    val driverId: Int,

    @ColumnInfo(name = "startHour")
    val startHour: Long = 0,

    @ColumnInfo(name = "finalHour")
    val finalHour: Long = 0,

    @ColumnInfo(name = "totalPrice")
    val totalPrice: Float,

    @ColumnInfo(name = "state")
    val state: String,

    @ColumnInfo(name = "methodPay")
    val methodPay: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}
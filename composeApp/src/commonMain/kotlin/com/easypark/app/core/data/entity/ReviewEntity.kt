package com.easypark.app.core.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "review")
data class ReviewEntity(
    @ColumnInfo(name = "userId")
    val userId: Int,

    @ColumnInfo(name = "parkingId")
    val parkingId: Int,

    @ColumnInfo(name = "rating")
    val rating: Float
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}
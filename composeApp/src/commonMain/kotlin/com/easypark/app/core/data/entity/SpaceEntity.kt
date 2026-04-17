package com.easypark.app.core.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "space")
data class SpaceEntity (
    @ColumnInfo(name = "parkingId")
    val parkingId: Int,

    @ColumnInfo(name = "number")
    val number: Int,

    @ColumnInfo(name = "state")
    val state: String = "Libre",
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}
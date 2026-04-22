package com.easypark.app.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "form_drafts")
data class FormDraftEntity(
    @PrimaryKey
    val formId: String, // e.g., "register_vehicle"
    val data: String,   // JSON string with the form content
    val lastUpdated: Long
)

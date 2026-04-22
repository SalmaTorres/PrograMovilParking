package com.easypark.app.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easypark.app.core.data.entity.FormDraftEntity

@Dao
interface FormDraftDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: FormDraftEntity)

    @Query("SELECT * FROM form_drafts WHERE formId = :formId")
    suspend fun getDraft(formId: String): FormDraftEntity?

    @Query("DELETE FROM form_drafts WHERE formId = :formId")
    suspend fun deleteDraft(formId: String)
}

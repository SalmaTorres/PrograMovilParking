package com.easypark.app.core.data.repository

import com.easypark.app.core.data.dao.FormDraftDao
import com.easypark.app.core.data.entity.FormDraftEntity
import kotlinx.datetime.Clock

class FormDraftRepository(private val formDraftDao: FormDraftDao) {

    suspend fun saveDraft(formId: String, dataJson: String) {
        val draft = FormDraftEntity(
            formId = formId,
            data = dataJson,
            lastUpdated = Clock.System.now().toEpochMilliseconds()
        )
        formDraftDao.saveDraft(draft)
    }

    suspend fun getDraft(formId: String): String? {
        return formDraftDao.getDraft(formId)?.data
    }

    suspend fun clearDraft(formId: String) {
        formDraftDao.deleteDraft(formId)
    }
}

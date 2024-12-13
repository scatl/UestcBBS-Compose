package com.scatl.uestcbbs.compose.module.dayquestion

import com.scatl.uestcbbs.compose.base.BaseRepository
import com.scatl.uestcbbs.compose.datastore.DataStore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/9/25 14:55:49
 */
class DayQuestionRepository @Inject constructor(): BaseRepository() {

    suspend fun getDayQuestion() = legacyService.getDayQuestion()

    suspend fun confirmNextQuestion(): String? {
        val map = mutableMapOf<String, RequestBody>()
        map["formhash"] = DataStore.legacyForumHash.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["next"] = "true".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        return legacyService.confirmNextQuestion(map)
    }

    suspend fun submitQuestion(answer: String): String? {
        val map = mutableMapOf<String, RequestBody>()
        map["formhash"] = DataStore.legacyForumHash.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["answer"] = answer.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["submit"] = "true".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        return legacyService.submitQuestion(map)
    }

    suspend fun confirmFinishQuestion(): String? {
        val map = mutableMapOf<String, RequestBody>()
        map["formhash"] = DataStore.legacyForumHash.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["finish"] = "true".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        return legacyService.confirmFinishQuestion(map)
    }
}
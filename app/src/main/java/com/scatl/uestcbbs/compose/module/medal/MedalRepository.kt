package com.scatl.uestcbbs.compose.module.medal

import com.scatl.uestcbbs.compose.base.BaseRepository
import com.scatl.uestcbbs.compose.datastore.DataStore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/9/26 9:36:20
 */
class MedalRepository @Inject constructor(): BaseRepository() {

    suspend fun getMedalList() = legacyService.getMedalList()

    suspend fun buyMedal(medalId: String?): String? {
        val map = mutableMapOf<String, RequestBody>()
        map["formhash"] = DataStore.legacyForumHash.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["medalid"] = medalId.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["operation"] = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["medalsubmit"] = "true".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["handlekey"] = "medal".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        return legacyService.buyMedal(map)
    }
}
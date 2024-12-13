package com.scatl.uestcbbs.compose.module.magic

import com.scatl.uestcbbs.compose.base.BaseRepository
import com.scatl.uestcbbs.compose.datastore.DataStore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/9/26 9:36:20
 */
class MagicRepository @Inject constructor(): BaseRepository() {

    suspend fun getMagicList() = legacyService.getMagicList()

    suspend fun getMagicDetail(id: String?) = legacyService.getMagicDetail(id)

    suspend fun buyMagic(id: String, count: Int): String? {
        val map = mutableMapOf<String, RequestBody>()
        map["operation"] = "buy".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["operatesubmit"] = "true".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["magicnum"] = "$count".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["formhash"] = DataStore.legacyForumHash.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["mid"] = id.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        return legacyService.buyMagic(map)
    }

    suspend fun getMyMagic() = legacyService.getMyMagic()

    suspend fun beforeUseMagic(magicId: String?) = legacyService.beforeUseMagic(magicId)

    suspend fun confirmUseMagic(magicId: String?): String? {
        val map = mutableMapOf<String, RequestBody>()
        map["operation"] = "use".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["usesubmit"] = "yes".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["handlekey"] = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["formhash"] = DataStore.legacyForumHash.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["magicid"] = (magicId ?: "").toRequestBody("multipart/form-data".toMediaTypeOrNull())

        return legacyService.confirmUseMagic(map)
    }
}
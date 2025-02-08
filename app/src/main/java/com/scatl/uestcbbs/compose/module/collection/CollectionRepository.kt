package com.scatl.uestcbbs.compose.module.collection

import com.scatl.uestcbbs.compose.api.entity.request.DeleteFavoriteRequestEntity
import com.scatl.uestcbbs.compose.base.BaseRepository
import com.scatl.uestcbbs.compose.datastore.DataStore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/10/10 18:04:20
 */
class CollectionRepository @Inject constructor(): BaseRepository() {

    suspend fun getCollectionList(
        op: CollectionType,
        order: CollectionOrder,
        page: Int
    ) = legacyService.collectionList(op.type, order.order, page)

    suspend fun getCollectionDetail(
        id: Int,
        page: Int
    ) = legacyService.collectionDetail(id, page)

    suspend fun subscribeCollection(
        ctid: Int,
        subscribe: Boolean
    ) = legacyService.subscribeCollection(
        ctid = ctid,
        op = if (subscribe) "follow" else "unfo",
        formHash = DataStore.legacyForumHash
    )

    suspend fun createCollection(
        title: String,
        desc: String,
        keyword: String
    ): String? {
        val map = mutableMapOf<String, RequestBody>()
        map["title"] = title.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["desc"] = desc.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["keyword"] = keyword.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["submitcollection"] = "1".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["op"] = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["ctid"] = "0".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["formhash"] = DataStore.legacyForumHash.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["collectionsubmit"] = "submit".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        return legacyService.createCollection(map)
    }

    suspend fun deleteCollection(
        ctid: Int
    ) = legacyService.deleteCollection(ctid, DataStore.legacyForumHash)

    suspend fun removeCollectionPost(
        requestEntity: DeleteFavoriteRequestEntity,
    ) = userService.deleteFavorite(requestEntity)

    suspend fun getMyCollectionList(tid: String) = postService.getFavoriteStatus(tid)

    suspend fun confirmAddToCollection(tid: Int, ctid: Int, reason: String): String? {
        val map = mutableMapOf<String, RequestBody>()
        map["ctid"] = ctid.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["reason"] = reason.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["tids[]"] = tid.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["inajax"] = "0".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["handlekey"] = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["formhash"] = DataStore.legacyForumHash.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["addthread"] = "1".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["submitaddthread"] = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())

        return legacyService.confirmAddToCollection(map)
    }
}
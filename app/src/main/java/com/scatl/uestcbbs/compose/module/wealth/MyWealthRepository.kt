package com.scatl.uestcbbs.compose.module.wealth

import com.scatl.uestcbbs.compose.base.BaseRepository
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.toRequestBodyMap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

/**
 * Created by sca_tl at 2025/6/6 10:51:42
 */
class MyWealthRepository @Inject constructor(): BaseRepository() {

    suspend fun getCreditInfo() = legacyService.creditInfo("base")

    suspend fun getTransferInfo() = legacyService.creditInfo("transfer")

    suspend fun waterTransfer(waterCount: Int, userName: String, psw: String, message: String): String? {
        val map = mutableMapOf<String, RequestBody>()
        map["formhash"] = DataStore.legacyForumHash.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["transfersubmit"] = "true".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["handlekey"] = "transfercredit".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["transfersubmit_btn"] = "true".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["transferamount"] = waterCount.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["to"] = userName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["password"] = psw.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        map["transfermessage"] = message.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return legacyService.waterTransfer(map)
    }

    suspend fun getWealthHistory(page: Int, income: String, type: String): String? {
        val map = mutableMapOf<String, String>()
        map["exttype"] = type
        map["income"] = income
        map["starttime"] = ""
        map["endtime"] = ""
        map["optype"] = ""
        map["search"] = "true"
        map["op"] = "log"
        map["ac"] = "credit"
        map["mod"] = "spacecp"
        return legacyService.wealthHistory(page, map.toRequestBodyMap())
    }
}
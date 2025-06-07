package com.scatl.uestcbbs.compose.api.service

import com.scatl.uestcbbs.compose.api.entity.DarkRoomEntity
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.net.BaseApiResult
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PartMap
import retrofit2.http.Query

/**
 * Created by sca_tl at 2024/9/24 16:52:54
 */
interface LegacyService {

    // <editor-fold desc="水滴任务">

    @POST("home.php?mod=task&item=doing")
    suspend fun getDoingTask(): String?

    @FormUrlEncoded
    @POST("home.php?mod=task&do=delete")
    suspend fun deleteDoingTask(
        @Field("id") id: Int,
        @Field("formhash") formhash: String? = DataStore.legacyForumHash
    ): String?

    @POST("home.php?mod=task&item=new")
    suspend fun getNewTask(): String?

    @FormUrlEncoded
    @POST("home.php?mod=task&do=apply")
    suspend fun applyNewTask(
        @Field("id") id: Int
    ): String?

    @POST("home.php?mod=task&item=done")
    suspend fun getDoneTask(): String?

    @FormUrlEncoded
    @POST("home.php?mod=task&do=draw")
    suspend fun getTaskAward(
        @Field("id") id: Int
    ): String?

    @POST("home.php?mod=task&item=failed")
    suspend fun getFailedTask(): String?

    // </editor-fold>

    // <editor-fold desc="每日答题">

    @POST("plugin.php?id=ahome_dayquestion:pop")
    suspend fun getDayQuestion(): String?

    @Multipart
    @POST("plugin.php?id=ahome_dayquestion:pop")
    suspend fun confirmNextQuestion(
        @PartMap map: MutableMap<String, RequestBody>
    ): String?

    @Multipart
    @POST("plugin.php?id=ahome_dayquestion:pop")
    suspend fun submitQuestion(
        @PartMap map: MutableMap<String, RequestBody>
    ): String?

    @Multipart
    @POST("plugin.php?id=ahome_dayquestion:pop")
    suspend fun confirmFinishQuestion(
        @PartMap map: MutableMap<String, RequestBody>
    ): String?

    // </editor-fold>

    // <editor-fold desc="道具">

    @POST("home.php?mod=magic&action=shop")
    suspend fun getMagicList(): String?

    @FormUrlEncoded
    @POST("home.php?mod=magic&action=shop&operation=buy")
    suspend fun getMagicDetail(
        @Field("mid") mid: String?
    ): String?

    @Multipart
    @POST("home.php?mod=magic&action=shop&infloat=yes")
    suspend fun buyMagic(
        @PartMap map: MutableMap<String, RequestBody>
    ): String?

    @POST("home.php?mod=magic&action=mybox")
    suspend fun getMyMagic(): String?

    @FormUrlEncoded
    @POST("home.php?mod=magic&action=mybox&operation=use")
    suspend fun beforeUseMagic(
        @Field("magicid") magicid: String?
    ): String?

    @Multipart
    @POST("home.php?mod=magic&action=mybox&infloat=yes&inajax=1")
    suspend fun confirmUseMagic(
        @PartMap map: MutableMap<String, RequestBody>
    ): String?

    @FormUrlEncoded
    @POST("home.php?mod=magic&mid=repent&idtype=pid")
    suspend fun beforeUseRegretMagic(
        @Field("id") id: String?
    ): String?

    @Multipart
    @POST("home.php?mod=magic&action=mybox&infloat=yes&inajax=1")
    suspend fun confirmUseRegretMagic(
        @PartMap map: MutableMap<String, RequestBody>
    ): String?

    // </editor-fold>

    // <editor-fold desc="勋章">

    @POST("home.php?mod=medal")
    suspend fun getMedalList(): String?

    @Multipart
    @POST("home.php?mod=medal&action=apply&medalsubmit=yes")
    suspend fun buyMedal(
        @PartMap map: MutableMap<String, RequestBody>
    ): String?

    // </editor-fold>

    // <editor-fold desc="专辑">

    @FormUrlEncoded
    @POST("forum.php?mod=collection")
    suspend fun collectionList(
        @Field("op") op: String,
        @Field("order") order: String,
        @Field("page") page: Int
    ): String?

    @FormUrlEncoded
    @POST("forum.php?mod=collection&action=view")
    suspend fun collectionDetail(
        @Field("ctid") ctid: Int,
        @Field("page") page: Int
    ): String?

    @FormUrlEncoded
    @POST("forum.php?mod=collection&action=follow&inajax=1&ajaxtarget=undefined")
    suspend fun subscribeCollection(
        @Field("ctid") ctid: Int,
        @Field("op") op: String,
        @Field("formhash") formHash: String,
    ): String?

    @Multipart
    @POST("forum.php?mod=collection&action=edit")
    suspend fun createCollection(
        @PartMap map: MutableMap<String, RequestBody>
    ): String?

    @FormUrlEncoded
    @POST("forum.php?mod=collection&action=edit&op=remove")
    suspend fun deleteCollection(
        @Field("ctid") ctid: Int,
        @Field("formhash") formHash: String,
    ): String?

    @Multipart
    @POST("forum.php?mod=collection&action=edit&op=addthread")
    suspend fun confirmAddToCollection(
        @PartMap map: MutableMap<String, RequestBody>
    ): String?

    // </editor-fold>

    // <editor-fold desc="用户主页">

    @FormUrlEncoded
    @POST("home.php?mod=space")
    suspend fun userSpace(
        @Field("uid") uid: String,
        @Field("do") `do`: String,
    ): String?

    // </editor-fold>

    // <editor-fold desc="积分">

    @GET("https://bbs.uestc.edu.cn/home.php?mod=spacecp&ac=credit")
    suspend fun creditInfo(
        @Query("op") op: String
    ): String

    // </editor-fold>

    // <editor-fold desc="其它">

    @GET("forum.php")
    suspend fun homeData(): String

    @GET("forum.php?mod=misc&action=showdarkroom&ajaxdata=json")
    suspend fun darkRoomList(
        @Query("cid") cid: String,
        @Query("t") t: String,
    ): String

    // </editor-fold>
}
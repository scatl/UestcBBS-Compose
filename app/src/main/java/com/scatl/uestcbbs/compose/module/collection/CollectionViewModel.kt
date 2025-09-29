package com.scatl.uestcbbs.compose.module.collection

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.api.entity.CollectionEntity
import com.scatl.uestcbbs.compose.api.entity.request.DeleteFavoriteRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.FavoriteRequestEntity
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.module.collection.entity.CollectionDetailData
import com.scatl.uestcbbs.compose.module.collection.entity.CollectionDetailEntity
import com.scatl.uestcbbs.compose.module.collection.entity.CollectionListEntity
import com.scatl.uestcbbs.compose.net.onFailure
import com.scatl.uestcbbs.compose.net.onSuccess
import com.scatl.uestcbbs.compose.util.BBSLinkUtil
import com.scatl.uestcbbs.compose.util.LinkType
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jsoup.Jsoup
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/10/10 18:04:02
 */
@HiltViewModel
class CollectionViewModel @Inject constructor(
    val collectionRepository: CollectionRepository
): ViewModel() {

    private val collectionListDataMap = mutableMapOf<CollectionType, MutableStateFlow<UiState<SnapshotStateList<CollectionListEntity>>>>()
    private val collectionListPageMap = mutableMapOf<CollectionType, Int>()
    private val initializedPageMap = mutableStateMapOf<CollectionType, Boolean>()

    private val _collectionInfo = MutableStateFlow(UiState<CollectionDetailEntity>().init())
    val collectionInfo: StateFlow<UiState<CollectionDetailEntity>> = _collectionInfo

    private val _collectionDetailData = MutableStateFlow(UiState<SnapshotStateList<CollectionDetailData>>().init())
    val collectionDetailData: StateFlow<UiState<SnapshotStateList<CollectionDetailData>>> = _collectionDetailData
    private var currentCollectionDetailPage = 1

    private val _subscribeCollectionData = MutableStateFlow(UiState<Boolean?>().init())
    val subscribeCollectionData: StateFlow<UiState<Boolean?>> = _subscribeCollectionData

    private val _createCollectionData = MutableStateFlow(UiState<String?>().init())
    val createCollectionData: StateFlow<UiState<String?>> = _createCollectionData

    private val _deleteCollectionData = MutableStateFlow(UiState<Boolean?>().init())
    val deleteCollectionData: StateFlow<UiState<Boolean?>> = _deleteCollectionData

    private val _removeCollectionPostData = MutableStateFlow(UiState<Int?>().init())
    val removeCollectionPostData: StateFlow<UiState<Int?>> = _removeCollectionPostData

    private val _myCollectionListData = MutableStateFlow(UiState<SnapshotStateList<CollectionEntity>>().init())
    val myCollectionListData: StateFlow<UiState<SnapshotStateList<CollectionEntity>>> = _myCollectionListData

    private val _addToCollectionData = MutableStateFlow(UiState<Boolean?>().init())
    val addToCollectionData: StateFlow<UiState<Boolean?>> = _addToCollectionData

    fun resetCreateCollectionData() {
        _createCollectionData.value = UiState<String?>().apply { data = null }
    }

    fun resetDeleteCollectionData() {
        _deleteCollectionData.value = UiState<Boolean?>().apply { data = null }
    }

    fun resetRemoveCollectionPostData() {
        _removeCollectionPostData.value = UiState<Int?>().apply { data = null }
    }

    fun resetAddToCollectionData() {
        _addToCollectionData.value = UiState<Boolean?>().apply { data = null }
    }

    fun removePost(tid: Int) {
        _collectionDetailData.value.data?.removeIf {
            it is CollectionDetailData.Thread && it.data.topicId == tid
        }
    }

    fun isPageInitialized(type: CollectionType): Boolean {
        return initializedPageMap[type] ?: false
    }

    fun setPageInitialized(type: CollectionType) {
        initializedPageMap[type] = true
    }

    fun collectionListData(type: CollectionType): StateFlow<UiState<SnapshotStateList<CollectionListEntity>>> {
        return collectionListDataMap.getOrPut(type) {
            MutableStateFlow(UiState<SnapshotStateList<CollectionListEntity>>().init())
        }
    }

    fun getCollectionList(
        op: CollectionType,
        order: CollectionOrder,
        loadMore: Boolean,
        init: Boolean = false
    ) {
        val _collectionListData = collectionListDataMap[op]

        if (init) {
            collectionListPageMap[op] = 1
            _collectionListData?.value?.init()
        } else {
            if (loadMore) {
                collectionListPageMap[op] = (collectionListPageMap[op] ?: 1) + 1
                _collectionListData?.value?.loadingMore()
            } else {
                collectionListPageMap[op] = 1
                _collectionListData?.value?.refreshing()
            }
        }

        viewModelScope.launchSafety {
            val html = collectionRepository.getCollectionList(op, order, collectionListPageMap[op] ?: 1) ?: ""
            val hasMore = html.contains("下一页")

            val document = Jsoup.parse(html)
            val elements = document.select("div[class=clct_list cl]").select("div[class=xld xlda cl]")

            val collectionList = SnapshotStateList<CollectionListEntity>()
            for (i in elements.indices) {
                val collectionBean = CollectionListEntity()

                val dd1Data = elements[i].select("dd[class=m hm]")
                collectionBean.collectionLink = dd1Data.select("a").attr("href")

                val linkType = BBSLinkUtil.getLinkType(collectionBean.collectionLink)
                if (linkType is LinkType.Collection) {
                    collectionBean.collectionId = linkType.id
                }

                val dtData = elements[i].select("dt[class=xw1]")
                collectionBean.collectionTags = dtData.select("span[class=ctag_keyword]").select("a").eachText()
                collectionBean.createByMe = dtData.text().contains("我创建的")
                collectionBean.subscribeByMe = dtData.text().contains("我订阅的")
                if (dtData.select("a").size > 0) {
                    collectionBean.collectionTitle = dtData.select("a")[0].text()
                    collectionBean.hasUnreadPost = dtData.select("a")[0].attr("style").contains("red") && collectionBean.subscribeByMe
                }

                if (elements[i].select("dd").size > 1) {
                    val dd2Data = elements[i].select("dd")[1]

                    collectionBean.authorLink = dd2Data.select("p[class=xg1]").select("a").attr("href")

                    val linkTypeUser = BBSLinkUtil.getLinkType(collectionBean.authorLink)
                    if (linkTypeUser is LinkType.UserDetail) {
                        collectionBean.authorId = linkTypeUser.id
                    }

                    collectionBean.authorName = dd2Data.select("p[class=xg1]").select("a").text()
                    collectionBean.authorAvatar = collectionBean.authorId.toAvatarUrl()
                    collectionBean.latestUpdateDate = dd2Data.select("p[class=xg1]")[0].ownText().replace("创建, 最后更新 ", "")
                    collectionBean.collectionDsp = dd2Data.select("p").getOrNull(0)?.text()

                    when(dd1Data.select("span").text()) {
                        "主题" -> {
                            collectionBean.postCount = dd1Data.select("a").select("strong[class=xi2]").text()
                            val matcher = Pattern.compile("订阅 (\\d+), 评论 (\\d+)").matcher(dd2Data.select("p")[1].text())
                            if (matcher.find()) {
                                collectionBean.subscribeCount = matcher.group(1)
                                collectionBean.commentCount = matcher.group(2)
                            }
                        }
                        "评论" -> {
                            collectionBean.commentCount = dd1Data.select("a").select("strong[class=xi2]").text()
                            val matcher = Pattern.compile("订阅 (\\d+), 主题 (\\d+)").matcher(dd2Data.select("p")[1].text())
                            if (matcher.find()) {
                                collectionBean.subscribeCount = matcher.group(1)
                                collectionBean.postCount = matcher.group(2)
                            }
                        }
                        "订阅" -> {
                            collectionBean.subscribeCount = dd1Data.select("a").select("strong[class=xi2]").text()
                            val matcher = Pattern.compile("主题 (\\d+), 评论 (\\d+)").matcher(dd2Data.select("p")[1].text())
                            if (matcher.find()) {
                                collectionBean.postCount = matcher.group(1)
                                collectionBean.commentCount = matcher.group(2)
                            }
                        }
                    }
//                    if (dd2Data.select("p").size > 3) {
//                        collectionBean.latestPostTitle = dd2Data.select("p")[3].select("a").text()
//                        collectionBean.latestPostLink = dd2Data.select("p")[3].select("a").attr("href")
//                        collectionBean.latestPostId = BBSLinkUtil.getLinkInfo(collectionBean.latestPostLink).id
//                    }
                }

                collectionList.add(collectionBean)
            }

            if (collectionList.isEmpty()) {
                _collectionListData?.value?.empty()
            } else {
                if (loadMore) {
                    _collectionListData?.value?.success(
                        data = _collectionListData.value.data?.apply { addAll(collectionList) },
                        hasMore = hasMore
                    )
                } else {
                    _collectionListData?.value?.success(
                        data = collectionList,
                        hasMore = hasMore
                    )
                }
            }
        }.onCatch {
            collectionListPageMap[op] = (collectionListPageMap[op] ?: 2) - 1
            _collectionListData?.value?.error(
                errorData = it,
                initState = init
            )
        }
    }

    fun getCollectionDetail(
        id: Int,
        loadMore: Boolean,
        init: Boolean = false
    ) {
        if (init) {
            currentCollectionDetailPage = 1
            _collectionDetailData.value.init()
        } else {
            if (loadMore) {
                currentCollectionDetailPage += 1
                _collectionDetailData.value.loadingMore()
            } else {
                currentCollectionDetailPage = 1
                _collectionDetailData.value.refreshing()
            }
        }
        viewModelScope.launchSafety {
            val html = collectionRepository.getCollectionDetail(id, currentCollectionDetailPage) ?: ""

            val document = Jsoup.parse(html)
            val formHash = document.select("div[class=hdc]").select("div[class=wp]").select("div[class=cl]").select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
            DataStore.legacyForumHash = formHash

            val collectionDetailEntity = CollectionDetailEntity()
            val basicInfo = document.select("div[class=ct2 wp cl]").select("div[class=mn]").select("div[class=bm bml pbn]")
            val authorInfo = basicInfo.select("div[class=bm_c]").select("div[class=mbn cl]").select("p").last()
            collectionDetailEntity.apply {
                collectionTitle = basicInfo.select("h1[class=xs2 z]").text()
                collectionId = id
                subscribeCount = basicInfo.select("div[class=clct_flw]").select("strong[id=follownum_display]").text()
                isSubscribe = basicInfo.select("div[class=clct_flw]").select("a").text() == "取消订阅"
                collectionDsp = basicInfo.select("div[class=bm_c]").select("div").last()?.text()
                collectionTags = basicInfo.select("div[class=bm_c]").select("div[class=mbn cl]").select("p[class=mbn]").select("a").eachText()
                ratingScore = basicInfo.select("div[class=ptn pbn xg1 cl]").attr("title").toFloat()
                ratingTitle = basicInfo.select("div[class=ptn pbn xg1 cl]").text()

                threadCount = document.select("div[class=ct2 wp cl]").select("div[class=sd]").select("div[class=bm bml tns]").select("th").getOrNull(0)?.select("p")?.text()

                collectionAuthorLink = authorInfo?.select("a")?.first()?.attr("href")
                collectionAuthorId = (BBSLinkUtil.getLinkType(collectionAuthorLink) as? LinkType.UserDetail?)?.id
                collectionAuthorAvatar = collectionAuthorId.toAvatarUrl()
                collectionAuthorName = authorInfo?.select("a")?.first()?.text()

                authorInfo?.select("a")?.forEachIndexed { index, element ->
                    if (index > 0) {
                        collectionDetailEntity.maintainer.add(
                            CollectionDetailEntity.Maintainer().apply {
                                userName = element.text()
                                userId = (BBSLinkUtil.getLinkType(element.attr("href")) as? LinkType.UserDetail?)?.id
                                userAvatar = userId.toAvatarUrl()
                            }
                        )
                    }
                }

                val recentSubscriberData = document.select("div[class=ct2 wp cl]").select("div[class=sd]").select("div[class=bm]").getOrNull(0)?.select("div[class=bm_c]")?.select("ul[class=ml mls cl]")?.select("li")
                recentSubscriberData?.forEach {
                    val r = CollectionDetailEntity.RecentSubscriber().apply {
                        userName = it.select("p").text()
                        userId = (BBSLinkUtil.getLinkType(it.select("p").select("a").attr("href")) as? LinkType.UserDetail?)?.id
                        userAvatar = userId.toAvatarUrl()
                    }
                    collectionDetailEntity.recentSubscriber.add(r)
                }

                val authorOtherCollectionData = document.select("div[class=ct2 wp cl]").select("div[class=sd]").select("div[class=bm]").getOrNull(1)?.select("div[class=bm_c]")?.select("div[class=pbn]")
                authorOtherCollectionData?.forEach {
                    val r = CollectionDetailEntity.AuthorOtherCollection().apply {
                        name = it.select("a").text()
                        cid = (BBSLinkUtil.getLinkType(it.select("a").attr("href")) as? LinkType.Collection?)?.id
                    }
                    collectionDetailEntity.authorOtherCollection.add(r)
                }
            }

            val threadItems = mutableListOf<CollectionDetailEntity.ThreadItem>()
            val threads = document.select("div[class=ct2 wp cl]").select("div[class=mn]").select("div[class=tl bm]").select("div[class=bm_c]").select("tr")
            threads.forEach {
                val threadItem = CollectionDetailEntity.ThreadItem().apply {
                    topicTitle = it.select("th").select("a").attr("title")
                    topicLink = it.select("th").select("a").attr("href")
                    topicId = (BBSLinkUtil.getLinkType(topicLink) as? LinkType.ThreadDetail?)?.id
                    postDate = it.select("td[class=by]")[0].select("em[class=xi1]").text()
                    commentCount = it.select("td[class=num]").select("a").text()
                    viewCount = it.select("td[class=num]").select("em").text()

                    val threadAuthorInfo = it.select("td[class=by]")[0].select("cite")
                    authorLink = threadAuthorInfo.select("a").attr("href")
                    authorName = threadAuthorInfo.select("a").text()
                    authorId = (BBSLinkUtil.getLinkType(authorLink) as? LinkType.UserDetail?)?.id
                    authorAvatar = authorId.toAvatarUrl()

                    val lastPostData = it.select("td[class=by]")[1].select("cite")
                    lastPostAuthorLink = lastPostData.select("a").attr("href")
                    lastPostAuthorName = lastPostData.select("a").text()
                    lastPostAuthorId = (BBSLinkUtil.getLinkType(lastPostAuthorLink) as? LinkType.UserDetail?)?.id
                    lastPostAuthorAvatar = lastPostAuthorId.toAvatarUrl()
                    lastPostDate = it.select("td[class=by]")[1].select("em").text()
                }
                threadItems.add(threadItem)
            }

            val result = SnapshotStateList<CollectionDetailData>()
            val hasMore = html.contains("下一页")

            _collectionInfo.value.success(
                data = collectionDetailEntity
            )

            if (loadMore) {
                threadItems.forEach {
                    result.add(CollectionDetailData.Thread(it))
                }
                _collectionDetailData.value.success(
                    data = _collectionDetailData.value.data?.apply { addAll(result) },
                    hasMore = hasMore
                )
            } else {
                result.add(CollectionDetailData.Info(collectionDetailEntity))
                threadItems.forEach {
                    result.add(CollectionDetailData.Thread(it))
                }
                _collectionDetailData.value.success(
                    data = result,
                    hasMore = hasMore
                )
            }
        }.onCatch {
            currentCollectionDetailPage -= 1
            _collectionDetailData.value.error(
                errorData = it,
                initState = init
            )
        }
    }

    fun subscribeCollection(ctid: Int, subscribe: Boolean) {
        fun error(e: Throwable) {
            _subscribeCollectionData.value = UiState<Boolean?>().apply {
                isSuccess = false
                data = false
                errorData = e
            }
        }

        viewModelScope.launchSafety {
            val html = collectionRepository.subscribeCollection(ctid, subscribe) ?: ""
            if (html.contains("成功订阅")) {
                _subscribeCollectionData.value = UiState<Boolean?>().apply {
                    isSuccess = true
                    data = true
                }
            } else if (html.contains("取消订阅")) {
                _subscribeCollectionData.value = UiState<Boolean?>().apply {
                    isSuccess = true
                    data = false
                }
            } else {
                error(Throwable())
            }
        }.onCatch {
            error(it)
        }
    }

    fun createCollection(
        title: String,
        desc: String,
        keyword: String
    ) {
        fun error(e: Throwable) {
            _createCollectionData.value = UiState<String?>().apply {
                isSuccess = false
                errorData = e
                data = ""
            }
        }
        viewModelScope.launchSafety {
            val html = collectionRepository.createCollection(title, desc, keyword) ?: ""
            val info = Jsoup.parse(html).select("div[id=messagetext]").text()
            if (info.contains("新建淘专辑成功")) {
                _createCollectionData.value = UiState<String?>().apply {
                    isSuccess = true
                    data = ""
                }
            } else {
                error(Throwable(info))
            }
        }.onCatch {
            error(it)
        }
    }

    fun deleteCollection(ctid: Int) {
        fun error(e: Throwable) {
            _deleteCollectionData.value = UiState<Boolean?>().apply {
                isSuccess = false
                errorData = e
                data = false
            }
        }
        viewModelScope.launchSafety {
            val html = collectionRepository.deleteCollection(ctid) ?: ""
            val info = Jsoup.parse(html).select("div[id=messagetext]").text()
            if (info.contains("删除淘专辑成功")) {
                _deleteCollectionData.value = UiState<Boolean?>().apply {
                    isSuccess = true
                    data = true
                }
            } else {
                error(Throwable(info))
            }
        }.onCatch {
            error(it)
        }
    }

    fun removeCollectionPost(ctid: Int, tid: Int) {
        fun error(e: Throwable) {
            _removeCollectionPostData.value = UiState<Int?>().apply {
                isSuccess = false
                errorData = e
                data = tid
            }
        }
        viewModelScope.launchSafety {
            collectionRepository
                .removeCollectionPost(
                    DeleteFavoriteRequestEntity(
                        personalFavorite = false,
                        tidList = mutableListOf(tid.toIntOrElse()),
                        collectionId = ctid
                    )
                )
                .onSuccess {
                    _removeCollectionPostData.value = UiState<Int?>().apply {
                        isSuccess = true
                        data = tid
                    }
                }
                .onFailure {
                    error(Throwable(it.message))
                }
        }.onCatch {
            error(it)
        }
    }

    fun getMyCollectionList(tid: Int) {
        fun error(e: Throwable) {
            _myCollectionListData.value.error(
                errorData = e
            )
        }
        viewModelScope.launchSafety {
            collectionRepository
                .getMyCollectionList(tid.toString())
                .onSuccess {
                    if (it?.publicFavorites.isNullOrEmpty()) {
                        _myCollectionListData.value.success(
                            data = SnapshotStateList()
                        )
                    } else {
                        _myCollectionListData.value.success(
                            data = SnapshotStateList<CollectionEntity>().apply {
                                addAll(it?.publicFavorites!!)
                            }
                        )
                    }
                }
                .onFailure {
                    error(Throwable(it.message))
                }
        }.onCatch {
            error(it)
        }
    }

    fun addThreadToCollection(ctid: Int, tid: Int) {
        fun error(e: Throwable) {
            _addToCollectionData.value = UiState<Boolean?>().apply {
                isSuccess = false
                errorData = e
                data = null
            }
        }

        viewModelScope.launchSafety {
            collectionRepository
                .collectionService
                .addThreadToCollection(FavoriteRequestEntity(false, ctid), tid)
                .onSuccess {
                    _addToCollectionData.value = UiState<Boolean?>().apply {
                        isSuccess = true
                        data = true
                    }
                }
                .onFailure {
                    error(it)
                }
        }.onCatch {
            error(it)
        }
    }
}
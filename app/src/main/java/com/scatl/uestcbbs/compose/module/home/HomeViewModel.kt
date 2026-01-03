package com.scatl.uestcbbs.compose.module.home

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.api.entity.BingDailyPicEntity
import com.scatl.uestcbbs.compose.api.entity.CommonThreadEntity
import com.scatl.uestcbbs.compose.api.entity.IndexEntity
import com.scatl.uestcbbs.compose.api.entity.TopListEntity
import com.scatl.uestcbbs.compose.api.service.TopListService.IdListType
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.manager.CacheDataManager
import com.scatl.uestcbbs.compose.module.home.entity.NewThreadCacheEntity
import com.scatl.uestcbbs.compose.module.home.newpost.entity.NewThreadData
import com.scatl.uestcbbs.compose.module.home.newpost.entity.SiteStatusData
import com.scatl.uestcbbs.compose.net.onFailure
import com.scatl.uestcbbs.compose.net.onSuccess
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/8/8 17:39:01
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _newThreadData = MutableStateFlow(UiState<SnapshotStateList<NewThreadData>>().init())
    val newThreadData: StateFlow<UiState<SnapshotStateList<NewThreadData>>> = _newThreadData

    private val _topListDataMap = mutableMapOf<IdListType, MutableStateFlow<UiState<SnapshotStateList<CommonThreadEntity>>>>()

    private val initializedPageMap = mutableStateMapOf<IdListType, Boolean>()
    private val currentTopListPageMap = mutableMapOf<IdListType, Int>()
    private var currentNewThreadPage = 1

    fun isPageInitialized(idList: IdListType): Boolean {
        return initializedPageMap[idList] ?: false
    }

    fun setPageInitialized(idList: IdListType) {
        initializedPageMap[idList] = true
    }

    fun topListData(idList: IdListType): StateFlow<UiState<SnapshotStateList<CommonThreadEntity>>> {
        return _topListDataMap.getOrPut(idList) {
            MutableStateFlow(UiState<SnapshotStateList<CommonThreadEntity>>().init())
        }
    }

    fun getNewThreadData(loadMore: Boolean, init: Boolean = false) {
        if (init) {
            currentNewThreadPage = 1

            val cacheData = CacheDataManager.getNewThreadData()
            var finalData: SnapshotStateList<NewThreadData>? = null
            if (cacheData != null) {
                finalData = packNewThreadData(cacheData.bannerData, cacheData.homeData, cacheData.newPostData, cacheData.indexData)
            }

            if (finalData == null) {
                _newThreadData.value.init(finalData)
            } else {
                _newThreadData.value.init(finalData).refreshing()
            }
        } else {
            if (loadMore) {
                currentNewThreadPage += 1
                _newThreadData.value.loadingMore()
            } else {
                currentNewThreadPage = 1
                _newThreadData.value.refreshing()
            }
        }

        if (!loadMore) {
            //轮播图
            val bannerFlow = flow {
                emit(homeRepository.getBingDailyPic())
            }.catch {
                emit(BingDailyPicEntity())
            }

            //主页数据
            val homeFlow = flow {
                emit(homeRepository.getHomeData())
            }.catch {
                emit("")
            }

            //帖子列表
            val newPostFlow = flow {
                emit(homeRepository.getNewThreadList(currentNewThreadPage))
            }

            //帖子数等
            val indexFlow = flow {
                emit(homeRepository.getIndexData())
            }

            viewModelScope.launchSafety {
                combine(bannerFlow, homeFlow, newPostFlow, indexFlow) { bannerData, homeData, newPostData, indexData ->
                    val finalData = packNewThreadData(bannerData, homeData, newPostData.data, indexData.data)
                    CacheDataManager.saveNewThreadData(NewThreadCacheEntity(bannerData, homeData, newPostData.data, indexData.data))
                    _newThreadData.value.success(data = finalData, hasMore = true)
                }.collect()
            }.onCatch {
                _newThreadData.value.error(
                    errorData = it,
                    initState = init
                )
            }
        } else {
            viewModelScope.launchSafety {
                homeRepository
                    .getNewThreadList(currentNewThreadPage)
                    .onSuccess {
                        if (it != null && it.newThread.isNotNullAndEmpty()) {
                            val finalData = SnapshotStateList<NewThreadData>()
                            it.newThread.forEach {
                                finalData.add(NewThreadData.NewThread(data = it))
                            }
                            _newThreadData.value.data?.addAll(finalData)
                            _newThreadData.value.success(
                                data = _newThreadData.value.data,
                                hasMore = true
                            )
                        }
                    }
                    .onFailure {
                        currentNewThreadPage -= 1
                        _newThreadData.value.error(errorData = Throwable(it.message), initState = init)
                    }
            }.onCatch {
                currentNewThreadPage -= 1
                _newThreadData.value.error(errorData = it, initState = init)
            }
        }
    }

    private fun packNewThreadData(
        bannerData: BingDailyPicEntity,
        homeData: String,
        newPostData: TopListEntity?,
        indexData: IndexEntity?
    ): SnapshotStateList<NewThreadData> {
        val finalData = SnapshotStateList<NewThreadData>()

        if (bannerData.images.isNotNullAndEmpty()) {
            bannerData.images.forEach {
                it.fullThumbUrl = Constants.BING_BASE_URL + it.urlbase + "_1920x1080.jpg"
                it.fullOriginUrl = Constants.BING_BASE_URL + it.urlbase + "_UHD.jpg"
            }
            finalData.add(NewThreadData.Banner(data = bannerData.images))
        }

        if (indexData != null) {
            val siteStatusData = SiteStatusData(
                indexEntity = indexData,
            )
            siteStatusData.onlineNum = try {
                Jsoup
                    .parse(homeData)
                    .select("span[class=xs1]")
                    .select("strong")[0]
                    .text()
                    .toIntOrElse()
            } catch (e: Exception) {
                0
            }

            finalData.add(NewThreadData.SiteStatus(data = siteStatusData))
        }

        newPostData?.newThread?.forEach {
            finalData.add(NewThreadData.NewThread(data = it))
        }

        return finalData
    }

    fun getTopListData(
        idList: IdListType,
        loadMore: Boolean,
        init: Boolean = false
    ) {
        val _postData = _topListDataMap[idList]

        if (init) {
            currentTopListPageMap[idList] = 1
            _postData?.value?.init()
        } else {
            if (loadMore) {
                currentTopListPageMap[idList] = (currentTopListPageMap[idList] ?: 1) + 1
                _postData?.value?.loadingMore()
            } else {
                currentTopListPageMap[idList] = 1
                _postData?.value?.refreshing()
            }
        }

        viewModelScope.launchSafety {
            homeRepository
                .getTopList(idList, currentTopListPageMap[idList] ?: 1)
                .onSuccess {
                    val data = when(idList) {
                        IdListType.NEW_REPLY -> it?.newReply
                        IdListType.HOT_LIST -> it?.hotList
                        IdListType.DIGEST -> it?.digest
                        else -> it?.life
                    }
                    if (!data.isNullOrEmpty()) {
                        if (loadMore) {
                            _postData?.value?.success(
                                data = _postData.value.data?.apply { addAll(data) },
                                hasMore = true
                            )
                        } else {
                            _postData?.value?.success(
                                data = SnapshotStateList<CommonThreadEntity>().apply { addAll(data) },
                                hasMore = true
                            )
                        }
                    } else {
                        _postData?.value?.empty()
                    }
                }
                .onFailure {
                    currentTopListPageMap[idList] = (currentTopListPageMap[idList] ?: 2) - 1
                    _postData?.value?.error(
                        errorData = Throwable(it.message),
                        initState = init
                    )
                }
        }.onCatch {
            currentTopListPageMap[idList] = (currentTopListPageMap[idList] ?: 2) - 1
            _postData?.value?.error(
                errorData = it,
                initState = init
            )
        }
    }
}
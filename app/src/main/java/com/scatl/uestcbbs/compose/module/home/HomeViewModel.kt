package com.scatl.uestcbbs.compose.module.home

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.api.entity.BingDailyPicEntity
import com.scatl.uestcbbs.compose.api.entity.CommonThreadEntity
import com.scatl.uestcbbs.compose.api.service.TopListService.IdListType
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.module.home.newpost.entity.NewThreadData
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
            _newThreadData.value.init()
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
            val bannerFlow = flow {
                emit(homeRepository.getBingDailyPic())
            }.catch {
                emit(BingDailyPicEntity())
            }

            val newPostFlow = flow {
                emit(homeRepository.getNewThreadList(currentNewThreadPage))
            }
            val indexFlow = flow {
                emit(homeRepository.getIndexData())
            }

            viewModelScope.launchSafety {
                combine(bannerFlow, newPostFlow, indexFlow) { bannerData, newPostData, indexData ->

                    val finalData = SnapshotStateList<NewThreadData>()

                    if (bannerData.images.isNotNullAndEmpty()) {
                        bannerData.images.forEach {
                            it.fullThumbUrl = Constants.BING_BASE_URL + it.urlbase + "_1920x1080.jpg"
                            it.fullOriginUrl = Constants.BING_BASE_URL + it.urlbase + "_UHD.jpg"
                        }
                        finalData.add(NewThreadData.Banner(data = bannerData.images))
                    }

                    if (indexData.data != null) {
                        finalData.add(NewThreadData.SiteStatus(data = indexData.data))
                    }

                    newPostData.data?.newThread?.forEach {
                        finalData.add(NewThreadData.NewThread(data = it))
                    }

                    _newThreadData.value.success(
                        data = finalData,
                        hasMore = true
                    )
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
package com.scatl.uestcbbs.compose.module.forum

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import com.scatl.uestcbbs.compose.api.entity.ForumDetailEntity
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.manager.ForumCategoryManager
import com.scatl.uestcbbs.compose.module.forum.entity.ForumCategoryData
import com.scatl.uestcbbs.compose.module.forum.entity.ForumThreadsData
import com.scatl.uestcbbs.compose.net.onFailure
import com.scatl.uestcbbs.compose.net.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/7/11 15:24:05
 */
@HiltViewModel
class ForumViewModel @Inject constructor(
    val forumRepository: ForumRepository
) : ViewModel() {

    private val _forumCategoryData = MutableStateFlow(UiState<SnapshotStateList<ForumCategoryData>>().init())
    val forumCategoryData: StateFlow<UiState<SnapshotStateList<ForumCategoryData>>> = _forumCategoryData

    private val _forumDetailData = MutableStateFlow(UiState<ForumDetailEntity>().init())
    val forumDetailData: StateFlow<UiState<ForumDetailEntity>> = _forumDetailData

    private val _forumChildren = MutableStateFlow<MutableList<ForumDetailEntity>>(mutableListOf())
    val forumChildren: StateFlow<MutableList<ForumDetailEntity>> = _forumChildren

    private val initializedChildMap = mutableStateMapOf<String, Boolean>()
    private val forumThreadsDataMap = mutableMapOf<String, MutableStateFlow<UiState<SnapshotStateList<ForumThreadsData>>>>()
    private val forumThreadsPageMap = mutableMapOf<String, Int>()

    fun isChildInitialized(fid: String): Boolean {
        return initializedChildMap[fid] ?: false
    }

    fun setChildInitialized(fid: String) {
        initializedChildMap[fid] = true
    }

    fun resetForumDetail() {
        _forumDetailData.value.init()
    }

    fun getForumCategory(init: Boolean = false) {
        if (init) {
            _forumCategoryData.value.init()
        } else {
            _forumCategoryData.value.refreshing()
        }

        viewModelScope.launchSafety {
            forumRepository
                .getForumListData()
                .onSuccess {
                    if (it != null && it.forumList.isNotNullAndEmpty()) {
                        val finalData = SnapshotStateList<ForumCategoryData>()
                        it.forumList.forEach {
                            finalData.add(ForumCategoryData.Title(data = it))
                            finalData.add(ForumCategoryData.Children(data = it))
                        }
                        _forumCategoryData.value.success(
                            data = finalData,
                            hasMore = false
                        )
                        ForumCategoryManager.initData(it.forumList)
                    } else {
                        _forumCategoryData.value.empty()
                    }
                }
                .onFailure {
                    _forumCategoryData.value.error(Throwable(it.message))
                }
        }.onCatch {
            _forumCategoryData.value.error(it)
        }
    }

    fun getForumDetail(init: Boolean, fid: Int) {
        if (init) {
            _forumDetailData.value.init()
        } else {
            _forumDetailData.value.refreshing()
        }

        viewModelScope.launchSafety {
            forumRepository
                .getForumDetail(fid)
                .onSuccess {
                    _forumDetailData.value.success(data = it)
                }
                .onFailure {
                    _forumDetailData.value.error(Throwable(it.message))
                }
        }.onCatch {
            _forumDetailData.value.error(it)
        }
    }

    fun initForumChildren(data: ForumDetailEntity) {
        if (_forumChildren.value.isEmpty()) {
            _forumChildren.value = mutableListOf<ForumDetailEntity>().apply {
                add(ForumDetailEntity(
                    fid = data.fid,
                    name = data.name,
                    threads = data.threads,
                    posts = data.posts,
                    todayPosts = data.todayPosts,
                    yesterdayPosts = data.yesterdayPosts
                ))
                addAll(data.children ?: emptyList())
            }
        }
    }

    fun forumThreadsData(fid: String): StateFlow<UiState<SnapshotStateList<ForumThreadsData>>> {
        return forumThreadsDataMap.getOrPut(fid) {
            MutableStateFlow(UiState<SnapshotStateList<ForumThreadsData>>().init())
        }
    }

    fun getForumThreadsData(
        fid: String,
        typeId: Int?,
        sortBy: String,
        loadMore: Boolean,
        init: Boolean = false
    ) {
        val _forumThreadsData = forumThreadsDataMap[fid]

        if (init) {
            forumThreadsPageMap[fid] = 1
            _forumThreadsData?.value?.init()
        } else {
            if (loadMore) {
                forumThreadsPageMap[fid] = (forumThreadsPageMap[fid] ?: 1) + 1
                _forumThreadsData?.value?.loadingMore()
            } else {
                forumThreadsPageMap[fid] = 1
                _forumThreadsData?.value?.refreshing()
            }
        }

        viewModelScope.launchSafety {
            forumRepository
                .getForumThreads(
                    forumId = fid,
                    typeId,
                    sortBy = sortBy,
                    details = "1",
                    page = forumThreadsPageMap[fid] ?: 1
                )
                .onSuccess {
                    if (it?.forum != null) {
                        val finaleData = SnapshotStateList<ForumThreadsData>()
                        val hasMore = (it.total ?: 0) > (it.page ?: 0) * (it.pageSize ?: 0)

                        if (loadMore) {
                            it.rows?.forEach { row ->
                                finaleData.add(ForumThreadsData.Thread(
                                    data = row
                                ))
                            }
                            _forumThreadsData?.value?.success(
                                data = _forumThreadsData.value.data?.apply { addAll(finaleData) },
                                hasMore = hasMore
                            )
                        } else {
                            finaleData.add(ForumThreadsData.Filter(
                                data = it.forum
                            ))

                            val sticks = it.rows?.filter { row -> (row.displayOrder ?: 0) > 0 }
                            if (!sticks.isNullOrEmpty()) {
                                finaleData.add(ForumThreadsData.Stick(data = sticks))
                            }

                            finaleData.add(ForumThreadsData.ThreadTitle(data = ""))

                            it.rows
                                ?.filter { row ->
                                    (row.displayOrder ?: 0) <= 0
                                }
                                ?.forEach { row ->
                                    finaleData.add(ForumThreadsData.Thread(data = row))
                                }

                            _forumThreadsData?.value?.success(
                                data = finaleData,
                                hasMore = hasMore
                            )
                        }
                    } else {
                        _forumThreadsData?.value?.empty()
                    }
                }
                .onFailure {
                    forumThreadsPageMap[fid] = (forumThreadsPageMap[fid] ?: 2) - 1
                    _forumThreadsData?.value?.error(
                        errorData = Throwable(it.message),
                        initState = init
                    )
                }
        }.onCatch {
            forumThreadsPageMap[fid] = (forumThreadsPageMap[fid] ?: 2) - 1
            _forumThreadsData?.value?.error(
                errorData = it,
                initState = init
            )
        }
    }
}
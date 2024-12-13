package com.scatl.uestcbbs.compose.module.search

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.api.entity.CommonThreadEntity
import com.scatl.uestcbbs.compose.api.entity.message.ChatDetailEntity
import com.scatl.uestcbbs.compose.api.entity.search.SearchSummaryEntity
import com.scatl.uestcbbs.compose.api.entity.search.SearchThreadEntity
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.safeSubList
import com.scatl.uestcbbs.compose.module.search.entity.SearchSummaryData
import com.scatl.uestcbbs.compose.net.onFailure
import com.scatl.uestcbbs.compose.net.onSuccess
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/9/12 17:23:22
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    val searchRepository: SearchRepository
): ViewModel() {

    private val _searchSummaryData = MutableStateFlow(UiState<SnapshotStateList<SearchSummaryData>>().success())
    val searchSummaryData: StateFlow<UiState<SnapshotStateList<SearchSummaryData>>> = _searchSummaryData

    private val _searchThreadData = MutableStateFlow(UiState<SnapshotStateList<CommonThreadEntity>>().init())
    val searchThreadData: StateFlow<UiState<SnapshotStateList<CommonThreadEntity>>> = _searchThreadData

    private val _searchUserData = MutableStateFlow(UiState<SnapshotStateList<SearchSummaryEntity.User>>().init())
    val searchUserData: StateFlow<UiState<SnapshotStateList<SearchSummaryEntity.User>>> = _searchUserData

    private var currentSearchThreadPage = 1
    private var currentSearchUserPage = 1

    fun getSearchSummary(keyword: String) {
        viewModelScope.launchSafety {
            searchRepository
                .searchSummary(keyword)
                .onSuccess {
                    if (it != null
                        && (it.users.isNotNullAndEmpty()
                                || it.threads.isNotNullAndEmpty()
                                || it.tidMatch != null
                                || it.uidMatch != null)
                        ) {
                        val finalData = SnapshotStateList<SearchSummaryData>()

                        if (it.threads == null) {
                            it.threads = mutableListOf()
                        }
                        var threadCount = it.threadCount ?: 0
                        if (it.tidMatch != null) {
                            threadCount += 1
                            it.threads?.add(0, it.tidMatch.apply { tidMatch = true })
                        }
                        if (it.threads.isNotNullAndEmpty()) {
                            finalData.add(SearchSummaryData.Title(data = SearchSummaryData.TitleData(
                                count = threadCount,
                                type = SearchSummaryData.SearchSummaryDataType.THREAD
                            )))
                            it.threads?.safeSubList(0, 5)?.forEach { thread ->
                                finalData.add(SearchSummaryData.Thread(data = thread))
                            }
                        }

                        if (it.users == null) {
                            it.users = mutableListOf()
                        }
                        var userCount = it.userCount ?: 0
                        if (it.uidMatch != null) {
                            userCount += 1
                            it.users?.add(0, it.uidMatch.apply { uidMatch = true })
                        }
                        if (it.users.isNotNullAndEmpty()) {
                            finalData.add(SearchSummaryData.Title(data = SearchSummaryData.TitleData(
                                count = userCount,
                                type = SearchSummaryData.SearchSummaryDataType.User
                            )))
                            it.users?.safeSubList(0, 5)?.forEach { user ->
                                finalData.add(SearchSummaryData.User(data = user))
                            }
                        }

                        _searchSummaryData.value = _searchSummaryData.value.success(
                            data = finalData,
                            hasMore = false
                        )
                    } else {
                        _searchSummaryData.value = _searchSummaryData.value.empty().apply {
                            data = null
                        }
                    }
                }
        }.onCatch {

        }
    }

    fun searchThread(
        keyword: String,
        loadMore: Boolean,
        init: Boolean = false
    ) {
        if (init) {
            currentSearchThreadPage = 1
            _searchThreadData.value.init()
        } else {
            if (loadMore) {
                currentSearchThreadPage += 1
                _searchThreadData.value.loadingMore()
            } else {
                currentSearchThreadPage = 1
                _searchThreadData.value.refreshing()
            }
        }

        viewModelScope.launchSafety {
            searchRepository
                .searchThread(keyword, currentSearchThreadPage)
                .onSuccess {
                    if (it != null && it.rows.isNotNullAndEmpty()) {
                        val finalData = SnapshotStateList<CommonThreadEntity>().apply {
                            addAll(it.rows)
                        }
                        val hasMore = (it.total ?: 0) > (it.page ?: 0) * (it.pageSize ?: 0)

                        _searchThreadData.value.success(
                            data = if (loadMore) _searchThreadData.value.data?.apply { addAll(finalData) } else finalData,
                            hasMore = hasMore
                        )
                    } else {
                        _searchThreadData.value.empty()
                    }
                }
                .onFailure {
                    _searchThreadData.value.error(Throwable(it.message))
                }
        }.onCatch {
            _searchThreadData.value.error(it)
        }
    }

    fun searchUser(
        keyword: String,
        loadMore: Boolean,
        init: Boolean = false
    ) {
        if (init) {
            currentSearchUserPage = 1
            _searchUserData.value.init()
        } else {
            if (loadMore) {
                currentSearchUserPage += 1
                _searchUserData.value.loadingMore()
            } else {
                currentSearchUserPage = 1
                _searchUserData.value.refreshing()
            }
        }

        viewModelScope.launchSafety {
            searchRepository
                .searchUser(keyword, currentSearchUserPage)
                .onSuccess {
                    if (it != null && it.rows.isNotNullAndEmpty()) {
                        val finalData = SnapshotStateList<SearchSummaryEntity.User>().apply {
                            addAll(it.rows)
                        }
                        val hasMore = (it.total ?: 0) > (it.page ?: 0) * (it.pageSize ?: 0)

                        _searchUserData.value.success(
                            data = if (loadMore) _searchUserData.value.data?.apply { addAll(finalData) } else finalData,
                            hasMore = hasMore
                        )
                    } else {
                        _searchUserData.value.empty()
                    }
                }
                .onFailure {
                    _searchUserData.value.error(Throwable(it.message))
                }
        }.onCatch {
            _searchUserData.value.error(it)
        }
    }

    fun clearFullSearchResult() {
        _searchThreadData.value.init(data = null)
        _searchUserData.value.init(data = null)
    }
}
package com.scatl.uestcbbs.compose.module.post.commentrate

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.api.entity.PostCommentAndRateEntity
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.module.post.PostRepository
import com.scatl.uestcbbs.compose.net.onFailure
import com.scatl.uestcbbs.compose.net.onSuccess
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/8/29 15:09:47
 */
@HiltViewModel
class CommentAndRateViewModel @Inject constructor(
    val postRepository: PostRepository
) : ViewModel() {

    private val _commentData = MutableStateFlow(UiState<SnapshotStateList<PostCommentAndRateEntity.Comment>>().init())
    val commentData: StateFlow<UiState<SnapshotStateList<PostCommentAndRateEntity.Comment>>> = _commentData

    private val _rateData = MutableStateFlow(UiState<SnapshotStateList<PostCommentAndRateEntity.Rate>>().init())
    val rateData: StateFlow<UiState<SnapshotStateList<PostCommentAndRateEntity.Rate>>> = _rateData

    private var currentCommentDataPage: Int = 1

    fun getComment(
        threadId: String,
        pid: String,
        loadMore: Boolean,
        init: Boolean = false
    ) {
        if (init) {
            currentCommentDataPage = 1
            _commentData.value.init()
        } else {
            if (loadMore) {
                currentCommentDataPage += 1
                _commentData.value.loadingMore()
            } else {
                currentCommentDataPage = 1
                _commentData.value.refreshing()
            }
        }
        viewModelScope.launchSafety {
            postRepository
                .getCommentAndRate(
                    threadId = threadId,
                    commentPids = pid,
                    ratePids = "",
                    page = currentCommentDataPage
                )
                .onSuccess {
                    if (it != null && it.getOrDefault(pid, null)?.comments.isNotNullAndEmpty()) {
                        val finalData = SnapshotStateList<PostCommentAndRateEntity.Comment>().apply {
                            addAll(it[pid]?.comments!!)
                        }

                        _commentData.value.success(
                            data = if (loadMore) _commentData.value.data?.apply { addAll(finalData) } else finalData,
                            hasMore = (it[pid]?.commentPages ?: 0) > currentCommentDataPage
                        )
                    } else {
                        _commentData.value.empty()
                    }
                }
                .onFailure {
                    _commentData.value.error(Throwable(it.message))
                }
        }.onCatch {
            _commentData.value.error(it)
        }
    }

    fun getRate(
        threadId: String,
        pid: String,
    ) {
        viewModelScope.launchSafety {
            postRepository
                .getCommentAndRate(
                    threadId = threadId,
                    commentPids = "",
                    ratePids = pid,
                    page = 1
                )
                .onSuccess {
                    if (it != null && it.getOrDefault(pid, null)?.rates.isNotNullAndEmpty()) {
                        _rateData.value.success(
                            data = SnapshotStateList<PostCommentAndRateEntity.Rate>().apply {
                                addAll(it[pid]?.rates!!)
                            },
                            hasMore = false
                        )
                    } else {
                        _rateData.value.empty()
                    }
                }
                .onFailure {
                    _rateData.value.error(Throwable(it.message))
                }
        }.onCatch {
            _rateData.value.error(it)
        }
    }
}
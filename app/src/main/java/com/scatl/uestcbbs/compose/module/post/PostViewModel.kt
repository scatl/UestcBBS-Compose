package com.scatl.uestcbbs.compose.module.post

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.App
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.FavoriteStatusEntity
import com.scatl.uestcbbs.compose.api.entity.PostCommentAndRateEntity
import com.scatl.uestcbbs.compose.api.entity.PostSupportEntity
import com.scatl.uestcbbs.compose.api.entity.RateOptionsEntity
import com.scatl.uestcbbs.compose.api.entity.ThreadDetailEntity
import com.scatl.uestcbbs.compose.api.entity.ThreadPollEntity
import com.scatl.uestcbbs.compose.api.entity.ThreadReplyEntity
import com.scatl.uestcbbs.compose.api.entity.ThreadSupportEntity
import com.scatl.uestcbbs.compose.api.entity.request.CreatePostRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.DeleteFavoriteRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.FavoriteRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.PostReportRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.RateRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.VoteRequestEntity
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.db.entity.BrowsingHistoryDBEntity
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.safeSubstring
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.manager.AccountManager
import com.scatl.uestcbbs.compose.manager.ThreadSnapshotManager
import com.scatl.uestcbbs.compose.net.BaseApiResult
import com.scatl.uestcbbs.compose.net.onFailure
import com.scatl.uestcbbs.compose.net.onSuccess
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/7/15 10:04:42
 */
@HiltViewModel
class PostViewModel @Inject constructor(
    val postRepository: PostRepository
) : ViewModel() {

    private val _threadDetailData = MutableStateFlow(UiState<ThreadDetailEntity>().init())
    val threadDetailData: StateFlow<UiState<ThreadDetailEntity>> = _threadDetailData

    private val _repliesData = MutableStateFlow(UiState<SnapshotStateList<ThreadDetailEntity.Row>>().init())
    val repliesData: StateFlow<UiState<SnapshotStateList<ThreadDetailEntity.Row>>> = _repliesData

    private val _voteData = MutableStateFlow(UiState<ThreadPollEntity>().init())
    val voteData: StateFlow<UiState<ThreadPollEntity>> = _voteData

    private val _commentData = MutableStateFlow(UiState<Int?>().init())
    val commentData: StateFlow<UiState<Int?>> = _commentData

    private val _confirmDeleteData = MutableStateFlow(UiState<String?>().init())
    val confirmDeleteData: StateFlow<UiState<String?>> = _confirmDeleteData

    private val _currentReplyCount = MutableStateFlow(0)
    val currentReplyCount: StateFlow<Int> = _currentReplyCount

    private val _createReplyData = MutableStateFlow(UiState<ThreadReplyEntity>().init())
    val createReplyData: StateFlow<UiState<ThreadReplyEntity>> = _createReplyData

    private val _threadFavoriteData = MutableStateFlow(UiState<Boolean?>().init())
    val threadFavoriteData: StateFlow<UiState<Boolean?>> = _threadFavoriteData

    private val _rateOptionData = MutableStateFlow(UiState<RateOptionsEntity?>().init())
    val rateOptionData: StateFlow<UiState<RateOptionsEntity?>> = _rateOptionData

    private val _rateData = MutableStateFlow(UiState<Boolean?>().init())
    val rateData: StateFlow<UiState<Boolean?>> = _rateData

    private val _reportData = MutableStateFlow(UiState<Boolean?>().init())
    val reportData: StateFlow<UiState<Boolean?>> = _reportData

    private val _supportDataMap = mutableMapOf<String, MutableStateFlow<UiState<PostSupportEntity>>>()
    private val _supportThreadDataMap = mutableMapOf<String, MutableStateFlow<UiState<ThreadSupportEntity>>>()
    private var currentThreadDetailPage: Int = 1
    private var firstVisibleIndex = 0

    fun setFirstVisibleIndex(index: Int) {
        firstVisibleIndex = index
    }

    fun webViewReady() {
        //_threadDetailData.value.initState = false
    }

    fun insertComment(pid: Int, msg: String) {
        val signInAccount = AccountManager.getSignedInAccount()
        val insertEntity = PostCommentAndRateEntity.Comment().apply {
            author = signInAccount?.name
            authorId = signInAccount?.uid.toIntOrElse()
            dateline = (System.currentTimeMillis() / 1000).toInt()
            id = dateline
            message = msg
        }

        val data = if (_threadDetailData.value.data?.rows?.getOrNull(0)?.postId == pid) {
            _threadDetailData.value.data?.rows?.getOrNull(0)?.commentAndRate
        } else {
            _repliesData.value.data?.find { it.postId == pid }?.commentAndRate
        }
        data?.comments?.add(0, insertEntity)
        data?.commentTotal = data?.commentTotal.toIntOrElse() + 1
    }

    fun supportData(pid: String): StateFlow<UiState<PostSupportEntity>> {
        return _supportDataMap.getOrPut(pid) {
            MutableStateFlow(UiState<PostSupportEntity>().init())
        }
    }

    fun threadSupportData(pid: String): StateFlow<UiState<ThreadSupportEntity>> {
        return _supportThreadDataMap.getOrPut(pid) {
            MutableStateFlow(UiState<ThreadSupportEntity>().init())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getThreadDetail(threadId: String) {
        viewModelScope.launchSafety {
            val combineFlow = flow {
                val detailData = async {
                    postRepository.getThreadDetail(
                        threadId = threadId,
                        order = "2",
                        page = currentThreadDetailPage
                    )
                }.await()

                val favoriteData = async {
                    try {
                        postRepository.getFavoriteStatus(threadId)
                    } catch (e: Exception) {
                        BaseApiResult(data = FavoriteStatusEntity())
                    }
                }.await()

                emit(Pair(detailData, favoriteData))
            }

            combineFlow.flatMapConcat { (detailData, favoriteData) ->
                detailData.data?.thread?.isFavorite = favoriteData.data?.isPersonalFavorite == true

                var commentPids = ""
                var ratePids = ""

                detailData.data?.rows?.forEach {
                    if (it.hasComment == true) {
                        commentPids = commentPids.plus(it.postId).plus(",")
                    }
                    if (it.hasRate == true) {
                        ratePids = ratePids.plus(it.postId).plus(",")
                    }
                }

                commentPids = commentPids.dropLast(1)
                ratePids = ratePids.dropLast(1)

                if (commentPids.isEmpty() && ratePids.isEmpty()) {
                    flow {
                        emit(BaseApiResult(data = null))
                    }
                } else {
                    flow {
                        emit(
                            postRepository.getCommentAndRate(
                                threadId = detailData.data?.thread?.threadId.toString(),
                                commentPids = commentPids,
                                ratePids = ratePids,
                                page = 1
                            )
                        )
                    }.catch {
                        emit(BaseApiResult(data = null))
                    }
                }.map { commentAndRate ->
                    Pair(detailData, commentAndRate)
                }
            }.collect { (detailData, commentAndRate) ->
                if (detailData.data == null) {
                    _threadDetailData.value.error(errorData = Throwable(detailData.message))
                } else {
                    if (commentAndRate.data.isNotNullAndEmpty()) {
                        detailData.data.rows?.forEach {
                            it.commentAndRate = commentAndRate.data.getOrDefault(it.postId.toString(), PostCommentAndRateEntity())
                        }
                    }

                    detailData.data.thread?.postId = detailData.data.rows?.getOrNull(0)?.postId
                    _threadDetailData.value.success(data = detailData.data)

                    _repliesData.value.success(
                        data = detailData.data.rows?.filter { it.isFirst != 1 }?.toMutableStateList(),
                        hasMore = (detailData.data.total ?: 0) > currentThreadDetailPage * (detailData.data.pageSize ?: 0)
                    )

                    _currentReplyCount.value = _threadDetailData.value.data?.total.toIntOrElse() - 1

                    val dbEntity = BrowsingHistoryDBEntity(
                        threadId = detailData.data.thread?.threadId.toString(),
                        forumId = detailData.data.forum?.fid.toString(),
                        forumName = detailData.data.forum?.name.toString(),
                        authorId = detailData.data.thread?.authorId.toString(),
                        authorName = detailData.data.thread?.author.toString(),
                        subject = detailData.data.thread?.subject.toString(),
                        summary = detailData.data.rows?.getOrNull(0)?.message?.safeSubstring(0, 200),
                        dateLine = detailData.data.thread?.dateline.toIntOrElse(),
                        lastBrowserDate = System.currentTimeMillis()
                    )
                    postRepository.dataBase.getBrowsingHistoryDao().insert(dbEntity)
                }
            }
        }
        .onCatch {
            _threadDetailData.value.error(it)
        }
    }

    fun getThreadDetailByPid(pid: String) {
        viewModelScope.launchSafety {
            postRepository
                .findThreadId(pid)
                .onSuccess {
                    getThreadDetail(it?.threadId.toString())
                }
                .onFailure {
                    _threadDetailData.value.error(Throwable(it.message))
                }
        }.onCatch {
            _threadDetailData.value.error(it)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getReplies(
        loadMore: Boolean,
        init: Boolean = false,
        threadId: String,
        authorId: String? = null,
        order: String
    ) {
        if (init) {
            currentThreadDetailPage = 1
            _repliesData.value.init()
        } else {
            if (loadMore) {
                currentThreadDetailPage += 1
                _repliesData.value.loadingMore()
            } else {
                currentThreadDetailPage = 1
                _repliesData.value.refreshing()
            }
        }

        viewModelScope.launchSafety {
            val detailFlow = flow {
                emit(
                    postRepository.getThreadDetail(threadId, authorId, order, currentThreadDetailPage)
                )
            }

            detailFlow.flatMapConcat { detailData ->
                var commentPids = ""
                var ratePids = ""

                detailData.data?.rows?.forEach {
                    if (it.hasComment == true) {
                        commentPids = commentPids.plus(it.postId).plus(",")
                    }
                    if (it.hasRate == true) {
                        ratePids = ratePids.plus(it.postId).plus(",")
                    }
                }

                commentPids = commentPids.dropLast(1)
                ratePids = ratePids.dropLast(1)

                if (commentPids.isEmpty() && ratePids.isEmpty()) {
                    flow {
                        emit(BaseApiResult(data = null))
                    }
                } else {
                    flow {
                        emit(
                            postRepository.getCommentAndRate(
                                threadId = detailData.data?.thread?.threadId.toString(),
                                commentPids = commentPids,
                                ratePids = ratePids,
                                page = 1
                            )
                        )
                    }.catch {
                        emit(BaseApiResult(data = null))
                    }
                }.map { commentAndRate ->
                    Pair(detailData, commentAndRate)
                }
            }.collect { (detailData, commentAndRate) ->
                if (detailData.data != null && detailData.data.rows.isNotNullAndEmpty()) {
                    if (commentAndRate.data.isNotNullAndEmpty()) {
                        detailData.data.rows?.forEach {
                            it.commentAndRate = commentAndRate.data.getOrDefault(it.postId.toString(), PostCommentAndRateEntity())
                        }
                    }

                    val finalData = SnapshotStateList<ThreadDetailEntity.Row>().apply {
                        addAll(detailData.data.rows!!.filter { it.isFirst != 1 })
                        if (_repliesData.value.data != null) {
                            removeAll(this.intersect(_repliesData.value.data!!))
                        }
                    }
                    val hasMore = (detailData.data.total.toIntOrElse() - 1) > currentThreadDetailPage * detailData.data.pageSize.toIntOrElse()

                    if (loadMore) {
                        _repliesData.value.success(
                            data = _repliesData.value.data?.apply { addAll(finalData) },
                            hasMore = hasMore
                        )
                        _threadDetailData.value.data?.rows?.addAll(finalData)
                    } else {
                        _repliesData.value.success(
                            data = finalData,
                            hasMore = hasMore
                        )
                        _threadDetailData.value.data?.rows = detailData.data.rows
                    }

                    _currentReplyCount.value = detailData.data.total.toIntOrElse() - 1
                } else {
                    _repliesData.value.empty()
                }
            }
        }.onCatch {
            currentThreadDetailPage -= 1
            _repliesData.value.error(errorData = it)
        }
    }

    fun vote(requestEntity: VoteRequestEntity) {
        viewModelScope.launchSafety {
            postRepository
                .vote(requestEntity)
                .onSuccess {
                    _voteData.value.success(data = it)
                }
                .onFailure {
                    _voteData.value.error(Throwable(it.message))
                }
        }.onCatch {
            _voteData.value.error(it)
        }
    }

    fun support(tid: String, pid: String, support: Boolean) {
        viewModelScope.launchSafety {
            postRepository
                .support(tid, pid, support)
                .onSuccess {
                    _supportDataMap[pid]?.value?.success(
                        data = PostSupportEntity(
                            success = it,
                            support = support
                        )
                    )
                }
                .onFailure {
                    _supportDataMap[pid]?.value?.error(Throwable(it.message))
                }
        }.onCatch {
            _supportDataMap[pid]?.value?.error(it)
        }
    }

    fun supportThread(tid: String, pid: String, support: Boolean) {
        viewModelScope.launchSafety {
            postRepository
                .supportThread(tid, support)
                .onSuccess {
                    _supportThreadDataMap[pid]?.value = UiState<ThreadSupportEntity>().apply {
                        data = ThreadSupportEntity(
                            type = it,
                            support = support
                        )
                    }
                }
                .onFailure {
                    _supportThreadDataMap[pid]?.value?.error(Throwable(it.message))
                }
        }.onCatch {
            _supportThreadDataMap[pid]?.value?.error(it)
        }
    }

    fun deletePost(pid: String?, tid: String?) {
        fun error(e: Throwable) {
            _confirmDeleteData.value = UiState<String?>().apply {
                errorData = e
                isSuccess = false
                data = ""
            }
        }

        viewModelScope.launchSafety {
            val html = postRepository.beforeUseRegretMagic("$pid:$tid") ?: ""
            if (html.contains("您尚未登录")) {
                error(Throwable(ContextCompat.getString(App.context, R.string.need_login_dsp)))
            } else if (html.contains("messagetext")) {
                val document = Jsoup.parse(html)
                val info = document.select("div[id=messagetext]").select("p")[0].text()
                error(Throwable(info))
            } else if (html.contains("购买道具")) {
                error(Throwable("您需要先购买悔悟卡才能删除帖子"))
            } else if (html.contains("使用道具")) {
                val formHash = Jsoup.parse(html).select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
                DataStore.legacyForumHash = formHash

                val result = postRepository.confirmUseRegretMagic(pid, tid) ?: ""
                if (result.contains("已删除")) {
                    _confirmDeleteData.value = UiState<String?>().apply {
                        data = "您操作的帖子已删除"
                        isSuccess = true
                    }
                } else if (result.contains("小时内")) {
                    error(Throwable("抱歉，24小时内本道具使用次数达到上限"))
                } else {
                    error(Throwable("未知错误"))
                }
            }
        }.onCatch {
            error(it)
        }
    }

    fun resetDeleteData() {
        _confirmDeleteData.value = UiState<String?>().init().apply {
            data = null
        }
    }

    fun resetCommentData() {
        _commentData.value = UiState<Int?>().init().apply {
            data = null
        }
    }

    suspend fun getSnapshotData(snapshot: String?): Boolean {
        return withContext(Dispatchers.IO) {
            val entity = ThreadSnapshotManager.getSnapshot(snapshot)
            if (entity != null) {
                _threadDetailData.value.success(data = entity)
                _repliesData.value.success(
                    data = _threadDetailData.value.data?.rows?.filter { it.isFirst != 1 }?.toMutableStateList(),
                    hasMore = false
                )
                true
            } else {
                false
            }
        }
    }

    fun comment(postId: Int, threadId: Int, message: String) {
        fun error(e: Throwable) {
            _commentData.value = UiState<Int?>().error(
                errorData = e
            ).apply {
                data = -1
            }
        }

        viewModelScope.launchSafety {
            postRepository
                .comment(postId, threadId, message)
                .onSuccess {
                    if (it == true) {
                        _commentData.value = UiState<Int?>().success(
                            data = postId
                        )
                    } else {
                        error(Throwable("点评失败"))
                    }
                }
                .onFailure {
                    error(Throwable(it.message))
                }
        }.onCatch {
            error(it)
        }
    }

    fun reply(requestEntity: CreatePostRequestEntity) {
        fun error(e: Throwable) {
            _createReplyData.value = UiState<ThreadReplyEntity>().error().apply {
                data = ThreadReplyEntity()
                errorData = e
            }
        }

        viewModelScope.launchSafety {
            postRepository
                .reply(requestEntity)
                .onSuccess { r ->
                    _createReplyData.value = UiState<ThreadReplyEntity>().success(data = r)

                    val latest = postRepository.getThreadDetail(
                        threadId = requestEntity.threadId.toString(),
                        order = "1",
                        page = 1
                    ).data?.rows?.find { it.postId == r?.postId }

                    if (latest != null) {
                        _repliesData.value.data?.add(firstVisibleIndex + 1, latest)
                        _threadDetailData.value.data?.rows?.add(firstVisibleIndex + 1, latest)
                        _currentReplyCount.value += 1
                    }
                }
                .onFailure {
                    error(Throwable(it.message))
                }
        }.onCatch {
            error(it)
        }
    }

    fun favorite(tid: String) {
        fun error(e: Throwable) {
            _threadFavoriteData.value = UiState<Boolean?>().error().apply {
                isSuccess = false
                data = false
                errorData = e
            }
        }

        viewModelScope.launchSafety {
            postRepository
                .favorite(FavoriteRequestEntity(true), tid)
                .onSuccess {
                    _threadFavoriteData.value = UiState<Boolean?>().apply {
                        isSuccess = true
                        data = true
                    }
                }
                .onFailure {
                    error(Throwable(it.message))
                }
        }.onCatch {
            error(it)
        }
    }

    fun delFavorite(tid: String) {
        fun error(e: Throwable) {
            _threadFavoriteData.value = UiState<Boolean?>().error().apply {
                isSuccess = false
                data = false
                errorData = e
            }
        }

        viewModelScope.launchSafety {
            postRepository
                .deleteFavorite(DeleteFavoriteRequestEntity(true, mutableListOf(tid.toIntOrElse())))
                .onSuccess {
                    _threadFavoriteData.value = UiState<Boolean?>().apply {
                        isSuccess = true
                        data = false
                    }
                }
                .onFailure {
                    error(Throwable(it.message))
                }
        }.onCatch {
            error(it)
        }
    }

    fun getRateOption(tid: String) {
        fun error(e: Throwable) {
            _rateOptionData.value = UiState<RateOptionsEntity?>().error().apply {
                isSuccess = false
                data = null
                errorData = e
            }
        }
        viewModelScope.launchSafety {
            postRepository
                .getRateOptions(tid)
                .onSuccess {
                    _rateOptionData.value = UiState<RateOptionsEntity?>().success().apply {
                        data = it
                    }
                }
                .onFailure {
                    error(Throwable(it.message))
                }
        }.onCatch {
            error(it)
        }
    }

    fun rate(pid: String, credit: Int) {
        fun error(e: Throwable) {
            _rateData.value = UiState<Boolean?>().error().apply {
                isSuccess = false
                data = false
                errorData = e
            }
        }
        viewModelScope.launchSafety {
            postRepository
                .rate(
                    pid = pid,
                    requestEntity = RateRequestEntity(
                        reason = "水滴操作",
                        credits = RateRequestEntity.Credit(
                            water = credit
                        )
                    )
                )
                .onSuccess {
                    _rateData.value = UiState<Boolean?>().success().apply {
                        isSuccess = true
                        data = true
                    }
                }
                .onFailure {
                    error(Throwable(it.message))
                }
        }.onCatch {
            error(it)
        }
    }

    fun report(pid: Int, fid: Int, message: String) {
        fun error(e: Throwable) {
            _reportData.value = UiState<Boolean?>().error().apply {
                isSuccess = false
                data = false
                errorData = e
            }
        }
        viewModelScope.launchSafety {
            postRepository
                .report(
                    requestEntity = PostReportRequestEntity(
                        pid = pid,
                        fid = fid,
                        message = message
                    )
                )
                .onSuccess {
                    _reportData.value = UiState<Boolean?>().success().apply {
                        isSuccess = true
                        data = true
                    }
                }
                .onFailure {
                    error(Throwable(it.message))
                }
        }.onCatch {
            error(it)
        }
    }
}

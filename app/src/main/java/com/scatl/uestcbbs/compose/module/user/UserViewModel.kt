package com.scatl.uestcbbs.compose.module.user

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.App
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import com.scatl.uestcbbs.compose.api.entity.CommonThreadEntity
import com.scatl.uestcbbs.compose.api.entity.MessageBoardEntity
import com.scatl.uestcbbs.compose.api.entity.request.AddFriendRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.EditFriendRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.UserAddCommentRequestEntity
import com.scatl.uestcbbs.compose.api.entity.request.UserEditCommentRequestEntity
import com.scatl.uestcbbs.compose.api.entity.user.AddFriendEntity
import com.scatl.uestcbbs.compose.api.entity.user.DeleteFriendEntity
import com.scatl.uestcbbs.compose.api.entity.user.EditCommentEntity
import com.scatl.uestcbbs.compose.api.entity.user.EditFriendEntity
import com.scatl.uestcbbs.compose.api.entity.user.UserCollectionEntity
import com.scatl.uestcbbs.compose.api.entity.user.UserFriendEntity
import com.scatl.uestcbbs.compose.api.entity.user.UserProfileEntity
import com.scatl.uestcbbs.compose.api.service.UserService
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.manager.AccountManager
import com.scatl.uestcbbs.compose.module.user.post.UserPostData
import com.scatl.uestcbbs.compose.net.onFailure
import com.scatl.uestcbbs.compose.net.onSuccess
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Created by sca_tl at 2024/7/15 10:04:42
 */
@HiltViewModel(assistedFactory = UserViewModel.Factory::class)
class UserViewModel @AssistedInject constructor(
    @Assisted("userId") private val userId: String?,
    @Assisted("userName") private val userName: String?,
    val userRepository: UserRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("userId") userId: String?,
            @Assisted("userName") userName: String?
        ): UserViewModel
    }

    private val _detailData = MutableStateFlow(UiState<UserProfileEntity>().init())
    val detailData: StateFlow<UiState<UserProfileEntity>> = _detailData

    private val _collectionData = MutableStateFlow(UiState<SnapshotStateList<UserCollectionEntity.Row>>().init())
    val collectionData: StateFlow<UiState<SnapshotStateList<UserCollectionEntity.Row>>> = _collectionData

    private val _friendData = MutableStateFlow(UiState<SnapshotStateList<UserFriendEntity.Row>>().init())
    val friendData: StateFlow<UiState<SnapshotStateList<UserFriendEntity.Row>>> = _friendData

    private val _messageBoardData = MutableStateFlow(UiState<SnapshotStateList<MessageBoardEntity.Row>>().init())
    val messageBoardData: StateFlow<UiState<SnapshotStateList<MessageBoardEntity.Row>>> = _messageBoardData

    private val _userPostData = MutableStateFlow(UiState<SnapshotStateList<UserPostData>>().init())
    val userPostData: StateFlow<UiState<SnapshotStateList<UserPostData>>> = _userPostData

    private val _addFriendData = MutableStateFlow(UiState<AddFriendEntity>().init())
    val addFriendData: StateFlow<UiState<AddFriendEntity>> = _addFriendData

    private val _editFriendData = MutableStateFlow(UiState<EditFriendEntity>().init())
    val editFriendData: StateFlow<UiState<EditFriendEntity>> = _editFriendData

    private val _deleteFriendData = MutableStateFlow(UiState<DeleteFriendEntity>().init())
    val deleteFriendData: StateFlow<UiState<DeleteFriendEntity>> = _deleteFriendData

    private val _addCommentData = MutableStateFlow(UiState<MessageBoardEntity.Row?>().init())
    val addCommentData: StateFlow<UiState<MessageBoardEntity.Row?>> = _addCommentData

    private val _editCommentData = MutableStateFlow(UiState<EditCommentEntity?>().init())
    val editCommentData: StateFlow<UiState<EditCommentEntity?>> = _editCommentData

    private val _deleteCommentData = MutableStateFlow(UiState<String?>().init())
    val deleteCommentData: StateFlow<UiState<String?>> = _deleteCommentData

    private val initializedPageMap = mutableStateMapOf<UserProfilePage, Boolean>()
    private var currentUserPostPage = 1
    private var currentUserCollectionPage = 1
    private var currentUserMsgBoardPage = 1
    private var currentUserFriendsPage = 1
    private var removeLog = false

    private val additional: String?
        get() = if (removeLog) {
            "removevlog"
        } else {
            null
        }

    fun isPageInitialized(page: UserProfilePage): Boolean {
        return initializedPageMap[page] ?: false
    }

    fun setPageInitialized(page: UserProfilePage) {
        initializedPageMap[page] = true
    }

    init {
        viewModelScope.launchSafety {
            delay(250)
            getUserProfile()
        }
    }

    fun getUserProfile() {
        viewModelScope.launchSafety {
            if (!userId.isNullOrEmpty()) {
                userRepository.getUserProfileByUid(
                    id = userId,
                    additional = additional
                )
            } else {
                userRepository.getUserProfileByName(
                    name = userName,
                    additional = additional
                )
            }.onSuccess {
                _detailData.value.success(data = it)
            }.onFailure {
                val msg = if (userId == "0" || userName == "匿名") {
                    ContextCompat.getString(App.context, R.string.user_detail_anonymous_error)
                } else {
                    it.message
                }
                _detailData.value.error(Throwable(msg))
            }
        }
        .onCatch {
            _detailData.value.error(it)
        }
    }

    fun getUserPosts(
        postType: UserService.UserPostType,
        userSummary: String?,
        visitors: String?,
        loadMore: Boolean,
        init: Boolean
    ) {
        if (init) {
            currentUserPostPage = 1
            _userPostData.value.init()
        } else {
            if (loadMore) {
                currentUserPostPage += 1
                _userPostData.value.loadingMore()
            } else {
                currentUserPostPage = 1
                _userPostData.value.refreshing()
            }
        }

        viewModelScope.launchSafety {
            if (postType == UserService.UserPostType.FEATURED) {
                userRepository
                    .getUserDigests(
                        author = userName,
                        page = currentUserPostPage,
                        additional = additional
                    )
            } else {
                if (!userId.isNullOrEmpty()) {
                    userRepository
                        .getUserPostsByUid(
                            postType = postType,
                            uid = userId,
                            userSummary = userSummary,
                            visitors = visitors,
                            page = currentUserPostPage,
                            additional = additional
                        )
                } else {
                    userRepository
                        .getUserPostsByName(
                            postType = postType,
                            name = userName,
                            userSummary = userSummary,
                            visitors = visitors,
                            page = currentUserPostPage,
                            additional = additional
                        )
                }
            }.onSuccess {
                if (it != null && it.rows.isNotNullAndEmpty()) {
                    val finalData = SnapshotStateList<UserPostData>()
                    val hasMore = (it.total ?: 0) > (it.page ?: 0) * (it.pageSize ?: 0)

                    when(postType) {
                        UserService.UserPostType.THREAD -> {
                            it.rows.forEach { row ->
                                finalData.add(UserPostData.Thread(data = row))
                            }
                        }
                        UserService.UserPostType.FEATURED -> {
                            it.rows.forEach { row ->
                                finalData.add(UserPostData.Featured(data = row))
                            }
                        }
                        UserService.UserPostType.REPLY,
                        UserService.UserPostType.COMMENT -> {
                            val groupedByThreadId: Map<Int, List<CommonThreadEntity>> = it.rows.groupBy { it.threadId!! }
                            groupedByThreadId.forEach { (_, value) ->
                                if (value.isNotEmpty()) {
                                    val item = value[0].apply {
                                        summaries = mutableListOf()
                                    }
                                    value.forEach {
                                        item.summaries?.add(it.summary ?: "")
                                    }

                                    if (postType == UserService.UserPostType.REPLY) {
                                        finalData.add(UserPostData.Reply(data = item))
                                    } else {
                                        finalData.add(UserPostData.Comment(data = item))
                                    }
                                }
                            }
                        }
                    }

                    if (!loadMore) {
                        _userPostData.value.success(
                            data = finalData,
                            hasMore = hasMore
                        )
                    } else {
                        _userPostData.value.success(
                            data = _userPostData.value.data?.apply { addAll(finalData) },
                            hasMore = hasMore
                        )
                    }
                } else {
                    _userPostData.value.empty()
                }
            }
            .onFailure {
                _userPostData.value.error(Throwable(it.message))
            }
        }.onCatch {
            _userPostData.value.error(Throwable(it.message))
        }
    }

    fun getUserCollection(
        loadMore: Boolean,
        init: Boolean
    ) {
        if (init) {
            currentUserCollectionPage = 1
            _collectionData.value.init()
        } else {
            if (loadMore) {
                currentUserCollectionPage += 1
                _collectionData.value.loadingMore()
            } else {
                currentUserCollectionPage = 1
                _collectionData.value.refreshing()
            }
        }

        viewModelScope
            .launchSafety {
                if (!userId.isNullOrEmpty()) {
                    userRepository
                        .getUserCollectionByUid(
                            uid = userId,
                            page = currentUserCollectionPage,
                            additional = additional
                        )
                } else {
                    userRepository
                        .getUserCollectionByName(
                            name = userName,
                            page = currentUserCollectionPage,
                            additional = additional
                        )
                }.onSuccess {
                    if (it != null && it.rows.isNotNullAndEmpty()) {
                        val finalData = SnapshotStateList<UserCollectionEntity.Row>().apply {
                            addAll(it.rows.filter { it.targetType == "tid" })
                        }
                        val hasMore = (it.total ?: 0) > (it.page ?: 0) * (it.pageSize ?: 0)

                        if (!loadMore) {
                            _collectionData.value.success(
                                data = finalData,
                                hasMore = hasMore
                            )
                        } else {
                            _collectionData.value.success(
                                data = _collectionData.value.data?.apply { addAll(finalData) },
                                hasMore = hasMore
                            )
                        }
                    } else {
                        _collectionData.value.empty()
                    }
                }.onFailure {
                    _collectionData.value.error(Throwable(it.message))
                }
            }
            .onCatch {
                _collectionData.value.error(it)
            }
    }

    fun getUserMessageBoard(
        loadMore: Boolean,
        init: Boolean
    ) {
        if (init) {
            currentUserMsgBoardPage = 1
            _messageBoardData.value.init()
        } else {
            if (loadMore) {
                currentUserMsgBoardPage += 1
                _messageBoardData.value.loadingMore()
            } else {
                currentUserMsgBoardPage = 1
                _messageBoardData.value.refreshing()
            }
        }

        viewModelScope
            .launchSafety {
                userRepository
                    .getUserMsgBoard(
                        uid = userId,
                        page = currentUserMsgBoardPage,
                        additional = additional
                    )
                    .onSuccess {
                        if (it != null && it.rows.isNotNullAndEmpty()) {
                            val finalData = SnapshotStateList<MessageBoardEntity.Row>().apply {
                                addAll(it.rows)
                            }
                            val hasMore = (it.total ?: 0) > (it.page ?: 0) * (it.pageSize ?: 0)

                            if (!loadMore) {
                                _messageBoardData.value.success(
                                    data = finalData,
                                    hasMore = hasMore
                                )
                            } else {
                                _messageBoardData.value.success(
                                    data = _messageBoardData.value.data?.apply { addAll(finalData) },
                                    hasMore = hasMore
                                )
                            }
                        } else {
                            _messageBoardData.value.empty()
                        }
                    }
                    .onFailure {
                        _messageBoardData.value.error(Throwable(it.message))
                    }
            }
            .onCatch {
                _messageBoardData.value.error(it)
            }
    }

    fun addFriend(msg: String) {
        viewModelScope.launchSafety {
            val requestEntity = AddFriendRequestEntity(
                uid = userId.toIntOrElse(),
                message = msg
            )
            userRepository
                .addFriend(
                    requestEntity = requestEntity,
                    additional = additional
                )
                .onSuccess {
                    if (it?.friendStatus == "requested") {
                        _addFriendData.value = UiState<AddFriendEntity>().success(data = it)
                    } else {
                        _addFriendData.value = UiState<AddFriendEntity>().error(Throwable("添加失败"))
                    }
                }
                .onFailure {
                    _addFriendData.value = UiState<AddFriendEntity>().error(Throwable(it.message))
                }
        }.onCatch {
            _addFriendData.value = UiState<AddFriendEntity>().error(it)
        }
    }

    fun getUserFriends(
        loadMore: Boolean,
        init: Boolean
    ) {
        if (init) {
            currentUserFriendsPage = 1
            _friendData.value.init()
        } else {
            if (loadMore) {
                currentUserFriendsPage += 1
                _friendData.value.loadingMore()
            } else {
                currentUserFriendsPage = 1
                _friendData.value.refreshing()
            }
        }

        viewModelScope
            .launchSafety {
                if (!userId.isNullOrEmpty()) {
                    userRepository
                        .getUserFriendsByUid(
                            uid = userId,
                            page = currentUserFriendsPage,
                            additional = additional
                        )
                } else {
                    userRepository
                        .getUserFriendsByName(
                            name = userName,
                            page = currentUserFriendsPage,
                            additional = additional
                        )
                }.onSuccess {
                    if (it != null && it.rows.isNotNullAndEmpty()) {
                        val finalData = SnapshotStateList<UserFriendEntity.Row>().apply {
                            addAll(it.rows)
                        }
                        val hasMore = (it.total ?: 0) > (it.page ?: 0) * (it.pageSize ?: 0)

                        if (!loadMore) {
                            _friendData.value.success(
                                data = finalData,
                                hasMore = hasMore
                            )
                        } else {
                            _friendData.value.success(
                                data = _friendData.value.data?.apply { addAll(finalData) },
                                hasMore = hasMore
                            )
                        }
                    } else {
                        _friendData.value.empty()
                    }
                }.onFailure {
                    _friendData.value.error(Throwable(it.message))
                }
            }
            .onCatch {
                _friendData.value.error(it)
            }
    }

    fun removeLog() {
        removeLog = true
        getUserProfile()
    }

    fun editFriend(uid: String, note: String) {
        viewModelScope.launchSafety {
            userRepository
                .editFriend(
                    uid = uid,
                    requestEntity = EditFriendRequestEntity(note)
                )
                .onSuccess {
                    _editFriendData.value = UiState<EditFriendEntity>()
                        .apply {
                            data = EditFriendEntity(
                                uid = uid,
                                success = true,
                                note = note
                            )
                        }
                }
                .onFailure {
                    _editFriendData.value = UiState<EditFriendEntity>()
                        .apply {
                            data = EditFriendEntity(
                                uid = uid,
                                success = false,
                                note = note
                            )
                        }
                }
        }.onCatch {
            _editFriendData.value = UiState<EditFriendEntity>()
                .apply {
                    data = EditFriendEntity(
                        uid = uid,
                        success = false,
                        note = note
                    )
                }
        }
    }

    fun deleteFriend(uid: String) {
        viewModelScope.launchSafety {
            userRepository
                .deleteFriend(
                    uid = uid
                )
                .onSuccess {
                    _deleteFriendData.value = UiState<DeleteFriendEntity>()
                        .apply {
                            data = DeleteFriendEntity(
                                uid = uid,
                                success = true
                            )
                        }
                }
                .onFailure {
                    _deleteFriendData.value = UiState<DeleteFriendEntity>()
                        .apply {
                            data = DeleteFriendEntity(
                                uid = uid,
                                success = false
                            )
                        }
                }
        }.onCatch {
            _deleteFriendData.value = UiState<DeleteFriendEntity>()
                .apply {
                    data = DeleteFriendEntity(
                        uid = uid,
                        success = false
                    )
                }
        }
    }

    fun addComment(msg: String) {
        fun error(e: Throwable) {
            _addCommentData.value = UiState<MessageBoardEntity.Row?>()
                .apply {
                    isSuccess = false
                    data = null
                    errorData = e
                }
        }

        viewModelScope.launchSafety {
            userRepository
                .addComment(
                    UserAddCommentRequestEntity(
                        uid = userId.toIntOrElse(),
                        message = msg
                    )
                )
                .onSuccess {
                    val signedInAccount = AccountManager.getSignedInAccount()
                    _addCommentData.value = UiState<MessageBoardEntity.Row?>()
                        .apply {
                            isSuccess = true
                            data = MessageBoardEntity.Row(
                                authorId = signedInAccount?.uid.toIntOrElse(),
                                author = signedInAccount?.name,
                                message = msg,
                                commentId = it?.commentId.toIntOrElse(),
                                dateline = (System.currentTimeMillis() / 1000).toInt()
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

    fun editComment(commentId: String, comment: String) {
        viewModelScope.launchSafety {
            userRepository
                .editComment(
                    id = commentId,
                    requestBody = UserEditCommentRequestEntity(comment)
                )
                .onSuccess {
                    _editCommentData.value = UiState<EditCommentEntity?>()
                        .apply {
                            isSuccess = true
                            data = EditCommentEntity(
                                commentId = commentId,
                                comment = comment
                            )
                        }
                }
                .onFailure {
                    _editCommentData.value = UiState<EditCommentEntity?>()
                        .apply {
                            error(errorData = Throwable(it.message))
                        }
                }
        }.onCatch {
            _editCommentData.value = UiState<EditCommentEntity?>()
                .apply {
                    error(it)
                }
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launchSafety {
            userRepository
                .deleteComment(
                    id = commentId
                )
                .onSuccess {
                    _deleteCommentData.value = UiState<String?>()
                        .apply {
                            success(data = commentId)
                        }
                }
                .onFailure {
                    _deleteCommentData.value = UiState<String?>()
                        .apply {
                            error(errorData = Throwable(it.message))
                        }
                }
        }.onCatch {
            _deleteCommentData.value = UiState<String?>()
                .apply {
                    error(it)
                }
        }
    }
}
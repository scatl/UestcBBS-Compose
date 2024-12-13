package com.scatl.uestcbbs.compose.module.message

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.api.entity.message.ChatDetailEntity
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import com.scatl.uestcbbs.compose.api.entity.message.MessageEntity
import com.scatl.uestcbbs.compose.api.service.MessageService
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.net.onFailure
import com.scatl.uestcbbs.compose.net.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/7/25 22:37
 */
@HiltViewModel
class MessageViewModel @Inject constructor(
    val messageRepository: MessageRepository
): ViewModel() {

    private val _msgListDataMap = mutableMapOf<MessageService.MessageType, MutableStateFlow<UiState<SnapshotStateList<MessageEntity.Row>>>>()
    private val currentPageMap = mutableMapOf<MessageService.MessageType, Int>()

    private val _privateMsgData = MutableStateFlow(UiState<SnapshotStateList<MessageEntity.Row>>().init())
    val privateMsgData: StateFlow<UiState<SnapshotStateList<MessageEntity.Row>>> = _privateMsgData

    private val _chatDetailData = MutableStateFlow(UiState<SnapshotStateList<ChatDetailEntity.Row>>().init())
    val chatDetailData: StateFlow<UiState<SnapshotStateList<ChatDetailEntity.Row>>> = _chatDetailData

    private val initializedPageMap = mutableStateMapOf<MessageService.MessageType, Boolean>()
    private var currentPrivateMsgPage = 1
    private var currentChatDetailPage = 1

    fun isPageInitialized(page: MessageService.MessageType): Boolean {
        return initializedPageMap[page] ?: false
    }

    fun setPageInitialized(page: MessageService.MessageType) {
        initializedPageMap[page] = true
    }

    fun msgListData(kind: MessageService.MessageType): StateFlow<UiState<SnapshotStateList<MessageEntity.Row>>> {
        return _msgListDataMap.getOrPut(kind) {
            MutableStateFlow(UiState<SnapshotStateList<MessageEntity.Row>>().init())
        }
    }

    fun getMsgListData(
        type: MessageService.MessageType,
        loadMore: Boolean,
        init: Boolean = false
    ) {
        msgListData(type)
        val _msgListData = _msgListDataMap[type]

        if (init) {
            currentPageMap[type] = 1
            _msgListData?.value?.init()
        } else {
            if (loadMore) {
                currentPageMap[type] = (currentPageMap[type] ?: 1) + 1
                _msgListData?.value?.loadingMore()
            } else {
                currentPageMap[type] = 1
                _msgListData?.value?.refreshing()
            }
        }

        viewModelScope.launchSafety {
            messageRepository
                .getMessageList(type, currentPageMap[type] ?: 1)
                .onSuccess {
                    if (it != null && it.rows.isNotNullAndEmpty()) {
                        val finalData = it.rows.filter {
                            when (type) {
                                MessageService.MessageType.REPLY,
                                MessageService.MessageType.COMMENT,
                                MessageService.MessageType.AT,
                                MessageService.MessageType.RATE -> {
                                    it.kind == type.toString()
                                }
                                else -> {
                                    true
                                }
                            }
                        }
                        val hasMore = (it.total ?: 0) > (it.page ?: 0) * (it.pageSize ?: 0)

                        if (loadMore) {
                            _msgListData?.value?.success(
                                data = _msgListData.value.data?.apply { addAll(finalData) },
                                hasMore = hasMore
                            )
                        } else {
                            _msgListData?.value?.success(
                                data = SnapshotStateList<MessageEntity.Row>().apply { addAll(finalData ) },
                                hasMore = hasMore
                            )
                        }
                    } else {
                        _msgListData?.value?.empty()
                    }
                }
                .onFailure {
                    currentPageMap[type] = (currentPageMap[type] ?: 2) - 1
                    _msgListData?.value?.error(
                        errorData = Throwable(it.message),
                        initState = init
                    )
                }
        }.onCatch {
            currentPageMap[type] = (currentPageMap[type] ?: 2) - 1
            _msgListData?.value?.error(
                errorData = Throwable(it.message),
                initState = init
            )
        }
    }

    fun getPrivateMsgList(
        loadMore: Boolean,
        init: Boolean = false
    ) {
        if (init) {
            currentPrivateMsgPage = 1
            _privateMsgData.value.init()
        } else {
            if (loadMore) {
                currentPrivateMsgPage += 1
                _privateMsgData.value.loadingMore()
            } else {
                currentPrivateMsgPage = 1
                _privateMsgData.value.refreshing()
            }
        }

        viewModelScope.launchSafety {
            messageRepository
                .getPrivateMsgList(currentPrivateMsgPage)
                .onSuccess {
                    if (it != null && it.rows.isNotNullAndEmpty()) {
                        val finalData = SnapshotStateList<MessageEntity.Row>().apply {
                            addAll(it.rows)
                        }
                        val hasMore = (it.total ?: 0) > (it.page ?: 0) * (it.pageSize ?: 0)

                        _privateMsgData.value.success(
                            data = if (loadMore) _privateMsgData.value.data?.apply { addAll(finalData) } else finalData,
                            hasMore = hasMore
                        )
                    } else {
                        _privateMsgData.value.empty()
                    }
                }
                .onFailure {
                    _privateMsgData.value.error(Throwable(it.message))
                }
        }.onCatch {
            _privateMsgData.value.error(errorData = it)
        }
    }

    fun getChatDetail(
        uid: Int,
        loadMore: Boolean,
        init: Boolean = false
    ) {
        if (init) {
            currentChatDetailPage = 1
            _chatDetailData.value.init()
        } else {
            if (loadMore) {
                currentChatDetailPage += 1
                _chatDetailData.value.loadingMore()
            } else {
                currentChatDetailPage = 1
                _chatDetailData.value.refreshing()
            }
        }

        viewModelScope.launchSafety {
            messageRepository
                .getChatDetail(uid, currentChatDetailPage)
                .onSuccess {
                    if (it != null && it.rows.isNotNullAndEmpty()) {
                        val finalData = SnapshotStateList<ChatDetailEntity.Row>().apply {
                            addAll(it.rows)
                        }
                        val hasMore = (it.total ?: 0) > (it.page ?: 0) * (it.pageSize ?: 0)

                        _chatDetailData.value.success(
                            data = if (loadMore) _chatDetailData.value.data?.apply { addAll(finalData) } else finalData,
                            hasMore = hasMore
                        )
                    } else {
                        _chatDetailData.value.empty()
                    }
                }
                .onFailure {
                    _chatDetailData.value.error(Throwable(it.message))
                }
        }.onCatch {
            _chatDetailData.value.error(it)
        }
    }
}
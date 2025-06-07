package com.scatl.uestcbbs.compose.module.darkroom

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.api.entity.DarkRoomEntity
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Created by sca_tl at 2025/6/4 20:09:28
 */
@HiltViewModel
class DarkRoomViewModel @Inject constructor(
    private val darkRoomRepository: DarkRoomRepository
): ViewModel() {

    private val _darkRoomListData = MutableStateFlow(UiState<SnapshotStateList<DarkRoomEntity.Item>>().init())
    val darkRoomListData: StateFlow<UiState<SnapshotStateList<DarkRoomEntity.Item>>> = _darkRoomListData
    private var currentCid = ""

    fun getDarkRoomList(
        loadMore: Boolean,
        init: Boolean = false
    ) {
        if (init) {
            currentCid = ""
            _darkRoomListData.value.init()
        } else {
            if (loadMore) {
                _darkRoomListData.value.loadingMore()
            } else {
                currentCid = ""
                _darkRoomListData.value.refreshing()
            }
        }

        viewModelScope.launchSafety {
            val result = darkRoomRepository.getDarkRoomList(
                cid = currentCid,
                t = ((System.currentTimeMillis() / 1000) / (Math.random() * 1000)).roundToInt().toString()
            )
            val jsonAdapter = Moshi.Builder().build().adapter(DarkRoomEntity::class.java).lenient()
            val entity = jsonAdapter.fromJson(result)
            currentCid = entity?.message?.cid ?: ""

            if (entity != null && entity.data.isNotNullAndEmpty()) {
                val finalData = SnapshotStateList<DarkRoomEntity.Item>()
                entity.data?.forEach { (_, u) ->
                    finalData.add(u)
                }

                _darkRoomListData.value.success(
                    data = if (loadMore) _darkRoomListData.value.data?.apply { addAll(finalData) } else finalData,
                    hasMore = entity.message?.dataexist == "1"
                )
            } else {
                _darkRoomListData.value.empty()
            }
        }.onCatch {
            _darkRoomListData.value.error(errorData = it)
        }
    }

}
package com.scatl.uestcbbs.compose.module.setting

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.App
import com.scatl.uestcbbs.compose.api.entity.search.SearchSummaryEntity
import com.scatl.uestcbbs.compose.db.entity.DayQuestionDBEntity
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.module.collection.entity.CollectionDetailData
import com.scatl.uestcbbs.compose.module.setting.SettingRepository
import com.scatl.uestcbbs.compose.module.setting.entity.OpenSourceEntity
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/6/27 14:34:12
 */
@HiltViewModel
class SettingViewModel @Inject constructor(
    val settingRepository: SettingRepository
) : ViewModel() {

    private val _cacheSize = MutableStateFlow("")
    val cacheSize: StateFlow<String> = _cacheSize

    private val _openSourceData = MutableStateFlow(UiState<SnapshotStateList<OpenSourceEntity>>().init())
    val openSourceData: StateFlow<UiState<SnapshotStateList<OpenSourceEntity>>> = _openSourceData

    fun getCacheSize() {
        viewModelScope.launchSafety {
            _cacheSize.value = withContext(Dispatchers.IO) {
                settingRepository.getCacheSize()
            }
        }
    }

    fun deleteCache() {
        viewModelScope.launchSafety {
            _cacheSize.value = withContext(Dispatchers.IO) {
                settingRepository.deleteCache()
                settingRepository.getCacheSize()
            }
        }
    }

    fun getOpenSourceList() {
        viewModelScope.launchSafety {
            val json = App.context.assets.open("open_source_projects.json").bufferedReader().use { it.readText() }

            val listType = Types.newParameterizedType(List::class.java, OpenSourceEntity::class.java)
            val adapter = Moshi.Builder().build().adapter<List<OpenSourceEntity>>(listType)
            val list = adapter.fromJson(json)
            list?.let {
                _openSourceData.value.success(
                    data = SnapshotStateList<OpenSourceEntity>().apply { addAll(list) },
                    hasMore = false
                )
            }
        }.onCatch {
            _openSourceData.value.error(
                errorData = it
            )
        }
    }
}
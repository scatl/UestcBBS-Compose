package com.scatl.uestcbbs.compose.module.dayquestion

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.db.entity.BrowsingHistoryDBEntity
import com.scatl.uestcbbs.compose.db.entity.DayQuestionDBEntity
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by sca_tl at 2025/3/21 17:41:15
 */
@HiltViewModel
class DayQuestionViewModel @Inject constructor(
    val dayQuestionRepository: DayQuestionRepository
) : ViewModel() {

    private val _dayQuestionData = MutableStateFlow(UiState<SnapshotStateList<DayQuestionDBEntity>>().init())
    val dayQuestionData: StateFlow<UiState<SnapshotStateList<DayQuestionDBEntity>>> = _dayQuestionData

    fun getDayQuestions() {
        viewModelScope.launchSafety {
            withContext(Dispatchers.IO) {
                val result = SnapshotStateList<DayQuestionDBEntity>()
                val data = dayQuestionRepository.dataBase.getDayQuestionDao().getAll()
                if (data.isNotEmpty()) {
                    _dayQuestionData.value.success(
                        data = result.apply { addAll(data) }
                    )
                } else {
                    _dayQuestionData.value.empty().apply {
                        this.data = null
                    }
                }
            }
        }
    }

}
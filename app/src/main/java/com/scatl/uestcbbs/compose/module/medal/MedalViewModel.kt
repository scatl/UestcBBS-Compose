package com.scatl.uestcbbs.compose.module.medal

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.App
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.module.magic.entity.BuyMagicEntity
import com.scatl.uestcbbs.compose.module.medal.entity.MedalListEntity
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jsoup.Jsoup
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/9/26 9:36:07
 */
@HiltViewModel
class MedalViewModel @Inject constructor(
    private val medalRepository: MedalRepository
): ViewModel() {

    private val _medalListData = MutableStateFlow(UiState<SnapshotStateList<MedalListEntity.MedalItem>>().init())
    val medalListData: StateFlow<UiState<SnapshotStateList<MedalListEntity.MedalItem>>> = _medalListData

    private val _buyMedalData = MutableStateFlow(UiState<String?>().init())
    val buyMedalData: StateFlow<UiState<String?>> = _buyMedalData

    fun getMedalList(init: Boolean = false, refresh: Boolean = false) {
        if (init) {
            _medalListData.value.init(data = null)
        } else if (refresh) {
            _medalListData.value.refreshing()
        }

        viewModelScope.launchSafety {
            val html = medalRepository.getMedalList() ?: ""
            if (html.contains("您尚未登录")) {
                _medalListData.value.error(
                    errorData = Throwable(ContextCompat.getString(App.context, R.string.need_login_dsp))
                )
            } else {
                runCatching {
                    val document = Jsoup.parse(html)
                    val formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
                    DataStore.legacyForumHash = formHash

                    val elements = document.select("ul[class=mtm mgcl cl]").select("li")

                    val medalListEntity = MedalListEntity()
                    elements.forEach {
                        medalListEntity.medalList.add(
                            MedalListEntity.MedalItem(
                                medalDsp = it.select("div[class=tip_c]").text(),
                                medalIcon = Constants.BBS_URL.plus("/${it.select("img").attr("src")}"),
                                medalName = it.select("p[class=xw1]").text(),
                                medalId = it.select("div[class=mg_img]").attr("id").replace("medal_", "").toIntOrElse(),
                                buyDsp = it.select("a[class=xi2]").text(),
                                alreadyOwn = it.select("p").last()?.text()?.contains("已拥有") == true
                            )
                        )
                    }
                    medalListEntity.medalList.sortByDescending { !it.buyDsp.isNullOrEmpty() }

                    _medalListData.value.success(
                        data = SnapshotStateList<MedalListEntity.MedalItem>().apply { addAll(medalListEntity.medalList) }
                    )
                }.onFailure {
                    _medalListData.value.error(
                        errorData = it
                    )
                }
            }
        }.onCatch {
            _medalListData.value.error(
                errorData = it
            )
        }
    }

    fun buyMedal(medalId: Int) {
        fun error(e: Throwable) {
            _buyMedalData.value = UiState<String?>().apply {
                isSuccess = false
                data = medalId.toString()
                errorData = e
            }
        }

        viewModelScope.launchSafety {
            val html = medalRepository.buyMedal(medalId.toString()) ?: ""
            if (html.contains("您尚未登录")) {
                error(Throwable(ContextCompat.getString(App.context, R.string.need_login_dsp)))
            } else {
                val document = Jsoup.parse(html)
                val msg = document.select("div[id=messagetext]").select("p")[0].text()

                if (html.contains("恭喜")) {
                    _buyMedalData.value = UiState<String?>().apply {
                        isSuccess = true
                        data = medalId.toString()
                    }
                } else {
                    error(Throwable(msg))
                }
            }
        }.onCatch {
            error(it)
        }
    }

    fun resetBuyMedalData() {
        _buyMedalData.value = UiState<String?>().apply {
            data = null
            isSuccess = false
        }
    }
}
package com.scatl.uestcbbs.compose.module.wealth

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.api.entity.wealth.BaseWealthEntity
import com.scatl.uestcbbs.compose.api.entity.wealth.WealthHistoryEntity
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jsoup.Jsoup
import javax.inject.Inject

/**
 * Created by sca_tl at 2025/6/6 10:50:47
 */
@HiltViewModel
class MyWealthViewModel @Inject constructor(
    private val myWealthRepository: MyWealthRepository
): ViewModel() {

    private val _baseData = MutableStateFlow(UiState<BaseWealthEntity>().init())
    val baseData: StateFlow<UiState<BaseWealthEntity>> = _baseData

    private val _waterTransferData = MutableStateFlow(UiState<String>().init())
    val waterTransferData: StateFlow<UiState<String>> = _waterTransferData

    private val _waterTransferResData = MutableStateFlow(UiState<Boolean?>().init())
    val waterTransferResData: StateFlow<UiState<Boolean?>> = _waterTransferResData

    private val _historyData = MutableStateFlow(UiState<SnapshotStateList<WealthHistoryEntity>>().init())
    val historyData: StateFlow<UiState<SnapshotStateList<WealthHistoryEntity>>> = _historyData

    private var currentHistoryPage = 1

    fun resetWaterTransferResData() {
        _waterTransferResData.value = UiState<Boolean?>().apply { data = null }
    }

    fun getBaseWealthData() {
        viewModelScope.launchSafety {
            val entity = BaseWealthEntity()
            val html = myWealthRepository.getCreditInfo()
            val document = Jsoup.parse(html)
            document
                .select("ul[class=creditl mtm bbda cl]")
                .select("li")
                .forEach { element ->
                    if (element.text().startsWith("水滴")) {
                        entity.waterCount = """\d+""".toRegex().find(element.ownText())?.value.toIntOrElse()
                    } else if (element.text().startsWith("威望")) {
                        entity.prestigeCount = """\d+""".toRegex().find(element.ownText())?.value.toIntOrElse()
                    } else if (element.text().startsWith("奖励券")) {
                        entity.couponCount = """\d+""".toRegex().find(element.ownText())?.value.toIntOrElse()
                    } else if (element.text().startsWith("积分")) {
                        entity.creditCount = """\d+""".toRegex().find(element.ownText())?.value.toIntOrElse()
                        entity.creditRule = element.select("span").text()
                    }
                }

            _baseData.value.success(data = entity)
        }.onCatch {
            _baseData.value.error(it)
        }
    }

    fun getWaterTransferData() {
        viewModelScope.launchSafety {
            val html = myWealthRepository.getTransferInfo()
            val document = Jsoup.parse(html)
            val dsp = document.select("table[class=tfm mtn]").select("tr")
                .getOrNull(0)?.select("td[class=d]")?.text()

            val formHash = document.select("div[class=hdc]").select("div[class=wp]")
                .select("div[class=cl]").select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
            DataStore.legacyForumHash = formHash

            _waterTransferData.value.success(data = dsp)
        }.onCatch {
            _waterTransferData.value.error(it)
        }
    }

    fun waterTransfer(waterCount: Int, userName: String, psw: String, message: String) {
        fun error(e: Throwable) {
            _waterTransferResData.value = UiState<Boolean?>().apply {
                isSuccess = false
                errorData = e
                data = false
            }
        }

        viewModelScope.launchSafety {
            val html = myWealthRepository.waterTransfer(waterCount, userName, psw, message) ?: ""
            if (html.contains("messagetext")) {
                val info = Jsoup.parse(html).select("div[id=messagetext]").text()
                if (info.contains("积分转帐成功")) {
                    _waterTransferResData.value = UiState<Boolean?>().apply {
                        isSuccess = true
                        data = true
                    }
                } else {
                    error(Throwable(info))
                }
            } else {
                error(Throwable("出现了一个问题，请查看转账记录确认是否转账成功"))
            }
        }.onCatch {
            error(it)
        }
    }

    fun getWealthHistory(loadMore: Boolean, init: Boolean = false, income: String, type: String) {
        fun error(e: Throwable) {
            _historyData.value.error(
                errorData = e
            )
        }

        if (init) {
            currentHistoryPage = 1
            _historyData.value.init()
        } else {
            if (loadMore) {
                currentHistoryPage += 1
                _historyData.value.loadingMore()
            } else {
                currentHistoryPage = 1
                _historyData.value.refreshing()
            }
        }

        viewModelScope.launchSafety {
            val html = myWealthRepository.getWealthHistory(currentHistoryPage, income, type) ?: ""
            val document = Jsoup.parse(html)
            val elements = document.select("table[summary=主题付费]").select("tbody").select("tr")

            val wealthHistoryEntities = SnapshotStateList<WealthHistoryEntity>()

            elements.forEachIndexed { index, element ->
                if (index > 0) {
                    val entity = WealthHistoryEntity().apply {
                        action = element.select("td")[0].text()
                        change = element.select("td")[1].text()
                        detail = element.select("td")[2].text()
                        time = element.select("td")[3].text()
                        link = element.select("td")[2].select("a").attr("href")
                        increase = change?.contains("+") == true
                    }
                    wealthHistoryEntities.add(entity)
                }
            }

            if (wealthHistoryEntities.size == 0) {
                _historyData.value.empty(errorData = Throwable("啊哦，这里空空的~"))
            } else {
                _historyData.value.success(
                    data = if (loadMore) _historyData.value.data?.apply { addAll(wealthHistoryEntities) } else wealthHistoryEntities,
                    hasMore = html.contains("下一页")
                )
            }
        }.onCatch {
            error(it)
        }
    }

}
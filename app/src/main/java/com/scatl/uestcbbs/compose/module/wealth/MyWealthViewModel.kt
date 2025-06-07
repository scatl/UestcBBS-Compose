package com.scatl.uestcbbs.compose.module.wealth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.api.entity.user.UserProfileEntity
import com.scatl.uestcbbs.compose.api.entity.wealth.BaseWealthEntity
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

    fun getBaseWealthData() {
        viewModelScope.launchSafety {
            val entity = BaseWealthEntity()
            val html = myWealthRepository.getCreditInfo("base")
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
}
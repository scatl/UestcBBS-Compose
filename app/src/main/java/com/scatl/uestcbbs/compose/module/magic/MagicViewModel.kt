package com.scatl.uestcbbs.compose.module.magic

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.App
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.module.magic.entity.BuyMagicEntity
import com.scatl.uestcbbs.compose.module.magic.entity.MagicDetailEntity
import com.scatl.uestcbbs.compose.module.magic.entity.MagicShopListEntity
import com.scatl.uestcbbs.compose.module.magic.entity.MyMagicEntity
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/9/26 9:36:07
 */
@HiltViewModel
class MagicViewModel @Inject constructor(
    private val magicRepository: MagicRepository
): ViewModel() {

    private val _magicData = MutableStateFlow(UiState<MagicShopListEntity>().init())
    val magicData: StateFlow<UiState<MagicShopListEntity>> = _magicData

    private val _magicListData = MutableStateFlow(UiState<SnapshotStateList<MagicShopListEntity.Item>>().init())
    val magicListData: StateFlow<UiState<SnapshotStateList<MagicShopListEntity.Item>>> = _magicListData

    private val _magicDetailData = MutableStateFlow(UiState<MagicDetailEntity>().init())
    val magicDetailData: StateFlow<UiState<MagicDetailEntity>> = _magicDetailData

    private val _buyMagicData = MutableStateFlow(UiState<BuyMagicEntity>().init())
    val buyMagicData: StateFlow<UiState<BuyMagicEntity>> = _buyMagicData

    private val _myMagicData = MutableStateFlow(UiState<SnapshotStateList<MyMagicEntity>>().init())
    val myMagicData: StateFlow<UiState<SnapshotStateList<MyMagicEntity>>> = _myMagicData

    private val _beforeUseMagicData = MutableStateFlow(UiState<MagicDetailEntity>().init())
    val beforeUseMagicData: StateFlow<UiState<MagicDetailEntity>> = _beforeUseMagicData

    private val _confirmUseMagicData = MutableStateFlow(UiState<String>().init())
    val confirmUseMagicData: StateFlow<UiState<String>> = _confirmUseMagicData

    fun getMagicList(init: Boolean = false, refresh: Boolean = false) {
        if (init) {
            _magicListData.value.init(data = null)
        } else if (refresh) {
            _magicListData.value.refreshing()
        }
        viewModelScope.launchSafety {
            val html = magicRepository.getMagicList() ?: ""
            if (html.contains("您尚未登录")) {
                _magicListData.value.error(
                    errorData = Throwable(ContextCompat.getString(App.context, R.string.need_login_dsp))
                )
            } else {
                runCatching {
                    val document = Jsoup.parse(html)

                    val magicShopListEntity = MagicShopListEntity()

                    val title = document.select("div[class=tbmu]").text().replace("|积分兑换", "").replace("|", "\n")
                    magicShopListEntity.dsp = title

                    val elements = document.select("ul[class=mtm mgcl cl]").select("li")
                    for (i in elements.indices) {
                        magicShopListEntity.itemLists.add(
                            MagicShopListEntity.Item().apply {
                                dsp = elements[i].select("div[class=tip_c]").text()
                                icon = Constants.BBS_URL.plus("/${elements[i].select("img").attr("src")}")
                                name = elements[i].select("p")[0].text()
                                price = elements[i].select("p")[1].text()
                                if (elements[i].select("p").size > 2) {
                                    outOfStock = elements[i].select("p")[2].text().contains("缺货")
                                }
                                id = elements[i].select("div[class=mg_img]").attr("id").replace("magic_", "")
                            }
                        )
                    }
                    _magicData.value.success(
                        data = magicShopListEntity
                    )
                    _magicListData.value.success(
                        data = SnapshotStateList<MagicShopListEntity.Item>().apply { addAll(magicShopListEntity.itemLists) },
                        hasMore = false
                    )
                }
            }
        }.onCatch {
            _magicListData.value.error(errorData = it)
        }
    }

    fun getMagicDetail(id: String?) {
        _magicDetailData.value.init(data = null)
        viewModelScope.launchSafety {
            val html = magicRepository.getMagicDetail(id) ?: ""
            if (html.contains("您尚未登录")) {
                _magicDetailData.value.error(
                    errorData = Throwable(ContextCompat.getString(App.context, R.string.need_login_dsp))
                )
            } else {
                runCatching {
                    val document = Jsoup.parse(html)
                    val formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
                    DataStore.legacyForumHash = formHash

                    val magicDetailEntity = MagicDetailEntity().apply {
                        icon = Constants.BBS_URL.plus("/${document.select("dl[class=xld cl]").select("dd[class=m]").select("img").attr("src")}")
                        name = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=mbm pbm bbda]").select("p")[0].text()
                        dsp = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=mbm pbm bbda]").select("p[class=mtn xw0 xg1]").text()
                        originalPrice = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=mbm pbm bbda]").select("span[id=magicprice]").text()
                        discountPrice = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=mbm pbm bbda]").select("span[id=discountprice]").text()
                        mineWaterDrop = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=mbm pbm bbda]").select("p[class=xw0 xg1]")[0].text()
                        weight = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=mbm pbm bbda]").select("span[id=magicweight]").text()
                        availableWeight = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=mbm pbm bbda]").select("p[class=xw0 xg1]")[1].text()
                        stock = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=xw0]").select("p[class=mtn xw0]").select("span[class=xi1 xw1 xs2]").text()
                        otherInfo = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=xw0]").select("p[class=xi1 mtn]").text()
                    }
                    _magicDetailData.value.success(
                        data = magicDetailEntity
                    )
                }.onFailure {
                    _magicDetailData.value.error(
                        errorData = it
                    )
                }
            }
        }.onCatch {
            _magicDetailData.value.error(
                errorData = it
            )
        }
    }

    fun buyMagic(id: String, count: Int) {
        fun error(e: Throwable) {
            _buyMagicData.value = UiState<BuyMagicEntity>().apply {
                isSuccess = false
                data = BuyMagicEntity(id)
                errorData = e
            }
        }

        viewModelScope.launchSafety {
            val html = magicRepository.buyMagic(id, count) ?: ""
            runCatching {
                val document = Jsoup.parse(html)
                val msg = document.select("div[id=messagetext]").select("p")[0].text()

                if (html.contains("alert_right")) {
                    _buyMagicData.value = UiState<BuyMagicEntity>().apply {
                        isSuccess = true
                        data = BuyMagicEntity(id = id)
                    }
                } else {
                    error(Throwable(msg))
                }
            }.onFailure {
                error(it)
            }
        }.onCatch {
            error(it)
        }
    }

    fun getMyMagic(init: Boolean = false, refresh: Boolean = false) {
        if (init) {
            _myMagicData.value.init(data = null)
        } else if (refresh) {
            _myMagicData.value.refreshing()
        }
        viewModelScope.launchSafety {
            val html = magicRepository.getMyMagic() ?: ""
            if (html.contains("您尚未登录")) {
                _myMagicData.value.error(
                    errorData = Throwable(ContextCompat.getString(App.context, R.string.need_login_dsp))
                )
            } else {
                val document = Jsoup.parse(html)

                val elements = document.select("ul[class=mtm mgcl cl]").select("li")
                val myMagicEntities = SnapshotStateList<MyMagicEntity>()

                for (i in elements.indices) {
                    val item = MyMagicEntity().apply {
                        dsp = elements[i].select("div[class=tip_c]").text()
                        icon = Constants.BBS_URL.plus("/${elements[i].select("img").attr("src")}")
                        name = elements[i].select("p")[0].text()
                        totalCount = elements[i].select("p")[1].select("font[class=xi1 xw1]").text()
                        totalWeight = elements[i].select("p")[1].select("font[class=xi1]").text()
                        magicId = elements[i].select("p[class=mtn]").select("a")[0].attr("href")
                            .replace("https://bbs.uestc.edu.cn/home.php?mod=magic&action=mybox&operation=use&magicid=", "")
                            .replace("https://bbs.uestc.edu.cn/home.php?mod=magic&action=mybox&operation=drop&magicid=", "")
                        showUseBtn = magicId == SCRATCH_CARD_MAGIC_ID //刮刮卡可以直接使用
                    }
                    myMagicEntities.add(item)
                }
                _myMagicData.value.success(
                    data = myMagicEntities,
                    hasMore = false
                )
            }
        }.onCatch {
            _myMagicData.value.error(errorData = it)
        }
    }

    fun beforeUseMagic(magicId: String?) {
        viewModelScope.launchSafety {
            val html = magicRepository.beforeUseMagic(magicId) ?: ""

            val document: Document = Jsoup.parse(html)
            val formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
            DataStore.legacyForumHash = formHash

            val magicDetailEntity = MagicDetailEntity().apply {
                icon = Constants.BBS_URL.plus("/${document.select("dl[class=xld cl]").select("dd[class=m]").select("img").attr("src")}")
                name = document.select("dl[class=xld cl]").select("dt[class=z]")[0].ownText()
                dsp = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=pns xw0 cl]").select("p")[0].text()
                otherInfo = document.select("dl[class=xld cl]").select("dt[class=z]").select("div[class=pns xw0 cl]").select("p[class=xi1]").text()
            }
            _beforeUseMagicData.value.success(
                data = magicDetailEntity
            )
        }.onCatch {
            _beforeUseMagicData.value.error(
                errorData = it
            )
        }
    }

    fun confirmUseMagic(magicId: String?) {
        fun error(e: Throwable) {
            _confirmUseMagicData.value = UiState<String>().apply {
                data = ""
                errorData = e
                isSuccess = false
            }
        }

        viewModelScope.launchSafety {
            val html = magicRepository.confirmUseMagic(magicId) ?: ""
            val matcher = Pattern.compile("(.*?)CDATA\\[(.*?)<script(.*?)").matcher(html)
            if (matcher.find()) {
                if (html.contains("恭喜")) {
                    _confirmUseMagicData.value = UiState<String>().apply {
                        data = matcher.group(2) ?: ""
                        isSuccess = true
                    }
                } else {
                    error(Throwable(matcher.group(2) ?: ""))
                }
            } else {
                error(Throwable(message = null))
            }
        }.onCatch {
            error(it)
        }
    }

    fun clearMyMagicData() {
        _myMagicData.value.init(data = null)
    }

    fun clearUseMagicData() {
        _beforeUseMagicData.value.init(data = null)
        _confirmUseMagicData.value.init(data = null)
    }
}
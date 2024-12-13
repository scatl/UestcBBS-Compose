package com.scatl.uestcbbs.compose.module.watertask

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.App
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.module.watertask.entity.GetAwardEntity
import com.scatl.uestcbbs.compose.module.watertask.entity.TaskEntity
import com.scatl.uestcbbs.compose.util.BBSLinkUtil
import com.scatl.uestcbbs.compose.util.LinkType
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jsoup.Jsoup
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/9/24 16:32:04
 */
@HiltViewModel
class WaterTaskViewModel @Inject constructor(
    val waterTaskRepository: WaterTaskRepository
) : ViewModel() {

    private val _waterTaskDoingData = MutableStateFlow(UiState<SnapshotStateList<TaskEntity>>().init())
    val waterTaskDoingData: StateFlow<UiState<SnapshotStateList<TaskEntity>>> = _waterTaskDoingData

    private val _waterTaskNewData = MutableStateFlow(UiState<SnapshotStateList<TaskEntity>>().init())
    val waterTaskNewData: StateFlow<UiState<SnapshotStateList<TaskEntity>>> = _waterTaskNewData

    private val _waterTaskDoneData = MutableStateFlow(UiState<SnapshotStateList<TaskEntity>>().init())
    val waterTaskDoneData: StateFlow<UiState<SnapshotStateList<TaskEntity>>> = _waterTaskDoneData

    private val _waterTaskFailedData = MutableStateFlow(UiState<SnapshotStateList<TaskEntity>>().init())
    val waterTaskFailedData: StateFlow<UiState<SnapshotStateList<TaskEntity>>> = _waterTaskFailedData

    private val _applyTaskData = MutableStateFlow(UiState<Any>().init())
    val applyTaskData: StateFlow<UiState<Any>> = _applyTaskData

    private val _taskAwardData = MutableStateFlow(UiState<GetAwardEntity>().init())
    val taskAwardData: StateFlow<UiState<GetAwardEntity>> = _taskAwardData

    fun getDoingTask(refresh: Boolean = false) {
        if (refresh) {
            _waterTaskDoingData.value.refreshing()
        }
        viewModelScope.launchSafety {
            val html = waterTaskRepository.getDoingTask()
            if (html?.contains("需要先登录") == true) {
                _waterTaskDoingData.value.error(
                    errorData = Throwable(ContextCompat.getString(App.context, R.string.need_login_dsp))
                )
            } else {
                runCatching {
                    val document = Jsoup.parse(html ?: "")
                    val elements = document.select("div[class=ct2_a wp cl]").select("div[class=ptm]").select("table").select("tbody").select("tr")
                    val formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
                    DataStore.legacyForumHash = formHash

                    val taskEntities = SnapshotStateList<TaskEntity>()

                    for (i in elements.indices) {
                        val taskEntity = TaskEntity().apply {
                            name = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].text()
                            popularNum = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("span[class=xs1 xg2 xw0]").select("a").text().toInt()
                            dsp = elements[i].select("td[class=bbda ptm pbm]").select("p[class=xg2]").text()
                            award = elements[i].select("td[class=xi1 bbda hm]").text()
                            icon = Constants.BBS_URL.plus("/${elements[i].select("td")[0].select("img").attr("src")}")

                            val linkType = BBSLinkUtil.getLinkType(elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].attr("href"))
                            if (linkType is LinkType.Task) {
                                id = linkType.id
                            }

                            progress = elements[i].select("td[class=bbda ptm pbm]")
                                .select("div[class=xs0]").text()
                                .replace("已完成 ", "")
                                .replace("%", "")
                                .toDouble()
                        }
                        taskEntities.add(taskEntity)
                    }

                    taskEntities.forEach {
                        val htmlLeftTime = waterTaskRepository.getTaskAward(it.id)
                        runCatching {
                            val msg = Jsoup.parse(htmlLeftTime ?: "").select("div[id=messagetext]").text()
                            if (msg.contains("时间")) {
                                val matcher = Pattern.compile(".*?还有(.*?)时间").matcher(msg)
                                if (matcher.find()) {
                                    it.leftTime = matcher.group(1)
                                }
                            } else if (msg.contains("恭喜")) {
                                it.autoGetAward = true
                            }
                        }
                    }

                    _waterTaskDoingData.value.success(
                        data = taskEntities,
                        hasMore = false
                    )
                }.onFailure {
                    _waterTaskDoingData.value.error(errorData = it)
                }
            }
        }.onCatch {
            _waterTaskDoingData.value.error(errorData = it)
        }
    }

    fun deleteDoingTask(id: Int) {
        viewModelScope.launchSafety {
            val html = waterTaskRepository.deleteDoingTask(id)
            runCatching {
                val document = Jsoup.parse(html ?: "")
                val msg = document.select("div[id=messagetext]").text()
                getDoingTask()
            }
        }.onCatch {

        }
    }

    fun getNewTask(refresh: Boolean = false) {
        if (refresh) {
            _waterTaskNewData.value.refreshing()
        }
        viewModelScope.launchSafety {
            val html = waterTaskRepository.getNewTask()
            if (html?.contains("需要先登录") == true) {
                _waterTaskNewData.value.error(
                    errorData = Throwable(ContextCompat.getString(App.context, R.string.need_login_dsp))
                )
            } else {
                runCatching {
                    val document = Jsoup.parse(html ?: "")
                    val elements = document.select("div[class=ct2_a wp cl]").select("div[class=ptm]").select("table").select("tbody").select("tr")
                    val formhash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
                    DataStore.legacyForumHash = formhash

                    val taskEntities = SnapshotStateList<TaskEntity>()
                    for (i in elements.indices) {
                        val taskEntity = TaskEntity().apply {
                            name = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].text()
                            popularNum = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("span[class=xs1 xg2 xw0]").select("a").text().toInt()
                            dsp = elements[i].select("td[class=bbda ptm pbm]").select("p[class=xg2]").text()
                            award = elements[i].select("td[class=xi1 bbda hm]").text()
                            icon = Constants.BBS_URL.plus("/${elements[i].select("td")[0].select("img").attr("src")}")

                            val linkType = BBSLinkUtil.getLinkType(elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].attr("href"))
                            if (linkType is LinkType.Task) {
                                id = linkType.id
                            }
                        }
                        taskEntities.add(taskEntity)
                    }
                    _waterTaskNewData.value.success(
                        data = taskEntities,
                        hasMore = false
                    )
                }.onFailure {
                    _waterTaskNewData.value.error(errorData = it)
                }
            }
        }.onCatch {
            _waterTaskNewData.value.error(errorData = it)
        }
    }

    fun applyNewTask(id: Int) {
        viewModelScope.launchSafety {
            val html = waterTaskRepository.applyNewTask(id)

            val document = Jsoup.parse(html ?: "")
            val msg = document.select("div[id=messagetext]").text()
            if (msg.contains("申请成功")) {
                _applyTaskData.value = UiState<Any>().apply {
                    isSuccess = true
                    data = Any()
                }
                delay(100)
                _applyTaskData.value = UiState<Any>().apply {
                    data = null
                }
            } else {
                _applyTaskData.value = UiState<Any>().apply {
                    isSuccess = false
                    data = Any()
                    errorData = Throwable(msg)
                }
            }
        }.onCatch {
            _applyTaskData.value = UiState<Any>().apply {
                isSuccess = false
                data = Any()
                errorData = it
            }
        }
    }

    fun getDoneTask(refresh: Boolean = false) {
        if (refresh) {
            _waterTaskDoneData.value.refreshing()
        }
        viewModelScope.launchSafety {
            val html = waterTaskRepository.getDoneTask()
            if (html?.contains("需要先登录") == true) {
                _waterTaskDoneData.value.error(
                    errorData = Throwable(ContextCompat.getString(App.context, R.string.need_login_dsp))
                )
            } else {
                runCatching {
                    val document = Jsoup.parse(html ?: "")
                    val elements = document.select("div[class=ct2_a wp cl]").select("div[class=ptm]").select("table").select("tbody").select("tr")
                    val formhash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
                    DataStore.legacyForumHash = formhash

                    val taskEntities = SnapshotStateList<TaskEntity>()
                    for (i in elements.indices) {
                        val taskEntity = TaskEntity().apply {
                            name = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].text()
                            popularNum = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("span[class=xs1 xg2 xw0]").select("a").text().toInt()
                            dsp = elements[i].select("td[class=bbda ptm pbm]").select("p[class=xg2]").text()
                            award = elements[i].select("td[class=xi1 bbda hm]").text()
                            doneTime = elements[i].select("td[class=bbda]").getOrNull(1)?.text()
                            icon = Constants.BBS_URL.plus("/${elements[i].select("td")[0].select("img").attr("src")}")

                            val linkType = BBSLinkUtil.getLinkType(elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].attr("href"))
                            if (linkType is LinkType.Task) {
                                id = linkType.id
                            }
                        }
                        taskEntities.add(taskEntity)
                    }
                    _waterTaskDoneData.value.success(
                        data = taskEntities,
                        hasMore = false
                    )
                }.onFailure {
                    _waterTaskDoneData.value.error(errorData = it)
                }
            }
        }.onCatch {
            _waterTaskDoneData.value.error(errorData = it)
        }
    }

    fun getTaskAward(id: Int) {
        fun error(error: Throwable) {
            _taskAwardData.value = UiState<GetAwardEntity>().apply {
                data = GetAwardEntity(
                    success = false,
                    msg = error.message,
                    taskId = id
                )
            }
        }

        viewModelScope.launchSafety {
            val html = waterTaskRepository.getTaskAward(id)
            val document = Jsoup.parse(html ?: "")
            val msg = document.select("div[id=messagetext]").text()

            //恭喜您，任务已成功完成，您将收到奖励通知，请注意查收
            //您已完成该任务的 35.00%，还有4小时10分钟时间，加油啊！ 如果您的浏览器没有自动跳转，请点击此链接
            if (msg.contains("恭喜")) {
                _taskAwardData.value.success(
                    data = GetAwardEntity(
                        success = true,
                        msg = msg,
                        taskId = id
                    )
                )
            } else {
                error(Throwable(""))
            }
        }.onCatch {
            error(it)
        }
    }

    fun getFailedTask(refresh: Boolean = false) {
        if (refresh) {
            _waterTaskFailedData.value.refreshing()
        }
        viewModelScope.launchSafety {
            val html = waterTaskRepository.getFailedTask() ?: ""
            if (html.contains("需要先登录")) {
                _waterTaskFailedData.value.error(
                    errorData = Throwable(ContextCompat.getString(App.context, R.string.need_login_dsp))
                )
            } else {
                val document = Jsoup.parse(html)
                val elements = document.select("div[class=ct2_a wp cl]").select("div[class=ptm]").select("table").select("tbody").select("tr")
                val formHash = document.select("form[id=scbar_form]").select("input[name=formhash]").attr("value")
                DataStore.legacyForumHash = formHash

                val taskEntities = SnapshotStateList<TaskEntity>()
                for (i in elements.indices) {
                    val taskEntity = TaskEntity().apply {
                        name = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].text()
                        popularNum = elements[i].select("td[class=bbda ptm pbm]").select("h3").select("span[class=xs1 xg2 xw0]").select("a").text().toInt()
                        dsp = elements[i].select("td[class=bbda ptm pbm]").select("p[class=xg2]").text()
                        award = elements[i].select("td[class=xi1 bbda hm]").text()
                        failedTime = elements[i].select("td[class=bbda]").getOrNull(1)?.text()
                        icon = Constants.BBS_URL.plus("/${elements[i].select("td")[0].select("img").attr("src")}")

                        val linkType = BBSLinkUtil.getLinkType(elements[i].select("td[class=bbda ptm pbm]").select("h3").select("a")[0].attr("href"))
                        if (linkType is LinkType.Task) {
                            id = linkType.id
                        }
                    }
                    taskEntities.add(taskEntity)
                }

                _waterTaskFailedData.value.success(
                    data = taskEntities,
                    hasMore = false
                )
            }
        }.onCatch {
            _waterTaskFailedData.value.error(errorData = it)
        }
    }
}
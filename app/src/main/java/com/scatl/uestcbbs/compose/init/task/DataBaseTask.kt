package com.scatl.uestcbbs.compose.init.task

import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.App
import com.scatl.uestcbbs.compose.db.entity.DayQuestionDBEntity
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.init.InitRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/9/25 16:20:44
 */
class DataBaseTask @Inject constructor(
    private val repository: InitRepository
): Task() {

    val tag = "DataBaseTask"

    override fun execute() {
        coroutineScope.launchSafety {
            val json = App.context.assets.open("day_question.json").bufferedReader().use { it.readText() }

            val listType = Types.newParameterizedType(List::class.java, DayQuestionDBEntity::class.java)
            val adapter = Moshi.Builder().build().adapter<List<DayQuestionDBEntity>>(listType)
            val answers = adapter.fromJson(json)
            answers?.let {
                repository.dataBase.getDayQuestionDao().insert(it)
            }
        }.onCatch {
            XLog.tag(tag).d(it)
        }
    }
}
package com.scatl.uestcbbs.compose.init.task

import com.elvishew.xlog.XLog
import com.scatl.uestcbbs.compose.db.entity.MedalDBEntity
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.toMealUrl
import com.scatl.uestcbbs.compose.init.InitRepository
import com.scatl.uestcbbs.compose.net.onFailure
import com.scatl.uestcbbs.compose.net.onSuccess
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/7/15 20:57:58
 */
class BBSSettingsTask @Inject constructor(
    private val repository: InitRepository
): Task() {

    companion object {
        const val TAG = "SystemTask"
    }

    override fun execute() {
        coroutineScope.launchSafety {
            repository
                .getSystemSetting()
                .onSuccess {
                    if (!it?.medals?.value.isNullOrEmpty()) {
                        val data = mutableListOf<MedalDBEntity>()
                        it?.medals?.value?.forEach { v ->
                            data.add(MedalDBEntity(
                                id = v?.id ?: 0,
                                name = v?.name,
                                image = v?.imagePath.toMealUrl(),
                                dsp = v?.description,
                                price = v?.price,
                                type = v?.type,
                                displayOrder = v?.displayOrder,
                                expirationDays = v?.expirationDays,
                                extCredit = v?.extCredit
                            ))
                        }
                        if (data.isNotEmpty()) {
                            repository.dataBase.getMedalDao().deleteAll()
                            repository.dataBase.getMedalDao().insert(data)
                        }
                    }
                }
                .onFailure {
                    XLog.tag(TAG).e(it)
                }
        }.onCatch {
            XLog.tag(TAG).e(it)
        }
    }

}
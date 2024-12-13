package com.scatl.uestcbbs.compose.module.dayquestion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by sca_tl at 2023/5/17 19:45
 */
class RetryDayQuestionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, DayQuestionService::class.java))
    }
}
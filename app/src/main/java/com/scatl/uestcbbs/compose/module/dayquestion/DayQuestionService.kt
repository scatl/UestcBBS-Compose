package com.scatl.uestcbbs.compose.module.dayquestion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.ext.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DayQuestionService : Service(), DayQuestionView {

    companion object {
        const val CHANNEL_NAME = "自动答题服务通知"
        const val CHANNEL_ID = 123456
        const val MSG_START = "开始后台自动答题"
        const val MSG_ERROR = "自动答题失败了，下拉查看详情"
    }

    @Inject lateinit var mPresenter: DayQuestionPresenter
    private lateinit var mDayQuestionBean: DayQuestionEntity
    private var questionNumber = 1
    private val notificationManager by lazy {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID.toString(),
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mPresenter.view = this
        mPresenter.getDayQuestion()
        return START_STICKY
    }

    override fun onGetDayQuestionSuccess(dayQuestionBean: DayQuestionEntity) {
        sendNotification("获取题目成功，正在获取答案", questionNumber)
        mDayQuestionBean = dayQuestionBean
        mPresenter.getQuestionAnswer(dayQuestionBean.questionTitle)
        questionNumber = dayQuestionBean.questionNum
    }

    override fun onGetDayQuestionError(msg: String?, netError: Boolean) {
        if (!netError) {
            sendNotification(msg, questionNumber, true, error = true)
        } else {
            stopSelf()
        }
    }

    override fun onDayQuestionFinished(msg: String?) {
        stopSelf()
    }

    override fun onConfirmFinishSuccess(msg: String?) {
        notificationManager.cancel(CHANNEL_ID)
        showToast("答题成功，水滴已发放\uD83C\uDF7B")
        stopSelf()
    }

    override fun onConfirmFinishError(msg: String?) {
        sendNotification("领取奖励失败，请稍后再试", 7, error = true)
    }

    override fun onGetConfirmDspSuccess(dsp: String?) {
        mPresenter.confirmNextQuestion()
        sendNotification("确认获取下一题中...", questionNumber, true)
    }

    override fun onGetConfirmDspError(msg: String?) {
        sendNotification(msg, questionNumber, error = true)
    }

    override fun onConfirmNextSuccess() {
        mPresenter.getDayQuestion()
        sendNotification("正在获取下一题...", questionNumber, true)
    }

    override fun onConfirmNextError(msg: String?) {
        sendNotification("获取下一题失败", questionNumber, error = true)
    }

    override fun onAnswerCorrect(question: String?, answer: String?) {
        mPresenter.submitQuestionAnswer(question, answer)
        mPresenter.getDayQuestion()
        sendNotification("答题正确，准备下一题", questionNumber, true)
    }

    override fun onAnswerIncorrect(msg: String?) {
        sendNotification(msg, questionNumber, error = true)
    }

    override fun onAnswerError(msg: String?) {
        sendNotification(msg, questionNumber, error = true)
    }

    override fun onFinishedAllCorrect(msg: String?) {
        mPresenter.confirmFinishQuestion()
        sendNotification("恭喜，全部回答正确，正在领取奖励", 7, true)
    }

    override fun onGetQuestionAnswerSuccess(answer: String?) {
        var answerIndex = -1
        for ((index, value) in mDayQuestionBean.options.withIndex()) {
            if (answer == value.dsp) {
                answerIndex = index
                break
            }
        }
        if (answerIndex in mDayQuestionBean.options.indices) {
            mPresenter.submitQuestion(
                mDayQuestionBean.options[answerIndex].answerValue,
                mDayQuestionBean.questionTitle,
                mDayQuestionBean.options[answerIndex].dsp
            )
        } else {
            sendNotification("未能提交答案", questionNumber, error = true)
        }
    }

    override fun onGetQuestionAnswerError(msg: String?) {
        sendNotification(msg, questionNumber, true, error = true)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun sendNotification(content: String?,
                                 progress: Int,
                                 indeterminate: Boolean = false,
                                 title: String = "",
                                 error: Boolean = false) {
        val title1 = title.ifBlank { "后台答题中(${progress}/7)，请稍候..." }
        val builder = NotificationCompat
                .Builder(this, CHANNEL_ID.toString())
                .setGroupSummary(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(false)
                .setContentTitle(title1)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setProgress(7, progress, indeterminate)
        if (error) {
            val intent = Intent(this, RetryDayQuestionReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            val action = NotificationCompat.Action.Builder(0, "重试", pendingIntent).build()
            builder.addAction(action)
        }
        notificationManager.notify(CHANNEL_ID, builder.build())

        if (error) {
            showToast(MSG_ERROR)
            stopSelf()
        }
    }

    private fun showToast(msg: String) {
        Handler(Looper.getMainLooper()).post {
            msg.showToast(this)
        }
    }
}
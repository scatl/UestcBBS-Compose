package com.scatl.uestcbbs.compose.module.dayquestion

/**
 * Created by sca_tl at 2024/9/25 15:04:06
 */
interface DayQuestionView {
    fun onGetDayQuestionSuccess(dayQuestionBean: DayQuestionEntity)
    fun onGetDayQuestionError(msg: String?, netError: Boolean)

    fun onDayQuestionFinished(msg: String?)

    fun onConfirmFinishSuccess(msg: String?)
    fun onConfirmFinishError(msg: String?)

    fun onGetConfirmDspSuccess(dsp: String?)
    fun onGetConfirmDspError(msg: String?)

    fun onConfirmNextSuccess()
    fun onConfirmNextError(msg: String?)

    fun onAnswerCorrect(question: String?, answer: String?)
    fun onAnswerIncorrect(msg: String?)
    fun onAnswerError(msg: String?)

    fun onFinishedAllCorrect(msg: String?)

    fun onGetQuestionAnswerSuccess(answer: String?)
    fun onGetQuestionAnswerError(msg: String?)
}
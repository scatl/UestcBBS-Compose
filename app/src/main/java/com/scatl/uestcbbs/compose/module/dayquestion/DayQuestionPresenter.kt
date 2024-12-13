package com.scatl.uestcbbs.compose.module.dayquestion

import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.db.entity.DayQuestionDBEntity
import com.scatl.uestcbbs.compose.ext.isNotNullAndEmpty
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jsoup.Jsoup
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/9/25 14:55:08
 */
class DayQuestionPresenter @Inject constructor(
    private val dayQuestionRepository: DayQuestionRepository
) {
    var view: DayQuestionView? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    //一共有4种情况：
    //1、获取到题目
    //2、已完成答题（失败或成功）
    //3、提示是否继续答题
    //4、未登录
    fun getDayQuestion() {
        coroutineScope.launchSafety {
            val html = dayQuestionRepository.getDayQuestion() ?: ""
            if (html.contains("答题规则")) {
                //获取到题目
                runCatching {
                    val document = Jsoup.parse(html)

                    val questionBean = DayQuestionEntity().apply {
                        description = document.select("div[class=bm bw0]").select("div")[1].text()
                        checkPoint = document.select("div[class=bm bw0]").select("form[id=myform]").select("div").select("span").select("font").eachText()[0]
                        questionTitle = document.select("div[class=bm bw0]").select("form[id=myform]").select("div").select("span").select("font").eachText()[1]

                        val formHash = document.select("div[class=bm bw0]").select("form[id=myform]").select("input[name=formhash]").attr("value")
                        DataStore.legacyForumHash = formHash

                        if (checkPoint.isNotNullAndEmpty()) {
                            val matcher = Pattern.compile("(.*)(\\d)(.*)(\\d)(.*)").matcher(checkPoint!!)
                            if (matcher.find()) {
                                questionNum = matcher.group(2).toIntOrElse(0)
                            }
                        }

                        val optionsHtml = document.select("div[class=bm bw0]").select("form[id=myform]").select("div[class=qs_option]")
                        for (i in optionsHtml.indices) {
                            options.add(
                                DayQuestionEntity.Options(
                                    answerValue = optionsHtml[i].select("input[name=answer]").attr("value"),
                                    dsp = optionsHtml[i].text(),
                                    answerChecked = false
                                )
                            )
                        }
                    }

                    view?.onGetDayQuestionSuccess(questionBean)
                }.onFailure {
                    view?.onGetDayQuestionError("获取题目失败：${it.message}", false)
                }
            } else if (html.contains("明天再来")) {
                //已完成答题
                view?.onDayQuestionFinished("今天已经答过题啦，明天再来吧")
            } else if (html.contains("闯关确认")) {
                //提示是否继续答题
                runCatching {
                    val document = Jsoup.parse(html)
                    val dsp = document.select("div[class=bm bw0]").select("div").select("span").text()
                    val formHash = document.select("div[class=bm bw0]").select("form[id=myform]").select("input[name=formhash]").attr("value")
                    DataStore.legacyForumHash = formHash
                    view?.onGetConfirmDspSuccess(dsp)
                }.onFailure {
                    view?.onGetConfirmDspError("加载闯关信息失败：${it.message}")
                }
            } else if (html.contains("登录后方可进入")) {
                view?.onGetDayQuestionError("请重新登录", false)
            } else if (html.contains("通关奖励")) {
                runCatching {
                    val document = Jsoup.parse(html)
                    val dsp = document.select("div[class=bm bw0]").select("div").select("span").text()
                    val formHash = document.select("div[class=bm bw0]").select("form[id=myform]").select("input[name=formhash]").attr("value")
                    DataStore.legacyForumHash = formHash
                    view?.onFinishedAllCorrect(dsp)
                }.onFailure {
                    view?.onGetDayQuestionError("加载通关信息失败：${it.message}", false)
                }
            } else if (html.contains("您的积分不足以")) {
                view?.onGetDayQuestionError(
                    "您的积分不足以支付答错惩罚，无法进行答题，至少需要拥有10水滴才可以参与答题！",
                    false
                )
            } else {
                view?.onGetDayQuestionError("未知错误", false)
            }
        }.onCatch {
            view?.onGetDayQuestionError("获取题目失败：${it.message}", true)
        }
    }

    //确认继续答题
    fun confirmNextQuestion() {
        coroutineScope.launchSafety {
            val html = dayQuestionRepository.confirmNextQuestion() ?: ""
            if (html.contains("正在为您准备")) {
                view?.onConfirmNextSuccess()
            } else {
                view?.onConfirmNextError("确认下一题失败")
            }
        }.onCatch {
            view?.onConfirmNextError("确认下一题失败：${it.message}")
        }
    }

    //提交答案
    fun submitQuestion(answerId: String, question: String?, answerStr: String?) {
        coroutineScope.launchSafety {
            val html = dayQuestionRepository.submitQuestion(answerId) ?: ""
            if (html.contains("闯关成功")) {
                view?.onAnswerCorrect(question, answerStr)
            } else if (html.contains("闯关失败")) {
                view?.onAnswerIncorrect("答题错误，闯关失败，已扣除水滴。不要灰心，明天再来吧")
            } else {
                view?.onAnswerError("遇到了一个错误")
            }
        }.onCatch {
            view?.onAnswerError("遇到了一个错误：${it.message}")
        }
    }

    //终止答题，领取奖励
    fun confirmFinishQuestion() {
        coroutineScope.launchSafety {
            val html = dayQuestionRepository.confirmFinishQuestion() ?: ""
            if (html.contains("完成了今日的")) {
                view?.onConfirmFinishSuccess("答题完成，奖励已发放，明天再来哦")
            } else {
                view?.onConfirmFinishError("领取奖励失败，请稍后再试")
            }
        }.onCatch {
            view?.onAnswerError("领取奖励失败：${it.message}")
        }
    }

    fun getQuestionAnswer(question: String?) {
        if (question != null) {
            val entity = dayQuestionRepository.dataBase.getDayQuestionDao().findAnswer(question)
            if (entity != null) {
                view?.onGetQuestionAnswerSuccess(entity.answer)
            } else {
                view?.onGetQuestionAnswerError("没有找到答案，请先手动记录！")
            }
        } else {
            view?.onGetQuestionAnswerError("获取答案失败，对应的题目为NULL")
        }
    }

    fun submitQuestionAnswer(question: String?, answer: String?) {
        if (question != null && answer != null) {
            dayQuestionRepository.dataBase.getDayQuestionDao().insert(
                DayQuestionDBEntity(
                    id = 0,
                    question = question,
                    answer = answer
                )
            )
        }
    }
}
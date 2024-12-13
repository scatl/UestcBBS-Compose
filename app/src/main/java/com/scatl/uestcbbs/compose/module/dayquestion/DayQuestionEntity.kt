package com.scatl.uestcbbs.compose.module.dayquestion

/**
 * Created by sca_tl at 2024/9/25 15:01:50
 */
data class DayQuestionEntity(
    var description: String? = null,
    var checkPoint: String? = null,
    var questionNum: Int = 0,
    var questionTitle: String? = null,
    var options: MutableList<Options> = mutableListOf(),
) {
    data class Options (
        var answerValue: String = "", //答案值，提交表单的answer
        var dsp: String? = null,
        var answerChecked: Boolean = false
    )
}

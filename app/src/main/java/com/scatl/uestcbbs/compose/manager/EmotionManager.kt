package com.scatl.uestcbbs.compose.manager

import android.content.Context
import com.scatl.uestcbbs.compose.ext.toIntOrElse

/**
 * Created by sca_tl on 2022/12/6 20:47
 */
object EmotionManager {

    private val emotions = mutableMapOf<String, List<EmotionItem>>()

    fun init(context: Context?) {
        for (i in 0..8) {
            val emotionItems = mutableListOf<EmotionItem>()
            context?.assets?.list("emotion/" + (i + 1))?.let {
                for (j in it.indices) {
                    val aPath = "file:///android_asset/emotion/" + (i + 1) + "/" + it[j]
                    val rPath = "emotion/" + (i + 1) + "/" + it[j]
                    emotionItems.add(
                        EmotionItem(
                            id = "\\d+".toRegex().find(it[j])?.value.toIntOrElse(),
                            fileName = it[j],
                            emotionName = when (i) {
                                0 -> "阿鲁"
                                1 -> "暴走"
                                2 -> "蛋黄脸"
                                3 -> "驴小毛"
                                4 -> "兔耳空"
                                5 -> "兔斯基"
                                6 -> "蘑菇头"
                                7 -> "洋葱头"
                                else -> "麻将脸"
                            },
                            aPath = aPath,
                            rPath = rPath
                        )
                    )
                }
            }
            emotions[(i + 1).toString()] = emotionItems
        }
    }

    fun getEmotionByName(name: String?): EmotionItem? {
        if (name.isNullOrEmpty()) {
            return null
        }
        emotions.forEach {
            it.value.forEach { item ->
                if (item.fileName == name) {
                    return item
                }
            }
        }
        return null
    }

    fun getEmotionById(id: String?): EmotionItem? {
        if (id.isNullOrEmpty()) {
            return null
        }
        emotions.forEach {
            it.value.forEach { item ->
                if (item.fileName.contains("_${id}]")) {
                    return item
                }
            }
        }
        return null
    }

    fun getEmotionPanelData(): List<Pair<EmotionItem, List<EmotionItem>>> {
        val result = mutableListOf<Pair<EmotionItem, List<EmotionItem>>>()
        emotions.forEach {
            result.add(Pair(it.value[1], it.value))
        }
        return result
    }

    data class EmotionItem(
        var id: Int,
        var fileName: String,
        var emotionName: String,
        var aPath: String,
        var rPath: String
    )
}
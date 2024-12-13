package com.scatl.uestcbbs.compose.manager

import com.scatl.uestcbbs.compose.api.entity.IndexEntity.Forum

/**
 * Created by sca_tl at 2024/7/11 20:03:29
 */
object ForumCategoryManager {

    private var forumList: MutableList<Forum> = mutableListOf() //扁平化处理后的板块数据
    var originData: List<Forum> = mutableListOf()
        private set

    fun initData(forumList: List<Forum>) {
        this.originData = forumList
        this.forumList.clear()
        forumList.forEach { forum ->
            initializeParent(forum, null)
        }
    }

    private fun initializeParent(forum: Forum, parent: Forum?) {
        forum.parent = parent
        this.forumList.add(forum)

        forum.children?.forEach { child ->
            initializeParent(child, forum)
        }
    }

    fun getForum(fid: Int?): Forum? {
        return this.forumList.find { it.fid == fid }
    }

    fun getRootForum(fid: Int?): Forum? {
        var current: Forum? = getForum(fid) ?: return null
        while (current?.parent != null) {
            current = current.parent
        }
        return current
    }

    fun getSecondaryRootForum(fid: Int?): Forum? {
        var current: Forum? = getForum(fid) ?: return null
        while (current?.parent?.parent != null) {
            current = current.parent
        }
        return if (current?.parent != null) current else null
    }

//    fun getSelectForumData(): List<Forum> {
//        val root = mutableListOf<Forum>()
//        originData.forEach {
//            root.add(it)
//            if (it.children == null) {
//                it.children = mutableListOf()
//            }
//        }
//    }
}
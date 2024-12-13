package com.scatl.uestcbbs.compose.init.task

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

/**
 * Created by sca_tl at 2024/5/27 19:43:45
 */
class TaskInitializer @Inject constructor (
    private val getIndexDataTask: GetIndexDataTask,
    private val bbsSettingsTask: BBSSettingsTask,
    private val msgSummaryTask: MsgSummaryTask,
    private val dataBaseTask: DataBaseTask
) {

    private val tasks = mutableListOf<Task>()

    fun init() {
        addTask(getIndexDataTask)
        addTask(bbsSettingsTask)
        addTask(msgSummaryTask)
        addTask(dataBaseTask)
    }

    private fun addTask(task: Task) {
        task.execute()
        tasks.add(task)
    }

    fun removeTask(taskClass: KClass<out Task>) {
        val t = getTask(taskClass)
        t?.cancel()
        tasks.remove(getTask(taskClass))
    }

    fun getTask(taskClass: KClass<out Task>): Task? {
        return tasks.find { it::class == taskClass }
    }

    fun restart(taskClass: KClass<out Task>) {
        val t = getTask(taskClass)
        t?.cancel()
        t?.execute()
    }

}
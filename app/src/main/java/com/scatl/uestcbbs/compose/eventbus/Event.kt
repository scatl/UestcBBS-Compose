package com.scatl.uestcbbs.compose.eventbus

/**
 * Created by sca_tl at 2024/7/12 15:23:22
 */
object Event {
    const val CHANGE_MAIN_NAVIGATION_VISIBILITY = "CHANGE_MAIN_NAVIGATION_VISIBILITY"
    const val HOME_REFRESH = "HOME_REFRESH"
}

sealed class BaseEvent {
    data class MainNavVisibleEvent(val visible: Boolean?): BaseEvent()
}
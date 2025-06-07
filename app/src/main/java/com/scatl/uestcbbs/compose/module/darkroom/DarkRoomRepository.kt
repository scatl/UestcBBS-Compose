package com.scatl.uestcbbs.compose.module.darkroom

import com.scatl.uestcbbs.compose.base.BaseRepository
import javax.inject.Inject

/**
 * Created by sca_tl at 2025/6/4 20:07:41
 */
class DarkRoomRepository @Inject constructor(): BaseRepository() {

    suspend fun getDarkRoomList(
        cid: String = "",
        t: String = ""
    ) = legacyService.darkRoomList(cid, t)

}
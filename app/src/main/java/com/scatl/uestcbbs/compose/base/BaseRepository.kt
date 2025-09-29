package com.scatl.uestcbbs.compose.base

import com.scatl.uestcbbs.compose.api.service.AuthService
import com.scatl.uestcbbs.compose.api.service.BingService
import com.scatl.uestcbbs.compose.api.service.CollectionService
import com.scatl.uestcbbs.compose.api.service.ForumService
import com.scatl.uestcbbs.compose.api.service.IndexService
import com.scatl.uestcbbs.compose.api.service.LegacyService
import com.scatl.uestcbbs.compose.api.service.MessageService
import com.scatl.uestcbbs.compose.api.service.PostService
import com.scatl.uestcbbs.compose.api.service.SearchService
import com.scatl.uestcbbs.compose.api.service.SystemService
import com.scatl.uestcbbs.compose.api.service.TopListService
import com.scatl.uestcbbs.compose.api.service.UserService
import com.scatl.uestcbbs.compose.db.AppDataBase
import com.scatl.uestcbbs.compose.init.task.TaskInitializer
import javax.inject.Inject

/**
 * created by sca_tl at 2023/7/23 21:43
 */
open class BaseRepository @Inject constructor() {

    @Inject lateinit var bingService: BingService
    @Inject lateinit var authService: AuthService
    @Inject lateinit var topListService: TopListService
    @Inject lateinit var indexService: IndexService
    @Inject lateinit var userService: UserService
    @Inject lateinit var systemService: SystemService
    @Inject lateinit var postService: PostService
    @Inject lateinit var searchService: SearchService
    @Inject lateinit var formService: ForumService
    @Inject lateinit var messageService: MessageService
    @Inject lateinit var legacyService: LegacyService
    @Inject lateinit var collectionService: CollectionService

    @Inject lateinit var dataBase: AppDataBase

}
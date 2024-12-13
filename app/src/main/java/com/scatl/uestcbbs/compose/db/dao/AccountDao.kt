package com.scatl.uestcbbs.compose.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.scatl.uestcbbs.compose.db.entity.AccountDBEntity

/**
 * Created by sca_tl at 2024/7/31 22:29
 */
@Dao
interface AccountDao: BaseDao<AccountDBEntity> {

    @Query("select * from account where uid = :uid")
    fun findAllById(uid: String): List<AccountDBEntity>

    @Query("select * from account where name = :name")
    fun findAllByName(name: String): List<AccountDBEntity>

    @Query("select * from account where signedIn = 1 limit 1")
    fun getSignedInAccount(): AccountDBEntity?

    @Query("update account set signedIn = 1 where uid = :uid and name = :name")
    fun setSignedIn(uid: String?, name: String?)

    @Query("update account set signedIn = 0")
    fun setAllSignedOut()

    @Query("select * from account")
    fun getAllAccounts(): List<AccountDBEntity>

    fun findFirstByUid(uid: String) = findAllById(uid).getOrNull(0)

    fun findFirstByName(name: String) = findAllByName(name).getOrNull(0)

}
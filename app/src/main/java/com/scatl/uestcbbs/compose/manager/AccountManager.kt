package com.scatl.uestcbbs.compose.manager

import com.scatl.uestcbbs.compose.db.entity.AccountDBEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AccountManager {

    private val _signedInAccount = MutableStateFlow<AccountDBEntity?>(null)
    val signedInAccount: StateFlow<AccountDBEntity?> = _signedInAccount

    fun toggleSigned(account: AccountDBEntity?) {
        _signedInAccount.value = account
    }

    fun getSignedInAccount() = signedInAccount.value
}
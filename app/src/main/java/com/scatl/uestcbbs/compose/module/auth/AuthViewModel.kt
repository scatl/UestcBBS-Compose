package com.scatl.uestcbbs.compose.module.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scatl.uestcbbs.compose.widget.refresh.UiState
import com.scatl.uestcbbs.compose.api.entity.SignInEntity
import com.scatl.uestcbbs.compose.api.entity.request.LoginRequestEntity
import com.scatl.uestcbbs.compose.db.entity.AccountDBEntity
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.toAvatarUrl
import com.scatl.uestcbbs.compose.init.task.MsgSummaryTask
import com.scatl.uestcbbs.compose.init.task.TaskInitializer
import com.scatl.uestcbbs.compose.manager.AccountManager
import com.scatl.uestcbbs.compose.manager.MessageManager
import com.scatl.uestcbbs.compose.manager.MessageUnreadEntity
import com.scatl.uestcbbs.compose.net.onFailure
import com.scatl.uestcbbs.compose.net.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Created by sca_tl at 2024/4/23 9:29:56
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    val authRepository: AuthRepository,
    val taskInitializer: TaskInitializer
): ViewModel()  {

    private val _signInData = MutableStateFlow(UiState<SignInEntity>().init())
    val signInData: StateFlow<UiState<SignInEntity>> = _signInData

    fun signIn(
        hCaptchaToken: String? = "",
        reCaptchaToken: String? = "",
        requestBody: LoginRequestEntity,
        signInAfterAdd: Boolean
    ) {
        viewModelScope.launchSafety {
            if (hCaptchaToken.isNullOrEmpty().not()) {
                authRepository.signInWithHcaptcha(hCaptchaToken, requestBody)
            } else {
                authRepository.signInWithRecaptcha(reCaptchaToken, requestBody)
            }
                .onSuccess {
                    if (it?.authorization.isNullOrEmpty()) {
                        _signInData.value = UiState<SignInEntity>().error(
                            errorData = Throwable("Authorization is empty")
                        )
                    } else {
                        _signInData.value = UiState<SignInEntity>().success(
                            data = it
                        )
                        authRepository.dataBase.getAccountDao().insert(
                            AccountDBEntity(
                                id = 0,
                                name = requestBody.username,
                                uid = it.uid,
                                token = it.authorization,
                                cookies = it.cookie,
                                icon = it.uid?.toIntOrNull().toAvatarUrl(),
                                signedIn = signInAfterAdd
                            )
                        )
                        if (signInAfterAdd) {
                            switchAccount(uid = it.uid, name = requestBody.username)
                            AccountManager.toggleSigned(authRepository.dataBase.getAccountDao().getSignedInAccount())
                        }
                    }
                }
                .onFailure {
                    _signInData.value = UiState<SignInEntity>().error(
                        errorData = Throwable(it.message)
                    )
                }
        }.onCatch {
            _signInData.value = UiState<SignInEntity>().error(
                errorData = it
            )
        }
    }

    fun switchAccount(uid: String?, name: String?) {
        authRepository.dataBase.getAccountDao().setAllSignedOut()
        authRepository.dataBase.getAccountDao().setSignedIn(uid = uid, name = name)
        AccountManager.toggleSigned(authRepository.dataBase.getAccountDao().getSignedInAccount())
        MessageManager.toggleUnread(MessageUnreadEntity())
        taskInitializer.restart(MsgSummaryTask::class)
    }

}
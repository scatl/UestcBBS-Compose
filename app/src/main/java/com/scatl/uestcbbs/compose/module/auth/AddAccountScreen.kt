package com.scatl.uestcbbs.compose.module.auth

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hcaptcha.sdk.HCaptchaConfig
import com.hcaptcha.sdk.HCaptchaResponse
import com.scatl.uestcbbs.compose.Constants
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.request.LoginRequestEntity
import com.scatl.uestcbbs.compose.ext.removeAllBlank
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.widget.LoadingDialog
import com.scatl.uestcbbs.compose.widget.RoundCheckBox
import com.scatl.uestcbbs.compose.widget.RoundCheckBoxDefaults

/**
 * Created by sca_tl at 2024/7/9 14:03:36
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddAccountScreen() {
    val viewModel: AuthViewModel = hiltViewModel()
    val signInData by viewModel.signInData.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var userName by remember { mutableStateOf(" ") }
    var password by remember { mutableStateOf(" ") }
    var hCaptchaToken by remember { mutableStateOf("") }
    var showHCaptchaDialog by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var signInAfterAdd by remember { mutableStateOf(true) }
    var ruleAgree by remember { mutableStateOf(true) }

    val appIcon = remember { mutableStateOf<Drawable?>(null) }

    LaunchedEffect(Unit) {
        appIcon.value = context.packageManager?.getApplicationIcon(context.packageName)
    }

    LaunchedEffect(key1 = hCaptchaToken) {
        if (hCaptchaToken.isNotEmpty()) {
            viewModel.signIn(
                hCaptcha = hCaptchaToken,
                requestBody = LoginRequestEntity(userName, password),
                signInAfterAdd = signInAfterAdd
            )
        }
    }

    LaunchedEffect(key1 = signInData) {
        showLoadingDialog = false
        if (signInData.isSuccess) {
            if (signInAfterAdd) {
                navHostController.navigate(Router.MainRouterEntity) {
                    popUpTo(Router.AccountManageRouterEntity) {
                        inclusive = true
                    }
                    popUpTo(Router.AddAccountRouterEntity) {
                        inclusive = true
                    }
                }
            } else {
                navHostController.navigate(Router.AccountManageRouterEntity) {
                    popUpTo(Router.AddAccountRouterEntity) {
                        inclusive = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        } else if (!signInData.errorData?.message.isNullOrEmpty()) {
            signInData.errorData?.message.showToast(context)
        }
    }

    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                appIcon.value?.toBitmap()?.asImageBitmap()?.let {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "清水河畔论坛第三方客户端",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .alpha(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(100.dp))

                OutlinedTextField(
                    value = userName.removeAllBlank().toString(),
                    onValueChange = {
                        userName = it
                    },
                    label = { Text(text = stringResource(R.string.user_name)) },
                    isError = userName.isEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                )

                Text(
                    text = if (userName.isEmpty()) {
                        stringResource(id = R.string.user_name_empty_hint)
                    } else {
                        ""
                    },
                    modifier = Modifier
                        .height(30.dp)
                        .padding(horizontal = 40.dp)
                        .align(Alignment.Start),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.error
                )

                OutlinedTextField(
                    value = password.removeAllBlank().toString(),
                    onValueChange = {
                        password = it
                    },
                    label = { Text(text = stringResource(R.string.password)) },
                    singleLine = true,
                    isError = password.isEmpty(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible }
                        ) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                )
                Text(
                    text = if (password.isEmpty()) stringResource(R.string.password_empty_hint) else "",
                    modifier = Modifier
                        .height(20.dp)
                        .padding(horizontal = 40.dp)
                        .align(Alignment.Start),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(40.dp))

                Column (
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        RoundCheckBox(
                            isChecked = signInAfterAdd,
                            onClick = {
                                signInAfterAdd = signInAfterAdd.not()
                            },
                            color = RoundCheckBoxDefaults.colors(
                                borderColor = MaterialTheme.colorScheme.primary,
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = stringResource(id = R.string.sign_in_after_account_added),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable (
                                    interactionSource = null,
                                    indication = null,
                                    onClick = {
                                        signInAfterAdd = signInAfterAdd.not()
                                    }
                                )
                        )
                    }

//                    Row (
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(5.dp)
//                    ) {
//                        RoundCheckBox(
//                            isChecked = ruleAgree,
//                            onClick = {
//                                ruleAgree = ruleAgree.not()
//                            },
//                            color = RoundCheckBoxDefaults.colors(
//                                borderColor = MaterialTheme.colorScheme.primary,
//                                selectedColor = MaterialTheme.colorScheme.primary
//                            )
//                        )
//                        Text(
//                            text = buildAnnotatedString {
//                                append("我已阅读并同意《清水河畔总版规》")
//                                addLink(
//                                    clickable = LinkAnnotation.Clickable(
//                                        tag = "subject",
//                                        styles = TextLinkStyles(
//                                            style = SpanStyle(color = MaterialTheme.colorScheme.primary)
//                                        ),
//                                        linkInteractionListener = {
//                                            navHostController.navigate(Router.ThreadDetailRouterEntity(
//                                                id = 752718,
//                                            ))
//                                        }
//                                    ),
//                                    start = 7,
//                                    end = 16
//                                )
//                            },
//                            fontSize = 14.sp,
//                            modifier = Modifier
//                                .clickable (
//                                    interactionSource = null,
//                                    indication = null,
//                                    onClick = {
//                                        ruleAgree = ruleAgree.not()
//                                    }
//                                )
//                        )
//                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    enabled = !userName.removeAllBlank().isNullOrEmpty()
                            && !password.removeAllBlank().isNullOrEmpty()
                            && ruleAgree,
                    onClick = {
                        keyboardController?.hide()
                        showHCaptchaDialog = !showHCaptchaDialog
                    }
                ) {
                    Text(
                        text = stringResource(
                            if (signInAfterAdd) R.string.login else R.string.add_account
                        ).toUpperCase(Locale.current),
                        modifier = Modifier.animateContentSize()
                    )
                }
            }

            LoadingDialog(
                showDialog = showLoadingDialog,
                cancelable = false,
                text = stringResource(R.string.add_account_process),
                onDismissRequest = {
                    showLoadingDialog = false
                }
            )

            HCaptchaDialog(
                showDialog = showHCaptchaDialog,
                onDismissRequest = {
                    showHCaptchaDialog = false
                },
                config = HCaptchaConfig
                    .builder()
                    .siteKey(Constants.HCAPTCHA_SITE_KEY)
                    .build(),
                onResult = {
                    when (it) {
                        is HCaptchaResponse.Success -> {
                            hCaptchaToken = it.token
                            showHCaptchaDialog = false
                            showLoadingDialog = true
                        }
                        is HCaptchaResponse.Failure -> {
                            showHCaptchaDialog = false
                            ContextCompat.getString(context, R.string.add_account_fail).showToast(context)
                        }
                        else -> { }
                    }
                }
            )
        }
    )

}
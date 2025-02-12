package com.scatl.uestcbbs.compose.module.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.db.entity.AccountDBEntity
import com.scatl.uestcbbs.compose.ext.cardCorner
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.commonCardBg
import com.scatl.uestcbbs.compose.ext.navigateAndClean
import com.scatl.uestcbbs.compose.ext.pagePadding
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.router.LocalNavController
import com.scatl.uestcbbs.compose.router.Router
import com.scatl.uestcbbs.compose.widget.CommonAlertDialog
import com.scatl.uestcbbs.compose.widget.TIP_ID_ACCOUNT_MANAGE
import com.scatl.uestcbbs.compose.widget.TIP_ID_RATE
import com.scatl.uestcbbs.compose.widget.Tip
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Created by sca_tl at 2024/8/1 15:18:04
 */
@Composable
fun AccountManageScreen() {
    val viewModel: AuthViewModel = hiltViewModel()
    val context = LocalContext.current
    val navHostController = LocalNavController.current
    val accounts = viewModel.authRepository.dataBase.getAccountDao().getAllAccounts()
    val showDeleteDialog = remember { mutableStateOf(false) }
    val longPressAccount = remember { mutableStateOf<AccountDBEntity?>(null) }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(top = 100.dp, bottom = 150.dp)
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.account_manage),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Tip(
            tip = stringResource(id = R.string.account_manage_dsp),
            tipId = TIP_ID_ACCOUNT_MANAGE,
            confirmText = null
        )

        LazyColumn {
            itemsIndexed(accounts) { _, item ->
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .commonCardBg()
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AsyncImage(
                            model = item.icon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(shape = RoundedCornerShape(50))
                        )
                        Text(
                            text = item.name.toString(),
                            fontSize = 16.sp
                        )
                    }

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (item.signedIn == true) {
                            Text(
                                text = stringResource(id = R.string.logout),
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable (unbound = false) {
                                        viewModel.authRepository.dataBase.getAccountDao().setAllSignedOut()
                                        navHostController.navigateAndClean(Router.AccountManageRouterEntity)
                                    }
                                    .background(
                                        color = Color.Red.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(cardCorner)
                                    )
                                    .padding(horizontal = 15.dp, vertical = 2.dp)
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.login),
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable (
                                        unbound = false
                                    ) {
                                        viewModel.switchAccount(uid = item.uid, name = item.name)
                                        navHostController.navigateAndClean(Router.MainRouterEntity)
                                    }
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(cardCorner)
                                    )
                                    .padding(horizontal = 15.dp, vertical = 2.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable(unbound = true) {
                                    longPressAccount.value = item
                                    showDeleteDialog.value = true
                                }
                        )
                    }
                }
            }

            item {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                    modifier = Modifier
                        .commonCardBg {
                            navHostController.navigate(Router.AddAccountRouterEntity)
                        }
                        .alpha(alpha = 0.7f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AddCircleOutline,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.add_account),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    CommonAlertDialog(
        showDialog = showDeleteDialog.value,
        title = stringResource(id = R.string.delete_account),
        text =  if (longPressAccount.value?.signedIn == true) {
            stringResource(id = R.string.account_delete_dialog_dsp_logged, longPressAccount.value?.name.toString())
        } else {
            stringResource(id = R.string.account_delete_dialog_dsp, longPressAccount.value?.name.toString())
        },
        onDismissRequest = {
            showDeleteDialog.value = false
        },
        onConfirmClick = {
            if (longPressAccount.value?.signedIn == true) {
                navHostController.navigateAndClean(Router.AccountManageRouterEntity)
            }
            longPressAccount.value?.let {
                viewModel.authRepository.dataBase.getAccountDao().delete(it)
            }
            longPressAccount.value = null
            showDeleteDialog.value = false
            ContextCompat.getString(context, R.string.account_delete_success).showToast(context)
        }
    )
}
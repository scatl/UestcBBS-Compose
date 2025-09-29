package com.scatl.uestcbbs.compose.module.post.bottomsheet

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.api.entity.CollectionEntity
import com.scatl.uestcbbs.compose.ext.LoadInitialDataIfNeeded
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.launchSafety
import com.scatl.uestcbbs.compose.ext.showToast
import com.scatl.uestcbbs.compose.ext.toIntOrElse
import com.scatl.uestcbbs.compose.ext.unboundClickable
import com.scatl.uestcbbs.compose.module.collection.CollectionViewModel
import com.scatl.uestcbbs.compose.module.collection.CreateCollectionBottomSheet
import com.scatl.uestcbbs.compose.widget.IconTitle
import com.scatl.uestcbbs.compose.widget.StatusLayout

/**
 * Created by sca_tl at 2025/2/8 13:55:24
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToCollectionBottomSheet(
    addToCollectionBottomSheet: MutableState<Boolean>,
    addToCollectionSheetState: SheetState,
    tid: Int
) {
    if (addToCollectionBottomSheet.value) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val viewModel: CollectionViewModel = hiltViewModel()
        val myCollectionListData by viewModel.myCollectionListData.collectAsStateWithLifecycle()
        val addToCollectionData by viewModel.addToCollectionData.collectAsStateWithLifecycle()
        val openCreateCollectionBottomSheet = rememberSaveable { mutableStateOf(false) }
        val currentSelected = rememberSaveable { mutableStateOf<CollectionEntity?>(null) }
        val showMenu = rememberSaveable { mutableStateOf(false) }

        val rotationDegree by animateFloatAsState(
            targetValue = if (showMenu.value) 180f else 0f,
            animationSpec = tween(durationMillis = 500),
            label = "arrow_rotation"
        )

        fun hide() {
            scope.launchSafety {
                addToCollectionSheetState.hide()
                addToCollectionBottomSheet.value = false
            }
        }

        LoadInitialDataIfNeeded(context) {
            scope.launchSafety {
                viewModel.getMyCollectionList(tid)
            }
        }

        LaunchedEffect(myCollectionListData.data) {
            currentSelected.value = myCollectionListData.data?.getOrNull(0)
        }

        LaunchedEffect(addToCollectionData) {
            if (addToCollectionData.data != null) {
                if (addToCollectionData.isSuccess) {
                    ContextCompat.getString(context, R.string.collection_add_to_success).showToast(context)
                    hide()
                } else {
                    (addToCollectionData.errorData?.message ?:
                    ContextCompat.getString(context, R.string.collection_add_to_fail)).showToast(context)
                }
                viewModel.resetAddToCollectionData()
            }
        }

        ModalBottomSheet(
            onDismissRequest = {
                addToCollectionBottomSheet.value = false
            },
            sheetState = addToCollectionSheetState
        ) {
            StatusLayout(
                uiState = myCollectionListData,
                loadingModifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                emptyModifier = Modifier.fillMaxWidth()
            ) {
                if (myCollectionListData.data?.isEmpty() == true) {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 100.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.collection_none_dsp)
                        )
                        Button(
                            onClick = {
                                openCreateCollectionBottomSheet.value = true
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.create)
                            )
                        }
                    }
                } else {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 50.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.collection_add_to),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Row {
                            Text(
                                text = stringResource(R.string.collection_add_to_select)
                            )
                            Box {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier
                                        .animateContentSize()
                                        .unboundClickable {
                                            showMenu.value = !showMenu.value
                                        }
                                ) {
                                    Text(
                                        text = currentSelected.value?.name.toString(),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(
                                        imageVector = Icons.Outlined.KeyboardArrowDown,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .rotate(rotationDegree)
                                    )
                                }

                                DropdownMenu(
                                    expanded = showMenu.value,
                                    onDismissRequest = { showMenu.value = false },
                                ) {
                                    myCollectionListData.data?.forEach {
                                        DropdownMenuItem(
                                            text = { Text(text = it.name.toString()) },
                                            onClick = {
                                                showMenu.value = false
                                                currentSelected.value = it
                                            },
                                            enabled = it.isFavorite != true,
                                            trailingIcon = {
                                                if (it.isOwner == true) {
                                                    IconTitle(
                                                        icon = Icons.Outlined.PersonOutline,
                                                        iconTint = MaterialTheme.colorScheme.outline,
                                                        gap = 0.dp,
                                                        iconSize = 18.dp,
                                                        text = "(Owner)",
                                                        textStyle = TextStyle(
                                                            fontSize = 14.sp,
                                                            color = MaterialTheme.colorScheme.outline
                                                        )
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = stringResource(R.string.collection_add_to_dsp),
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(70.dp))
                        Button (
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            onClick = {
                                viewModel.addThreadToCollection(currentSelected.value?.collectionId.toIntOrElse(), tid)
                            }
                        ) {
                            IconTitle(
                                icon = Icons.Outlined.AddCircleOutline,
                                iconSize = 20.dp,
                                text = stringResource(R.string.confirm),
                                gap = 4.dp,
                                textStyle = TextStyle()
                            )
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = stringResource(R.string.collection_create_title),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .clickable(unbound = true) {
                                    openCreateCollectionBottomSheet.value = true
                                }
                        )
                    }
                }
            }
        }

        CreateCollectionBottomSheet(
            openCreateCollectionBottomSheet = openCreateCollectionBottomSheet,
            onCreateSuccess = {
                viewModel.getMyCollectionList(tid)
            }
        )
    }
}
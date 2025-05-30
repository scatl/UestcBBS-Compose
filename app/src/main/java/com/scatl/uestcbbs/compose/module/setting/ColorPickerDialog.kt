package com.scatl.uestcbbs.compose.module.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicColorScheme
import com.scatl.uestcbbs.compose.R
import com.scatl.uestcbbs.compose.datastore.DataStore
import com.scatl.uestcbbs.compose.ext.hexToColor
import com.scatl.uestcbbs.compose.ext.toHexWithAlpha
import com.scatl.uestcbbs.compose.manager.ThemeManager
import com.scatl.uestcbbs.compose.widget.ColorPicker
import com.scatl.uestcbbs.compose.widget.SingleSelectionDialog

/**
 * Created by sca_tl at 2024/7/1 16:43:10
 */
@Composable
fun ColorPickerDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    initColor: Color? = null,
    onColorSelect: (seedColor: String, scheme: String) -> Unit
) {
    if (showDialog) {
        var selectedColor by remember { mutableStateOf(initColor?.toHexWithAlpha() ?: "#00000000") }
        val showSchemeDialog = rememberSaveable { mutableStateOf(false) }
        val currentScheme = rememberSaveable { mutableStateOf(DataStore.customThemeScheme) }

        val dynamicColor = rememberDynamicColorScheme(
            seedColor = selectedColor.hexToColor(),
            style = PaletteStyle.entries.find { it.name == currentScheme.value } ?: PaletteStyle.Fidelity,
            isDark = ThemeManager.isAppDarkMode,
        )

        Dialog(onDismissRequest = onDismissRequest) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(start = 0.dp, end = 0.dp, top = 20.dp, bottom = 0.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ColorLens,
                        modifier = Modifier.size(22.dp),
                        contentDescription = null
                    )
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Text(
                    text = stringResource(id = R.string.setting_custom_theme_dialog_dsp),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .alpha(0.6f)
                        .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    ColorPicker(
                        modifier = Modifier.size(180.dp),
                        initialColor = initColor ?: Color.Transparent,
                        showAlphaSlider = false,
                        showBrightnessSlider = false,
                        onColorChanged = {
                            selectedColor = "#${it.hexCode}"
                        }
                    )
                }

                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp)
                ) {
                    items(3) { index ->
                        val name: String
                        val color: Color
                        when (index) {
                            0 -> {
                                name = stringResource(id = R.string.setting_primary_color)
                                color = dynamicColor.primary
                            }
                            1 -> {
                                name = stringResource(id = R.string.setting_secondary_color)
                                color = dynamicColor.secondary
                            }
                            else -> {
                                name = stringResource(id = R.string.setting_tertiary_color)
                                color = dynamicColor.tertiary
                            }
                        }
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = name,
                                fontSize = 13.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        color = color,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                ) {
                    TextButton(
                        onClick = {
                            showSchemeDialog.value = true
                        }
                    ) {
                        Text(text = "Scheme")
                    }

                    Row {
                        TextButton(
                            onClick = {
                                onColorSelect(selectedColor, currentScheme.value)
                            }
                        ) {
                            Text(text = stringResource(id = R.string.apply))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        TextButton(
                            onClick = {
                                onColorSelect(selectedColor, currentScheme.value)
                                onDismissRequest()
                            }
                        ) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    }
                }
            }
        }

        val schemes = mutableListOf<Pair<String, String>>()
        PaletteStyle.entries.forEach {
            schemes.add(Pair(it.name, it.name))
        }
        SingleSelectionDialog(
            showDialog = showSchemeDialog.value,
            data = schemes,
            title = "选择Scheme",
            icon = {
                Icon(
                    imageVector = Icons.Outlined.ColorLens,
                    modifier = Modifier.size(22.dp),
                    contentDescription = null
                )
            },
            selected = currentScheme.value,
            onDismissRequest = {
                showSchemeDialog.value = false
            }
        ) {
            showSchemeDialog.value = false
            currentScheme.value = it
        }
    }
}
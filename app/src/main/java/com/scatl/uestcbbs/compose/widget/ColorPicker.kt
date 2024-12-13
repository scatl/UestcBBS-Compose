package com.scatl.uestcbbs.compose.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.drawColorIndicator
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

/**
 * Created by sca_tl at 2024/6/28 10:48:39
 */
@Composable
fun ColorPicker(
    modifier: Modifier,
    controller: ColorPickerController = rememberColorPickerController(),
    onColorChanged: (colorEnvelope: ColorEnvelope) -> Unit,
    initialColor: Color? = null,
    showAlphaSlider: Boolean = true,
    showBrightnessSlider: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HsvColorPicker(
            modifier = modifier,
            controller = controller,
            onColorChanged = onColorChanged,
            initialColor = initialColor,
            drawOnPosSelected = {
                drawColorIndicator(
                    controller.selectedPoint.value,
                    controller.selectedColor.value,
                )
            }
        )

        Spacer(modifier = Modifier.height(30.dp))

        if (showAlphaSlider) {
            AlphaSlider(
                modifier = Modifier
                    .testTag("HSV_AlphaSlider")
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(25.dp)
                    .align(Alignment.CenterHorizontally),
                controller = controller,
                tileSize = 6.dp,
                borderRadius = 15.dp,
                initialColor = initialColor
            )

            Spacer(modifier = Modifier.height(15.dp))
        }

        if (showBrightnessSlider) {
            BrightnessSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(25.dp)
                    .align(Alignment.CenterHorizontally),
                controller = controller,
                borderRadius = 15.dp,
                initialColor = initialColor
            )
        }
    }
}
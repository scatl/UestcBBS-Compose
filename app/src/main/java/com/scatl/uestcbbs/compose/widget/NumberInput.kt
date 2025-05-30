package com.scatl.uestcbbs.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.scatl.uestcbbs.compose.ext.clickable
import com.scatl.uestcbbs.compose.ext.toIntOrElse

/**
 * Created by sca_tl at 2024/9/26 14:34:51
 */
@Composable
fun NumberInput(
    modifier: Modifier = Modifier,
    initialValue: Int,
    minValue: Int,
    maxValue: Int,
    step: Int = 1,
    readOnly: Boolean = false,
    onValueChange: (Int) -> Unit
) {
    val adjustedInitialValue = initialValue.coerceIn(minValue, maxValue)
    var currentValue by remember { mutableStateOf(adjustedInitialValue.toString()) }

    key(currentValue) {
        if (currentValue.toIntOrElse() > maxValue) {
            currentValue = maxValue.toString()
        } else if (currentValue.toIntOrElse() < minValue) {
            currentValue = minValue.toString()
        }
        onValueChange(currentValue.toIntOrElse())
    }

    Row(
        modifier = modifier
            .wrapContentSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(30.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50)
                )
                .clickable(unbound = true) {
                    if (currentValue.toIntOrElse() - step >= minValue) {
                        currentValue = (currentValue.toIntOrElse() - step).toString()
                    }
                }
        ) {
            Text(
                text = "-",
                fontSize = 14.sp,
                lineHeight = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        CustomTextField(
            placeholder = {
                Text(
                    text = initialValue.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            },
            textStyle = TextStyle(
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .width(50.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            visualTransformation = numberFilter,
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            readOnly = readOnly,
            shape = RoundedCornerShape(20.dp),
            value = currentValue,
            maxLines = 1,
            onValueChange = {
                currentValue = it
            },
            contentPadding = PaddingValues(vertical = 5.dp, horizontal = 10.dp)
        )

        Box (
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(30.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50)
                )
                .clickable(unbound = true) {
                    if (currentValue.toIntOrElse() + step <= maxValue) {
                        currentValue = (currentValue.toIntOrElse() + step).toString()
                    }
                }
        ) {
            Text(
                text = "+",
                fontSize = 14.sp,
                lineHeight = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

val numberFilter = VisualTransformation { text ->
    val filteredText = text.text.filter { it.isDigit() }
    TransformedText(
        text = AnnotatedString(filteredText),
        offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return filteredText.take(offset).count()
            }
            override fun transformedToOriginal(offset: Int): Int {
                return filteredText.take(offset).count()
            }
        }
    )
}
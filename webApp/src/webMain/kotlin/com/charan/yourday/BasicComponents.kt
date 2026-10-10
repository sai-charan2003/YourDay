package com.charan.yourday

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.composeunstyled.UnstyledButton
import com.composeunstyled.theme.Theme

@Composable
fun CustomButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    UnstyledButton(
        onClick = onClick,
        modifier = modifier.clip(RoundedCornerShape(8.dp))
            .background(Theme[colors][surfaceVariant])
            .border(1.dp, Theme[colors][outline], RoundedCornerShape(8.dp))
    ) {
        BasicText(
            text = text
        )
    }
}

@Composable
fun CustomText(
    text: String,
    modifier: Modifier = Modifier
) {
    BasicText(
        text = text,
        modifier = modifier,

    )
}
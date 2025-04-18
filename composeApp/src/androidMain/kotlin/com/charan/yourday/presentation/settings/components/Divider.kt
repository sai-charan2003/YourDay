package com.charan.yourday.presentation.settings.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SectionDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.surfaceContainer,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

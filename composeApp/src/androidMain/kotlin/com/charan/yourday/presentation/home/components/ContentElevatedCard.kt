package com.charan.yourday.presentation.home.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable

fun ContentElevatedCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit

    ) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .then(modifier)
    ) {
        Column(
            modifier = Modifier.padding(
                top = 15.dp,
                start = 10.dp,
                end = 10.dp,
                bottom = 15.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()

        }
    }
}
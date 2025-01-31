package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GrantPermissionContent(
    title : String,
    onClick : () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall
    )
    Button(onClick = {onClick()}) {
        Text("Grant Permission")
    }

}








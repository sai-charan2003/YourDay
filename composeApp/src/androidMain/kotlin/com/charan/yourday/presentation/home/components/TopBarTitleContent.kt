package com.charan.yourday.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.charan.yourday.utils.DateUtils

@Composable
fun TopBarTitleContent() {

    Column {
        Text(DateUtils.getGreeting())
        Text(DateUtils.getDateInDDMMYYYY(), style = MaterialTheme.typography.bodyLarge)
    }
}
package org.example.project.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ChatContent(modifier: Modifier = Modifier) {
    // TODO: 实现聊天内容列表
    Box(modifier = modifier.background(Color(0xFFF5F5F5))) {

        Text("聊天内容区域", modifier = Modifier.align(Alignment.Center))
    }
}

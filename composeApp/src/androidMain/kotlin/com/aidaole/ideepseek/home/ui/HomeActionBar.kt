package com.aidaole.ideepseek.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun HomeActionBarPreview(){
    HomeActionBar({})
}

@Composable
fun HomeActionBar(
    onMenuClick: () -> Unit
) {
    Row(
        Modifier
            .background(color = Color.White)
            .windowInsetsPadding(WindowInsets.statusBars)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧设置按钮
        IconButton(onClick = onMenuClick) {
            Icon(
                Icons.Default.Menu, contentDescription = "Menu", tint = Color.Black
            )
        }

        // 中间标题
        Text(
            "新对话", color = Color.Black, style = MaterialTheme.typography.body1
        )

        // 右侧新建聊天按钮
        IconButton(onClick = { /* TODO: 处理新建聊天点击 */ }) {
            Icon(
                Icons.Default.Add, contentDescription = "新建聊天", tint = Color.Black
            )
        }
    }
}
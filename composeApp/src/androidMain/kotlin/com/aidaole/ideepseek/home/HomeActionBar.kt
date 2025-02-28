package org.example.project.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun HomeActionBar() {
    TopAppBar(
        backgroundColor = Color.White,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧设置按钮
            IconButton(onClick = { /* TODO: 处理设置点击 */ }) {
                Icon(
                    Icons.Default.Menu, contentDescription = "设置", tint = Color.Black
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
}
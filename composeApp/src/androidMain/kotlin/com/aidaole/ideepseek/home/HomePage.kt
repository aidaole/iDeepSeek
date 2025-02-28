package org.example.project.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

@Composable
fun App() {
    MaterialTheme {
        Column(
            Modifier.fillMaxWidth()
        ) {
            // 顶部ActionBar
            HomeActionBar()

            // 中间聊天内容区域
            ChatContent(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            // 底部输入区域
            BottomInputArea()
        }
    }
}
package com.aidaole.ideepseek.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.aidaole.ideepseek.home.ui.BottomInputArea
import com.aidaole.ideepseek.home.ui.ChatContent
import com.aidaole.ideepseek.home.ui.HomeActionBar

@Preview("HomePage")
@Composable
fun AppPreview() {
    HomePage()
}

@Composable
fun HomePage() {
    val viewModel = ChatViewModel()
    
    MaterialTheme {
        Column(
            Modifier.fillMaxWidth()
        ) {
            // 顶部ActionBar
            HomeActionBar()

            // 中间聊天内容区域
            ChatContent(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                messages = viewModel.messages
            )

            // 底部输入区域
            BottomInputArea(
                onSendMessage = viewModel::sendMessage
            )
        }
    }
}
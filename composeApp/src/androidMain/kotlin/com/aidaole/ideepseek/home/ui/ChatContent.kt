package com.aidaole.ideepseek.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aidaole.ideepseek.home.ChatMessage

@Composable
@Preview
fun ChatContentPreview() {
    val messages = remember {
        listOf(
            ChatMessage("你好，我是 Claude", false),
            ChatMessage("你好，请问有什么可以帮你的吗？", true),
            ChatMessage("我可以帮你回答问题，或者协助你完成一些任务。", false),
            ChatMessage("太好了，我正好需要你帮我写一段代码", true)
        )
    }
    ChatContent(
        modifier = Modifier
            .height(400.dp)
            .fillMaxWidth(),
        messages = messages
    )
}

@Composable
fun ChatContent(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage> = emptyList()
) {
    val listState = rememberLazyListState()
    
    // 当消息列表更新时自动滚动到底部
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(messages) { message ->
            ChatMessageItem(message)
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(0.dp, 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (message.isUser) Color(0xFFE3F2FD) else Color.White
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.content,
                color = Color.Black
            )
        }
    }
}

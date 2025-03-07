package com.aidaole.ideepseek.home.ui

import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.aidaole.ideepseek.home.ChatMessage
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.syntax.Prism4jTheme
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    messages: List<ChatMessage>
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
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
    
    // 监听消息变化并滚动到底部
    LaunchedEffect(messages.size, messages.lastOrNull()?.content) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                delay(100) // 给一点时间让内容更新
                // 获取当前可见区域的高度
                val visibleHeight = listState.layoutInfo.viewportEndOffset
                // 获取整个内容的高度
                val contentHeight = listState.layoutInfo.totalItemsCount * visibleHeight
                // 计算需要滚动的距离
                val scrollDistance = contentHeight - visibleHeight
                // 平滑滚动
                listState.animateScrollBy(scrollDistance.toFloat())
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val context = LocalContext.current
    
    // 创建Markwon实例
    val markwon = remember {
        Markwon.builder(context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(HtmlPlugin.create())
            .usePlugin(LinkifyPlugin.create())
//            .usePlugin(SyntaxHighlightPlugin.create(Prism4j(), Prism4jTheme()))
            .build()
    }

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (message.isUser) {
                    // 用户消息使用普通Text
                    Text(
                        text = message.content,
                        color = Color.Black,
                        modifier = Modifier.weight(1f, false)
                    )
                } else {
                    // AI消息使用Markdown
                    AndroidView(
                        modifier = Modifier.weight(1f, false),
                        factory = { context ->
                            TextView(context).apply {
                                setTextColor(android.graphics.Color.BLACK)
                            }
                        },
                        update = { textView ->
                            markwon.setMarkdown(textView, message.content)
                        }
                    )
                }
                
                if (!message.isUser && message.isLoading && message.content.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF2196F3)
                    )
                }
            }
        }
    }
}

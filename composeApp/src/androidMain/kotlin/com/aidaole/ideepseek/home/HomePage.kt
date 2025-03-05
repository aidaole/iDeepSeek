package com.aidaole.ideepseek.home

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aidaole.ideepseek.api.AndroidDeepSeekApi
import com.aidaole.ideepseek.api.TokenManager
import com.aidaole.ideepseek.home.ui.BottomInputArea
import com.aidaole.ideepseek.home.ui.ChatContent
import com.aidaole.ideepseek.home.ui.HomeActionBar
import com.aidaole.ideepseek.home.ui.TokenInputDialog
import com.aidaole.ideepseek.db.ChatDatabaseManager
import com.aidaole.ideepseek.db.ChatSession
import com.aidaole.ideepseek.db.DatabaseDriverFactory
import kotlinx.coroutines.launch

@Preview("HomePage")
@Composable
fun AppPreview() {
    val context = Application()
    val tokenManager = TokenManager(context)
    val dbManager = ChatDatabaseManager(DatabaseDriverFactory(context))
    val api = AndroidDeepSeekApi(tokenManager, context)
    HomePage(ChatViewModel(api, dbManager))
}

@Composable
fun HomePage(viewModel: ChatViewModel) {
    // 收集 UI 状态
    val tokenState by viewModel.tokenState.collectAsState()

    // 只在需要 Token 时显示对话框
    if (tokenState is ChatViewModel.TokenState.NeedToken) {
        TokenInputDialog(
            onTokenSubmit = { token ->
                viewModel.setApiToken(token)
            },
            onDismiss = {
                // 可以选择退出应用,因为没有 token 无法使用
                // 或者显示提示信息
            }
        )
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    MaterialTheme {
        ModalDrawer(
            modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                    viewModel,
                    onClose = {
                        scope.launch { drawerState.close() }
                    }
                )
            },
            gesturesEnabled = true // 启用右滑手势
        ) {
            Column(
                Modifier.fillMaxWidth()
            ) {
                // 收集当前标题状态
                val currentTitle by viewModel.currentTitle.collectAsState()
                
                // 顶部ActionBar
                HomeActionBar(
                    title = currentTitle,
                    onNewChatClick = {
                        viewModel.createNewChat()
                    },
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )

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
}

@Composable
private fun DrawerContent(
    viewModel: ChatViewModel,
    onClose: () -> Unit
) {
    // 添加对话框显示状态
    var showTokenDialog by remember { mutableStateOf(false) }

    // 如果showTokenDialog为true，显示对话框
    if (showTokenDialog) {
        TokenInputDialog(
            onTokenSubmit = { token ->
                viewModel.setApiToken(token)
                showTokenDialog = false  // 提交后关闭对话框
            },
            onDismiss = {
                showTokenDialog = false  // 关闭对话框
            }
        )
    }
    val chatSessions by viewModel.chatSessions.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color.White)
    ) {
        // 抽屉头部
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "历史聊天记录",
                style = MaterialTheme.typography.body1,
                color = Color.Black
            )
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "关闭",
                    tint = Color.Black
                )
            }
        }

        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = Color.LightGray
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(chatSessions) { session ->
                ChatSessionItem(
                    session = session,
                    onClick = {
                        viewModel.loadChatSession(session.id)
                        onClose()
                    }
                )
            }
        }
        
        DrawerMenuItem(
            icon = Icons.Default.Info,
            text = "输入apikey",
            onClick = {
                showTokenDialog = true  // 点击时显示对话框
            }
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun ChatSessionItem(
    session: ChatSession,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = session.title,
            style = MaterialTheme.typography.body1,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = text,
            tint = Color.Black
        )
        Spacer(modifier = Modifier.width(32.dp))
        Text(
            text,
            style = MaterialTheme.typography.body1,
            color = Color.Black
        )
    }
}
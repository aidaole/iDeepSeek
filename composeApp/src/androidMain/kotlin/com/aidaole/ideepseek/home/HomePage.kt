package com.aidaole.ideepseek.home

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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aidaole.ideepseek.home.ui.BottomInputArea
import com.aidaole.ideepseek.home.ui.ChatContent
import com.aidaole.ideepseek.home.ui.HomeActionBar
import kotlinx.coroutines.launch

@Preview("HomePage")
@Composable
fun AppPreview() {
    HomePage()
}

@Composable
fun HomePage() {
    val viewModel = ChatViewModel()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    MaterialTheme {
        ModalDrawer(
            modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
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
                // 顶部ActionBar
                HomeActionBar(
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
    onClose: () -> Unit
) {
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

        Spacer(
            modifier = Modifier.weight(1F)
        )

        DrawerMenuItem(
            icon = Icons.Default.Settings,
            text = "设置"
        )
        DrawerMenuItem(
            icon = Icons.Default.Info,
            text = "关于"
        )
        
        Spacer(modifier = Modifier.height(30.dp))
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
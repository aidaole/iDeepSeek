package com.aidaole.ideepseek.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.LineAwesomeIcons
import compose.icons.lineawesomeicons.CameraSolid
import compose.icons.lineawesomeicons.File
import compose.icons.lineawesomeicons.NetworkWiredSolid
import compose.icons.lineawesomeicons.PhotoVideoSolid

@Preview
@Composable
fun BottomInputAreaPreview() {
    BottomInputArea()
}

@Composable
fun BottomInputArea(
    onSendMessage: (String) -> Unit = {}
) {
    var inputText by remember { mutableStateOf("") }
    var sendBtnEnabled by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
            .imePadding()
    ) {
        InputArea(
            inputText = inputText,
            onValueChange = {
                inputText = it
                sendBtnEnabled = inputText.isNotEmpty()
            }
        )

        FunctionsArea(
            sendBtnEnabled,
            onSendClick = {
                onSendMessage(inputText)
                inputText = "" // 清空输入
                sendBtnEnabled = inputText.isNotEmpty()
            }
        )
    }
}

@Composable
fun InputArea(
    inputText: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = inputText,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 36.dp)
            .clip(RoundedCornerShape(28.dp)),
        placeholder = { Text("输入消息...") },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color(0xFFF5F5F5),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun FunctionsArea(
    sendBtnEnabled: Boolean,
    onSendClick: () -> Unit
) {
    var actionsRow2Visible by remember { mutableStateOf(false) }
    Column {
        ActionsRow1(
            actionsRow2Visible = actionsRow2Visible,
            sendBtnEnabled = sendBtnEnabled,
            onAddClick = { actionsRow2Visible = !actionsRow2Visible },
            onSendClick = onSendClick
        )

        AnimatedVisibility(
            visible = actionsRow2Visible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            ActionsRow2()
        }
    }
}

@Composable
private fun ActionsRow1(
    actionsRow2Visible: Boolean,
    sendBtnEnabled: Boolean,
    onAddClick: () -> Unit,
    onSendClick: () -> Unit
) {
    var deepThinkSelected by remember { mutableStateOf(false) }
    var searchSelected by remember { mutableStateOf(false) }
    
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧按钮组
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BlueActionButton(
                icon = LineAwesomeIcons.NetworkWiredSolid,
                text = "深度思考(R1)",
                selected = deepThinkSelected,
                onClick = { deepThinkSelected = !deepThinkSelected }
            )

            Spacer(Modifier.width(8.dp))

            BlueActionButton(
                icon = Icons.Default.Search,
                text = "联网搜索",
                selected = searchSelected,
                onClick = { searchSelected = !searchSelected }
            )
        }

        // 右侧按钮组
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAddClick) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "添加",
                    tint = Color.Black,
                    modifier = Modifier.rotate(if (actionsRow2Visible) 45f else 0f)
                )
            }

            IconButton(onClick = onSendClick, enabled = sendBtnEnabled) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "发送",
                    tint = if (sendBtnEnabled) Color(0xFF1565C0) else Color.Black
                )
            }
        }
    }
}

@Composable
fun ActionsRow2() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // 拍照识文字按钮
        ActionButton(icon = LineAwesomeIcons.CameraSolid,
            text = "拍照识文字",
            onClick = { /* TODO */ })
        // 图片识文字按钮
        ActionButton(icon = LineAwesomeIcons.PhotoVideoSolid,
            text = "图片识文字",
            onClick = { /* TODO */ })
        // 文件按钮
        ActionButton(icon = LineAwesomeIcons.File, text = "文件", onClick = { /* TODO */ })
    }
}


@Composable
private fun BlueActionButton(
    icon: ImageVector,
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = if (selected) Color(0xFF1565C0) else Color(0xFFE3F2FD)
            )
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.height(40.dp).padding(vertical = 0.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(20.dp),
                tint = if (selected) Color.White else Color(0xFF1565C0)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                color = if (selected) Color.White else Color(0xFF1565C0),
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector, text: String, onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF666666)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = text, style = MaterialTheme.typography.caption, color = Color(0xFF666666)
        )
    }
}

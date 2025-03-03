package com.aidaole.ideepseek.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aidaole.ideepseek.common.icon.Add_a_photo
import com.aidaole.ideepseek.common.icon.Add_photo_alternate
import com.aidaole.ideepseek.common.icon.Attach_file
import com.aidaole.ideepseek.common.icon.Network_node
import com.aidaole.ideepseek.common.ui.SelectedStateButton
import com.aidaole.ideepseek.common.ui.VerticalIconTextButton

@Preview
@Composable
fun BottomInputAreaPreview() {
    BottomInputArea()
}

@Composable
fun BottomInputArea(
    onSendMessage: (String) -> Unit = {}
) {
    var inputText by remember { mutableStateOf("写一个python的快速排序") }
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
            SelectedStateButton(
                icon = Network_node,
                text = "深度思考(R1)",
                selected = deepThinkSelected,
                onClick = { deepThinkSelected = !deepThinkSelected }
            )

            Spacer(Modifier.width(8.dp))

            SelectedStateButton(
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
        VerticalIconTextButton(icon = Add_a_photo,
            text = "拍照识文字",
            onClick = { /* TODO */ })
        // 图片识文字按钮
        VerticalIconTextButton(icon = Add_photo_alternate,
            text = "图片识文字",
            onClick = { /* TODO */ })
        // 文件按钮
        VerticalIconTextButton(icon = Attach_file, text = "文件", onClick = { /* TODO */ })
    }
}


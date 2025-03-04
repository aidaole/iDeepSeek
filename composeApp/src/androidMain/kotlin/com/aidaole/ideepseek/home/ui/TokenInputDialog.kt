package com.aidaole.ideepseek.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Composable
fun TokenInputDialog(
    onTokenSubmit: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var token by remember { mutableStateOf("") }
    
    AlertDialog(
        modifier = Modifier.padding(horizontal = 20.dp),
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.padding(bottom = 8.dp)  // 标题底部间距
            ) {
                Text(
                    "请输入 API Token",
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF1A73E8)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)  // 内容区域垂直间距
            ) {
                Box(modifier = Modifier.height(10.dp)) {
                    Text("")
                }
                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("API Token") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF1A73E8),
                        focusedLabelColor = Color(0xFF1A73E8),
                        cursorColor = Color(0xFF1A73E8)
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                if (token.isBlank()) {
                    Text(
                        "请输入您在deepseek中购买的apikey",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onTokenSubmit(token) },
                enabled = token.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF1A73E8),
                    contentColor = Color.White,
                    disabledBackgroundColor = Color(0xFF1A73E8).copy(alpha = 0.3f)
                ),
                modifier = Modifier
                    .height(50.dp)
                    .padding(bottom = 8.dp),  // 按钮底部间距
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "确定",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF1A73E8)
                ),
                modifier = Modifier
                    .height(50.dp)
                    .padding(bottom = 8.dp)  // 按钮底部间距
            ) {
                Text("取消")
            }
        },
        backgroundColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    )
} 
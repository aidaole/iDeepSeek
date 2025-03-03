package com.aidaole.ideepseek.home.ui

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource

@Composable
fun TokenInputDialog(
    onTokenSubmit: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var token by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("请输入 API Token") },
        text = {
            OutlinedTextField(
                value = token,
                onValueChange = { token = it },
                label = { Text("API Token") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = { onTokenSubmit(token) },
                enabled = token.isNotBlank()
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
} 
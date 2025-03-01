package com.aidaole.ideepseek.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SelectedStateButton(
    icon: ImageVector,
    text: String,
    selected: Boolean = false,
    buttonColor: Color = Color(0xFFE3F2FD),
    buttonSelectedColor: Color = Color(0xFF1565C0),
    iconColor: Color = Color(0xFF1565C0),
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = if (selected) buttonSelectedColor else buttonColor
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
                tint = if (selected) Color.White else iconColor
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                color = if (selected) Color.White else iconColor,
                style = MaterialTheme.typography.body2
            )
        }
    }
}
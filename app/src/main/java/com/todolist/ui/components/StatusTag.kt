package com.todolist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.todolist.R
import com.todolist.ui.theme.StatusActive
import com.todolist.ui.theme.StatusCompleted

/**
 * Status tag component showing task completion status
 */
@Composable
fun StatusTag(
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val (color, text, emoji) = if (isCompleted) {
        Triple(StatusCompleted, stringResource(R.string.status_completed), "🟢")
    } else {
        Triple(StatusActive, stringResource(R.string.status_active), "🟡")
    }
    
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 10.sp
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

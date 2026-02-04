package com.todolist.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.todolist.R
import com.todolist.domain.model.TaskStatus

/**
 * Empty state UI component shown when no tasks are available
 */
@Composable
fun EmptyState(
    filter: TaskStatus,
    modifier: Modifier = Modifier
) {
    val (title, subtitle) = when (filter) {
        TaskStatus.ALL -> stringResource(R.string.empty_tasks) to stringResource(R.string.empty_tasks_subtitle)
        TaskStatus.ACTIVE -> stringResource(R.string.empty_active_tasks) to stringResource(R.string.empty_active_subtitle)
        TaskStatus.COMPLETED -> stringResource(R.string.empty_completed_tasks) to stringResource(R.string.empty_completed_subtitle)
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

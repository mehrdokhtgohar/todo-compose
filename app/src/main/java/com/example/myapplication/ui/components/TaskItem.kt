package com.example.myapplication.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.Task
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.alpha
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.material.icons.outlined.Delete

@Composable
fun TaskItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit
) {
    val transition = updateTransition(targetState = task.isDone, label = "TaskDoneTransition")
    val alpha by transition.animateFloat(label = "AlphaTransition") { done ->
        if (done) 0.4f else 1f
    }

    val textDecoration = transition.animateValue(
        transitionSpec = { tween(durationMillis = 300) },
        typeConverter = TwoWayConverter<TextDecoration, AnimationVector1D>(
            convertToVector = { if (it == TextDecoration.LineThrough) AnimationVector1D(1f) else AnimationVector1D(0f) },
            convertFromVector = { if (it.value > 0.5f) TextDecoration.LineThrough else TextDecoration.None }
        ),
        label = "TextDecoration"
    ) { state ->
        if (state) TextDecoration.LineThrough else TextDecoration.None
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = onCheckedChange
            )
            Text(
                text = task.text,
                textDecoration = textDecoration.value,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        IconButton(onClick = onDeleteClick) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete Task"
            )
        }
    }
}


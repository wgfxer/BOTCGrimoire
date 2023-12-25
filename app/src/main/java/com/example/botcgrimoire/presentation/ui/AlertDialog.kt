package com.example.botcgrimoire.presentation.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.botcgrimoire.domain.ButtonData
import com.example.botcgrimoire.domain.DialogData

/**
 * @author Valeriy Minnulin
 */
@Composable
fun AlertDialog(dialogData: DialogData?) {
    if (dialogData != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = dialogData.dismissLambda,
            confirmButton = { AlertButton(dialogData.positiveButton, dialogData.dismissLambda) },
            dismissButton = dialogData.negativeButton?.let { { AlertButton(it, dialogData.dismissLambda) } },
            title = { Text(text = stringResource(dialogData.title)) },
            text = { Text(text = stringResource(id = dialogData.subtitle)) }
        )
    }
}

@Composable
fun AlertButton(buttonData: ButtonData, dismissLambda: () -> Unit) {
    Button(onClick = buttonData.onClick ?: dismissLambda) {
        Text(text = stringResource(id = buttonData.text))
    }
}
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.botcgrimoire.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.botcgrimoire.R
import com.example.botcgrimoire.domain.Role

/**
 * @author Valeriy Minnulin
 */
@Composable
fun ChangePlayerNameDialog(dialogData: Map<Role, String>, onDismiss: () -> Unit, onClick: (Map<Role, String>) -> Unit) {
    if (dialogData.isNotEmpty()) {
        val map = remember(key1 = dialogData) { dialogData.toMutableMap() }
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                ) {
                    dialogData.forEach { (role, playerName) ->
                        NameEditor(role, playerName) { newName ->
                            map[role] = newName
                        }
                    }
                }
                Button(onClick = { onClick(map) }) {
                    Text(text = stringResource(id = R.string.done))
                }
            }
        }
    }
}

@Composable
private fun NameEditor(role: Role, playerName: String, onChange: (String) -> Unit) {
    val state = remember { mutableStateOf(playerName) }
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(6.dp)) {
        Image(painterResource(id = role.icon), modifier = Modifier.size(36.dp), contentDescription = null)
        Text(text = stringResource(id = role.roleName))
        TextField(value = state.value, onValueChange = {
            state.value = it
            onChange(it)
        }, label = { Text(text = "Имя игрока") })
    }
}
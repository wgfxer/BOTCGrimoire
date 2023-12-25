package com.example.botcgrimoire.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botcgrimoire.R
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.presentation.ui.AlertDialog

/**
 * Экран настроек
 *
 * @author Valeriy Minnulin
 */
@Composable
fun SettingsScreen(navigateToStart: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        val viewModel: SettingsScreenViewModel = viewModel(factory = settingsScreenVMFactory())
        val state = viewModel.state.collectAsState()
        Column {
            SettingsItem(textRes = R.string.restart_game_button) { viewModel.onRestartGameButtonClick(navigateToStart) }
            SettingsItem(textRes = R.string.add_player) { viewModel.addPlayerClicked() }
            SettingsItem(textRes = R.string.remove_player) { viewModel.removePlayerClicked() }
            SettingsItem(textRes = R.string.change_player_name) { viewModel.changePlayerName() }
            SettingsItem(textRes = state.value.showNightOrderButtonText) { viewModel.onChangeNightOrderShow() }
        }
        AlertDialog(dialogData = state.value.dialogData)
        AddRoleDialog(dialogData = state.value.addRoleDialog, onDismiss = { viewModel.dismissDialog() }, onClick = { viewModel.addRole(it) })
        RemoveRoleDialog(dialogData = state.value.removeRoleDialog, onDismiss = { viewModel.dismissDialog() }, onClick = { viewModel.removeRole(it) })
        ChangePlayerNameDialog(dialogData = state.value.playerNames, onDismiss = { viewModel.dismissDialog() }, onClick = { viewModel.changeNames(it) })
    }

}

@Composable
private fun SettingsItem(textRes: Int, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.padding(16.dp)) {
        Text(text = stringResource(id = textRes))
    }
}

@Composable
private fun AddRoleDialog(dialogData: List<Role>, onDismiss: () -> Unit, onClick: (Role) -> Unit) {
    if (dialogData.isNotEmpty()) {
        Dialog(onDismissRequest = onDismiss) {
            Card(modifier = Modifier
                .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = "Добавить игрока")
                    dialogData.forEach { role ->
                        Row(
                            modifier = Modifier
                                .clickable { onClick(role) }
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painterResource(id = role.icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .size(36.dp)
                            )
                            Text(text = stringResource(id = role.roleName))
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun RemoveRoleDialog(dialogData: List<Role>, onDismiss: () -> Unit, onClick: (Role) -> Unit) {
    if (dialogData.isNotEmpty()) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = "Удалить игрока")
                    dialogData.forEach { role ->
                        Row(
                            modifier = Modifier
                                .clickable { onClick(role) }
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painterResource(id = role.icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .size(36.dp)
                            )
                            Text(text = stringResource(id = role.roleName))
                        }
                    }
                }
            }

        }
    }
}
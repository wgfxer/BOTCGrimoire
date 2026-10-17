package com.example.botcgrimoire.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.example.botcgrimoire.domain.AppLanguage
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.presentation.ui.AlertDialog

/**
 * Экран настроек
 *
 * @author Valeriy Minnulin
 */
@Composable
fun SettingsScreen(navigateToStart: () -> Unit, recreateActivity: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        val viewModel: SettingsScreenViewModel = viewModel(factory = settingsScreenVMFactory())
        val state = viewModel.state.collectAsState()
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            SettingsItem(textRes = R.string.restart_game_button) { viewModel.onRestartGameButtonClick(navigateToStart) }
            SettingsItem(textRes = R.string.add_player) { viewModel.addPlayerClicked() }
            SettingsItem(textRes = R.string.remove_player) { viewModel.removePlayerClicked() }
            SettingsItem(textRes = R.string.change_player_name) { viewModel.changePlayerName() }
            SettingsItem(textRes = state.value.showNightOrderButtonText) { viewModel.onChangeNightOrderShow() }
            LanguageSetting(
                selectedLanguage = state.value.language,
                onLanguageSelected = { viewModel.onLanguageChanged(it, recreateActivity) }
            )
        }
        AlertDialog(dialogData = state.value.dialogData)
        AddRoleDialog(dialogData = state.value.addRoleDialog, onDismiss = { viewModel.dismissDialog() }, onClick = { viewModel.addRole(it) })
        RemoveRoleDialog(dialogData = state.value.removeRoleDialog, onDismiss = { viewModel.dismissDialog() }, onClick = { viewModel.removeRole(it) })
        ChangePlayerNameDialog(dialogData = state.value.playerNames, onDismiss = { viewModel.dismissDialog() }, onClick = { viewModel.changeNames(it) })
    }

}

@Composable
private fun LanguageSetting(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = stringResource(id = R.string.language))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LanguageButton(
                textRes = R.string.language_russian,
                language = AppLanguage.Russian,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = onLanguageSelected,
                modifier = Modifier.weight(1f)
            )
            LanguageButton(
                textRes = R.string.language_english,
                language = AppLanguage.English,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = onLanguageSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LanguageButton(
    textRes: Int,
    language: AppLanguage,
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onLanguageSelected(language) },
        enabled = language != selectedLanguage,
        modifier = modifier
    ) {
        Text(text = stringResource(id = textRes))
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
                    Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = stringResource(R.string.add_player_dialog_title))
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
                    Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = stringResource(R.string.remove_player_dialog_title))
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

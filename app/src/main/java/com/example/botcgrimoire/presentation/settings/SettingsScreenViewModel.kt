package com.example.botcgrimoire.presentation.settings

import androidx.lifecycle.ViewModel
import com.example.botcgrimoire.R
import com.example.botcgrimoire.di.getAppStateInteractor
import com.example.botcgrimoire.domain.AppState
import com.example.botcgrimoire.domain.AppStateInteractor
import com.example.botcgrimoire.domain.ButtonData
import com.example.botcgrimoire.domain.DialogData
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.domain.RoleGameState
import com.example.botcgrimoire.domain.SettingsScreenState
import com.example.botcgrimoire.utils.ViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * @author Valeriy Minnulin
 */
class SettingsScreenViewModel(
    private val appStateInteractor: AppStateInteractor,
): ViewModel() {

    private val _state = MutableStateFlow(SettingsScreenState())
    val state: StateFlow<SettingsScreenState> = _state.asStateFlow()

    fun onRestartGameButtonClick(navigateToStart: () -> Unit) {
        val dialog = DialogData(
            title = R.string.restart_game_dialog_title,
            subtitle = R.string.restart_game_dialog_subtitle,
            positiveButton = ButtonData(text = R.string.restart_game_dialog_positive_button) {
                _state.value = _state.value.copy(dialogData = null)
                appStateInteractor.clearData()
                navigateToStart()
            },
            negativeButton = ButtonData(text = R.string.restart_game_dialog_negative_button),
            dismissLambda = { _state.value = _state.value.copy(dialogData = null) }
        )
        _state.value = _state.value.copy(dialogData = dialog)
    }

    fun addPlayerClicked() {
        val currentAppState = appStateInteractor.state.value as AppState.GameState
        val currentRoles = currentAppState.roles.map { it.role }
        val availableRoles = (Role.values().toList() - currentRoles.toSet()) - Role.Drunk
        _state.value = _state.value.copy(addRoleDialog = availableRoles)
    }

    fun removePlayerClicked() {
        val currentAppState = appStateInteractor.state.value as AppState.GameState
        val availableRoles = currentAppState.roles.map { it.role }
        _state.value = _state.value.copy(removeRoleDialog = availableRoles)
    }

    fun changePlayerName() {
        val currentAppState = appStateInteractor.state.value as AppState.GameState
        val availableRoles = currentAppState.roles.associate { it.role to it.playerName }
        _state.value = _state.value.copy(playerNames = availableRoles)
    }

    fun addRole(role: Role) {
        val currentAppState = appStateInteractor.state.value as AppState.GameState
        val newRolesList = currentAppState.roles.plus(RoleGameState(role = role))
        val newAppState = currentAppState.copy(roles = newRolesList)
        appStateInteractor.changeGameState(newAppState)
        dismissDialog()
    }

    fun removeRole(role: Role) {
        val currentAppState = appStateInteractor.state.value as AppState.GameState
        val newRolesList = currentAppState.roles.filter { it.role != role }
        val newAppState = currentAppState.copy(roles = newRolesList)
        appStateInteractor.changeGameState(newAppState)
        dismissDialog()
    }

    fun dismissDialog() {
        _state.value = SettingsScreenState()
    }

    fun changeNames(newNames: Map<Role, String>) {
        val currentAppState = appStateInteractor.state.value as AppState.GameState
        val newRolesList = currentAppState.roles.map {
            it.copy(playerName = newNames[it.role].orEmpty())
        }
        val newAppState = currentAppState.copy(roles = newRolesList)
        appStateInteractor.changeGameState(newAppState)
        dismissDialog()
    }
}

fun settingsScreenVMFactory() = ViewModelFactory { SettingsScreenViewModel(getAppStateInteractor()) }

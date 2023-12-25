package com.example.botcgrimoire.presentation.chooserole

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.botcgrimoire.R
import com.example.botcgrimoire.di.getAppStateInteractor
import com.example.botcgrimoire.domain.AppState
import com.example.botcgrimoire.domain.AppStateInteractor
import com.example.botcgrimoire.domain.ButtonData
import com.example.botcgrimoire.domain.ChooseYourRoleScreenEvent
import com.example.botcgrimoire.domain.ChooseYourRoleScreenState
import com.example.botcgrimoire.domain.DialogData
import com.example.botcgrimoire.domain.ReminderLink
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.domain.RoleGameState
import com.example.botcgrimoire.domain.drunkReminder
import com.example.botcgrimoire.domain.townsfolk
import com.example.botcgrimoire.domain.travellers
import com.example.botcgrimoire.utils.ViewModelFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * @author Valeriy Minnulin
 */
class ChooseYourRoleViewModel(
    private val appStateInteractor: AppStateInteractor
): ViewModel() {
    private val _state = MutableStateFlow(createInitialState())
    val state: StateFlow<ChooseYourRoleScreenState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<ChooseYourRoleScreenEvent>()
    val events = _events.asSharedFlow()

    private fun createInitialState(): ChooseYourRoleScreenState {
        val appState = appStateInteractor.state.value as AppState.RevealingRoles
        val currentRoles = appState.roles.map { it.role }
        val needToSelectDrunk = appState.roles.find { it.role == Role.Drunk } != null && appState.drunkRole == null
        val rolesForDrunk = if (needToSelectDrunk) {
            townsfolk.filterNot { currentRoles.contains(it) }
        } else {
            null
        }
        return ChooseYourRoleScreenState(appState.roles, rolesForDrunk)
    }

    private fun sendEvent(event: ChooseYourRoleScreenEvent) {
        viewModelScope.launch { _events.emit(event) }
    }

    fun onBackClicked() {
        if (_state.value.roles.find { it.isChosen } != null) {
            val dialog = DialogData(
                title = R.string.restart_game_dialog_title,
                subtitle = R.string.restart_game_dialog_subtitle,
                positiveButton = ButtonData(text = R.string.restart_game_dialog_positive_button) {
                    _state.value = _state.value.copy(dialog = null)
                    appStateInteractor.clearData()
                    sendEvent(ChooseYourRoleScreenEvent.NavigateToConfigureGame)
                },
                negativeButton = ButtonData(text = R.string.restart_game_dialog_negative_button),
                dismissLambda = { _state.value = _state.value.copy(dialog = null) }
            )
            _state.value = _state.value.copy(dialog = dialog)
        } else {
            sendEvent(ChooseYourRoleScreenEvent.NavigateToConfigureGame)
        }
    }

    fun onRoleClick(role: Role, navigateToRevealRoleScreen: (Role) -> Unit) {
        if (_state.value.roles.find { it.role == role }?.isChosen == true) {
            val dialog = DialogData(
                title = R.string.reselect_role_dialog_title,
                subtitle = R.string.reselect_role_dialog_subtitle,
                positiveButton = ButtonData(text = R.string.reselect_role_dialog_positive_button) {
                    _state.value = _state.value.copy(dialog = null)
                    navigateToRevealRoleScreen(role)
                },
                negativeButton = ButtonData(text = R.string.reselect_role_dialog_negative_button),
                dismissLambda = { _state.value = _state.value.copy(dialog = null) }
            )
            _state.value = _state.value.copy(dialog = dialog)
        } else {
            val oldState = appStateInteractor.state.value as AppState.RevealingRoles
            navigateToRevealRoleScreen(role)
            val newList = oldState.roles.map { chooseableRole ->
                if (chooseableRole.role == role) {
                    chooseableRole.copy(isChosen = true)
                } else {
                    chooseableRole
                }
            }
            _state.value = _state.value.copy(roles = newList)
            val newAppState = oldState.copy(roles = newList)
            appStateInteractor.changeGameState(newAppState)
        }
    }

    fun onDrunkRoleClick(role: Role) {
        appStateInteractor.changeGameState(AppState.RevealingRoles(_state.value.roles, drunkRole = role))
        _state.value = _state.value.copy(rolesForDrunk = null)
    }

    fun onContinueClick() {
        val currentState = appStateInteractor.state.value as AppState.RevealingRoles
        val drunkRole = currentState.drunkRole
        val travellers = currentState.travellers.map {
            RoleGameState(role = it)
        }
        val newRolesList = currentState.roles.map {
            val actualRole = if (it.role == Role.Drunk) drunkRole!! else it.role
            RoleGameState(role = actualRole, playerName = it.playerName)
        } + travellers
        val reminders = if (drunkRole != null) listOf(ReminderLink(drunkReminder, role = drunkRole)) else emptyList()
        appStateInteractor.changeGameState(
            AppState.GameState(
                roles = newRolesList,
                drunkRole = drunkRole,
                reminders = reminders
            )
        )
    }
}

fun chooseYourRoleVMFactory() = ViewModelFactory { ChooseYourRoleViewModel(getAppStateInteractor()) }
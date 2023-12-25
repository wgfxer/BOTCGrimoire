package com.example.botcgrimoire.presentation.grimoire

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.botcgrimoire.di.getAppStateInteractor
import com.example.botcgrimoire.domain.Action
import com.example.botcgrimoire.domain.Action.ReminderAction
import com.example.botcgrimoire.domain.ActionsDialog
import com.example.botcgrimoire.domain.AppState
import com.example.botcgrimoire.domain.AppStateInteractor
import com.example.botcgrimoire.domain.GrimoireScreenState
import com.example.botcgrimoire.domain.Reminder
import com.example.botcgrimoire.domain.ReminderLink
import com.example.botcgrimoire.domain.RemindersHelper
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.domain.RoleGameState
import com.example.botcgrimoire.domain.reminders
import com.example.botcgrimoire.presentation.zoom.ZoomState
import com.example.botcgrimoire.utils.ViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * @author Valeriy Minnulin
 */
class GrimoireViewModel(
    private val appStateInteractor: AppStateInteractor
) : ViewModel() {
    private val _screenState = MutableStateFlow(GrimoireScreenState())
    val screenState: StateFlow<GrimoireScreenState> = _screenState.asStateFlow()

    val state: StateFlow<AppState.GameState> = convertedState()

    private fun convertedState(): StateFlow<AppState.GameState> {
        return appStateInteractor.state.filterIsInstance<AppState.GameState>()
            .stateIn(started = Eagerly, scope = viewModelScope, initialValue = AppState.GameState())
    }

    fun onRoleClick(roleState: RoleGameState) {
        val actions = RemindersHelper.filterReminders(state.value, roleState).map {
            val alreadyHasReminder = remindersForRole(state.value, roleState.role).contains(it)
            ReminderAction(it, isAdd = !alreadyHasReminder)
        }
        val changeLifeStateAction = Action.ChangeLifeState(!roleState.isDead)
        val changeHasVoteAction = Action.ChangeHasVote(!roleState.hasVote).takeIf { roleState.isDead }
        val dismissDialog = { _screenState.value = GrimoireScreenState() }
        _screenState.value = GrimoireScreenState(actionsDialog = ActionsDialog(
            actionsInDialog = listOfNotNull(changeLifeStateAction, changeHasVoteAction) + actions,
            dismissLambda = dismissDialog,
            onActionClick = {
                onActionClick(roleState, it)
                dismissDialog()
            }
        ))
    }

    var zoomState: ZoomState? = null

    private fun remindersForRole(state: AppState.GameState, role: Role): List<Reminder> {
        return state.reminders.filter { it.role == role }.map { it.reminder }
    }

    fun onOffsetChanged(model: RoleGameState, offset: Offset) {
        val oldRoleState = state.value.roles.find { it.role == model.role }
        val newRoleState = model.copy(offsetX = offset.x, offsetY = offset.y)
        val newList = state.value.roles.toMutableList().also {
            val index = it.indexOf(oldRoleState)
            it.remove(oldRoleState)
            it.add(index, newRoleState)
        }
        val newState = state.value.copy(
            roles = newList
        )
        appStateInteractor.changeGameState(newState)
    }

    private fun onActionClick(roleState: RoleGameState, action: Action) {
        val listRoles = state.value.roles.toMutableList()
        val index = listRoles.indexOf(roleState)
        listRoles.remove(roleState)
        var newReminders = state.value.reminders
        val newRoleState = when (action) {
            is ReminderAction -> {
                if (action.isAdd) {
                    newReminders = newReminders
                        .filter { action.reminder.repeatable || it.reminder != action.reminder }
                        .toMutableList()
                        .also {
                            it.add(ReminderLink(action.reminder, roleState.role))
                        }
                } else {
                    newReminders = newReminders.filter { it.role != roleState.role || it.reminder != action.reminder }
                }

                roleState
            }

            is Action.ChangeLifeState -> {
                if (action.newIsDead) {
                    newReminders = newReminders.filter {
                        val deadRole = roleState.role
                        val reminderNotForDeadRole = it.role != deadRole
                        val reminderNotFromDeadRole = it.reminder.role != deadRole
                        reminderNotFromDeadRole && reminderNotForDeadRole || it.reminder.role == Role.Drunk
                    }
                }
                roleState.copy(isDead = action.newIsDead, hasVote = true)
            }

            is Action.ChangeHasVote -> {
                roleState.copy(hasVote = action.hasVote)
            }
        }
        listRoles.add(index, newRoleState)
        val newState = state.value.copy(roles = listRoles, reminders = newReminders)
        appStateInteractor.changeGameState(newState)
    }
}

fun grimoireVMFactory() = ViewModelFactory { GrimoireViewModel(getAppStateInteractor()) }
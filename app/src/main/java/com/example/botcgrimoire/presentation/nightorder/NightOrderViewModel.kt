package com.example.botcgrimoire.presentation.nightorder

import androidx.lifecycle.ViewModel
import com.example.botcgrimoire.di.getAppStateInteractor
import com.example.botcgrimoire.domain.AppState
import com.example.botcgrimoire.domain.AppStateInteractor
import com.example.botcgrimoire.domain.NightOrderModel
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.domain.firstNight
import com.example.botcgrimoire.domain.otherNights
import com.example.botcgrimoire.utils.ViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * @author Valeriy Minnulin
 */
class NightOrderViewModel(
    private val appStateInteractor: AppStateInteractor
): ViewModel() {

    private val _state = MutableStateFlow(initialState())
    val state = _state.asStateFlow()


    private fun initialState(): NightOrderScreenState {
        val currentState = appStateInteractor.state.value as AppState.GameState
        val isFirstNight = currentState.isFirstNight
        return getState(isFirstNight)
    }

    private fun getState(isForFirstNight: Boolean): NightOrderScreenState {
        val currentOrder = getOrderList(isForFirstNight)
        return NightOrderScreenState(isForFirstNight, currentOrder)
    }

    fun updateState() {
        _state.value = getState(_state.value.isForFirstNight)
    }

    fun onChangeNight(isFirstNight: Boolean) {
        val currentState = appStateInteractor.state.value as AppState.GameState
        val newState = currentState.copy(isFirstNight = isFirstNight)
        appStateInteractor.changeGameState(newState)
        _state.value = getState(isFirstNight)
    }

    private fun getOrderList(isFirstNight: Boolean): List<NightOrderModel> {
        val currentAppState = appStateInteractor.state.value as AppState.GameState
        val order = if (isFirstNight) firstNight else otherNights
        val currentRoles = currentAppState.roles.map { it.role }
        return order.filter { currentRoles.contains(it.role) }.map {
            it.copy(
                isDead = isDead(it.role, currentAppState),
                isDrunk = isDrunk(it.role, currentAppState),
                playerName = getPlayerName(it.role, currentAppState)
            )
        }
    }

    private fun getPlayerName(role: Role, currentAppState: AppState.GameState): String {
        return currentAppState.roles.find { it.role == role }?.playerName.orEmpty()
    }

    private fun isDead(role: Role, state: AppState.GameState): Boolean {
        return state.roles.find { it.role == role }?.isDead ?: false
    }

    private fun isDrunk(role: Role, state: AppState.GameState): Boolean { //TODO сделать какой-то интерфейс для ремайндера который говорит об отравлении
        return state.reminders.filter { it.role == role }.find { it.reminder.role == Role.Poisoner || it.reminder.role == Role.Drunk } != null
    }

}

fun nightOrderVMFactory() = ViewModelFactory { NightOrderViewModel(getAppStateInteractor()) }


data class NightOrderScreenState(
    val isForFirstNight: Boolean,
    val currentOrder: List<NightOrderModel>
)
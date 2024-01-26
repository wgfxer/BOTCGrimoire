package com.example.botcgrimoire.presentation.nightorder

import androidx.lifecycle.ViewModel
import com.example.botcgrimoire.R
import com.example.botcgrimoire.di.getAppStateInteractor
import com.example.botcgrimoire.di.resourceManager
import com.example.botcgrimoire.domain.AppState
import com.example.botcgrimoire.domain.AppStateInteractor
import com.example.botcgrimoire.domain.DemonBluffInfo
import com.example.botcgrimoire.domain.DemonLunaticInfo
import com.example.botcgrimoire.domain.LUNATIC_BLUFF_INFO_KEY
import com.example.botcgrimoire.domain.MinionsInfo
import com.example.botcgrimoire.domain.NightOrderItem
import com.example.botcgrimoire.domain.NightOrderModel
import com.example.botcgrimoire.domain.ResourceManager
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.domain.RoleType
import com.example.botcgrimoire.domain.TravellersInfo
import com.example.botcgrimoire.domain.getOrder
import com.example.botcgrimoire.utils.ViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * @author Valeriy Minnulin
 */
class NightOrderViewModel(
    private val appStateInteractor: AppStateInteractor,
    private val resourceManager: ResourceManager
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

    private fun getOrderList(isFirstNight: Boolean): List<NightOrderItem> {
        val currentAppState = appStateInteractor.state.value as AppState.GameState

        val order = getOrder(isFirstNight, currentAppState.currentEdition).toMutableList()


        val indexOfDemon = order.indexOfFirst {
            it is NightOrderModel && it.role == currentAppState.lunaticRole
        }
        val indexOfFirstDemon = order.indexOfFirst {
            it is NightOrderModel && it.role.type == RoleType.Demons
        }
        if (indexOfDemon != -1 && currentAppState.lunaticRole != null) {
            val demonCard = order[indexOfDemon] as NightOrderModel
            val demonInfo = resourceManager.getString(demonCard.description)
            order.add(indexOfFirstDemon, NightOrderModel(
                role = Role.Lunatic, description = 0,
                descriptionString = resourceManager.getString(R.string.lunatic_teller_info_start) + "\n\n\"$demonInfo\"\n\n"+ resourceManager.getString(R.string.lunatic_teller_info_end)
            ))
        }


        val currentRoles = currentAppState.roles.map { it.role }
        return order.filter { it !is NightOrderModel || currentRoles.contains(it.role) }.map {
            if (it is NightOrderModel) {
                it.copy(
                    isDead = isDead(it.role, currentAppState),
                    isDrunk = isDrunk(it.role, currentAppState),
                    playerName = getPlayerName(it.role, currentAppState)
                )
            } else {
                it
            }
        }.filter { filterNightOrderItem(it) }
    }

    private fun filterNightOrderItem(nightOrderItem: NightOrderItem): Boolean {
        return when  {
            nightOrderItem is TravellersInfo -> isTravellersInGame()
            nightOrderItem is MinionsInfo -> is7OrMorePlayers()
            nightOrderItem is DemonBluffInfo -> is7OrMorePlayers()
            nightOrderItem is DemonLunaticInfo -> isLunaticInGame()
            nightOrderItem.isLunaticBluffInfo() -> is7OrMorePlayers()
            else -> true
        }
    }

    private fun NightOrderItem.isLunaticBluffInfo(): Boolean {
        return this is NightOrderModel && this.key == LUNATIC_BLUFF_INFO_KEY
    }

    private fun isTravellersInGame(): Boolean {
        val currentAppState = appStateInteractor.state.value as AppState.GameState
        return currentAppState.roles.any { it.role.type == RoleType.Travellers }
    }

    private fun isLunaticInGame(): Boolean {
        val currentAppState = appStateInteractor.state.value as AppState.GameState
        return currentAppState.roles.any { it.role == Role.Lunatic }
    }

    private fun is7OrMorePlayers(): Boolean {
        val currentAppState = appStateInteractor.state.value as AppState.GameState
        return currentAppState.roles.count() >= 7
    }

    private fun getPlayerName(role: Role, currentAppState: AppState.GameState): String {
        return currentAppState.roles.find { it.role == role }?.playerName.orEmpty()
    }

    private fun isDead(role: Role, state: AppState.GameState): Boolean {
        return state.roles.find { it.role == role }?.isDead ?: false
    }

    private fun isDrunk(role: Role, state: AppState.GameState): Boolean {
        val isDrunk = state.reminders.filter { it.role == role }.find { it.reminder.isDrunk } != null || state.reminders.any { it.role == Role.Minstrel && it.reminder.role == Role.Minstrel }
        android.util.Log.i("MYTAG", "isDrunk for role $role = $isDrunk")
        return isDrunk
    }

}

fun nightOrderVMFactory() = ViewModelFactory { NightOrderViewModel(getAppStateInteractor(), resourceManager) }


data class NightOrderScreenState(
    val isForFirstNight: Boolean,
    val currentOrder: List<NightOrderItem>
)
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
import com.example.botcgrimoire.domain.Reminder
import com.example.botcgrimoire.domain.ReminderLink
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.domain.RoleGameState
import com.example.botcgrimoire.domain.RoleType
import com.example.botcgrimoire.domain.TravellersInfo
import com.example.botcgrimoire.domain.getOrder
import com.example.botcgrimoire.domain.reminders
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

    private fun getState(
        isForFirstNight: Boolean,
        currentStepIndex: Int = 0,
        completedStepIndexes: Set<Int> = emptySet()
    ): NightOrderScreenState {
        val currentOrder = getOrderList(isForFirstNight)
        val validCompletedSteps = completedStepIndexes.filterTo(mutableSetOf()) {
            it in currentOrder.indices
        }
        return NightOrderScreenState(
            isForFirstNight = isForFirstNight,
            currentOrder = currentOrder,
            currentStepIndex = currentStepIndex.coerceIn(0, currentOrder.size),
            completedStepIndexes = validCompletedSteps
        )
    }

    fun updateState() {
        val currentState = _state.value
        _state.value = getState(
            isForFirstNight = currentState.isForFirstNight,
            currentStepIndex = currentState.currentStepIndex,
            completedStepIndexes = currentState.completedStepIndexes
        )
    }

    fun onChangeNight(isFirstNight: Boolean) {
        val currentState = appStateInteractor.state.value as AppState.GameState
        val newState = currentState.copy(isFirstNight = isFirstNight)
        appStateInteractor.changeGameState(newState)
        _state.value = getState(isFirstNight)
    }

    fun completeCurrentStep(expectedStepIndex: Int) {
        val currentState = _state.value
        val currentStepIndex = currentState.currentStepIndex
        if (currentStepIndex != expectedStepIndex) return
        if (currentStepIndex !in currentState.currentOrder.indices) return

        val completedSteps = currentState.completedStepIndexes + currentStepIndex
        val nextStepIndex = (currentStepIndex + 1 until currentState.currentOrder.size)
            .firstOrNull { it !in completedSteps }
            ?: currentState.currentOrder.size

        _state.value = currentState.copy(
            currentStepIndex = nextStepIndex,
            completedStepIndexes = completedSteps
        )
    }

    fun goToPreviousStep() {
        val currentState = _state.value
        if (currentState.currentOrder.isEmpty()) return

        val previousStepIndex = if (currentState.currentStepIndex >= currentState.currentOrder.size) {
            currentState.currentOrder.lastIndex
        } else {
            (currentState.currentStepIndex - 1).coerceAtLeast(0)
        }
        _state.value = currentState.copy(currentStepIndex = previousStepIndex)
    }

    fun restartNight() {
        _state.value = getState(_state.value.isForFirstNight)
    }

    fun getRolesForInformation(role: Role): List<Role>? {
        val roleType = when (role) {
            Role.Washerwoman -> RoleType.Townsfolk
            Role.Librarian -> RoleType.Outsiders
            Role.Investigator -> RoleType.Minions
            else -> null
        } ?: return null

        val currentState = appStateInteractor.state.value as AppState.GameState
        val availableRoles = if (isDrunk(role, currentState)) {
            currentState.currentEdition.roles
        } else {
            buildList {
                addAll(currentState.roles.map { it.role })
                if (role == Role.Librarian && currentState.drunkRole != null) {
                    add(Role.Drunk)
                }
            }
        }
        return availableRoles.filter { it.type == roleType }.distinct()
    }

    fun getNightActionTargets(role: Role): List<RoleGameState>? {
        val currentState = appStateInteractor.state.value as AppState.GameState
        return when (role) {
            Role.Poisoner -> currentState.roles
            Role.Monk -> currentState.roles.filter { it.role != Role.Monk }
            Role.Imp -> currentState.roles.filter { !it.isDead }
            else -> null
        }
    }

    fun clearPreviousNightAction(role: Role) {
        changeNightActionTarget(role, targetRole = null)
    }

    fun setNightActionTarget(role: Role, targetRole: Role) {
        changeNightActionTarget(role, targetRole)
    }

    fun killPlayer(targetRole: Role) {
        val currentState = appStateInteractor.state.value as AppState.GameState
        val newRoles = currentState.roles.map { roleState ->
            if (roleState.role == targetRole) {
                roleState.copy(isDead = true, hasVote = true)
            } else {
                roleState
            }
        }
        val newReminders = currentState.reminders.filter { reminderLink ->
            val reminderIsNotForTarget = reminderLink.role != targetRole
            val reminderIsNotFromTarget = reminderLink.reminder.role != targetRole
            reminderIsNotForTarget && reminderIsNotFromTarget || reminderLink.reminder.role == Role.Drunk
        }
        appStateInteractor.changeGameState(
            currentState.copy(roles = newRoles, reminders = newReminders)
        )
        updateState()
    }

    private fun changeNightActionTarget(role: Role, targetRole: Role?) {
        val reminder = getNightActionReminder(role) ?: return
        val currentState = appStateInteractor.state.value as AppState.GameState
        val newReminders = currentState.reminders
            .filterNot { it.reminder == reminder }
            .toMutableList()
            .apply {
                if (targetRole != null) add(ReminderLink(reminder, targetRole))
            }
        appStateInteractor.changeGameState(currentState.copy(reminders = newReminders))
        updateState()
    }

    private fun getNightActionReminder(role: Role): Reminder? {
        if (role != Role.Poisoner && role != Role.Monk) return null
        return reminders.firstOrNull { it.role == role }
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
    val currentOrder: List<NightOrderItem>,
    val currentStepIndex: Int = 0,
    val completedStepIndexes: Set<Int> = emptySet()
) {
    val completedStepsCount: Int
        get() = completedStepIndexes.count { it in currentOrder.indices }

    val isNightComplete: Boolean
        get() = currentOrder.isNotEmpty() && completedStepsCount == currentOrder.size

    val hasProgress: Boolean
        get() = completedStepIndexes.isNotEmpty() || currentStepIndex > 0
}

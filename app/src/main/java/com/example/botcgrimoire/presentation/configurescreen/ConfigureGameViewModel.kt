package com.example.botcgrimoire.presentation.configurescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.botcgrimoire.di.getAppStateInteractor
import com.example.botcgrimoire.domain.AppState
import com.example.botcgrimoire.domain.AppStateInteractor
import com.example.botcgrimoire.domain.ConfigureGameScreenState
import com.example.botcgrimoire.domain.RandomRolesListGenerator
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.domain.RoleForChoose
import com.example.botcgrimoire.domain.RoleType
import com.example.botcgrimoire.domain.RolesCountModelHelper
import com.example.botcgrimoire.utils.ViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * @author Valeriy Minnulin
 */
class ConfigureGameViewModel(
    private val appStateInteractor: AppStateInteractor,
    private val rolesCountModelHelper: RolesCountModelHelper,
    private val randomRolesListGenerator: RandomRolesListGenerator
): ViewModel() {

    private val _state = MutableStateFlow(ConfigureGameScreenState())
    val state: StateFlow<ConfigureGameScreenState> = _state.asStateFlow()

    private var warningDeleteJob: Job? = null

    fun onRoleClick(role: Role) {
        val playersCount = _state.value.currentPlayersCount
        val isRemoved = _state.value.selectedRoles.contains(role)
        var warningText: String? = null
        val newSelectedRoles = if (isRemoved) {
            _state.value.selectedRoles.filterNot { it == role }
        } else {
            val countModel = rolesCountModelHelper.getCountModel(playersCount, _state.value.selectedRoles)
            val requiredCountForRoleType = countModel.getCountForType(role.type)
            val selectedCountForRoleType = _state.value.selectedRoles.filter { it.type == role.type }.size
            if (requiredCountForRoleType != null && selectedCountForRoleType + 1 > requiredCountForRoleType) {
                warningText = "Вы не можете добавить роль, пока не увеличите количество игроков"
                removeWarningAfterTimeout()
                _state.value.selectedRoles
            } else {
                _state.value.selectedRoles.plus(role)
            }
        }
        val newState = _state.value.copy(selectedRoles = newSelectedRoles, warningText = warningText)
        setNewState { newState }
    }

    private fun removeWarningAfterTimeout() {
        warningDeleteJob?.cancel()
        warningDeleteJob = viewModelScope.launch {
            delay(3000)
            setNewState { _state.value.copy(warningText = null) }
        }
    }

    fun onPlayersCountChanged(newCount: Int) {
        val newSelectedRoles = if (newCount < _state.value.currentPlayersCount) {
            rolesCountModelHelper.removeRolesIfExceedsLimit(newCount, _state.value.selectedRoles)
        } else {
            _state.value.selectedRoles
        }
        setNewState { oldState -> oldState.copy(currentPlayersCount = newCount, selectedRoles = newSelectedRoles) }
    }

    fun onRandomRolesClick() {
        val selectedRoles = randomRolesListGenerator.generateRandomRoles(_state.value.currentPlayersCount)
        setNewState { oldState -> oldState.copy(selectedRoles = selectedRoles) }
    }

    fun onClearSelectedRoles() {
        setNewState { oldState -> oldState.copy(selectedRoles = emptyList()) }
    }

    private fun setNewState(changer: (ConfigureGameScreenState) -> ConfigureGameScreenState) {
        val newState = changer.invoke(_state.value)
        val validatedRolesList = rolesCountModelHelper.removeRolesIfExceedsLimit(newState.currentPlayersCount, newState.selectedRoles)
        val countModel = rolesCountModelHelper.getCountModel(newState.currentPlayersCount, newState.selectedRoles)
        _state.value = newState.copy(rolesCountModel = countModel, selectedRoles = validatedRolesList)
    }

    fun onContinueClick() {
        val travellers = _state.value.selectedRoles.filter { it.type == RoleType.Travellers }

        val choosableRoles = _state.value.selectedRoles.filterNot { it.type == RoleType.Travellers }.shuffled().mapIndexed { index, role ->
            RoleForChoose(number = index + 1, role = role)
        }
        appStateInteractor.changeGameState(AppState.RevealingRoles(choosableRoles, travellers = travellers))
    }
}

fun configureGameVMFactory() = ViewModelFactory {
    val rolesCountModelHelper = RolesCountModelHelper()
    ConfigureGameViewModel(
        getAppStateInteractor(),
        rolesCountModelHelper,
        RandomRolesListGenerator(rolesCountModelHelper)
    )
}


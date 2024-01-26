package com.example.botcgrimoire.presentation.configurescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.botcgrimoire.R
import com.example.botcgrimoire.di.getAppStateInteractor
import com.example.botcgrimoire.di.resourceManager
import com.example.botcgrimoire.domain.AppState
import com.example.botcgrimoire.domain.AppStateInteractor
import com.example.botcgrimoire.domain.Edition
import com.example.botcgrimoire.domain.RandomRolesListGenerator
import com.example.botcgrimoire.domain.ResourceManager
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
    private val randomRolesListGenerator: RandomRolesListGenerator,
    private val resourceManager: ResourceManager
) : ViewModel() {

    private val _state = MutableStateFlow(initState())
    val state: StateFlow<AppState.ConfigureGame> = _state.asStateFlow()

    private val _warningText = MutableStateFlow<String?>(null)
    val warningText: StateFlow<String?> = _warningText.asStateFlow()

    private var warningDeleteJob: Job? = null

    private fun initState(): AppState.ConfigureGame {
        return appStateInteractor.state.value as AppState.ConfigureGame
    }

    fun onRoleClick(role: Role) {
        val playersCount = _state.value.currentPlayersCount
        val isRemoved = _state.value.selectedRoles.contains(role)
        val newSelectedRoles = if (isRemoved) {
            _state.value.selectedRoles.filterNot { it == role }
        } else {
            val countModel = rolesCountModelHelper.getCountModel(playersCount, _state.value.selectedRoles)
            val requiredCountForRoleType = countModel.getCountForType(role.type)
            val selectedCountForRoleType = _state.value.selectedRoles.filter { it.type == role.type }.size
            if (requiredCountForRoleType != null && selectedCountForRoleType + 1 > requiredCountForRoleType) {
                _warningText.value = resourceManager.getString(R.string.cannot_add_role_warning)
                removeWarningAfterTimeout()
                _state.value.selectedRoles
            } else {
                _state.value.selectedRoles.plus(role)
            }
        }
        val newMap = _state.value.selectedRolesMap.plus(_state.value.currentEdition to newSelectedRoles)
        val newState = _state.value.copy(selectedRolesMap = newMap)
        setNewState { newState }
    }

    private fun removeWarningAfterTimeout() {
        warningDeleteJob?.cancel()
        warningDeleteJob = viewModelScope.launch {
            delay(3000)
            _warningText.value = null
        }
    }

    fun onPlayersCountChanged(newCount: Int) {
        val newSelectedRoles = if (newCount < _state.value.currentPlayersCount) {
            rolesCountModelHelper.removeRolesIfExceedsLimit(newCount, _state.value.selectedRoles)
        } else {
            _state.value.selectedRoles
        }
        val newMap = _state.value.selectedRolesMap.plus(_state.value.currentEdition to newSelectedRoles)
        setNewState { oldState -> oldState.copy(currentPlayersCount = newCount, selectedRolesMap = newMap) }
    }

    fun onRandomRolesClick() {
        val currentState = _state.value
        val selectedRoles =
            randomRolesListGenerator.generateRandomRoles(currentState.currentPlayersCount, currentState.currentEdition)
        val newMap = _state.value.selectedRolesMap.plus(_state.value.currentEdition to selectedRoles)
        setNewState { oldState -> oldState.copy(selectedRolesMap = newMap) }
    }

    fun onClearSelectedRoles() {
        val newMap = _state.value.selectedRolesMap.plus(_state.value.currentEdition to emptyList())
        setNewState { oldState -> oldState.copy(selectedRolesMap = newMap) }
    }

    private fun setNewState(changer: (AppState.ConfigureGame) -> AppState.ConfigureGame) {
        val newState = changer.invoke(_state.value)
        val validatedRolesList =
            rolesCountModelHelper.removeRolesIfExceedsLimit(newState.currentPlayersCount, newState.selectedRoles)
        val countModel = rolesCountModelHelper.getCountModel(newState.currentPlayersCount, newState.selectedRoles)
        val newMap = _state.value.selectedRolesMap.plus(newState.currentEdition to validatedRolesList)
        val newActualState = newState.copy(rolesCountModel = countModel, selectedRolesMap = newMap)
        _state.value = newActualState
        appStateInteractor.changeGameState(newActualState)
    }

    fun onContinueClick() {
        val travellers = _state.value.selectedRoles.filter { it.type == RoleType.Travellers }

        val choosableRoles = _state.value.selectedRoles.filterNot { it.type == RoleType.Travellers }.shuffled()
            .mapIndexed { index, role ->
                RoleForChoose(number = index + 1, role = role)
            }
        appStateInteractor.changeGameState(
            AppState.RevealingRoles(
                previousState = _state.value,
                currentEdition = _state.value.currentEdition,
                choosableRoles,
                travellers = travellers
            )
        )
    }

    fun onEditionSelected(edition: Edition) {
        setNewState {
            it.copy(currentEdition = edition)
        }
    }
}

fun configureGameVMFactory() = ViewModelFactory {
    val rolesCountModelHelper = RolesCountModelHelper()
    ConfigureGameViewModel(
        getAppStateInteractor(),
        rolesCountModelHelper,
        RandomRolesListGenerator(rolesCountModelHelper),
        resourceManager
    )
}


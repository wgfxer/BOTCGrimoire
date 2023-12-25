package com.example.botcgrimoire.domain

import androidx.compose.runtime.Immutable

/**
 * @author Valeriy Minnulin
 */
@Immutable
data class ConfigureGameScreenState(
    val currentPlayersCount: Int = 5,
    val selectedRoles: List<Role> = emptyList(),
    val warningText: String? = null,
    val rolesCountModel: RolesCountModel = checkNotNull(rolesCountMap[currentPlayersCount]) {
        "При создании объекта ChooseRolesScreenState не получилось взять из мапы rolesCountMap количество для 5 игроков"
    }
) {
    val selectedTownsfolk: List<Role> = selectedRoles.filter { it.type == RoleType.Townsfolk }
    val selectedOutsiders: List<Role> = selectedRoles.filter { it.type == RoleType.Outsiders }
    val selectedMinions: List<Role> = selectedRoles.filter { it.type == RoleType.Minions }
    val selectedDemons: List<Role> = selectedRoles.filter { it.type == RoleType.Demons }
    val selectedTravellers: List<Role> = selectedRoles.filter { it.type == RoleType.Travellers }

    val isContinueButtonEnabled: Boolean = isRolesEnoughForPlayersCount(selectedRoles, rolesCountModel, currentPlayersCount)
}







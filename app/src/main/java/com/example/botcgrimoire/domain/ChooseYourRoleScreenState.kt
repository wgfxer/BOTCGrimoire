package com.example.botcgrimoire.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * @author Valeriy Minnulin
 */
@Serializable
data class ChooseYourRoleScreenState(
    val roles: List<RoleForChoose>,
    val rolesForDrunk: List<Role>?,
    val rolesForLunatic: List<Role>?,
    @Transient
    val dialog: DialogData? = null
) {
    val isStartGameButtonEnabled = roles.all { it.isChosen }
}
package com.example.botcgrimoire.domain

/**
 * @author Valeriy Minnulin
 */
data class SettingsScreenState(
    val dialogData: DialogData? = null,
    val addRoleDialog: List<Role> = emptyList(),
    val removeRoleDialog: List<Role> = emptyList(),
    val playerNames: Map<Role, String> = emptyMap()
)
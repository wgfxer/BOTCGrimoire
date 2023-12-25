package com.example.botcgrimoire.domain

import com.example.botcgrimoire.R

/**
 * @author Valeriy Minnulin
 */
data class SettingsScreenState(
    val dialogData: DialogData? = null,
    val addRoleDialog: List<Role> = emptyList(),
    val removeRoleDialog: List<Role> = emptyList(),
    val playerNames: Map<Role, String> = emptyMap(),
    val showNightOrderInGrimoire: Boolean
) {
    val showNightOrderButtonText = if (showNightOrderInGrimoire) R.string.hide_night_order else R.string.show_night_order
}
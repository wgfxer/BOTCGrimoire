package com.example.botcgrimoire.domain

/**
 * @author Valeriy Minnulin
 */
object RemindersHelper {
    fun filterReminders(gameState: AppState.GameState, clickedRole: RoleGameState): List<Reminder> {
        val resultList = mutableListOf<Reminder>()
        val aliveRoles = gameState.roles.filter { !it.isDead }.map { roleState -> roleState.role }
        val availableReminders = reminders.filter {
            it.role == null || aliveRoles.contains(it.role) || it.canBeUsedAfterDeath
        }

        availableReminders.forEach {
            val canBeUsed = when (it.applyType) {
                ReminderApplyType.ForDead -> clickedRole.isDead
                is ReminderApplyType.ByRole -> clickedRole.role == it.applyType.role
                is ReminderApplyType.ByRoleSide -> clickedRole.role.isGood == it.applyType.isGood
                ReminderApplyType.ExceptMyself -> clickedRole.role != it.role
                ReminderApplyType.NoLimits -> true
                ReminderApplyType.OnlyMyself -> clickedRole.role == it.role
                ReminderApplyType.WhenImpDead -> !clickedRole.role.isGood && gameState.roles.find { it.role == Role.Imp }?.isDead ?: false
                is ReminderApplyType.ByRoleType -> clickedRole.role.type == it.applyType.roleType
            }
            if (canBeUsed) {
                resultList.add(it)
            }
        }
        if (clickedRole.isDead) resultList.removeAll { it.applyType != ReminderApplyType.ForDead }
        return resultList
    }

}
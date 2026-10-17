package com.example.botcgrimoire.domain

/**
 * @author Valeriy Minnulin
 */
object RemindersHelper {
    fun filterReminders(gameState: AppState.GameState, clickedRole: RoleGameState): List<Reminder> {
        val resultList = mutableListOf<Reminder>()
        val rolesInGame = gameState.roles.associateBy { it.role }
        val availableReminders = reminders.filter {
            val sourceRoleState = it.role?.let(rolesInGame::get)
            it.role == null || sourceRoleState != null && (!sourceRoleState.isDead || it.canBeUsedAfterDeath)
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

package com.example.botcgrimoire.domain

/**
 * @author Valeriy Minnulin
 */
class RandomRolesListGenerator(
    private val rolesCountModelHelper: RolesCountModelHelper
) {
    fun generateRandomRoles(playersCount: Int): List<Role> {
        val selectedRoles = mutableListOf<Role>()
        val defaultCountModel = checkNotNull(rolesCountMap[playersCount]) {
            "Нет модели количества ролей для числа игроков $playersCount"
        }
        RoleType.values().forEach { roleType ->
            val countForRoleType = defaultCountModel.getCountForType(roleType) ?: 0
            val allRolesForType = Role.values().filter { it.type == roleType }
            val chosenRolesForType = allRolesForType.shuffled().take(countForRoleType)
            selectedRoles.addAll(chosenRolesForType)
        }
        return validateRolesList(playersCount, selectedRoles)
    }

    private fun validateRolesList(playersCount: Int, selectedRoles: MutableList<Role>): List<Role> {
        val countModel = rolesCountModelHelper.getCountModel(playersCount, selectedRoles)
        RoleType.values().forEach {  roleType ->
            val requiredCount = countModel.getCountForType(roleType)
            val actualCount = selectedRoles.filter { it.type == roleType }.size
            if (requiredCount != null && requiredCount != actualCount) {
                selectedRoles.removeIf { it.type == roleType }
                val newListForRoleType = roleType.getAllRoles().shuffled().take(requiredCount)
                selectedRoles.addAll(newListForRoleType)
            }
        }
        return selectedRoles.toList()
    }
}
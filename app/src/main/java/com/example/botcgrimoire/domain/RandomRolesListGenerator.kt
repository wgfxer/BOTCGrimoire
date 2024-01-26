package com.example.botcgrimoire.domain

/**
 * @author Valeriy Minnulin
 */
class RandomRolesListGenerator(
    private val rolesCountModelHelper: RolesCountModelHelper
) {
    fun generateRandomRoles(playersCount: Int, edition: Edition): List<Role> {
        val allRoles = edition.roles
        val selectedRoles = mutableListOf<Role>()
        val defaultCountModel = checkNotNull(rolesCountMap[playersCount]) {
            "Нет модели количества ролей для числа игроков $playersCount"
        }
        RoleType.values().forEach { roleType ->
            val countForRoleType = defaultCountModel.getCountForType(roleType) ?: 0
            val allRolesForType = allRoles.filter { it.type == roleType }
            val chosenRolesForType = allRolesForType.shuffled().take(countForRoleType)
            selectedRoles.addAll(chosenRolesForType)
        }
        return validateRolesList(playersCount, selectedRoles, edition)
    }

    private fun validateRolesList(playersCount: Int, selectedRoles: MutableList<Role>, edition: Edition): List<Role> {
        val countModel = rolesCountModelHelper.getCountModel(playersCount, selectedRoles)
        RoleType.values().forEach {  roleType ->
            val requiredCount = countModel.getCountForType(roleType)
            val actualCount = selectedRoles.filter { it.type == roleType }.size
            if (requiredCount != null && requiredCount != actualCount) {
                selectedRoles.removeIf { it.type == roleType }
                val newListForRoleType = edition.roles.filterByType(roleType).shuffled().take(requiredCount)
                selectedRoles.addAll(newListForRoleType)
            }
        }
        return selectedRoles.toList()
    }
}
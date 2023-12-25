package com.example.botcgrimoire.domain

/**
 * @author Valeriy Minnulin
 */
class RolesCountModelHelper {
    fun getCountModel(playersCount: Int, selectedRoles: List<Role>): RolesCountModel {
        val defaultModel = checkNotNull(rolesCountMap[playersCount]) {
            "При получении модели количества ролей не получилось найти дефолтную модель для $playersCount игроков "
        }
        if (selectedRoles.contains(Role.Baron)) {
            return defaultModel.copy(
                townsfolkCount = defaultModel.townsfolkCount - 2,
                outsidersCount = defaultModel.outsidersCount + 2
            )
        }
        return defaultModel
    }

    /**
     * Возвращает новый список, на основании [selectedRoles], из которого удалены
     * роли, в том случае если эти роли превышают лимит для текущего количества игроков [playersCount].
     * Может быть ситуация что был удален Барон, и нужна повторная валидация(чтобы убрались лишние изгои)
     */
    fun removeRolesIfExceedsLimit(playersCount: Int, selectedRoles: List<Role>): List<Role> {
        val rolesToRemove = mutableListOf<Role>()
        val countModel = getCountModel(playersCount, selectedRoles)
        RoleType.values().forEach { roleType ->
            val selectedRolesForType = selectedRoles.filter { it.type == roleType }
            val requiredCountForRoleType = countModel.getCountForType(roleType)
            if (requiredCountForRoleType != null && selectedRolesForType.size > requiredCountForRoleType) {
                val countOfRolesToRemove = selectedRolesForType.size - requiredCountForRoleType
                rolesToRemove.addAll(selectedRolesForType.shuffled().take(countOfRolesToRemove))
            }
        }
        return selectedRoles.minus(rolesToRemove.toSet())
    }
}
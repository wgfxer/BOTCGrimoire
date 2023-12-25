package com.example.botcgrimoire.domain

import androidx.annotation.StringRes
import com.example.botcgrimoire.R
import com.example.botcgrimoire.domain.ReminderApplyType.ByRole
import com.example.botcgrimoire.domain.ReminderApplyType.ByRoleSide
import com.example.botcgrimoire.domain.ReminderApplyType.ForDead
import com.example.botcgrimoire.domain.ReminderApplyType.NoLimits
import com.example.botcgrimoire.domain.ReminderApplyType.OnlyMyself
import kotlinx.serialization.Serializable

/**
 * @author Valeriy Minnulin
 */
@Serializable
data class Reminder(
    val role: Role?,
    // @StringRes val actionInfo: Int,
    @StringRes val infoOnCard: Int,
    val applyType: ReminderApplyType = ReminderApplyType.ExceptMyself,
    val canBeUsedAfterDeath: Boolean = false,
    private val _icon: Int = -1,
    val repeatable: Boolean = false
) {
    val icon: Int = role?.icon ?: _icon
}

@Serializable
sealed interface ReminderApplyType {
    @Serializable
    object ExceptMyself: ReminderApplyType
    @Serializable
    object OnlyMyself: ReminderApplyType
    @Serializable
    object NoLimits: ReminderApplyType
    @Serializable
    data class ByRoleSide(val isGood: Boolean): ReminderApplyType
    @Serializable
    data class ByRole(val role: Role): ReminderApplyType
    @Serializable
    object ForDead: ReminderApplyType
    @Serializable
    object WhenImpDead: ReminderApplyType
    @Serializable
    data class ByRoleType(val roleType: RoleType): ReminderApplyType

}

val drunkReminder = Reminder(Role.Drunk, R.string.drunk_reminder_description, applyType = OnlyMyself)
val reminders = listOf(
    Reminder(Role.Butler, R.string.butler_reminder_description),
    Reminder(Role.Monk, R.string.monk_reminder_description),
    drunkReminder,
    Reminder(Role.Poisoner, R.string.poisoner_reminder_description, NoLimits),
    Reminder(Role.FortuneTeller, R.string.fortune_teller_reminder_description, ByRoleSide(true)),
    Reminder(Role.ScarletWoman, R.string.scarlet_woman_reminder_description, ByRole(Role.ScarletWoman)),
    Reminder(Role.Imp, R.string.imp_reminder_description, ReminderApplyType.WhenImpDead, canBeUsedAfterDeath = true),
    Reminder(Role.Undertaker, R.string.undertaker_reminder_description, ForDead),
    Reminder(Role.Investigator, R.string.investigator_reminder_description),
    Reminder(Role.Investigator, R.string.investigator_reminder_description_false),
    Reminder(Role.Librarian, R.string.librarian_reminder_description),
    Reminder(Role.Librarian, R.string.librarian_reminder_description_false),
    Reminder(Role.Washerwoman, R.string.washerwoman_reminder_description),
    Reminder(Role.Washerwoman, R.string.washerwoman_reminder_description_false),
    Reminder(Role.Slayer, R.string.slayer_reminder_description, OnlyMyself),
    Reminder(Role.Virgin, R.string.virgin_reminder_description, OnlyMyself),
    Reminder(Role.Thief, R.string.thief_reminder_description, NoLimits),
    Reminder(Role.Bureaucrat, R.string.bureaucrat_reminder_description),
    Reminder(role = null, _icon = R.drawable.demon, infoOnCard = R.string.evil_traveller_reminder_description, applyType = ReminderApplyType.ByRoleType(RoleType.Travellers), repeatable = true),
    Reminder(role = null, _icon = R.drawable.townsfolk, infoOnCard = R.string.good_traveller_reminder_description, applyType = ReminderApplyType.ByRoleType(RoleType.Travellers), repeatable = true),
)


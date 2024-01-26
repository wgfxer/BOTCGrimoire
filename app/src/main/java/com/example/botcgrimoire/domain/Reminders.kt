package com.example.botcgrimoire.domain

import androidx.annotation.StringRes
import com.example.botcgrimoire.R
import com.example.botcgrimoire.domain.ReminderApplyType.ByRole
import com.example.botcgrimoire.domain.ReminderApplyType.ByRoleSide
import com.example.botcgrimoire.domain.ReminderApplyType.ByRoleType
import com.example.botcgrimoire.domain.ReminderApplyType.ExceptMyself
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
    val applyType: ReminderApplyType = ExceptMyself,
    val canBeUsedAfterDeath: Boolean = false,
    private val _icon: Int = -1,
    val repeatable: Boolean = false,
    val isDrunk: Boolean = false
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

val drunkReminder = Reminder(Role.Drunk, R.string.drunk_reminder_description, applyType = OnlyMyself, isDrunk = true)
val reminders = listOf(
    //trouble brewing
    Reminder(Role.Butler, R.string.butler_reminder_description),
    Reminder(Role.Monk, R.string.secured_reminder),
    drunkReminder,
    Reminder(Role.Poisoner, R.string.poisoned_reminder, NoLimits, isDrunk = true),
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
    Reminder(Role.Slayer, R.string.used_reminder, OnlyMyself),
    Reminder(Role.Virgin, R.string.used_reminder, OnlyMyself),
    Reminder(Role.Thief, R.string.thief_reminder_description, NoLimits),
    Reminder(Role.Bureaucrat, R.string.bureaucrat_reminder_description),
    Reminder(role = null, _icon = R.drawable.demon, infoOnCard = R.string.evil_traveller_reminder_description, applyType = ByRoleType(RoleType.Travellers), repeatable = true),
    Reminder(role = null, _icon = R.drawable.townsfolk, infoOnCard = R.string.good_traveller_reminder_description, applyType = ByRoleType(RoleType.Travellers), repeatable = true),

    //bad moon rising
    Reminder(Role.Tinker, R.string.dying_reminder, ByRole(Role.Tinker)),
    Reminder(Role.Assassin, R.string.dying_reminder, NoLimits),
    Reminder(Role.Zombuul, R.string.dying_reminder, ByRole(Role.Zombuul)),
    Reminder(Role.Courtier, R.string.drunk_reminder_1, NoLimits, isDrunk = true),
    Reminder(Role.Courtier, R.string.drunk_reminder_2, NoLimits, isDrunk = true),
    Reminder(Role.Courtier, R.string.drunk_reminder_3, NoLimits, isDrunk = true),
    Reminder(Role.DevilsAdvocate, R.string.survive_execution_reminder, NoLimits),
    Reminder(Role.Exorcist, R.string.chosen_reminder, NoLimits),
    Reminder(Role.Zombuul, R.string.not_going_to_die_reminder, ByRole(Role.Zombuul)),
    Reminder(Role.Gambler, R.string.dying_reminder, ByRole(Role.Gambler)),
    Reminder(Role.Godfather, R.string.dead_day_reminder, ByRoleType(roleType = RoleType.Outsiders)),
    Reminder(Role.Godfather, R.string.dying_reminder, NoLimits),
    Reminder(Role.Goon, R.string.drunk_reminder, NoLimits, isDrunk = true),
    Reminder(Role.Gossip, R.string.dying_reminder, NoLimits),
    Reminder(Role.Grandmother, R.string.grandson_reminder, ExceptMyself),//TODO and byRoleSide
    Reminder(Role.Grandmother, R.string.dying_reminder, OnlyMyself),
    Reminder(Role.Innkeeper, R.string.secured_reminder, NoLimits),
    Reminder(Role.Innkeeper, R.string.drunk_innkeeper_reminder, NoLimits, isDrunk = true),
    Reminder(Role.Lunatic, R.string.attack_reminder_1, NoLimits),
    Reminder(Role.Lunatic, R.string.attack_reminder_2, NoLimits),
    Reminder(Role.Lunatic, R.string.attack_reminder_3, NoLimits),
    Reminder(Role.Minstrel, R.string.everybody_drunk_reminder, OnlyMyself),
    Reminder(Role.Moonchild, R.string.dying_reminder, ExceptMyself),
    Reminder(Role.Po, R.string.dying_reminder_1, NoLimits),
    Reminder(Role.Po, R.string.dying_reminder_2, NoLimits),
    Reminder(Role.Po, R.string.dying_reminder_3, NoLimits),
    Reminder(Role.Po, R.string.attack_reminder_x3, OnlyMyself),
    Reminder(Role.Professor, R.string.aliving_reminder, ForDead, canBeUsedAfterDeath = true),//TODO For dead
    Reminder(Role.TeaLady, R.string.secured_reminder, ByRoleSide(true), repeatable = true),
    Reminder(Role.TeaLady, R.string.secured_reminder, ByRoleSide(true), repeatable = true),
    Reminder(Role.Pukka, R.string.poisoned_reminder, NoLimits, isDrunk = true),
    Reminder(Role.Pukka, R.string.dying_reminder, NoLimits),
    Reminder(Role.Sailor, R.string.drunk_reminder, NoLimits, isDrunk = true),
    Reminder(Role.Shabaloth, R.string.dying_reminder_1, NoLimits),
    Reminder(Role.Shabaloth, R.string.dying_reminder_2, NoLimits),
    Reminder(Role.Shabaloth, R.string.aliving_reminder, NoLimits),
    Reminder(Role.Judge, R.string.used_reminder, OnlyMyself),
    Reminder(Role.Fool, R.string.used_reminder, OnlyMyself),
)


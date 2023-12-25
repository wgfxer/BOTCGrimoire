package com.example.botcgrimoire.domain

import com.example.botcgrimoire.R

/**
 * @author Valeriy Minnulin
 */
val firstNight = listOf(
    NightOrderModel(Role.Poisoner, R.string.poisoner_teller_info_fn),
    NightOrderModel(Role.Washerwoman, R.string.washerwoman_teller_info),
    NightOrderModel(Role.Librarian, R.string.librarian_teller_info),
    NightOrderModel(Role.Investigator, R.string.investigator_teller_info),
    NightOrderModel(Role.Chef, R.string.chef_teller_info),
    NightOrderModel(Role.Empath, R.string.empath_teller_info),
    NightOrderModel(Role.FortuneTeller, R.string.fortune_teller_teller_info),
    NightOrderModel(Role.Butler, R.string.butler_teller_info),
    NightOrderModel(Role.Spy, R.string.spy_teller_info),
)

val otherNights = listOf(
    NightOrderModel(Role.Poisoner, R.string.poisoner_teller_info_on),
    NightOrderModel(Role.Monk, R.string.monk_teller_info),
    NightOrderModel(Role.ScarletWoman, R.string.scarlet_woman_teller_info, isConditional = true),
    NightOrderModel(Role.Imp, R.string.imp_teller_info),
    NightOrderModel(Role.RavenKeeper, R.string.ravenkeeper_teller_info, isConditional = true),
    NightOrderModel(Role.Empath, R.string.empath_teller_info),
    NightOrderModel(Role.FortuneTeller, R.string.fortune_teller_teller_info),
    NightOrderModel(Role.Undertaker, R.string.undertaker_teller_info, isConditional = true),
    NightOrderModel(Role.Butler, R.string.butler_teller_info),
    NightOrderModel(Role.Spy, R.string.spy_teller_info),
)

data class NightOrderModel(
    val role: Role,
    val description: Int,
    val isDrunk: Boolean = false,
    val isDead: Boolean = false,
    val isConditional: Boolean = false,
    val playerName: String = ""
)
package com.example.botcgrimoire.domain

import com.example.botcgrimoire.R

/**
 * @author Valeriy Minnulin
 */

//TROUBLE BREWING

private val firstNightTroubleBrewing = listOf(
    TravellersInfo,
    MinionsInfo,
    DemonBluffInfo,
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

private val otherNightsTroubleBrewing = listOf(
    TravellersInfo,
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

//BAD MOON RISING
const val LUNATIC_BLUFF_INFO_KEY = "LUNATIC_BLUFF_INFO_KEY"

private val firstNightBadMoonRising = listOf(
    TravellersInfo,
    MinionsInfo,
    NightOrderModel(Role.Lunatic, R.string.lunatic_teller_info_fn, key = LUNATIC_BLUFF_INFO_KEY),
    DemonBluffInfo,
    DemonLunaticInfo,
    NightOrderModel(Role.Sailor, R.string.sailor_teller_info_fn),
    NightOrderModel(Role.Courtier, R.string.courtier_teller_info_fn),
    NightOrderModel(Role.Godfather, R.string.godfather_teller_info_fn),
    NightOrderModel(Role.DevilsAdvocate, R.string.devils_advocate_teller_info_fn),
    // NightOrderModel(Role.Lunatic, R.string.lunatic_teller_info_fn_second, isConditional = true),
    NightOrderModel(Role.Pukka, R.string.pukka_teller_info_fn),
    NightOrderModel(Role.Grandmother, R.string.grandmother_teller_info_fn),
    NightOrderModel(Role.Chambermaid, R.string.chambermaid_teller_info),
    NightOrderModel(Role.Goon, R.string.goon_teller_info, isConditional = true),
)


private val otherNightsBadMoonRising = listOf(
    TravellersInfo,
    NightOrderModel(Role.Minstrel, R.string.minstrel_teller_info_on, isConditional = true),
    NightOrderModel(Role.Sailor, R.string.sailor_teller_info_on),
    NightOrderModel(Role.Innkeeper, R.string.innkeeper_teller_info_on),
    NightOrderModel(Role.Courtier, R.string.courtier_teller_info_on, isConditional = true),
    NightOrderModel(Role.Gambler, R.string.gambler_teller_info_on),
    NightOrderModel(Role.DevilsAdvocate, R.string.devils_advocate_teller_info_on),
    // NightOrderModel(Role.Lunatic, R.string.lunatic_teller_info_on),
    NightOrderModel(Role.Exorcist, R.string.exorcist_teller_info_on),
    NightOrderModel(Role.Zombuul, R.string.zombuul_teller_info_on, isConditional = true),
    NightOrderModel(Role.Pukka, R.string.pukka_teller_info_on),
    NightOrderModel(Role.Shabaloth, R.string.shabaloth_teller_info_on),
    NightOrderModel(Role.Po, R.string.po_teller_info_on),
    NightOrderModel(Role.Assassin, R.string.assassin_teller_info_on, isConditional = true),
    NightOrderModel(Role.Godfather, R.string.godfather_teller_info_on, isConditional = true),
    NightOrderModel(Role.Professor, R.string.professor_teller_info_on, isConditional = true),
    NightOrderModel(Role.Gossip, R.string.gossip_teller_info_on, isConditional = true),
    NightOrderModel(Role.Tinker, R.string.tinker_teller_info_on),
    NightOrderModel(Role.Moonchild, R.string.moonchild_teller_info_on, isConditional = true),
    NightOrderModel(Role.Grandmother, R.string.grandmother_teller_info_on, isConditional = true),
    NightOrderModel(Role.Chambermaid, R.string.chambermaid_teller_info),
    NightOrderModel(Role.Goon, R.string.goon_teller_info, isConditional = true),
)

fun getOrder(isFirst: Boolean, edition: Edition): List<NightOrderItem> {
    return when (edition) {
        Edition.TroubleBrewing -> if (isFirst) firstNightTroubleBrewing else otherNightsTroubleBrewing
        Edition.BadMoonRising -> if (isFirst) firstNightBadMoonRising else otherNightsBadMoonRising
    }
}

sealed interface NightOrderItem

data class NightOrderModel(
    val role: Role,
    val description: Int,
    val isDrunk: Boolean = false,
    val isDead: Boolean = false,
    val isConditional: Boolean = false,
    val playerName: String = "",
    val key: String = "",
    val descriptionString: String? = null
) : NightOrderItem


object TravellersInfo : NightOrderItem
object MinionsInfo : NightOrderItem
object DemonBluffInfo : NightOrderItem
object DemonLunaticInfo : NightOrderItem


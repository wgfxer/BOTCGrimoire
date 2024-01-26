package com.example.botcgrimoire.domain

import com.example.botcgrimoire.R
import kotlinx.serialization.Serializable

/**
 * @author Valeriy Minnulin
 */
@Serializable
enum class Edition(
    val nameResId: Int,
    val roles: List<Role>
) {
    TroubleBrewing(R.string.trouble_brewing, troubleBrewingRoles),
    BadMoonRising(R.string.bad_moon_rising, badMoonRisingRoles),
    // SectsAndViolets(R.string.sects_and_violets),
    // CustomScript(R.string.custom_script)

}

private val troubleBrewingRoles = listOf(
    Role.Washerwoman,
    Role.Librarian,
    Role.Investigator,
    Role.Chef,
    Role.Empath,
    Role.FortuneTeller,
    Role.Undertaker,
    Role.Monk,
    Role.RavenKeeper,
    Role.Virgin,
    Role.Slayer,
    Role.Soldier,
    Role.Mayor,
    Role.Saint,
    Role.Recluse,
    Role.Drunk,
    Role.Butler,
    Role.Poisoner,
    Role.Baron,
    Role.ScarletWoman,
    Role.Spy,
    Role.Imp,
    Role.Bureaucrat,
    Role.Thief,
    Role.Gunslinger,
    Role.Scapegoat,
    Role.Beggar,
)

private val badMoonRisingRoles = listOf(
    Role.Grandmother,
    Role.Sailor,
    Role.Chambermaid,
    Role.Exorcist,
    Role.Innkeeper,
    Role.Gambler,
    Role.Gossip,
    Role.Courtier,
    Role.Professor,
    Role.Minstrel,
    Role.TeaLady,
    Role.Pacifist,
    Role.Fool,
    Role.Tinker,
    Role.Moonchild,
    Role.Goon,
    Role.Lunatic,
    Role.Godfather,
    Role.DevilsAdvocate,
    Role.Assassin,
    Role.Mastermind,
    Role.Zombuul,
    Role.Pukka,
    Role.Shabaloth,
    Role.Po,
    Role.Apprentice,
    Role.Matron,
    Role.Judge,
    Role.Bishop,
    Role.Voudon,
)
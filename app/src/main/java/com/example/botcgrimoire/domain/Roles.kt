package com.example.botcgrimoire.domain

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.geometry.Offset
import com.example.botcgrimoire.R
import kotlinx.serialization.Serializable

/**
 * @author Valeriy Minnulin
 */
val townsfolk = Role.values().filter { it.type == RoleType.Townsfolk }
val outsiders = Role.values().filter { it.type == RoleType.Outsiders }
val minions = Role.values().filter { it.type == RoleType.Minions }
val demons = Role.values().filter { it.type == RoleType.Demons }
val travellers = Role.values().filter { it.type == RoleType.Travellers }

@Serializable
data class RoleGameState(
    val role: Role,
    val isDead: Boolean = false,
    val hasVote: Boolean = true,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val playerName: String = ""
)

@Serializable
enum class Role(
    @StringRes val roleName: Int,
    val isGood: Boolean,
    val playerInfo: Int,
    val tellerInfo: Int? = null,
    @DrawableRes val icon: Int,
    val type: RoleType
) {
                                            //Trouble Brewing
    //region townsfolk
    Washerwoman(
        roleName = R.string.washerwoman_role_name,
        icon = R.drawable.icon_washerwoman,
        playerInfo = R.string.washerwoman_player_info,
        tellerInfo = R.string.washerwoman_teller_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Librarian(
        roleName = R.string.librarian_role_name,
        icon = R.drawable.icon_librarian,
        playerInfo = R.string.librarian_player_info,
        tellerInfo = R.string.librarian_teller_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Investigator(
        roleName = R.string.investigator_role_name,
        icon = R.drawable.icon_investigator,
        playerInfo = R.string.investigator_player_info,
        tellerInfo = R.string.investigator_teller_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Chef(
        roleName = R.string.chef_role_name,
        icon = R.drawable.icon_chef,
        playerInfo = R.string.chef_player_info,
        tellerInfo = R.string.chef_teller_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Empath(
        roleName = R.string.empath_role_name,
        icon = R.drawable.icon_empath,
        playerInfo = R.string.empath_player_info,
        tellerInfo = R.string.empath_teller_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    FortuneTeller(
        roleName = R.string.fortune_teller_role_name,
        icon = R.drawable.icon_fortune_teller,
        playerInfo = R.string.fortune_teller_player_info,
        tellerInfo = R.string.fortune_teller_teller_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Undertaker(
        roleName = R.string.undertaker_role_name,
        icon = R.drawable.icon_undertaker,
        playerInfo = R.string.undertaker_player_info,
        tellerInfo = R.string.undertaker_teller_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Monk(
        roleName = R.string.monk_role_name,
        icon = R.drawable.icon_monk,
        playerInfo = R.string.monk_player_info,
        tellerInfo = R.string.monk_teller_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    RavenKeeper(
        roleName = R.string.ravenkeeper_role_name,
        icon = R.drawable.icon_ravenkeeper,
        playerInfo = R.string.ravenkeeper_player_info,
        tellerInfo = R.string.ravenkeeper_teller_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Virgin(
        roleName = R.string.virgin_role_name,
        icon = R.drawable.icon_virgin,
        playerInfo = R.string.virgin_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Slayer(
        roleName = R.string.slayer_role_name,
        icon = R.drawable.icon_slayer,
        playerInfo = R.string.slayer_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Soldier(
        roleName = R.string.soldier_role_name,
        icon = R.drawable.icon_soldier,
        playerInfo = R.string.soldier_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Mayor(
        roleName = R.string.mayor_role_name,
        icon = R.drawable.icon_mayor,
        playerInfo = R.string.mayor_player_info,
        isGood = true, type = RoleType.Townsfolk
    ),

    //region outsiders
    Saint(
        roleName = R.string.saint_role_name,
        icon = R.drawable.icon_saint,
        playerInfo = R.string.saint_player_info,
        isGood = true,
        type = RoleType.Outsiders
    ),
    Recluse(
        roleName = R.string.recluse_role_name,
        icon = R.drawable.icon_recluse,
        playerInfo = R.string.recluse_player_info,
        isGood = true,
        type = RoleType.Outsiders
    ),
    Drunk(
        roleName = R.string.drunk_role_name,
        icon = R.drawable.icon_drunk,
        playerInfo = R.string.drunk_player_info,
        isGood = true,
        type = RoleType.Outsiders
    ),
    Butler(
        roleName = R.string.butler_role_name,
        icon = R.drawable.icon_butler,
        playerInfo = R.string.butler_player_info,
        tellerInfo = R.string.butler_teller_info,
        isGood = true,
        type = RoleType.Outsiders
    ),

    //region minions
    Poisoner(
        roleName = R.string.poisoner_role_name,
        icon = R.drawable.icon_poisoner,
        playerInfo = R.string.poisoner_player_info,
        tellerInfo = R.string.poisoner_teller_info_fn,
        isGood = false,
        type = RoleType.Minions
    ),
    Baron(
        roleName = R.string.baron_role_name,
        icon = R.drawable.icon_baron,
        playerInfo = R.string.baron_player_info,
        isGood = false,
        type = RoleType.Minions
    ),
    ScarletWoman(
        roleName = R.string.scarlet_woman_role_name,
        icon = R.drawable.icon_scarlet_woman,
        playerInfo = R.string.scarlet_woman_player_info,
        tellerInfo = R.string.scarlet_woman_teller_info,
        isGood = false,
        type = RoleType.Minions
    ),
    Spy(
        roleName = R.string.spy_role_name,
        icon = R.drawable.icon_spy,
        playerInfo = R.string.spy_player_info,
        tellerInfo = R.string.spy_teller_info,
        isGood = false,
        type = RoleType.Minions
    ),

    //region demons
    Imp(
        roleName = R.string.imp_role_name,
        icon = R.drawable.icon_imp,
        playerInfo = R.string.imp_player_info,
        tellerInfo = R.string.imp_teller_info,
        isGood = false,
        type = RoleType.Demons
    ),

    //region travellers
    Bureaucrat(
        roleName = R.string.bureaucrat_role_name,
        icon = R.drawable.icon_bureaucrat,
        playerInfo = R.string.bureaucrat_player_info,
        isGood = false,
        type = RoleType.Travellers
    ),
    Thief(
        roleName = R.string.thief_role_name,
        icon = R.drawable.icon_thief,
        playerInfo = R.string.thief_player_info,
        isGood = false,
        type = RoleType.Travellers
    ),
    Gunslinger(
        roleName = R.string.gunslinger_role_name,
        icon = R.drawable.icon_gunslinger,
        playerInfo = R.string.gunslinger_player_info,
        isGood = false,
        type = RoleType.Travellers
    ),
    Scapegoat(
        roleName = R.string.scapegoat_role_name,
        icon = R.drawable.icon_scapegoat,
        playerInfo = R.string.scapegoat_player_info,
        isGood = false,
        type = RoleType.Travellers
    ),
    Beggar(
        roleName = R.string.beggar_role_name,
        icon = R.drawable.icon_beggar,
        playerInfo = R.string.beggar_player_info,
        isGood = false,
        type = RoleType.Travellers
    ),



                                                // Bad Moon Rising

    Grandmother(
        roleName = R.string.grandmother_role_name,
        icon = R.drawable.icon_grandmother,
        playerInfo = R.string.grandmother_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Sailor(
        roleName = R.string.sailor_role_name,
        icon = R.drawable.icon_sailor,
        playerInfo = R.string.sailor_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Chambermaid(
        roleName = R.string.chambermaid_role_name,
        icon = R.drawable.icon_chambermaid,
        playerInfo = R.string.chambermaid_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Exorcist(
        roleName = R.string.exorcist_role_name,
        icon = R.drawable.icon_exorcist,
        playerInfo = R.string.exorcist_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Innkeeper(
        roleName = R.string.innkeeper_role_name,
        icon = R.drawable.icon_innkeeper,
        playerInfo = R.string.innkeeper_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Gambler(
        roleName = R.string.gambler_role_name,
        icon = R.drawable.icon_gambler,
        playerInfo = R.string.gambler_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Gossip(
        roleName = R.string.gossip_role_name,
        icon = R.drawable.icon_gossip,
        playerInfo = R.string.gossip_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Courtier(
        roleName = R.string.courtier_role_name,
        icon = R.drawable.icon_courtier,
        playerInfo = R.string.courtier_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Professor(
        roleName = R.string.professor_role_name,
        icon = R.drawable.icon_professor,
        playerInfo = R.string.professor_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Minstrel(
        roleName = R.string.minstrel_role_name,
        icon = R.drawable.icon_minstrel,
        playerInfo = R.string.minstrel_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    TeaLady(
        roleName = R.string.tea_lady_role_name,
        icon = R.drawable.icon_tea_lady,
        playerInfo = R.string.tea_lady_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Pacifist(
        roleName = R.string.pacifist_role_name,
        icon = R.drawable.icon_pacifist,
        playerInfo = R.string.pacifist_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),
    Fool(
        roleName = R.string.fool_role_name,
        icon = R.drawable.icon_fool,
        playerInfo = R.string.fool_player_info,
        isGood = true,
        type = RoleType.Townsfolk
    ),

    Tinker(
        roleName = R.string.tinker_role_name,
        icon = R.drawable.icon_tinker,
        playerInfo = R.string.tinker_player_info,
        isGood = true,
        type = RoleType.Outsiders
    ),
    Moonchild(
        roleName = R.string.moonchild_role_name,
        icon = R.drawable.icon_moonchild,
        playerInfo = R.string.moonchild_player_info,
        isGood = true,
        type = RoleType.Outsiders
    ),
    Goon(
        roleName = R.string.goon_role_name,
        icon = R.drawable.icon_goon,
        playerInfo = R.string.goon_player_info,
        isGood = true,
        type = RoleType.Outsiders
    ),
    Lunatic(
        roleName = R.string.lunatic_role_name,
        icon = R.drawable.icon_lunatic,
        playerInfo = R.string.lunatic_player_info,
        isGood = true,
        type = RoleType.Outsiders
    ),

    Godfather(
        roleName = R.string.godfather_role_name,
        icon = R.drawable.icon_godfather,
        playerInfo = R.string.godfather_player_info,
        isGood = false,
        type = RoleType.Minions
    ),
    DevilsAdvocate(
        roleName = R.string.devils_advocate_role_name,
        icon = R.drawable.icon_devils_advocate,
        playerInfo = R.string.devils_advocate_player_info,
        isGood = false,
        type = RoleType.Minions
    ),
    Assassin(
        roleName = R.string.assassin_role_name,
        icon = R.drawable.icon_assassin,
        playerInfo = R.string.assassin_player_info,
        isGood = false,
        type = RoleType.Minions
    ),
    Mastermind(
        roleName = R.string.mastermind_role_name,
        icon = R.drawable.icon_mastermind,
        playerInfo = R.string.mastermind_player_info,
        isGood = false,
        type = RoleType.Minions
    ),


    Zombuul(
        roleName = R.string.zombuul_role_name,
        icon = R.drawable.icon_zombuul,
        playerInfo = R.string.zombuul_player_info,
        isGood = false,
        type = RoleType.Demons
    ),
    Pukka(
        roleName = R.string.pukka_role_name,
        icon = R.drawable.icon_pukka,
        playerInfo = R.string.pukka_player_info,
        isGood = false,
        type = RoleType.Demons
    ),
    Shabaloth(
        roleName = R.string.shabaloth_role_name,
        icon = R.drawable.icon_shabaloth,
        playerInfo = R.string.shabaloth_player_info,
        isGood = false,
        type = RoleType.Demons
    ),
    Po(
        roleName = R.string.po_role_name,
        icon = R.drawable.icon_po,
        playerInfo = R.string.po_player_info,
        isGood = false,
        type = RoleType.Demons
    ),

    Apprentice(
        roleName = R.string.apprentice_role_name,
        icon = R.drawable.icon_apprentice,
        playerInfo = R.string.apprentice_player_info,
        isGood = false,
        type = RoleType.Travellers
    ),
    Matron(
        roleName = R.string.matron_role_name,
        icon = R.drawable.icon_matron,
        playerInfo = R.string.matron_player_info,
        isGood = false,
        type = RoleType.Travellers
    ),
    Judge(
        roleName = R.string.judge_role_name,
        icon = R.drawable.icon_judge,
        playerInfo = R.string.judge_player_info,
        isGood = false,
        type = RoleType.Travellers
    ),
    Bishop(
        roleName = R.string.bishop_role_name,
        icon = R.drawable.icon_bishop,
        playerInfo = R.string.bishop_player_info,
        isGood = false,
        type = RoleType.Travellers
    ),
    Voudon(
        roleName = R.string.voudon_role_name,
        icon = R.drawable.icon_voudon,
        playerInfo = R.string.voudon_player_info,
        isGood = false,
        type = RoleType.Travellers
    ),

}

@Serializable
enum class RoleType(val typeName: Int) {
    Townsfolk(R.string.townsfolk_type_name),
    Outsiders(R.string.outsiders_type_name),
    Minions(R.string.minions_type_name),
    Demons(R.string.demons_type_name),
    Travellers(R.string.travellers_type_name),
}

@Serializable
data class RolesCountModel(
    val townsfolkCount: Int,
    val outsidersCount: Int,
    val minionsCount: Int,
    val demonsCount: Int,
) {
    fun getCountForType(roleType: RoleType): Int? {
        return when (roleType) {
            RoleType.Townsfolk -> townsfolkCount
            RoleType.Outsiders -> outsidersCount
            RoleType.Minions -> minionsCount
            RoleType.Demons -> demonsCount
            RoleType.Travellers -> null
        }
    }
}

val rolesCountMap = run {
    val fifteenOrMorePlayersModel = RolesCountModel(9, 2, 3, 1)
    mapOf(
        5 to RolesCountModel(3, 0, 1, 1),
        6 to RolesCountModel(3, 1, 1, 1),
        7 to RolesCountModel(5, 0, 1, 1),
        8 to RolesCountModel(5, 1, 1, 1),
        9 to RolesCountModel(5, 2, 1, 1),
        10 to RolesCountModel(7, 0, 2, 1),
        11 to RolesCountModel(7, 1, 2, 1),
        12 to RolesCountModel(7, 2, 2, 1),
        13 to RolesCountModel(9, 0, 3, 1),
        14 to RolesCountModel(9, 1, 3, 1),
        15 to fifteenOrMorePlayersModel,
        16 to fifteenOrMorePlayersModel,
        17 to fifteenOrMorePlayersModel,
        18 to fifteenOrMorePlayersModel,
        19 to fifteenOrMorePlayersModel,
        20 to fifteenOrMorePlayersModel,
    )
}

fun RoleType.getAllRoles(): List<Role> {
    return when (this) {
        RoleType.Townsfolk -> townsfolk
        RoleType.Outsiders -> outsiders
        RoleType.Minions -> minions
        RoleType.Demons -> demons
        RoleType.Travellers -> travellers
    }
}

fun List<Role>.filterByType(roleType: RoleType): List<Role> {
    return this.filter { it.type == roleType }
}

fun isRolesEnoughForPlayersCount(
    selectedRoles: List<Role>,
    countModel: RolesCountModel,
    playersCount: Int
): Boolean {
    val countMatches = selectedRoles.filterNot { it.type == RoleType.Travellers }.size == playersCount
    if (!countMatches) return false
    RoleType.values().forEach { roleType ->
        val selectedRolesCountForType = selectedRoles.filterByType(roleType).size
        val requiredRolesCountForType = countModel.getCountForType(roleType)
        val roleCountMatches = selectedRolesCountForType == requiredRolesCountForType || requiredRolesCountForType == null
        if (!roleCountMatches) return false
    }
    return true
}
package com.example.botcgrimoire.presentation.infocards

import androidx.lifecycle.ViewModel
import com.example.botcgrimoire.di.getAppStateInteractor
import com.example.botcgrimoire.domain.AppState
import com.example.botcgrimoire.domain.AppStateInteractor
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.presentation.chooserole.ChooseYourRoleViewModel
import com.example.botcgrimoire.utils.ViewModelFactory

/**
 * @author Valeriy Minnulin
 */
class InfoCardsScreenViewModel(
    private val appStateInteractor: AppStateInteractor,
): ViewModel() {

    val infoCards: List<InfoCard>

    init {
        val filters = mapOf(
            InfoCard.LunaticBluffs to { isLunaticInGame() },
            InfoCard.OneOfTwoPlayers to { isRolesThatAcceptTwoPlayersInfoInGame() }
        )
        infoCards = InfoCard.values().filter {
            val filter = filters[it]
            filter == null || filter.invoke()
        }
    }

    private fun isLunaticInGame(): Boolean {
        val state = appStateInteractor.state.value as AppState.GameState
        return state.roles.map { it.role }.contains(Role.Lunatic)
    }

    private fun isRolesThatAcceptTwoPlayersInfoInGame(): Boolean {
        val state = appStateInteractor.state.value as AppState.GameState
        val rolesInGame = state.roles.map { it.role }
        return rolesInGame.any {
            it == Role.Washerwoman || it == Role.Librarian || it == Role.Investigator
        }
    }

}
fun infoCardsScreenVMFactory() = ViewModelFactory { InfoCardsScreenViewModel(getAppStateInteractor()) }

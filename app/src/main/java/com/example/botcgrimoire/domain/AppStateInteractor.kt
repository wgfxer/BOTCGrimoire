package com.example.botcgrimoire.domain

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.botcgrimoire.domain.AppStateInteractor.PreferencesKey.APP_STATE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * @author Valeriy Minnulin
 */
class AppStateInteractor(
    private val context: Context
) {

    private val Context.dataStore by preferencesDataStore("GAME_STATE")
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _state = MutableStateFlow<AppState>(AppState.ConfigureGame)
    val state: StateFlow<AppState> = _state.asStateFlow()

    fun changeGameState(state: AppState) {
        _state.value = state
        coroutineScope.launch {
            val string = Json.encodeToString(state)
            context.dataStore.edit {
                it[APP_STATE] = string
            }
        }
    }


    fun init() {
        val start = System.currentTimeMillis()
        runBlocking {
            val currentState = context.dataStore.data.first()
            val stateFromCache = currentState[APP_STATE]?.let { Json.decodeFromString<AppState>(it) }
            if (stateFromCache != null) _state.value = stateFromCache
        }
        val time = System.currentTimeMillis() - start
        android.util.Log.i("AppStateInteractor", "Иницииализация прошла за $time ms")
    }

    fun clearData() {
        _state.value = AppState.ConfigureGame
        coroutineScope.launch {
            context.dataStore.edit {
                it.clear()
            }
        }
    }

    private object PreferencesKey {
        val APP_STATE = stringPreferencesKey("APP_STATE")
    }
}


@Serializable
sealed interface AppState {
    @Serializable
    object ConfigureGame: AppState

    @Serializable
    data class RevealingRoles(
        val roles: List<RoleForChoose> = emptyList(),
        val drunkRole: Role? = null,
        val travellers: List<Role> = emptyList()
    ): AppState

    @Serializable
    data class GameState(
        val roles: List<RoleGameState> = emptyList(),
        val reminders: List<ReminderLink> = emptyList(),
        val isFirstNight: Boolean = true,
        val drunkRole: Role? = null
    ): AppState {

        val firstNightOrder: List<Role> = getOrderList(true)
        val otherNightsOrder: List<Role> = getOrderList(false)

        fun remindersForRole(role: Role): List<Reminder> {
            return reminders.filter { it.role == role }.map { it.reminder }
        }

        private fun getOrderList(isFirst: Boolean): List<Role> {
            val list = (if (isFirst) firstNight else otherNights)
                .filter { roleFromOrder ->
                    val roleExists = roles.map { it.role }.contains(roleFromOrder.role)
                    val roleNotDead = (roles.find { it.role == roleFromOrder.role })?.isDead != true
                    roleExists && roleNotDead
                }
            return list.map { it.role }
        }
    }

}

@Serializable
data class RoleForChoose(
    val number: Int,
    val role: Role,
    val isChosen: Boolean = false,
    val playerName: String = ""
)
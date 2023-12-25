package com.example.botcgrimoire.presentation.revealrole

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.botcgrimoire.R
import com.example.botcgrimoire.di.getAppStateInteractor
import com.example.botcgrimoire.domain.AppState
import com.example.botcgrimoire.domain.Role

/**
 * Экран с отображенной ролью для игрока. Отображается после того как игрок выбрал жетон с каким-то номерком.
 * Для пьяницы показывается не роль пьяницы,а пьяная роль, например "Девственница".
 *
 * @author Valeriy Minnulin
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevealedRoleScreen(actualRole: Role, navigateBack: () -> Unit) {
    val role = remember(actualRole) {
        if (actualRole == Role.Drunk) {
            checkNotNull((getAppStateInteractor().state.value as AppState.RevealingRoles).drunkRole) {
                "Выбрана роль пьяницы, но пьянице не задана роль для отображения игроку"
            }
        } else {
            actualRole
        }
    }
    val appStateInteractor = remember { getAppStateInteractor() }
    val playerName = remember {
        mutableStateOf((appStateInteractor.state.value as AppState.RevealingRoles).roles.find { it.role == actualRole }?.playerName.orEmpty())
    }

    val saveNameLambda = remember {
        {
            val currentState = appStateInteractor.state.value as AppState.RevealingRoles
            val rolesList = currentState.roles.toMutableList()
            val oldRole = rolesList.find { it.role == actualRole }
            val newRole = oldRole?.copy(playerName = playerName.value)
            val index = rolesList.indexOf(oldRole)
            rolesList.remove(oldRole)
            newRole?.let { rolesList.add(index, it) }
            appStateInteractor.changeGameState(currentState.copy(roles = rolesList))
        }
    }
    Column {
        Text(
            "Запомни свою роль",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.titleMedium
        )
        Image(
            painter = painterResource(id = role.icon),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
        )
        Text(stringResource(id = role.roleName),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            stringResource(id = role.playerInfo),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = playerName.value,
            onValueChange = { playerName.value = it },
            label = { Text(text = stringResource(id = R.string.enter_name)) }
        )
        Button(onClick = { navigateBack(); saveNameLambda() }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Вернуться к выбору ролей")
        }
    }
    BackHandler {
        navigateBack()
        saveNameLambda()
    }
}
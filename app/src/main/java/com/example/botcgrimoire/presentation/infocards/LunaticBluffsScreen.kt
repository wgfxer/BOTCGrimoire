package com.example.botcgrimoire.presentation.infocards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.botcgrimoire.R
import com.example.botcgrimoire.di.getAppStateInteractor
import com.example.botcgrimoire.domain.AppState
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.presentation.grimoire.CircleBoxWithText

/**
 * @author Valeriy Minnulin
 */
@Composable
fun LunaticBluffsScreen(backPressedHandle: () -> Unit) {
    val color = Color(0xFF8b0e0e)
    val demonBluffs = remember { mutableStateOf(emptyList<Role>()) }
    val rolesSelected = remember { mutableStateOf(false) }
    val realRolesForBluff = remember {
        val appStateInteractor = getAppStateInteractor()
        val currentState = appStateInteractor.state.value as AppState.GameState
        val edition = currentState.currentEdition
        val selectedRoles = currentState.roles.map { it.role }
        edition.roles.filter { it.isGood && !selectedRoles.contains(it) && it != Role.Drunk && it != Role.Lunatic }
    }
    val wrongRolesForBluff = remember {
        val appStateInteractor = getAppStateInteractor()
        val currentState = appStateInteractor.state.value as AppState.GameState
        val edition = currentState.currentEdition
        val selectedRoles = currentState.roles.map { it.role }
        edition.roles.filter { it.isGood && selectedRoles.contains(it) && it != Role.Drunk && it != Role.Lunatic }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!rolesSelected.value) {
            Dialog(onDismissRequest = backPressedHandle) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .heightIn(max = 600.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.Absolute.spacedBy(8.dp)
                        ) {
                            Text(text = "Выберите 3 роли для блефа демона(Лунатику)", fontSize = 32.sp)
                            Text(text = "Отсутствующие роли: ", fontSize = 20.sp)
                            realRolesForBluff.forEach { role ->
                                RoleWithCheckBox(demonBluffs, role)
                            }
                            Text(text = "Присутствующие роли: ", fontSize = 20.sp)
                            wrongRolesForBluff.forEach { role ->
                                RoleWithCheckBox(demonBluffs, role)
                            }
                        }
                        Button(enabled = demonBluffs.value.size == 3, onClick = { rolesSelected.value = true }) {
                            Text(text = stringResource(id = R.string.done))
                        }
                    }

                }
            }
        } else {
            Text(text = stringResource(id = R.string.demon_bluffs_card_hint), color = Color.White, fontSize = 30.sp)
            Row {
                demonBluffs.value.forEach {
                    CircleBoxWithText(stringResource(id = it.roleName), it.icon, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun RoleWithCheckBox(
    selectedBluffs: MutableState<List<Role>>,
    role: Role,
) {
    val isRoleSelected = selectedBluffs.value.contains(role)
    Row(modifier = Modifier
        .clickable {
            if (isRoleSelected) {
                selectedBluffs.value = selectedBluffs.value.minus(role)
            } else if (selectedBluffs.value.size < 3) {
                selectedBluffs.value = selectedBluffs.value.plus(role)
            }
        }
        .fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(
            text = stringResource(id = role.roleName),
            modifier = Modifier,
            fontSize = 24.sp
        )
        Checkbox(
            checked = isRoleSelected,
            onCheckedChange = null,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
package com.example.botcgrimoire.presentation.infocards

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
fun InfoCardsScreen() {
    val currentCardScreen = remember { mutableStateOf<InfoCard?>(null) }
    val currentCardScreenLocal = currentCardScreen.value
    val backPressedHandle = { currentCardScreen.value = null }
    if (currentCardScreenLocal != null) {
        InfoCardScreen(currentCardScreenLocal) { backPressedHandle() }
        BackHandler {
            backPressedHandle()
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            InfoCard.values().forEach {
                ListElement(stringResource(id = it.text)) { currentCardScreen.value = it }
            }
        }
    }
}

@Composable
private fun ListElement(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.padding(16.dp)) {
        Text(text = text)
    }
}

@Composable
fun InfoCardScreen(infoCard: InfoCard, backPressedHandle: () -> Unit) {
    when (infoCard) {
        InfoCard.Demon -> EvilInfoScreen(stringResource(infoCard.text))
        InfoCard.Minions -> EvilInfoScreen(stringResource(infoCard.text))
        InfoCard.DemonBluffs -> DemonBluffsScreen(backPressedHandle)
        InfoCard.AnotherPlayerRole -> ShowRoleScreen(backPressedHandle = backPressedHandle)
        InfoCard.YourNewRole -> ShowRoleScreen(stringResource(id = R.string.your_new_role_card), backPressedHandle)
        InfoCard.CustomText -> CustomInfoScreen()
    }
}

@Composable
private fun DemonBluffsScreen(backPressedHandle: () -> Unit) {
    val color = Color(0xFF8b0e0e)
    val demonBluffs = remember { mutableStateOf(emptyList<Role>()) }
    val rolesSelected = remember { mutableStateOf(false) }
    val allRolesForBluff = remember {
        val appStateInteractor = getAppStateInteractor()
        val currentState = appStateInteractor.state.value as AppState.GameState
        val selectedRoles = currentState.roles.map { it.role }
        Role.values().filter { it.isGood && !selectedRoles.contains(it) && it != Role.Drunk }
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
                            Text(text = "Выберите 3 роли для блефа Демона", fontSize = 32.sp)
                            allRolesForBluff.forEach { role ->
                                val isRoleSelected = demonBluffs.value.contains(role)
                                Row(modifier = Modifier
                                    .clickable {
                                        if (isRoleSelected) {
                                            demonBluffs.value = demonBluffs.value.minus(role)
                                        } else if (demonBluffs.value.size < 3) {
                                            demonBluffs.value = demonBluffs.value.plus(role)
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
private fun ShowRoleScreen(titleText: String? = null, backPressedHandle: () -> Unit) {
    val color = Color(0xFF494949)
    val selectedRole = remember { mutableStateOf<Role?>(null) }
    val rolesForSelect = remember { Role.values().toList() }
    val roleForShow = selectedRole.value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (roleForShow == null) {
            Dialog(onDismissRequest = backPressedHandle) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .heightIn(max = 600.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Absolute.spacedBy(8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.choose_role_card_hint), fontSize = 32.sp)
                        rolesForSelect.forEach {
                            Text(
                                text = stringResource(id = it.roleName),
                                modifier = Modifier
                                    .clickable { selectedRole.value = it }
                                    .align(Alignment.CenterHorizontally)
                                    .fillMaxWidth(),
                                fontSize = 24.sp
                            )
                        }
                    }
                }
            }
        } else {
            titleText?.let { Text(text = it, color = Color.White, fontSize = 30.sp) }
            CircleBoxWithText(stringResource(id = roleForShow.roleName), icon = roleForShow.icon)
            Text(stringResource(id = roleForShow.playerInfo), textAlign = TextAlign.Center, color = Color.White)
        }
    }
}

@Composable
private fun EvilInfoScreen(text: String) {
    val color = Color(0xFF8b0e0e)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color), contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White, fontSize = 30.sp)
    }
}

@Composable
private fun CustomInfoScreen() {
    val color = Color(0xFF494949)
    val text = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
    ) {
        BasicTextField(
            value = text.value,
            onValueChange = { text.value = it },
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester),
            textStyle = MaterialTheme.typography.headlineLarge.copy(color = Color.White, textAlign = TextAlign.Center),
        )
    }
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
}

enum class InfoCard(
    val text: Int
) {
    Demon(R.string.demon_card),
    Minions(R.string.minions_card),
    DemonBluffs(R.string.demon_bluffs_card),
    AnotherPlayerRole(R.string.another_player_role_card),
    YourNewRole(R.string.your_new_role_card),
    CustomText(R.string.custom_text_card),
}
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.botcgrimoire.presentation.configurescreen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botcgrimoire.R
import com.example.botcgrimoire.domain.AppState
import com.example.botcgrimoire.domain.Edition
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.domain.RoleType
import com.example.botcgrimoire.domain.RoleType.Demons
import com.example.botcgrimoire.domain.RoleType.Minions
import com.example.botcgrimoire.domain.RoleType.Outsiders
import com.example.botcgrimoire.domain.RoleType.Townsfolk
import com.example.botcgrimoire.domain.RoleType.Travellers
import com.example.botcgrimoire.domain.getAllRoles

/**
 * Экран с начальной конфигурацией игры. Тут задается количество игроков и роли в игре.
 *
 * @author Valeriy Minnulin
 */
@Composable
fun ConfigureGameScreen(
    onContinueButtonClick: () -> Unit
) {
    val viewModel: ConfigureGameViewModel = viewModel(factory = configureGameVMFactory())
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        val state = viewModel.state.collectAsState()
        val warningText = viewModel.warningText.collectAsState()
        val onPlayersCountChanged: (Int) -> Unit = remember { { viewModel.onPlayersCountChanged(it) } }
        val onRoleSelected: (Role) -> Unit = remember { { viewModel.onRoleClick(it) } }
        val onRandomRolesClick: () -> Unit = remember { { viewModel.onRandomRolesClick() } }
        val onClearSelectedRoles: () -> Unit = remember { { viewModel.onClearSelectedRoles() } }
        val onContinueClick: () -> Unit = remember {
            {
                viewModel.onContinueClick()
                onContinueButtonClick()
            }
        }
        val onEditionSelected: (Edition) -> Unit = remember {
            {
                viewModel.onEditionSelected(it)
            }
        }
        ChoosePlayersCount(
            state.value,
            warningText.value,
            onPlayersCountChanged,
            onRoleSelected,
            onRandomRolesClick,
            onClearSelectedRoles,
            onContinueClick,
            onEditionSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoosePlayersCount(
    state: AppState.ConfigureGame,
    warningText: String?,
    onPlayersCountChanged: (Int) -> Unit,
    onRoleSelected: (Role) -> Unit,
    onRandomRolesClick: () -> Unit,
    onClearSelectedRoles: () -> Unit,
    onContinueClick: () -> Unit,
    onEditionSelected: (Edition) -> Unit
) {
    Column {
        LazyColumn(modifier = Modifier.weight(1f)) {
            val playersCount = state.currentPlayersCount
            val edition = state.currentEdition
            item(key = "SettingsHeader") {
                SettingsHeader(
                    edition,
                    playersCount,
                    onPlayersCountChanged,
                    onRandomRolesClick,
                    onClearSelectedRoles,
                    onEditionSelected
                )
            }
            val countModel = state.rolesCountModel
            rolesSection(
                Townsfolk,
                state.currentEdition,
                onRoleSelected,
                state.selectedTownsfolk,
                countModel.townsfolkCount
            )
            rolesSection(
                Outsiders,
                state.currentEdition,
                onRoleSelected,
                state.selectedOutsiders,
                countModel.outsidersCount
            )
            rolesSection(Minions, state.currentEdition, onRoleSelected, state.selectedMinions, countModel.minionsCount)
            rolesSection(Demons, state.currentEdition, onRoleSelected, state.selectedDemons, countModel.demonsCount)
            rolesSection(Travellers, state.currentEdition, onRoleSelected, state.selectedTravellers, null)
        }
        Footer(onContinueClick, state.isContinueButtonEnabled, warningText)
    }
}

@Composable
private fun Footer(onContinueClick: () -> Unit, isContinueButtonEnabled: Boolean, warningText: String?) {
    Column(modifier = Modifier.animateContentSize()) {
        Button(
            onClick = onContinueClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = isContinueButtonEnabled
        ) {
            Text(text = "Продолжить")
        }
        WarningText(warningText)
    }
}

@Composable
private fun WarningText(warningText: String?) {
    if (warningText != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(8.dp)
            ) {
                Text(text = warningText, color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    }
}

@Composable
private fun SettingsHeader(
    currentEdition: Edition,
    currentPlayersCount: Int,
    onPlayersCountChanged: (Int) -> Unit,
    onRandomRolesClick: () -> Unit,
    onClearSelectedRoles: () -> Unit,
    onEditionSelected: (Edition) -> Unit
) {
    Column(modifier = Modifier.padding(26.dp)) {
        EditionMenu(currentEdition, onEditionSelected)
        Text(text = "Выберите начальное количество игроков")
        Text(text = currentPlayersCount.toString(), modifier = Modifier.align(Alignment.CenterHorizontally))
        Slider(
            value = currentPlayersCount.toFloat(),
            onValueChange = { onPlayersCountChanged(it.toInt()) },
            valueRange = 5f..15f
        )
        Row {
            Button(onClick = onRandomRolesClick) {
                Text(text = "Выбрать роли случайно")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onClearSelectedRoles) {
                Text(text = "Очистить роли")
            }
        }
    }
}

@Composable
private fun EditionMenu(
    currentEdition: Edition,
    onEditionSelected: (Edition) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded.value, onExpandedChange = {
        expanded.value = it
    }) {
        TextField(
            value = stringResource(id = currentEdition.nameResId),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
            Edition.values().forEach { edition ->
                val editionName = stringResource(id = edition.nameResId)
                DropdownMenuItem(text = { Text(text = editionName) }, onClick = {
                    onEditionSelected(edition)
                    expanded.value = false
                })
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.rolesSection(
    roleType: RoleType,
    currentEdition: Edition,
    onRoleSelected: (Role) -> Unit,
    selectedRoles: List<Role>,
    requiredCountForRoleType: Int?
) {
    stickyHeader(key = roleType) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(text = stringResource(id = roleType.typeName), modifier = Modifier.weight(1f))
            requiredCountForRoleType?.let { Text(text = "${selectedRoles.size} / $it") }
        }
    }
    val availableRolesForType = roleType.getAllRoles().filter { currentEdition.roles.contains(it) }
    android.util.Log.i("MYTAG", "берем все роли, фильтруем по типу и потом еще смотрим есть ли в редакции")
    availableRolesForType.forEach {
        item(key = it) {
            val isSelected = selectedRoles.contains(it)
            RoleCard(it, isSelected, onRoleSelected)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleCard(role: Role, isSelected: Boolean, onRoleSelected: (Role) -> Unit) {
    val goodColor = colorResource(id = R.color.good_role_background)
    val badColor = colorResource(id = R.color.evil_role_background)
    val backgroundColor = if (role.isGood) goodColor else badColor
    val backgroundModifier = if (isSelected) {
        if (role.type == Travellers) Modifier.travellersBackgroundModifier(
            goodColor,
            badColor
        ) else Modifier.background(backgroundColor)
    } else Modifier
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
    Card(
        modifier = Modifier.padding(16.dp),
        onClick = { onRoleSelected.invoke(role) }
    ) {
        Row(modifier = backgroundModifier.padding(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = role.icon),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(text = stringResource(id = role.roleName), fontWeight = FontWeight.Bold, color = textColor)
                }
                Text(text = stringResource(id = role.playerInfo), color = textColor)
            }
        }
    }
}

private fun Modifier.travellersBackgroundModifier(goodColor: Color, badColor: Color): Modifier = Modifier.drawBehind {
    drawIntoCanvas {
        drawRect(goodColor, Offset.Zero, size = this.size.copy(width = this.size.width / 2f))
        drawRect(badColor, Offset.Zero.copy(x = this.center.x), size = this.size.copy(width = this.size.width / 2f))
    }
}
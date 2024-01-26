package com.example.botcgrimoire.presentation.infocards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.botcgrimoire.R
import com.example.botcgrimoire.di.getAppStateInteractor
import com.example.botcgrimoire.domain.AppState
import com.example.botcgrimoire.domain.Edition
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.domain.RoleType
import com.example.botcgrimoire.domain.getAllRoles
import com.example.botcgrimoire.domain.rolesCountMap

/**
 * @author Valeriy Minnulin
 */
@Composable
fun RolesInfoScreen() {
    val state = remember { getAppStateInteractor().state.value as AppState.GameState }
    val edition = state.currentEdition
    val playersCount = state.roles.count()
    Column {
        LazyColumn(modifier = Modifier.weight(1f)) {
            item(key = "CountModel") {
                CountModel(playersCount)
            }
            rolesSection(RoleType.Townsfolk,edition)
            rolesSection(RoleType.Outsiders,edition)
            rolesSection(RoleType.Minions, edition)
            rolesSection(RoleType.Demons, edition)
            rolesSection(RoleType.Travellers, edition)
        }
    }
}

@Composable
private fun CountModel(playersCount: Int) {
    val model = rolesCountMap[playersCount]
    if (model != null) {
        Text(text = "Горожан: ${model.townsfolkCount}")
        Text(text = "Изгоев ${model.outsidersCount}")
        Text(text = "Приспешников ${model.minionsCount}")
        Text(text = "Демон: 1")
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.rolesSection(
    roleType: RoleType,
    currentEdition: Edition
) {
    stickyHeader(key = roleType) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(text = stringResource(id = roleType.typeName), modifier = Modifier.weight(1f))
        }
    }
    val availableRolesForType = roleType.getAllRoles().filter { currentEdition.roles.contains(it) }
    android.util.Log.i("MYTAG", "берем все роли, фильтруем по типу и потом еще смотрим есть ли в редакции")
    availableRolesForType.forEach {
        item(key = it) {
            RoleCard(it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleCard(role: Role) {
    val textColor = MaterialTheme.colorScheme.onPrimaryContainer
    Card(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
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
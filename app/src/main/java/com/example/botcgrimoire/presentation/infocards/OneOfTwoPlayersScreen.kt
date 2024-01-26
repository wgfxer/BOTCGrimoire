package com.example.botcgrimoire.presentation.infocards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun OneOfTwoPlayersScreen(titleText: String? = null, backPressedHandle: () -> Unit) {
    val color = Color(0xFF494949)
    val selectedRole = remember { mutableStateOf<Role?>(null) }
    val rolesForSelect = remember {
        val appStateInteractor = getAppStateInteractor()
        val currentState = appStateInteractor.state.value as AppState.GameState
        currentState.currentEdition.roles
    }
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
            Text(text = "Один из них: ", color = Color.White, fontSize = 30.sp)
            CircleBoxWithText(stringResource(id = roleForShow.roleName), icon = roleForShow.icon)
        }
    }
}
package com.example.botcgrimoire.presentation.chooserole

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botcgrimoire.domain.ChooseYourRoleScreenEvent
import com.example.botcgrimoire.domain.ChooseableRole
import com.example.botcgrimoire.domain.ChooseYourRoleScreenState
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.domain.RoleForChoose
import com.example.botcgrimoire.presentation.ui.AlertDialog

/**
 * Экран с выбором роли для игрока. Отображается как список жетонов с номерами, а игрок выбирает случайный номер.
 *
 * @author Valeriy Minnulin
 */
@Composable
fun ChooseYourRoleScreen(
    navigateToRevealRoleScreen: (Role) -> Unit,
    navigateToGrimoire: () -> Unit,
    navigateToStart: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        val viewModel: ChooseYourRoleViewModel = viewModel(factory = chooseYourRoleVMFactory())
        val state = viewModel.state.collectAsState()
        val onRoleChosen: (Role) -> Unit = remember {
            {
                viewModel.onRoleClick(it, navigateToRevealRoleScreen)
            }
        }
        LaunchedEffect(key1 = Unit) {
            viewModel.events.collect {
                when (it) {
                    ChooseYourRoleScreenEvent.NavigateToConfigureGame -> navigateToStart()
                }
            }
        }
        BackHandler {
            viewModel.onBackClicked()
        }
        val onDrunkRoleClick: (Role) -> Unit = remember { { viewModel.onDrunkRoleClick(it) } }
        ChooseYourRoleScreen(state.value, onRoleChosen,
            {
                viewModel.onContinueClick()
                navigateToGrimoire()
            }
        )
        DrunkDialog(state.value.rolesForDrunk, onDrunkRoleClick)
        AlertDialog(state.value.dialog)
    }
}

@Composable
private fun DrunkDialog(rolesForDrunk: List<Role>?, onDrunkRoleClick: (Role) -> Unit) {
    if (rolesForDrunk != null) {
        Dialog(onDismissRequest = {  }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = spacedBy(8.dp)) {
                    Text(text = "Выберите роль для пьяницы", fontSize = 32.sp)
                    rolesForDrunk.forEach {
                        Text(
                            text = stringResource(id = it.roleName),
                            modifier = Modifier
                                .clickable { onDrunkRoleClick(it) }
                                .align(Alignment.CenterHorizontally),
                            fontSize = 24.sp
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun ChooseYourRoleScreen(chooseYourRoleScreenState: ChooseYourRoleScreenState, onRoleChosen: (Role) -> Unit, navigateToGrimoire: () -> Unit) {
    val rolesChunked = chooseYourRoleScreenState.roles.chunked(ELEMENTS_IN_ROW)
    Column {
        Column(modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
        ) {
            rolesChunked.forEach { rolesLine ->
                Row {
                    rolesLine.forEach { hideableRole ->
                        IndexedRole(hideableRole, onRoleChosen)
                    }
                    if (rolesLine.size < ELEMENTS_IN_ROW) {
                        repeat(ELEMENTS_IN_ROW - rolesLine.size) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        Button(onClick = navigateToGrimoire, enabled = chooseYourRoleScreenState.isStartGameButtonEnabled) {
            Text(text = "Начать игру")
        }
    }

}

private const val ELEMENTS_IN_ROW = 3

@Composable
private fun RowScope.IndexedRole(chooseableRole: RoleForChoose, onRoleChosen: (Role) -> Unit) {
    val background = if (chooseableRole.isChosen) {
        Modifier
            .alpha(0.2f)
            .background(MaterialTheme.colorScheme.secondary)
    } else Modifier
        .background(MaterialTheme.colorScheme.secondary)
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(CircleShape)
            .then(background)
            .clickable { onRoleChosen(chooseableRole.role) }
            .weight(1f)
            .aspectRatio(1f)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = chooseableRole.number.toString(),
            fontSize = 40.sp,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}

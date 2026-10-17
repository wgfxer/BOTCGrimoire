package com.example.botcgrimoire.presentation.nightorder

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botcgrimoire.R
import com.example.botcgrimoire.domain.NightOrderModel
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.domain.RoleGameState
import com.example.botcgrimoire.presentation.infocards.ShowRoleScreen

/**
 * @author Valeriy Minnulin
 */
@Composable
fun NightOrderScreen() {
    val viewModel: NightOrderViewModel = viewModel(factory = nightOrderVMFactory())
    val state = viewModel.state.collectAsState()
    val onChangeNight: (Boolean) -> Unit = remember { { viewModel.onChangeNight(it) } }
    val rolesForInformation = remember { mutableStateOf<List<Role>?>(null) }
    val nightActionRole = remember { mutableStateOf<Role?>(null) }
    val pendingNightChange = remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(key1 = Unit) {
        viewModel.updateState()
    }
    val selectableRoles = rolesForInformation.value
    if (selectableRoles != null) {
        val informationStepIndex = state.value.currentStepIndex
        val closeInformationScreen = { rolesForInformation.value = null }
        ShowRoleScreen(
            titleText = stringResource(id = R.string.one_of_them),
            backPressedHandle = closeInformationScreen,
            rolesForSelect = selectableRoles,
            onCompleted = {
                closeInformationScreen()
                viewModel.completeCurrentStep(informationStepIndex)
            }
        )
        BackHandler(onBack = closeInformationScreen)
        return
    }

    val screenState = state.value
    val currentStepIndex = screenState.currentStepIndex
    val currentItem = screenState.currentOrder.getOrNull(currentStepIndex)
    val isCompletionScreen = screenState.isNightComplete && currentItem == null
    val currentStepCompleted = currentStepIndex in screenState.completedStepIndexes
    val currentRoleModel = currentItem as? NightOrderModel
    val rolesToShow = currentRoleModel?.let { viewModel.getRolesForInformation(it.role) }
    val targetsToSelect = currentRoleModel?.let { viewModel.getNightActionTargets(it.role) }
    val startCurrentAction: (() -> Unit)? = when {
        currentRoleModel == null -> null
        rolesToShow != null && rolesToShow.isNotEmpty() -> {
            { rolesForInformation.value = rolesToShow }
        }
        targetsToSelect != null && targetsToSelect.isNotEmpty() -> {
            {
                viewModel.clearPreviousNightAction(currentRoleModel.role)
                nightActionRole.value = currentRoleModel.role
            }
        }
        else -> null
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        NightSwitcher(screenState.isForFirstNight) { requestedFirstNight ->
            if (screenState.hasProgress && !screenState.isNightComplete) {
                pendingNightChange.value = requestedFirstNight
            } else {
                onChangeNight(requestedFirstNight)
            }
        }
        NightProgressHeader(screenState)

        when {
            isCompletionScreen -> {
                NightCompleteContent(
                    isFirstNight = screenState.isForFirstNight,
                    onReviewPrevious = viewModel::goToPreviousStep,
                    onStartNextNight = {
                        if (screenState.isForFirstNight) {
                            onChangeNight(false)
                        } else {
                            viewModel.restartNight()
                        }
                    }
                )
            }
            currentItem != null -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    when (currentItem) {
                        com.example.botcgrimoire.domain.DemonBluffInfo -> DemonBluffInfo()
                        com.example.botcgrimoire.domain.MinionsInfo -> MinionsInfo()
                        is NightOrderModel -> NightOrderElement(
                            model = currentItem,
                            isCompleted = currentStepCompleted,
                            onClick = if (currentStepCompleted) null else startCurrentAction
                        )
                        com.example.botcgrimoire.domain.TravellersInfo -> TravellersInfo()
                        com.example.botcgrimoire.domain.DemonLunaticInfo -> DemonLunaticInfo()
                    }
                }
                NightStepControls(
                    canGoBack = currentStepIndex > 0,
                    isCompleted = currentStepCompleted,
                    isLastStep = currentStepIndex == screenState.currentOrder.lastIndex,
                    requiresAction = startCurrentAction != null,
                    onPrevious = viewModel::goToPreviousStep,
                    onPrimaryAction = {
                        if (currentStepCompleted || startCurrentAction == null) {
                            viewModel.completeCurrentStep(currentStepIndex)
                        } else {
                            startCurrentAction()
                        }
                    }
                )
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(id = R.string.no_night_steps))
                }
            }
        }
    }

    nightActionRole.value?.let { actionRole ->
        NightActionTargetDialog(
            actionRole = actionRole,
            roles = viewModel.getNightActionTargets(actionRole).orEmpty(),
            onDismiss = { nightActionRole.value = null },
            onRoleSelected = { targetRole ->
                if (actionRole == Role.Imp) {
                    viewModel.killPlayer(targetRole.role)
                } else {
                    viewModel.setNightActionTarget(actionRole, targetRole.role)
                }
                nightActionRole.value = null
                viewModel.completeCurrentStep(currentStepIndex)
            }
        )
    }

    pendingNightChange.value?.let { requestedFirstNight ->
        AlertDialog(
            onDismissRequest = { pendingNightChange.value = null },
            title = { Text(text = stringResource(id = R.string.change_night_progress_title)) },
            text = { Text(text = stringResource(id = R.string.change_night_progress_message)) },
            confirmButton = {
                Button(onClick = {
                    pendingNightChange.value = null
                    onChangeNight(requestedFirstNight)
                }) {
                    Text(text = stringResource(id = R.string.change_night_progress_confirm))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { pendingNightChange.value = null }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun NightProgressHeader(state: NightOrderScreenState) {
    val totalSteps = state.currentOrder.size
    val progress = if (totalSteps == 0) 0f else state.completedStepsCount.toFloat() / totalSteps
    val isCompletionScreen = state.isNightComplete && state.currentStepIndex >= totalSteps
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isCompletionScreen) {
                    stringResource(id = R.string.night_complete_title)
                } else {
                    stringResource(
                        id = R.string.night_step,
                        (state.currentStepIndex + 1).coerceAtMost(totalSteps),
                        totalSteps
                    )
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(
                    id = R.string.night_progress,
                    state.completedStepsCount,
                    totalSteps
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

@Composable
private fun NightStepControls(
    canGoBack: Boolean,
    isCompleted: Boolean,
    isLastStep: Boolean,
    requiresAction: Boolean,
    onPrevious: () -> Unit,
    onPrimaryAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onPrevious,
            enabled = canGoBack,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = stringResource(id = R.string.previous_step))
        }
        Button(
            onClick = onPrimaryAction,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(
                    id = when {
                        isCompleted -> R.string.next_step
                        requiresAction -> R.string.perform_night_action
                        isLastStep -> R.string.complete_night
                        else -> R.string.complete_and_continue
                    }
                )
            )
        }
    }
}

@Composable
private fun NightCompleteContent(
    isFirstNight: Boolean,
    onReviewPrevious: () -> Unit,
    onStartNextNight: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.night_complete_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.night_complete_message),
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = onStartNextNight, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(
                    id = if (isFirstNight) R.string.start_next_night else R.string.restart_night_order
                )
            )
        }
        OutlinedButton(
            onClick = onReviewPrevious,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text(text = stringResource(id = R.string.review_previous_step))
        }
    }
}

@Composable
private fun NightActionTargetDialog(
    actionRole: Role,
    roles: List<RoleGameState>,
    onDismiss: () -> Unit,
    onRoleSelected: (RoleGameState) -> Unit
) {
    val title = when (actionRole) {
        Role.Poisoner -> R.string.choose_player_to_poison
        Role.Monk -> R.string.choose_player_to_protect
        Role.Imp -> R.string.choose_player_to_kill
        else -> return
    }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(max = 600.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(id = title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                roles.forEach { roleState ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRoleSelected(roleState) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = roleState.role.icon),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Row(
                            modifier = Modifier.padding(start = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = roleState.role.roleName),
                                fontWeight = FontWeight.Bold
                            )
                            if (roleState.playerName.isNotEmpty()) {
                                Text(text = " (${roleState.playerName})")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TravellersInfo() {
    Card(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(modifier = Modifier.padding(16.dp), text = stringResource(id = R.string.travellers_night_info))
    }
}

@Composable
private fun DemonLunaticInfo() {
    Card(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(modifier = Modifier.padding(16.dp), text = stringResource(id = R.string.demon_lunatic_night_info))
    }
}

@Composable
private fun MinionsInfo() {
    Card(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row {
                val color = MaterialTheme.colorScheme.onBackground
                Text(stringResource(id = R.string.minions_fn_info_title), fontWeight = FontWeight.Bold)
            }
            TextWithIcons(stringResource(id = R.string.minions_fn_info_content))
        }
    }
}

@Composable
private fun DemonBluffInfo() {
    Card(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row {
                Text(stringResource(id = R.string.demon_fn_info_title), fontWeight = FontWeight.Bold)
                val color = MaterialTheme.colorScheme.onBackground
            }
            TextWithIcons(stringResource(id = R.string.demon_fn_info_content))
        }
    }
}

@Composable
private fun NightOrderElement(
    model: NightOrderModel,
    isCompleted: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val alpha = if (model.isDead) 0.5f else 1f
    Card(
        modifier = Modifier
            .padding(16.dp)
            .alpha(alpha)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(id = model.role.icon), contentDescription = null, modifier = Modifier.size(36.dp))
                Text(stringResource(id = model.role.roleName), fontWeight = FontWeight.Bold)
                if (model.playerName.isNotEmpty()) Text("(${model.playerName})")
                if (model.isConditional) {
                    val color = MaterialTheme.colorScheme.onBackground
                    Icon(
                        painter = painterResource(id = R.drawable.ic_question_24),
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (model.isDrunk) {
                    Text(stringResource(id = R.string.poisoned_reminder), fontWeight = FontWeight.Bold)
                }
                if (isCompleted) {
                    Text(
                        text = "✓",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                if (onClick != null) {
                    IconButton(onClick = onClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_play_arrow_24),
                            contentDescription = stringResource(
                                id = when (model.role) {
                                    Role.Poisoner -> R.string.choose_player_to_poison
                                    Role.Monk -> R.string.choose_player_to_protect
                                    Role.Imp -> R.string.choose_player_to_kill
                                    else -> R.string.show_role
                                }
                            ),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            TextWithIcons(model.descriptionString ?: stringResource(id = model.description))
        }
    }
}

@Composable
private fun NightSwitcher(isFirstNight: Boolean, onChangeNight: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        SwitcherButton(enabled = !isFirstNight, stringResource(id = R.string.first_night)) { onChangeNight(true) }
        SwitcherButton(enabled = isFirstNight, stringResource(id = R.string.other_nights)) { onChangeNight(false) }
    }
}

@Composable
private fun RowScope.SwitcherButton(enabled: Boolean, text: String, onClick: () -> Unit) {
    val background = if (!enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
    val textColor =
        if (!enabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    Box(
        modifier = Modifier
            .weight(1f)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge.copy(color = textColor)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .align(Alignment.BottomCenter)
                .background(background)
        )
    }
}

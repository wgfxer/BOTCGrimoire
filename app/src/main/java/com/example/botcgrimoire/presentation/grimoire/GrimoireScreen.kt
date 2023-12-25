package com.example.botcgrimoire.presentation.grimoire

import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botcgrimoire.domain.Action
import com.example.botcgrimoire.domain.ActionsDialog
import com.example.botcgrimoire.domain.AppState
import com.example.botcgrimoire.domain.GrimoireScreenState
import com.example.botcgrimoire.domain.Reminder
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.domain.RoleGameState
import com.example.botcgrimoire.domain.reminders
import com.example.botcgrimoire.presentation.zoom.ZoomState
import com.example.botcgrimoire.presentation.zoom.rememberZoomState
import com.example.botcgrimoire.presentation.zoom.zoomable
import com.example.botcgrimoire.ui.theme.Beige
import kotlin.math.roundToInt

/**
 * @author Valeriy Minnulin
 */
@Composable
fun GrimoireScreen() {
    val viewModel: GrimoireViewModel = viewModel(factory = grimoireVMFactory())
    val state = viewModel.state.collectAsState()
    val dialogState = viewModel.screenState.collectAsState().value.actionsDialog
    val clickLambda: (RoleGameState) -> Unit = remember { { viewModel.onRoleClick(it) } }
    val onOffsetChanged: (RoleGameState, Offset) -> Unit = remember { { state, offset -> viewModel.onOffsetChanged(state,offset) } }
    val zoomState = viewModel.zoomState ?: rememberZoomState().also { viewModel.zoomState = it }
    GrimoireScreen(state.value, clickLambda, onOffsetChanged, zoomState)
    ActionsDialog(dialogState)
}

@Composable
private fun GrimoireScreen(
    state: AppState.GameState,
    onRoleClick: (RoleGameState) -> Unit,
    onOffsetChanged: (RoleGameState, Offset) -> Unit,
    zoomState: ZoomState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zoomable(zoomState)
    ) {
        GrimoireRolesLayout(roles = {
            state.roles.forEach {
                RoleCard(
                    roleState = it,
                    reminders = state.remindersForRole(it.role),
                    onRoleClick,
                    if (state.showNightOrderInGrimoire) state.firstNightOrder.indexOf(it.role) + 1 else 0,
                    if (state.showNightOrderInGrimoire) state.otherNightsOrder.indexOf(it.role) + 1 else 0,
                    onOffsetChanged
                )
            }
        })
    }

}

@Composable
private fun ActionsDialog(state: ActionsDialog?) {
    if (state != null) {
        Dialog(onDismissRequest = state.dismissLambda) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Absolute.spacedBy(8.dp)) {
                    state.actionsInDialog.forEach { action ->
                        val clickableModifier = Modifier.clickable { state.onActionClick(action) }
                        when (action) {
                            is Action.ReminderAction -> ReminderAction(action.reminder, action.isAdd, clickableModifier)
                            is Action.ChangeHasVote -> ChangeHasVoteAction(action.hasVote, modifier = clickableModifier)
                            is Action.ChangeLifeState -> ChangeLifeStateAction(
                                action.newIsDead,
                                modifier = clickableModifier
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun ReminderAction(reminder: Reminder, isAdd: Boolean, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        val prefix = if (isAdd) "" else "Убрать "
        Text(text = prefix + stringResource(id = reminder.infoOnCard))
        Image(
            painterResource(id = reminder.icon),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 16.dp)
                .size(36.dp)
        )
    }
}

@Composable
private fun ChangeLifeStateAction(newIsDead: Boolean, modifier: Modifier) {
    val text = if (newIsDead) "Убить" else "Оживить"
    Box(modifier = modifier
        .heightIn(min = 36.dp)
        .fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Text(text = text)
    }
}

@Composable
private fun ChangeHasVoteAction(newHasVote: Boolean, modifier: Modifier) {
    val text = if (newHasVote) "Дать жетон голоса" else "Забрать жетон голоса"
    Box(modifier = modifier
        .heightIn(min = 36.dp)
        .fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Text(text = text)
    }
}

@Composable
private fun RoleCard(
    roleState: RoleGameState,
    reminders: List<Reminder>?,
    onRoleClick: (RoleGameState) -> Unit,
    firstNightOrder: Int,
    otherNightsOrder: Int,
    onOffsetChanged: (RoleGameState, Offset) -> Unit
) {
    val view = LocalView.current
    val isDragging = remember { mutableStateOf(false) }
    val offset = remember { mutableStateOf(Offset(roleState.offsetX, roleState.offsetY)) }
    val scale = animateFloatAsState(targetValue = if (isDragging.value) 1.2f else 1f)
    Box(modifier = Modifier
        .graphicsLayer {
            translationX = offset.value.x
            translationY = offset.value.y
            scaleX = scale.value
            scaleY = scale.value
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    isDragging.value = true
                },
                onDragCancel = { isDragging.value = false },
                onDragEnd = {
                    isDragging.value = false
                    onOffsetChanged(roleState, offset.value)
                }
            ) { change, dragAmount ->
                offset.value += dragAmount
            }
        }
    ) {
        CircleBoxWithText(
            topText = roleState.playerName,
            bottomText = stringResource(id = roleState.role.roleName),
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            onClick = { onRoleClick.invoke(roleState) },
            icon = roleState.role.icon,
            backgroundColor = if (roleState.isDead) Color.DarkGray else Color.LightGray,
            contentOnBounds = convertRemindersToComposable(reminders),
            leftContent = { NightOrder(firstNightOrder, roleState.role.isGood) },
            rightContent = { NightOrder(otherNightsOrder, roleState.role.isGood) }
        )
        if (roleState.isDead && roleState.hasVote) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Beige)
            )
        }
    }
}

@Composable
private fun NightOrder(order: Int, isGood: Boolean) {
    if (order != 0) {
        Box(modifier = Modifier
            .clip(CircleShape)
            .aspectRatio(1f)
            .background(if (isGood) Color.Blue else Color.Red),
            contentAlignment = Alignment.Center
        ) {
            val textMeasurer = rememberTextMeasurer()

            val textToDraw = order.toString()
            Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
                val style = TextStyle(
                    fontSize = this.size.width.toSp() * 0.8f,
                    color = Color.White
                )
                val textLayoutResult = textMeasurer.measure(textToDraw, style)
                drawText(
                    textMeasurer = textMeasurer,
                    text = textToDraw,
                    style = style,
                    topLeft = Offset(
                        x = center.x - textLayoutResult.size.width / 2,
                        y = center.y - textLayoutResult.size.height / 2,
                    )
                )
            })
        }
    }
}

private fun convertRemindersToComposable(reminders: List<Reminder>?): @Composable (() -> Unit)? {
    if (reminders.isNullOrEmpty()) return null
    val content: @Composable () -> Unit = {
        reminders.forEach {
            CircleBoxWithText(
                bottomText = stringResource(id = it.infoOnCard),
                icon = it.icon,
                backgroundColor = Beige
            )
        }
    }
    return content
}

@Composable
fun CircleBoxWithText(
    bottomText: String,
    icon: Int,
    modifier: Modifier = Modifier,
    topText: String = "",
    onClick: (() -> Unit)? = null,
    leftContent: @Composable (() -> Unit)? = null,
    rightContent: @Composable (() -> Unit)? = null,
    backgroundColor: Color = Color.Gray,
    contentOnBounds: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val indication = rememberRipple()
    Box(modifier = modifier
        .clip(CircleShape)
        .background(backgroundColor)
        .indication(interactionSource, indication)
        .height(IntrinsicSize.Min)
        .width(IntrinsicSize.Min)
        .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Image(
            modifier = Modifier.align(Alignment.Center),
            painter = painterResource(id = icon),
            contentDescription = null
        )

        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = this.size.width
            val height = this.size.height
            paint.apply {
                textSize = width / 10
                color = android.graphics.Color.BLACK
            }
            val paddingBottom = this.size.height * 0.1f
            drawIntoCanvas {
                val path = Path()
                path.addArc(RectF(0f, 0f + 3 * paddingBottom, width, height - paddingBottom), 180f, 180f)
                it.nativeCanvas.drawTextOnPath(topText, path, 0f, 0f, paint)
            }
            drawIntoCanvas {
                val path = Path()
                path.addArc(RectF(0f, 0f - paddingBottom, width, height - paddingBottom), 180f, -180f)
                it.nativeCanvas.drawTextOnPath(bottomText, path, 0f, 0f, paint)
            }
        }
    }
    if (contentOnBounds != null) {
        GrimoireRolesLayout(
            roles = contentOnBounds,
            modifier = Modifier.fillMaxSize(),
            sizeType = CircleSizeType.ParentQuarter,
            positionType = CirclePositionType.OnTop
        )
    }
    if (leftContent != null || rightContent != null) {
        Layout(content = {
            Box { leftContent?.invoke() }
            Box { rightContent?.invoke() }
        }, modifier = Modifier.fillMaxSize()
        ) { measurables, constraints ->
            val size = (minOf(constraints.maxHeight, constraints.maxWidth) / 4f).roundToInt()
            val padding = size / 2f
            val placeables = measurables.map { it.measure(Constraints.fixed(size, size)) }
            val rightX = constraints.maxWidth - placeables.last().width - padding
            val height = maxOf(placeables.first().height, placeables.last().height)
            val width = constraints.maxWidth
            layout(width, height) {
                placeables.first().placeRelative(padding.roundToInt(), 0)
                placeables.last().placeRelative(rightX.roundToInt(), 0)
            }
        }
    }
}

@Composable
@Preview
fun myPreview() {
    Surface(modifier = Modifier.fillMaxSize()) {
        val count = remember { mutableStateOf(5) }
        val remindersCount = remember { mutableStateOf(0) }
        val remindersLocal = remember(remindersCount.value) {
            reminders.take(remindersCount.value)
        }
        val zoomState = rememberZoomState()
        val remindersSize = reminders.size
        val roles = Role.values().take(count.value).map { RoleGameState(it) }
        Column {
            Slider(
                value = count.value.toFloat(),
                onValueChange = { count.value = it.toInt() },
                valueRange = 5f..20f
            )
            Slider(
                value = remindersCount.value.toFloat(),
                onValueChange = { remindersCount.value = it.toInt() },
                valueRange = 0f..remindersSize.toFloat()
            )
            GrimoireScreen(AppState.GameState(roles), {}, { _, _ -> }, zoomState = zoomState)
        }
    }
}
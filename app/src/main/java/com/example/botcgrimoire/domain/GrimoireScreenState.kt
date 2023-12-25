package com.example.botcgrimoire.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * @author Valeriy Minnulin
 */
data class GrimoireScreenState(
    val actionsDialog: ActionsDialog? = null,
)

@Serializable
data class ReminderLink(
    val reminder: Reminder,
    val role: Role
)

data class ActionsDialog(
    val actionsInDialog: List<Action>,
    val dismissLambda: (() -> Unit),
    val onActionClick: (Action) -> Unit
)

sealed interface Action {
    data class ReminderAction(
        val reminder: Reminder,
        val isAdd: Boolean = true
    ) : Action

    data class ChangeLifeState(val newIsDead: Boolean) : Action
    data class ChangeHasVote(val hasVote: Boolean) : Action
}


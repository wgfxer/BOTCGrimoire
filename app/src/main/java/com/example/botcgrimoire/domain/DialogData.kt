package com.example.botcgrimoire.domain

/**
 * @author Valeriy Minnulin
 */
data class DialogData(
    val title: Int,
    val subtitle: Int,
    val positiveButton: ButtonData,
    val negativeButton: ButtonData?,
    val dismissLambda: () -> Unit
)

data class ButtonData(
    val text: Int,
    val onClick: (() -> Unit)? = null
)
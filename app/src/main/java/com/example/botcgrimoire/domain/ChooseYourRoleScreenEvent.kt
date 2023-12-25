package com.example.botcgrimoire.domain

/**
 * @author Valeriy Minnulin
 */
sealed interface ChooseYourRoleScreenEvent {
    object NavigateToConfigureGame: ChooseYourRoleScreenEvent
}
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.botcgrimoire.presentation.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.botcgrimoire.R
import com.example.botcgrimoire.presentation.infocards.InfoCardsScreen
import com.example.botcgrimoire.presentation.nightorder.NightOrderScreen
import com.example.botcgrimoire.presentation.settings.SettingsScreen
import com.example.botcgrimoire.presentation.grimoire.GrimoireScreen

/**
 * @author Valeriy Minnulin
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainGameScreen(navigateToStart: () -> Unit, finishActivity: () -> Unit) {
    val currentScreen = remember { mutableStateOf(MainScreenTab.Grimoire) }
    Column {
        Surface(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (currentScreen.value) {
                MainScreenTab.Grimoire -> GrimoireScreen()
                MainScreenTab.Cards -> InfoCardsScreen()
                MainScreenTab.NightOrder -> NightOrderScreen()
                MainScreenTab.Settings -> SettingsScreen(navigateToStart)
            }
        }
        Bottombar(currentScreen.value) { currentScreen.value = it }
    }
    BackHandler {
        if (currentScreen.value != MainScreenTab.Grimoire) {
            currentScreen.value = MainScreenTab.Grimoire
        } else {
            finishActivity()
        }
    }
}

@Composable
private fun Bottombar(selectedTab: MainScreenTab, onTabSelected: (MainScreenTab) -> Unit) {
    BottomAppBar {
        MainScreenTab.values().forEach {
            val isSelected = selectedTab == it
            BottomTab(it, isSelected) { onTabSelected.invoke(it) }
        }
    }
}

@Composable
private fun RowScope.BottomTab(tab: MainScreenTab, isSelected: Boolean, onTabSelected: () -> Unit) {
    val source = remember { MutableInteractionSource() }
    val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
    Column(modifier = Modifier
        .weight(1f)
        .clickable(interactionSource = source, indication = rememberRipple(bounded = false), onClick = onTabSelected),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(painter = painterResource(id = tab.icon), tint = color, contentDescription = null)
        Text(text = stringResource(id = tab.text), color = color)
    }
}

enum class MainScreenTab(
    val text: Int,
    val icon: Int
) {
    Grimoire(R.string.grimoire_tab, R.drawable.ic_play_arrow_24),
    Cards(R.string.cards_tab, R.drawable.ic_chat_bubble_outline_24),
    NightOrder(R.string.night_order_tab, R.drawable.ic_nights_stay_24),
    Settings(R.string.settings_tab, R.drawable.ic_settings_24),
}
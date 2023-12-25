package com.example.botcgrimoire.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.botcgrimoire.di.getAppStateInteractor
import com.example.botcgrimoire.domain.AppState
import com.example.botcgrimoire.domain.Role
import com.example.botcgrimoire.presentation.chooserole.ChooseYourRoleScreen
import com.example.botcgrimoire.presentation.configurescreen.ConfigureGameScreen
import com.example.botcgrimoire.presentation.revealrole.RevealedRoleScreen
import com.example.botcgrimoire.ui.theme.BOTCGrimoireTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val interactor = getAppStateInteractor()
        val currentState = interactor.state.value
        val startDestination = when (currentState) {
            AppState.ConfigureGame -> ConfigureGameScreen
            is AppState.GameState -> MainGameScreen
            is AppState.RevealingRoles -> ChooseYourRoleScreen
        }
        setContent {
            BOTCGrimoireTheme {
                Surface(modifier = Modifier.fillMaxSize()
                    .imePadding()
                    .safeDrawingPadding()
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable(ConfigureGameScreen) {
                            ConfigureGameScreen { navController.navigate(ChooseYourRoleScreen) }
                        }
                        composable(ChooseYourRoleScreen) {
                            ChooseYourRoleScreen(
                                navigateToRevealRoleScreen = { role: Role -> navController.navigate("$RevealedRoleScreen/${role.name}") },
                                navigateToGrimoire = { navController.navigate(MainGameScreen) },
                                navigateToStart = { navController.navigate(ConfigureGameScreen) }
                            )
                        }
                        composable("$RevealedRoleScreen/{$roleId}") {
                            val roleId = checkNotNull(it.arguments?.getString(roleId)) {
                                "Попытка перейти на экран раскрытой роли игроку без самой роли"
                            }
                            RevealedRoleScreen(Role.valueOf(roleId)) { navController.popBackStack() }
                        }
                        composable(MainGameScreen) {
                            MainGameScreen(
                                navigateToStart = { navController.navigate(ConfigureGameScreen) },
                                finishActivity = { finish() }
                            )
                        }
                    }
                }

            }
        }
    }
}

const val ConfigureGameScreen = "ConfigureGameScreen"
const val ChooseYourRoleScreen = "ChooseYourRoleScreen"
const val RevealedRoleScreen = "RevealedRoleScreen"
const val MainGameScreen = "MainGameScreen"
const val roleId = "roleId"
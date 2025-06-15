package com.example.passman.navigation


import RecoveryKeyScreen
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.passman.SecuritySetup
import com.example.passman.screens.ConfirmRecoveryKeyScreen
import com.example.passman.screens.DataBreachScreen
import com.example.passman.screens.EditPasswordScreen
import com.example.passman.screens.FirstLaunchScreen
import com.example.passman.screens.LoginScreen
import com.example.passman.screens.PasswordAddScreen
import com.example.passman.screens.PasswordListScreen
import com.example.passman.screens.RecoveryScreen


object Routes {
    const val CONFIRM_RECOVER_KEY = "ConfirmRecoverKeyScreen"
    const val FIRST_LAUNCH = "FirstLaunchScreen"
    const val LOGIN = "LoginScreen"
    const val PASSWORD_LIST = "PasswordListScreen"
    const val RECOVERY_KEY = "RecoveryKeyScreen"
    const val RECOVERY = "RecoveryScreen"
    const val ADD_PASSWORD = "AddPasswordScreen"
    const val EDIT_PASSWORD = "EditPasswordScreen"
    const val DATA_BREACH = "DataBreachScreen"
}

sealed class Screen(val route: String) {
    data object FirstLaunch : Screen(Routes.FIRST_LAUNCH)
    data object RecoveryKey : Screen(Routes.RECOVERY_KEY)
    data object PasswordList : Screen(Routes.PASSWORD_LIST)
    data object Login : Screen(Routes.LOGIN)
    data object AddPassword : Screen(Routes.ADD_PASSWORD)
    data object Recovery : Screen(Routes.RECOVERY)
    data object EditPassword : Screen(Routes.EDIT_PASSWORD) {
        fun createRoute(id: Int) = "$route/$id"
    }

    data object ConfirmRecoveryKey : Screen(Routes.CONFIRM_RECOVER_KEY)
    data object DataBreach : Screen(Routes.DATA_BREACH)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val startDestination = if (SecuritySetup(LocalContext.current).isSetupCompleted()) {
        Screen.Login.route
    } else {
        Screen.FirstLaunch.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.FirstLaunch.route) { FirstLaunchScreen(navController) }
        composable(Screen.RecoveryKey.route + "/{recoveryKey}") { backStackEntry ->
            val recoveryKey = backStackEntry.arguments?.getString("recoveryKey") ?: ""
            RecoveryKeyScreen(navController, recoveryKey)
        }
        composable(Screen.Recovery.route) { RecoveryScreen(navController) }
        composable(Screen.PasswordList.route) { PasswordListScreen(navController = navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.AddPassword.route) { PasswordAddScreen(navController) }
        composable(Screen.EditPassword.route + "/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
            EditPasswordScreen(navController, id)
        }
        composable(Screen.ConfirmRecoveryKey.route) {
            ConfirmRecoveryKeyScreen(
                navController,
                recoveryKey = ""
            )
        }
        composable(Screen.ConfirmRecoveryKey.route + "/{recoveryKey}") { backStackEntry ->
            val recoveryKey = backStackEntry.arguments?.getString("recoveryKey") ?: ""
            ConfirmRecoveryKeyScreen(navController, recoveryKey)
        }
        composable(Screen.DataBreach.route) { DataBreachScreen(navController) }
    }
}


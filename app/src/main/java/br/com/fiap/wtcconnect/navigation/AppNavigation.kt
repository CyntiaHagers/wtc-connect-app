package br.com.fiap.wtcconnect.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.fiap.wtcconnect.screens.login.LoginScreen
import br.com.fiap.wtcconnect.screens.main.MainScreen

object Routes {
    const val LOGIN = "login"
    const val MAIN = "main"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN) {
                        // Limpa a pilha de navegação para que o usuário não volte para o login
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Routes.MAIN) {
            MainScreen()
        }
    }
}

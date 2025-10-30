@file:Suppress("DEPRECATION")

package br.com.fiap.wtcconnect.screens.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.fiap.wtcconnect.screens.campaigns.CampaignsScreen
import br.com.fiap.wtcconnect.screens.ChatScreen
import br.com.fiap.wtcconnect.screens.ConversationListScreen
import br.com.fiap.wtcconnect.screens.clients.ClientsScreen
import br.com.fiap.wtcconnect.screens.profile.ChangePasswordScreen
import br.com.fiap.wtcconnect.screens.profile.HelpScreen
import br.com.fiap.wtcconnect.screens.profile.NotificationsScreen
import br.com.fiap.wtcconnect.screens.profile.ProfileScreen
import br.com.fiap.wtcconnect.screens.groups.GroupsScreen
import br.com.fiap.wtcconnect.ui.theme.AccentGreen
import br.com.fiap.wtcconnect.ui.theme.AccentOrange
import br.com.fiap.wtcconnect.viewmodel.AuthViewModel
import br.com.fiap.wtcconnect.viewmodel.UserType

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Chat : BottomNavItem("chat", Icons.Default.Chat, "Chat")
    object Campaigns : BottomNavItem("campaigns", Icons.Default.Campaign, "Campanhas")
    object Clients : BottomNavItem("clients", Icons.Default.People, "Clientes")
    object Groups : BottomNavItem("groups", Icons.Default.People, "Grupos")
    object Profile : BottomNavItem("profile", Icons.Default.Settings, "Perfil")
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()
    val isOperator = authState.userType == UserType.OPERATOR

    val items = mutableListOf(
        BottomNavItem.Chat,
        BottomNavItem.Campaigns,
    ).apply {
        if (isOperator) {
            add(BottomNavItem.Clients)
        } else {
            add(BottomNavItem.Groups)
        }
        add(BottomNavItem.Profile)
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, items = items)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(navController = navController, authViewModel = authViewModel)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, items: List<BottomNavItem>) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = when(item) {
                        BottomNavItem.Chat -> AccentGreen
                        BottomNavItem.Campaigns -> AccentOrange
                        else -> MaterialTheme.colorScheme.primary
                    },
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}


@Composable
fun NavigationGraph(navController: androidx.navigation.NavHostController, authViewModel: AuthViewModel) {
    // Cria um repositório compartilhado entre a lista de conversas e as telas de chat
    // Passa informações do usuário autenticado (id/email) para que o repositório fake saiba o contexto
    val authState by authViewModel.authState.collectAsState()
    val sharedRepo = remember(authState.userId, authState.userEmail) {
        br.com.fiap.wtcconnect.data.FakeChatRepository(currentUserId = authState.userId ?: "me", currentUserEmail = authState.userEmail)
    }
    NavHost(navController, startDestination = BottomNavItem.Chat.route) {
        // Rota 'chat' agora apresenta a lista de conversas (ConversationListScreen)
        composable(BottomNavItem.Chat.route) {
            // Exibe a lista de conversas usando o repositório compartilhado
            ConversationListScreen(navController = navController, repository = sharedRepo, currentUserId = authState.userId, currentUserType = authState.userType)
        }
        // Rota com argumentos para abrir o chat 1:1 com conversationId e peerUserId
        composable("chat/{conversationId}/{peerUserId}") { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            val peerUserId = backStackEntry.arguments?.getString("peerUserId") ?: ""
            // Usa o mesmo repositório compartilhado para que mensagens/estado sejam visíveis entre telas
            ChatScreen(navController = navController, conversationId = conversationId, peerUserId = peerUserId, repository = sharedRepo, currentUserId = authState.userId, currentUserType = authState.userType)
        }
        // Rota para chat de grupo (broadcast)
        composable("group_chat/{groupId}") { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            ChatScreen(navController = navController, conversationId = "group_$groupId", peerUserId = "group:$groupId", repository = sharedRepo, currentUserId = authState.userId, currentUserType = authState.userType)
        }
        composable(BottomNavItem.Campaigns.route) { CampaignsScreen(authViewModel = authViewModel) }
        composable(BottomNavItem.Clients.route) { ClientsScreen() }
        composable(BottomNavItem.Groups.route) {
            // Chamada direta da tela de Grupos (tratamento de erros deve ser interno à tela)
            GroupsScreen(navController = navController, repository = sharedRepo, currentUserId = authState.userId, currentUserType = authState.userType)
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateToChangePassword = {
                    navController.navigate("change_password")
                },
                onNavigateToNotifications = {
                    navController.navigate("notifications")
                },
                onNavigateToHelp = {
                    navController.navigate("help")
                }
            )
        }

        // Novas rotas para as telas de configuração
        composable("change_password") {
            ChangePasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("notifications") {
            NotificationsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("help") {
            HelpScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

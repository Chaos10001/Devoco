package com.chaos.devoco.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.chaos.devoco.MainActivity
import com.chaos.devoco.ui.home.HomeScreen
import com.chaos.devoco.ui.objective.ObjectiveScreen
import com.chaos.devoco.ui.quiz.QuizScreen
import com.chaos.devoco.ui.theory.TheoryScreen

sealed class Screen(val route: String){
    object Home: Screen("home")
    
    object Objective : Screen("objective/{documentId}"){
        fun createRoute(docId: String) = "objective/$docId"
    }

    object Theory : Screen("theory/{documentId}"){
        fun createRoute(docId: String) = "theory/$docId"
    }

    object Quiz : Screen("quiz/{documentId}"){
        fun createRoute(docId: String) = "quiz/$docId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    sharedPdfUri: Uri? = null
){
    val context = LocalContext.current
    val activity = context as? MainActivity

    // If a PDF is shared while on another screen, navigate back to Home to process it
    LaunchedEffect(sharedPdfUri) {
        if (sharedPdfUri != null) {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute != Screen.Home.route) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ){
        composable(route = Screen.Home.route){
            HomeScreen(
                navController = navController,
                sharedPdfUri = sharedPdfUri
            )
            
            // Consume the URI once it has been passed to HomeScreen to prevent re-processing
            LaunchedEffect(sharedPdfUri) {
                if (sharedPdfUri != null) {
                    activity?.consumeSharedUri()
                }
            }
        }

        composable(
            route = Screen.Objective.route,
            arguments = listOf(
                navArgument("documentId") {type = NavType.StringType}
            )
        ){ backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: return@composable
            ObjectiveScreen(
                documentId = documentId,
                navController= navController
            )
        }

        composable(
            route = Screen.Theory.route,
            arguments = listOf(
                navArgument("documentId") {type = NavType.StringType}
            )
        ){ backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: return@composable
            TheoryScreen(
                documentId = documentId,
                navController= navController
            )
        }

        composable(
            route = Screen.Quiz.route,
            arguments = listOf(
                navArgument("documentId") {type = NavType.StringType}
            )
        ){ backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: return@composable
            QuizScreen(
                documentId = documentId,
                navController= navController
            )
        }
    }
}
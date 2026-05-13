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

    // Consolidated handling for shared PDF URI
    LaunchedEffect(sharedPdfUri) {
        if (sharedPdfUri != null) {
            // 1. If we are not on Home, navigate there to show the importing UI
            if (navController.currentDestination?.route != Screen.Home.route) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = false }
                    launchSingleTop = true
                }
            }
            // 2. Immediately consume the URI in MainActivity.
            // Even if HomeScreen recomposes with null, its internal LaunchedEffect 
            // will have already triggered the ViewModel processing.
            activity?.consumeSharedUri()
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
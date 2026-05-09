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
    object Home: Screen("home?sharedUri={sharedUri}"){
        fun createRoute(sharedUri: String? = null) = "home${sharedUri?.let { 
            "?sharedUri=${Uri.encode(it)}"
        } ?: ""}"
    }
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
    val uriToPass = sharedPdfUri?.toString() ?: activity?.consumeSharedUri()?.toString()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.createRoute(uriToPass)
    ){
        composable(
            route = Screen.Home.route,
            arguments = listOf(
                navArgument("sharedUri") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ){ backStackEntry ->
            val sharedUriString = backStackEntry.arguments?.getString("sharedUri")
            val uri = sharedUriString?.let { Uri.parse(it) }

            HomeScreen(
                navController = navController,
                sharedPdfUri = uri
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
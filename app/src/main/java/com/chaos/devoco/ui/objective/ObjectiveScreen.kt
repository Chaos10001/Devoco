package com.chaos.devoco.ui.objective

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chaos.devoco.ui.component.objective.ObjectiveQuestionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectiveScreen (
    documentId:String,
    navController: NavHostController,
    viewModel: ObjectiveViewModel= hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(documentId) {
        viewModel.loadQuestions(documentId)
    }
    Scaffold(
        topBar ={
            TopAppBar(
                title = {
                    Text(
                        "Objective Questions",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}){
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,"Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            )
        },
    ) { paddingValue ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
        ){
            when{
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null ->{
                    Column(
                        modifier= Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text= uiState.error ?: "Error loading questions",
                            color= MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {viewModel.loadQuestions(documentId)}) {
                            Text("Retry")
                        }
                    }
                }

                else ->{
                    LazyColumn(
                        modifier= Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                        item {
                            Text(
                                text= "${uiState.questions.size} objective question with answers",
                                color= MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        itemsIndexed(uiState.questions){index, question ->
                            ObjectiveQuestionCard(
                                question = question,
                                isRevealed= uiState.revealedAnswers.contains(index),
                                onReveal={viewModel.toggleReveal(index)}
                            )
                        }
                    }
                }
            }
        }
    }
}
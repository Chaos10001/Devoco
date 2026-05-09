package com.chaos.devoco.ui.theory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.chaos.devoco.ui.component.objective.ObjectiveQuestionCard
import com.chaos.devoco.ui.component.theory.TheoryQuestionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheoryScreen(
    documentId:String,
    navController: NavHostController,
    viewModel: TheoryViewModel= hiltViewModel()
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
                        "Theory Questions",
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ){
            when{
                uiState.isLoading ->{
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
                                text= "${uiState.questions.size} theory question",
                                color= MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        itemsIndexed(uiState.questions){index, question ->
                            TheoryQuestionCard(
                                question=question,
                                isRevealed=uiState.revealedAnswers.contains(index),
                                onReveal={viewModel.toggleReveal(index)}
                            )
                        }
                    }
                }
            }
        }
    }

}
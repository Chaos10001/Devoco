package com.chaos.devoco.ui.component.theory

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chaos.devoco.domain.model.TheoryQuestion

@Composable
fun TheoryQuestionCard(
    question: TheoryQuestion,
    isRevealed: Boolean,
    onReveal: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Q${question.questionNumber} - ${question.question}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onReveal,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (isRevealed) "Hide Answer" else "Show Answer")
            }

            if (isRevealed){
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Answer:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = question.answer,
                            style = MaterialTheme.typography.bodyMedium,

                        )
                    }
                }
            }
        }
    }
}
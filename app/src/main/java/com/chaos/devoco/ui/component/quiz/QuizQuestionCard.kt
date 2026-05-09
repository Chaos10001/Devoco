package com.chaos.devoco.ui.component.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chaos.devoco.domain.model.QuizQuestion
import com.chaos.devoco.ui.theme.CorrectGreen
import com.chaos.devoco.ui.theme.IncorrectRed

@Composable
fun QuizQuestionCard(
    question: QuizQuestion,
    questionNumber: Int,
    onAnswerSelected: (String) -> Unit,
    showResult: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Q$questionNumber: ${question.question}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))

            question.options.forEach { option ->
                val isSelected = question.userAnswer == option
                val isCorrect = question.correctAnswer == option
                val backgroundColor = when {
                    showResult && isCorrect -> CorrectGreen.copy(alpha = 0.1f)
                    showResult && isSelected && !isCorrect -> IncorrectRed.copy(alpha = 0.1f)
                    isSelected -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surface
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    onClick = { if (!showResult) onAnswerSelected(option) },
                    color = backgroundColor,
                    shape = MaterialTheme.shapes.small,
                    enabled = !showResult
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = when {
                                    showResult && isCorrect -> CorrectGreen
                                    showResult && !isCorrect && isSelected -> IncorrectRed
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = when {
                                showResult && isCorrect -> CorrectGreen
                                showResult && !isCorrect && isSelected -> IncorrectRed
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )

                        if (showResult && isCorrect) {
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                "✓",
                                color = CorrectGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Explanation after submission
            if (showResult) {
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "Explanation: ${question.explanation}",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
package com.chaos.devoco.ui.component.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.chaos.devoco.domain.model.PdfDocument
import com.chaos.devoco.ui.navigation.Screen
import com.chaos.devoco.ui.theme.QuizOrange

@Composable
fun DocumentCard(
    document: PdfDocument,
    navController: NavHostController,
    onDelete: (PdfDocument) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors= CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = document.fileName,
                    style= MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                IconButton(
                    onClick = {showDeleteDialog = true}
                ) {
                    Icon(
                        Icons.Default.Delete,
                    contentDescription = "Delete PDF",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier =Modifier.height(4.dp))
            Text(
                text = "${document.pageCount} pages • ${document.extractedText.length} characters extracted",
                style= MaterialTheme.typography.bodySmall,
                color= MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier =Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(modifier =Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Button(
                    onClick={
                     navController.navigate(Screen.Objective.createRoute(document.id))
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier =Modifier.height(2.dp))
                        Text(
                            "Objective",
                            style=MaterialTheme.typography.labelSmall
                        )
                    }
                }

                OutlinedButton(
                    onClick={
                        navController.navigate(Screen.Theory.createRoute(document.id))
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier =Modifier.height(2.dp))
                        Text(
                            "Theory",
                            style=MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Button(
                    onClick={
                        navController.navigate(Screen.Quiz.createRoute(document.id))
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = QuizOrange
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Quiz,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier =Modifier.height(2.dp))
                        Text(
                            "Quiz",
                            style=MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }

    //Show delete confirmation dialog
    if (showDeleteDialog){
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text("Delete PDF?")
            },
            text = {
                Text(
                    "Are you sure you want to delete \"${document.fileName}\"? " +
                            "All generated questions will also be deleted."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog= false
                        onDelete(document)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {showDeleteDialog = false}
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
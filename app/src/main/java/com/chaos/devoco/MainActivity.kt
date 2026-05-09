package com.chaos.devoco

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.chaos.devoco.ui.navigation.NavGraph
import com.chaos.devoco.ui.theme.DevocoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var sharedPdfUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if app was opened with a PDF
        handleIncomingIntent(intent)
        
        enableEdgeToEdge()
        setContent {
            DevocoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        sharedPdfUri = sharedPdfUri
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Important: Update the intent so that it can be correctly handled
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {
        val action = intent.action
        val type = intent.type ?: intent.resolveType(this)

        if (type == "application/pdf" || type?.startsWith("application/pdf") == true) {
            when (action) {
                Intent.ACTION_SEND -> {
                    val uri = if (Build.VERSION.SDK_INT >= 33) {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(Intent.EXTRA_STREAM)
                    }
                    if (uri != null) {
                        sharedPdfUri = uri
                    }
                }
                Intent.ACTION_VIEW -> {
                    intent.data?.let {
                        sharedPdfUri = it
                    }
                }
            }
        }
    }

    /**
     * Consumes the shared URI so it's not processed multiple times.
     */
    fun consumeSharedUri(): Uri? {
        val uri = sharedPdfUri
        sharedPdfUri = null
        return uri
    }
}
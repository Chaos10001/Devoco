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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.chaos.devoco.data.local.datastore.UserPreferences
import com.chaos.devoco.ui.navigation.NavGraph
import com.chaos.devoco.ui.theme.DevocoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var userPreferences: UserPreferences

    private var sharedPdfUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Only handle intent if this is a fresh start, not a configuration change
        if (savedInstanceState == null) {
            handleIncomingIntent(intent)
        }
        
        enableEdgeToEdge()
        setContent {
//            val isDarkMode by userPreferences.isDarkMode.collectAsState(initial = false)
            
            DevocoTheme() {
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
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {
        val action = intent.action ?: return
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
                        // Clear the intent action/extra so it doesn't trigger again on recreation
                        intent.action = null
                        intent.removeExtra(Intent.EXTRA_STREAM)
                    }
                }
                Intent.ACTION_VIEW -> {
                    intent.data?.let {
                        sharedPdfUri = it
                        // Clear the intent action/data so it doesn't trigger again on recreation
                        intent.action = null
                        intent.data = null
                    }
                }
            }
        }
    }

    fun consumeSharedUri(): Uri? {
        val uri = sharedPdfUri
        sharedPdfUri = null
        return uri
    }
}
package com.app.ecarepro.testing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Registry of available module screens
object ModuleRegistry {
    val screens: Map<String, @Composable () -> Unit> = mapOf(
        "OnBoarding" to {  },
    )
}

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TestHostScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestHostScreen() {
    var selected by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selected ?: "Select a Module") }
            )
        }
    ) { padding ->
        if (selected == null) {
            // Show list of available modules
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ModuleRegistry.screens.keys.toList()) { name ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected = name }
                            .padding(4.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        } else {
            // Show selected module screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(8.dp)
            ) {
                ModuleRegistry.screens[selected]?.invoke()
            }
        }
    }
}


package com.sahih

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sahih.ui.components.SahihBottomBar
import com.sahih.ui.screens.CallShieldScreen
import com.sahih.ui.screens.CheckoutScreen
import com.sahih.ui.screens.EvidenceScreen
import com.sahih.ui.screens.HomeScreen
import com.sahih.ui.screens.RadarScreen
import com.sahih.ui.screens.VerifyScreen
import com.sahih.ui.theme.SahihTheme

class MainActivity : ComponentActivity() {
    private val viewModel: SahihViewModel by viewModels()
    private val sharedTextState = mutableStateOf<String?>(null)
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); handleShareIntent(intent); setContent { SahihTheme { Surface(Modifier.fillMaxSize()) { SahihApp(sharedTextState.value, { sharedTextState.value = null }, viewModel) } } } }
    override fun onNewIntent(intent: Intent) { super.onNewIntent(intent); setIntent(intent); handleShareIntent(intent) }
    private fun handleShareIntent(intent: Intent?) { if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") sharedTextState.value = intent.getStringExtra(Intent.EXTRA_TEXT) }
}

@androidx.compose.runtime.Composable
fun SahihApp(sharedText: String?, onSharedTextConsumed: () -> Unit, viewModel: SahihViewModel) {
    val navController = rememberNavController()
    LaunchedEffect(sharedText) { if (sharedText != null) { viewModel.analyseSharedContent(sharedText); navController.navigate("verify") { launchSingleTop = true }; onSharedTextConsumed() } }
    Scaffold(bottomBar = { SahihBottomBar(navController) }) { padding ->
        NavHost(navController, "home", Modifier.padding(padding)) {
            composable("home") { HomeScreen(navController) }
            composable("verify") { VerifyScreen(viewModel) { viewModel.createEvidence(); navController.navigate("evidence") { launchSingleTop = true } } }
            composable("checkout") { CheckoutScreen(viewModel) { viewModel.createEvidence(); navController.navigate("evidence") { launchSingleTop = true } } }
            composable("callshield") { CallShieldScreen() }
            composable("radar") { RadarScreen(viewModel) { viewModel.createEvidence(); navController.navigate("evidence") { launchSingleTop = true } } }
            composable("evidence") { EvidenceScreen(viewModel.evidence) { navController.popBackStack() } }
        }
    }
}

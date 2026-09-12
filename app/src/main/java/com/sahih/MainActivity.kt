package com.sahih

import android.content.Intent
import android.net.Uri
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
import com.sahih.ui.screens.*
import com.sahih.ui.theme.SahihTheme

private sealed class SharedContent {
    data class Text(val value: String, val target: String) : SharedContent()
    data class Image(val uri: Uri, val target: String) : SharedContent()
}

class MainActivity : ComponentActivity() {
    private val viewModel: SahihViewModel by viewModels()
    private val sharedContentState = mutableStateOf<SharedContent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleShareIntent(intent)
        setContent {
            SahihTheme {
                Surface(Modifier.fillMaxSize()) {
                    SahihApp(sharedContentState.value, { sharedContentState.value = null }, viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleShareIntent(intent)
    }

    private fun handleShareIntent(intent: Intent?) {
        if (intent == null || intent.action != Intent.ACTION_SEND) return

        val aliasName = intent.component?.shortClassName.orEmpty()
        val target = when {
            aliasName.endsWith("PayGuardShareTarget") -> "checkout"
            aliasName.endsWith("CekduluCheckoutShareTarget") -> "checkout"
            aliasName.endsWith("CekduluShareTarget") -> null
            else -> null
        }

        when (intent.type) {
            "text/plain" -> {
                val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return
                val resolvedTarget = target ?: routeForSharedText(text)
                sharedContentState.value = SharedContent.Text(text, resolvedTarget)
            }
            else -> {
                if (intent.type?.startsWith("image/") == true) {
                    val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM) ?: return
                    sharedContentState.value = SharedContent.Image(uri, target ?: "checkout")
                }
            }
        }
    }

    // Non-payment shared text now goes to SellerCheckScreen (the app's
    // "check before you trust it" screen) since VerifyScreen was removed.
    private fun routeForSharedText(text: String): String {
        val lower = text.lowercase()
        val paymentSignals = listOf("rm ", "transfer", "duitnow", "bank in", "paid to", "account no", "acc no", "otp")
        return if (paymentSignals.any { lower.contains(it) }) "checkout" else "sellercheck"
    }
}

@androidx.compose.runtime.Composable
private fun SahihApp(shared: SharedContent?, onConsumed: () -> Unit, viewModel: SahihViewModel) {
    val navController = rememberNavController()

    LaunchedEffect(shared) {
        when (shared) {
            is SharedContent.Text -> {
                if (shared.target == "sellercheck") {
                    // Prefill the bio field; SellerCheckScreen detects this
                    // and jumps straight to its scanning stage on compose.
                    viewModel.sellerBioText = shared.value
                } else {
                    viewModel.analyseSharedContent(shared.value)
                }
                navController.navigate(shared.target) { launchSingleTop = true }
                onConsumed()
            }
            is SharedContent.Image -> {
                viewModel.analyseSharedImage(shared.uri)
                navController.navigate(shared.target) { launchSingleTop = true }
                onConsumed()
            }
            null -> Unit
        }
    }

    Scaffold(bottomBar = { SahihBottomBar(navController) }) { padding ->
        NavHost(navController, "home", Modifier.padding(padding)) {
            composable("home") { HomeScreen(navController) }
            composable("checkout") { CheckoutScreen(viewModel) { viewModel.createEvidence(); navController.navigate("evidence") { launchSingleTop = true } } }
            composable("radar") {
                RadarScreen(
                    vm = viewModel,
                    onEvidence = { viewModel.createEvidence(); navController.navigate("evidence") { launchSingleTop = true } },
                    onReportScam = { navController.navigate("reportscam") { launchSingleTop = true } }
                )
            }
            composable("evidence") { EvidenceScreen(viewModel.evidence) { navController.popBackStack() } }
            composable("sellercheck") {
                SellerCheckScreen(viewModel) {
                    navController.navigate("sellerresult") { launchSingleTop = true }
                }
            }
            composable("sellerresult") {
                SellerCredibilityResultScreen(viewModel.credibilityResult) { navController.popBackStack() }
            }
            composable("reportscam") {
                ReportScamScreen(
                    vm = viewModel,
                    onBack = { navController.popBackStack() },
                    onSubmitted = { navController.popBackStack() }
                )
            }
        }
    }
}
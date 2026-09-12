package com.sahih

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sahih.ui.components.SahihBottomBar
import com.sahih.ui.screens.CallShieldScreen
import com.sahih.ui.screens.CheckoutScreen
import com.sahih.ui.screens.HomeScreen
import com.sahih.ui.screens.RadarScreen
import com.sahih.ui.screens.ReportScamScreen
import com.sahih.ui.screens.VerifyScreen
import com.sahih.ui.theme.SahihTheme

class MainActivity : ComponentActivity() {

    // Holds text shared in from other apps ("Share to Cekdulu"), e.g. a
    // copied link, a WhatsApp message, or a caption from Instagram/TikTok.
    private val sharedTextState = mutableStateOf<String?>(null)

    // Flips true when a screenshot / payment receipt is shared in via the
    // "Cekdulu Checkout" or "PayGuard" share-sheet buttons, simulating a
    // payment app handing off a transaction to Sahih's pre-checkout guard
    // for a validity check.
    private val sharedPaymentState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleShareIntent(intent)

        setContent {
            SahihTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SahihApp(
                        sharedText = sharedTextState.value,
                        onSharedTextConsumed = { sharedTextState.value = null },
                        sharedPayment = sharedPaymentState.value,
                        onSharedPaymentConsumed = { sharedPaymentState.value = false }
                    )
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
        if (intent?.action != Intent.ACTION_SEND) return

        // Which share-sheet entry the user tapped. component is the alias
        // (e.g. com.sahih.PayGuardShareTarget), not MainActivity itself,
        // since aliases forward the intent but keep their own identity here.
        val className = intent.component?.className.orEmpty()
        val viaPayGuard = className.endsWith("PayGuardShareTarget")

        when {
            intent.type == "text/plain" -> {
                val text = intent.getStringExtra(Intent.EXTRA_TEXT)
                sharedTextState.value = text
                // PayGuard always means "check this as a payment", regardless
                // of whether the text looks like one -- unlike plain
                // "Cekdulu", which lets routeForSharedText() decide.
                if (viaPayGuard) {
                    sharedPaymentState.value = true
                }
            }
            intent.type?.startsWith("image/") == true -> {
                // TODO: run OCR / receipt parsing on the shared screenshot once
                // that pipeline exists, so the real bank account / recipient
                // name gets pulled out. For the demo, this just triggers the
                // pre-checkout guard, same as tapping "Cekdulu Checkout" or
                // "PayGuard".
                sharedPaymentState.value = true
            }
        }
    }
}

private const val ROUTE_HOME = "home"
private const val ROUTE_VERIFY = "verify"
private const val ROUTE_CHECKOUT = "checkout"
private const val ROUTE_CALLS = "callshield"
private const val ROUTE_RADAR = "radar"
private const val ROUTE_REPORT_SCAM = "report_scam"

/**
 * Payment-looking shares (has an "RM" amount + a long digit run, e.g. an
 * account number) go to the pre-checkout guard instead of the general
 * shop/link verifier. Top-level so both MainActivity and SahihApp can
 * reach it without either depending on the other.
 */
private fun routeForSharedText(text: String): String {
    val looksLikePayment = Regex("RM\\s?[\\d,]+", RegexOption.IGNORE_CASE).containsMatchIn(text) &&
            Regex("\\b\\d{10,16}\\b").containsMatchIn(text)
    return if (looksLikePayment) ROUTE_CHECKOUT else ROUTE_VERIFY
}

@androidx.compose.runtime.Composable
fun SahihApp(
    sharedText: String?,
    onSharedTextConsumed: () -> Unit,
    sharedPayment: Boolean,
    onSharedPaymentConsumed: () -> Unit,
) {
    val navController: NavHostController = rememberNavController()

    // Any time a link/message arrives via "Cekdulu" or "PayGuard", route it:
    // if the payment flag is already set (PayGuard, or an image share), go
    // straight to Checkout. Otherwise fall back to routeForSharedText() to
    // decide between Verify and Checkout based on content. Either way this
    // is the "zero-friction" flow -- no home screen, no typing, the
    // extracted content is already there.
    androidx.compose.runtime.LaunchedEffect(sharedText, sharedPayment) {
        if (sharedText != null) {
            val destination = if (sharedPayment) ROUTE_CHECKOUT else routeForSharedText(sharedText)
            navController.navigate(destination)
        }
    }

    // Any time a screenshot/receipt arrives via "Cekdulu Checkout" or
    // "PayGuard" with no accompanying text, jump straight to the pre-checkout
    // guard instead, simulating a payment app handing the transaction off to
    // Sahih for a validity check before paying.
    androidx.compose.runtime.LaunchedEffect(sharedPayment) {
        if (sharedPayment && sharedText == null) {
            navController.navigate(ROUTE_CHECKOUT)
        }
    }

    Scaffold(
        bottomBar = { SahihBottomBar(navController) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Box(modifier = Modifier.fillMaxSize()) {
                NavHost(navController = navController, startDestination = ROUTE_HOME) {
                    composable(ROUTE_HOME) { HomeScreen(navController) }
                    composable(ROUTE_VERIFY) {
                        VerifyScreen(
                            sharedSource = sharedText,
                            onConsumed = onSharedTextConsumed
                        )
                    }
                    composable(ROUTE_CHECKOUT) {
                        // sharedText is only meaningful here when
                        // routeForSharedText() or the PayGuard/image flag
                        // actually sent us here (i.e. it looked like or was
                        // declared to be a payment). CheckoutScreen already
                        // falls back to demo data when sharedSource is null,
                        // which covers both "opened directly" and "opened
                        // via the image-share/PayGuard flag" cases.
                        CheckoutScreen(
                            sharedSource = sharedText,
                            onConsumed = onSharedTextConsumed
                        )
                        androidx.compose.runtime.LaunchedEffect(Unit) {
                            if (sharedPayment) onSharedPaymentConsumed()
                        }
                    }
                    composable(ROUTE_CALLS) { CallShieldScreen() }
                    composable(ROUTE_RADAR) { RadarScreen(navController) }
                    composable(ROUTE_REPORT_SCAM) {
                        ReportScamScreen(
                            onBack = { navController.popBackStack() },
                            onSubmitted = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
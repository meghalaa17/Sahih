package com.sahih.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.sahih.SahihViewModel
import com.sahih.data.QrPaymentParser
import com.sahih.model.RiskLevel
import com.sahih.model.RiskSignal
import com.sahih.model.SellerVerification
import com.sahih.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun CheckoutScreen(vm: SahihViewModel, onEvidence: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isScanning by remember { mutableStateOf(false) }
    var scanStatus by remember { mutableStateOf<String?>(null) }

    val pickScreenshot = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        vm.setCheckoutScreenshot(uri)
        vm.resetCheckoutQrState()
        isScanning = true
        scanStatus = null

        scope.launch {
            try {
                val image = InputImage.fromFilePath(context, uri)

                // Step 1: try to decode a payment QR code first.
                val barcodeScanner = BarcodeScanning.getClient()
                val barcodes = barcodeScanner.process(image).await()
                val qrValue = barcodes.firstOrNull()?.rawValue

                if (qrValue != null && QrPaymentParser.looksLikePaymentQr(qrValue)) {
                    val parsed = QrPaymentParser.parse(qrValue)
                    vm.autofillCheckoutFromQr(parsed)
                    scanStatus = "Payment QR code detected and read."
                } else {
                    // Step 2: fall back to reading it as a text receipt/screenshot.
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    val result = recognizer.process(image).await()
                    vm.autofillCheckoutFromOcr(result.text)
                    scanStatus = if (result.text.isBlank())
                        "Couldn't read this image. Please fill in the details manually."
                    else
                        "Receipt text read and filled in below."
                }
            } catch (e: Exception) {
                scanStatus = "Couldn't process this image. Please fill in the details manually."
            } finally {
                isScanning = false
            }
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column(Modifier.fillMaxWidth().padding(20.dp)) {
            Text("Pre-checkout guard", color = TextMuted, fontSize = 12.sp)
            Text("Before you pay", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { pickScreenshot.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(if (isScanning) "Reading…" else "Attach a payment QR or receipt screenshot")
            }

            scanStatus?.let { status ->
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (vm.checkoutQrDetected) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Jade, modifier = Modifier.padding(end = 6.dp))
                    }
                    Text(status, color = if (vm.checkoutQrDetected) Jade else TextMuted, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                vm.sellerName,
                { vm.sellerName = it },
                Modifier.fillMaxWidth(),
                label = { Text("Merchant name (from QR)") }
            )

            if (vm.checkoutQrDetected) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "Now scan this same QR code in your own banking app, and type the recipient name YOUR bank shows below.",
                    color = TextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(vm.recipientName, { vm.recipientName = it }, Modifier.fillMaxWidth(), label = { Text("Recipient name shown by your bank app") })
            OutlinedTextField(vm.accountNumber, { vm.accountNumber = it }, Modifier.fillMaxWidth(), label = { Text("Account number (optional)") })
            OutlinedTextField(vm.amount, { vm.amount = it }, Modifier.fillMaxWidth(), label = { Text("Amount") })

            Spacer(Modifier.height(8.dp))
            Button(vm::analyseCheckout, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)) { Text("Check payment details") }
        }

        // Result: rendered as a payment security checkpoint, not a plain form result.
        vm.sellerVerification?.let { result ->
            Spacer(Modifier.height(8.dp))
            PaymentCheckpoint(
                level = vm.assessment.level,
                verification = result,
                signals = vm.assessment.signals,
                amount = vm.amount,
                onEvidence = onEvidence,
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}

/**
 * The PayGuard result screen: evidence first, then a clear verdict, then an action.
 * Pulls real risk data from [SahihViewModel.assessment] and [SahihViewModel.sellerVerification]
 * (LocalRiskEngine) rather than stubbed values.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentCheckpoint(
    level: RiskLevel,
    verification: SellerVerification,
    signals: List<RiskSignal>,
    amount: String,
    onEvidence: () -> Unit,
) {
    val tierColor = levelColor(level)
    val (headerLabel, headerIcon) = when (level) {
        RiskLevel.HIGH -> "HIGH RISK — DON'T PAY" to Icons.Default.Warning
        RiskLevel.CAUTION -> "CAUTION — VERIFY BEFORE PAYING" to Icons.Default.Warning
        RiskLevel.LOW -> "LOW RISK — STILL VERIFY INDEPENDENTLY" to Icons.Default.CheckCircle
    }

    Column(Modifier.fillMaxWidth()) {

        // 1. Full-width header band, color-coded by risk tier.
        Column(
            Modifier
                .fillMaxWidth()
                .background(tierColor.copy(alpha = 0.16f))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(headerIcon, contentDescription = null, tint = tierColor, modifier = Modifier.padding(end = 8.dp))
                Text("PAYMENT CHECK", color = TextMuted, fontSize = 11.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text(headerLabel, color = tierColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(Modifier.fillMaxWidth().padding(20.dp)) {

            // 2. Identity comparison card: account holder vs claimed business.
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(CardBg, RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Text("Identity check", color = TextMuted, fontSize = 11.sp)
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Account holder", color = TextMuted, fontSize = 11.sp)
                        Text(verification.accountHolder, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                    Icon(
                        if (verification.hasNameMismatch) Icons.Default.Close else Icons.Default.Check,
                        contentDescription = if (verification.hasNameMismatch) "Mismatch" else "Match",
                        tint = if (verification.hasNameMismatch) Coral else Jade,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .background((if (verification.hasNameMismatch) Coral else Jade).copy(alpha = 0.14f), CircleShape)
                            .padding(6.dp)
                    )
                    Column(Modifier.weight(1f)) {
                        Text("Claimed business", color = TextMuted, fontSize = 11.sp)
                        Text(verification.claimedBusiness, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
                if (amount.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text("Amount: RM$amount", color = TextPrimary, fontSize = 13.sp)
                }
                if (verification.hasNameMismatch) {
                    Spacer(Modifier.height(8.dp))
                    Text("Mismatch detected", color = Coral, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // 3. Risk factor chips — one per signal from LocalRiskEngine.assess().
            Text("Risk factors: ${signals.size} detected", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                signals.forEach { signal ->
                    val chipColor = if (signal.positive) Jade else tierColor
                    AssistChip(
                        onClick = {},
                        label = { Text(signal.title, fontSize = 12.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = chipColor.copy(alpha = 0.14f),
                            labelColor = chipColor,
                        ),
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // 4. Why this matters — one plain-language sentence.
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Surface, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Text("Why this matters", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(
                    if (verification.hasNameMismatch)
                        "The payment account does not match the claimed business identity."
                    else
                        "The recipient name and claimed business appear to match, but this is a local demo check, not a bank or SSM lookup.",
                    color = TextMuted,
                    fontSize = 12.sp,
                )
            }

            Spacer(Modifier.height(20.dp))

            // 5. Actions — primary discourages paying, secondary is an explicit override.
            Button(
                onClick = onEvidence,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Coral, contentColor = TextPrimary),
            ) { Text("Do not transfer") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                // TODO: wire to a real "proceed anyway" flow (e.g. dismiss the checkpoint,
                // log the override for the evidence pack) once that UX is decided.
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            ) { Text("I understand the risk, proceed anyway") }

            Spacer(Modifier.height(10.dp))
            Text(
                "SAHIH cannot cancel a bank transfer. Continue only after you verify independently.",
                color = TextMuted,
                fontSize = 11.sp,
            )
        }
    }
}
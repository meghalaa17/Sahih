package com.sahih.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
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
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
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

        vm.sellerVerification?.let { result ->
            Spacer(Modifier.height(16.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(if (result.hasNameMismatch) Coral.copy(.12f) else Jade.copy(.12f), RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Text(if (result.hasNameMismatch) "ACCOUNT NAME MISMATCH" else "NAMES APPEAR TO MATCH", color = if (result.hasNameMismatch) Coral else Jade, fontWeight = FontWeight.Bold)
                Text("You are paying: ${result.claimedBusiness}\nAccount holder: ${result.accountHolder}", color = TextPrimary, modifier = Modifier.padding(top = 8.dp))
                Text(result.note, color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                Text("Simulated demo verification data", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onEvidence, Modifier.fillMaxWidth()) { Text("Copy or share evidence") }
            Text("SAHIH cannot cancel a bank transfer. Continue only after you verify independently.", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
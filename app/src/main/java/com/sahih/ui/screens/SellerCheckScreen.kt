package com.sahih.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.sahih.SahihViewModel
import com.sahih.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun SellerCheckScreen(vm: SahihViewModel, onResult: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isReadingScreenshot by remember { mutableStateOf(false) }

    val pickScreenshot = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        vm.setSellerScreenshot(uri)
        isReadingScreenshot = true
        scope.launch {
            try {
                val image = InputImage.fromFilePath(context, uri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                val result = recognizer.process(image).await()
                vm.appendExtractedBioText(result.text)
            } catch (e: Exception) {
                // OCR failed silently -- buyer can still type the bio manually
            } finally {
                isReadingScreenshot = false
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Seller credibility", color = TextMuted, fontSize = 12.sp)
        Text("Check an Instagram/Telegram seller", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = vm.sellerBioText,
            onValueChange = { vm.sellerBioText = it },
            label = { Text("Paste their bio text") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = { pickScreenshot.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text(if (isReadingScreenshot) "Reading screenshot…" else "Attach a screenshot of their bio")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = vm.sellerFollowerCount,
            onValueChange = { vm.sellerFollowerCount = it },
            label = { Text("Follower count (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = vm.sellerAvgEngagement,
            onValueChange = { vm.sellerAvgEngagement = it },
            label = { Text("Average likes/views per post (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Text("What did you notice on their profile?", color = TextPrimary, fontWeight = FontWeight.SemiBold)

        CheckboxRow("Profile photo looks reused/stolen", vm.sellerPhotoReused) { vm.sellerPhotoReused = it }
        CheckboxRow("Profile photo looks AI-generated", vm.sellerPhotoAiGenerated) { vm.sellerPhotoAiGenerated = it }
        CheckboxRow("Account name was recently changed", vm.sellerNameChanged) { vm.sellerNameChanged = it }
        CheckboxRow("Old posts don't match current business", vm.sellerPostsMismatch) { vm.sellerPostsMismatch = it }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                vm.analyseSellerCredibility()
                onResult()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
        ) { Text("Check credibility") }
    }
}

@Composable
private fun CheckboxRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label, color = TextPrimary, fontSize = 13.sp)
    }
}
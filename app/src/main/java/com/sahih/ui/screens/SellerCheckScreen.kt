package com.sahih.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahih.SahihViewModel
import com.sahih.ui.theme.*

@Composable
fun SellerCheckScreen(vm: SahihViewModel) {
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
            onClick = vm::analyseSellerCredibility,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)
        ) { Text("Check credibility") }

        vm.credibilityResult?.let { result ->
            Spacer(Modifier.height(20.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(CardBg, RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Text(result.level.label.uppercase(), color = levelColor(result.level), fontWeight = FontWeight.Bold)
                Text("Score: ${result.score}", color = TextPrimary, modifier = Modifier.padding(top = 6.dp))
                Spacer(Modifier.height(10.dp))
                result.signals.forEach { signal ->
                    Text("• ${signal.title}: ${signal.detail} (+${signal.points})", color = TextMuted, fontSize = 12.sp)
                }
                if (result.signals.isEmpty()) {
                    Text("No red flags detected in this local demo check.", color = TextMuted, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun CheckboxRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label, color = TextPrimary, fontSize = 13.sp)
    }
}
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

@Composable fun CheckoutScreen(vm: SahihViewModel, onEvidence: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("Pre-checkout guard", color = TextMuted, fontSize = 12.sp); Text("Before you pay", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(vm.sellerName, { vm.sellerName = it }, Modifier.fillMaxWidth(), label = { Text("Claimed seller / merchant") })
        OutlinedTextField(vm.recipientName, { vm.recipientName = it }, Modifier.fillMaxWidth(), label = { Text("Recipient / account holder") })
        OutlinedTextField(vm.accountNumber, { vm.accountNumber = it }, Modifier.fillMaxWidth(), label = { Text("Account number (optional)") })
        OutlinedTextField(vm.amount, { vm.amount = it }, Modifier.fillMaxWidth(), label = { Text("Amount (optional)") })
        Spacer(Modifier.height(8.dp)); Button(vm::analyseCheckout, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)) { Text("Check payment details") }
        vm.sellerVerification?.let { result ->
            Spacer(Modifier.height(16.dp)); Column(Modifier.fillMaxWidth().background(if (result.hasNameMismatch) Coral.copy(.12f) else Jade.copy(.12f), RoundedCornerShape(18.dp)).padding(18.dp)) {
            Text(if (result.hasNameMismatch) "ACCOUNT NAME MISMATCH" else "NAMES APPEAR TO MATCH", color = if (result.hasNameMismatch) Coral else Jade, fontWeight = FontWeight.Bold)
            Text("You are paying: ${result.claimedBusiness}\nAccount holder: ${result.accountHolder}", color = TextPrimary, modifier = Modifier.padding(top = 8.dp))
            Text(result.note, color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp)); Text("Simulated demo verification data", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
        }
            Spacer(Modifier.height(12.dp)); OutlinedButton(onEvidence, Modifier.fillMaxWidth()) { Text("Copy or share evidence") }
            Text("SAHIH cannot cancel a bank transfer. Continue only after you verify independently.", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

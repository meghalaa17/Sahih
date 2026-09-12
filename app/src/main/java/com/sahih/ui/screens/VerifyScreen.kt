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
import com.sahih.model.RiskLevel
import com.sahih.ui.theme.*

@Composable fun VerifyScreen(vm: SahihViewModel, onEvidence: () -> Unit) {
    val assessment = vm.assessment
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("Scan and verify", color = TextMuted, fontSize = 12.sp)
        Text("Verification result", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(vm.verificationInput, vm::setVerificationInput, Modifier.fillMaxWidth(), label = { Text("Paste a message, link, or phone number") }, minLines = 3)
        Spacer(Modifier.height(8.dp)); Button(vm::analyseVerification, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)) { Text("Analyse locally") }
        Spacer(Modifier.height(16.dp))
        val color = levelColor(assessment.level)
        Column(Modifier.fillMaxWidth().background(CardBg, RoundedCornerShape(18.dp)).padding(18.dp)) {
            Text(assessment.level.label.uppercase(), color = color, fontWeight = FontWeight.Bold)
            Text(assessment.explanation, color = TextPrimary, fontSize = 14.sp, modifier = Modifier.padding(top = 6.dp))
            Text("Local demo analysis — not an SSM, bank, or PDRM lookup.", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
        }
        Spacer(Modifier.height(14.dp)); Text("Why we flagged this", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        assessment.signals.forEach { signal -> ListItem(headlineContent = { Text(signal.title, color = if (signal.positive) Jade else TextPrimary) }, supportingContent = { Text(signal.detail, color = TextMuted) }) }
        if (assessment.identifiers.urls.isNotEmpty() || assessment.identifiers.phones.isNotEmpty() || assessment.identifiers.accountNumbers.isNotEmpty()) {
            Text("Detected", color = TextPrimary, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
            assessment.identifiers.urls.forEach { Text("Link: $it", color = TextMuted, fontSize = 12.sp) }; assessment.identifiers.phones.forEach { Text("Phone: $it", color = TextMuted, fontSize = 12.sp) }; assessment.identifiers.accountNumbers.forEach { Text("Account: $it", color = TextMuted, fontSize = 12.sp) }
        }
        Spacer(Modifier.height(16.dp)); OutlinedButton(onEvidence, Modifier.fillMaxWidth()) { Text("Prepare evidence") }
    }
}
@Composable fun levelColor(level: RiskLevel) = when (level) { RiskLevel.LOW -> Jade; RiskLevel.CAUTION -> Amber; RiskLevel.HIGH -> Coral }

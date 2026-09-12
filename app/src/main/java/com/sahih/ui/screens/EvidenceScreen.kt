package com.sahih.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahih.app.model.EvidencePack
import com.sahih.app.ui.theme.*

@Composable fun EvidenceScreen(pack: EvidencePack?, onBack: () -> Unit) {
    val context = LocalContext.current
    val text = pack?.toShareText() ?: "No evidence has been prepared yet. Analyse content first."
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("Evidence pack", color = TextMuted, fontSize = 12.sp); Text("Prepare a report", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Text("This is a local demo pack. SAHIH does not submit reports automatically.", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(vertical = 12.dp))
        Text(text, color = TextPrimary, fontSize = 13.sp)
        Spacer(Modifier.height(20.dp)); Button({ copyEvidence(context, text) }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)) { Text("Copy evidence") }
        OutlinedButton({ shareEvidence(context, text) }, Modifier.fillMaxWidth(), modifier = Modifier.padding(top = 8.dp)) { Text("Share evidence") }
        TextButton(onBack, Modifier.fillMaxWidth()) { Text("Go back", color = TextMuted) }
    }
}
private fun EvidencePack.toShareText(): String = buildString {
    appendLine("SAHIH — LOCAL DEMO EVIDENCE PACK"); appendLine("Prepared: $timestamp"); appendLine("Verdict: ${assessment.level.label}"); appendLine(); appendLine("Original content:"); appendLine(assessment.originalText.ifBlank { "No content supplied" }); appendLine(); appendLine("Detected identifiers:")
    assessment.identifiers.urls.forEach { appendLine("• URL: $it") }; assessment.identifiers.phones.forEach { appendLine("• Phone: $it") }; assessment.identifiers.accountNumbers.forEach { appendLine("• Account: $it") }
    appendLine("Risk signals:"); assessment.signals.forEach { appendLine("• ${it.title}: ${it.detail}") }; sellerVerification?.let { appendLine("Seller: ${it.claimedBusiness}; recipient: ${it.accountHolder}; mismatch: ${it.hasNameMismatch}") }; appendLine(); append("This is generated from SAHIH's local hackathon-demo data, not live government or banking data.")
}
private fun copyEvidence(context: Context, text: String) { (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("SAHIH evidence", text)) }
private fun shareEvidence(context: Context, text: String) { context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text), "Share SAHIH evidence")) }

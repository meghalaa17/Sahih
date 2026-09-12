package com.sahih.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.sahih.model.DemoScamReport
import com.sahih.model.RiskLevel
import com.sahih.ui.theme.*

private val reports = listOf(DemoScamReport("kedaielektronikmurah.shop", "Fake electronics store — 22 simulated reports", "12 min ago", "Klang Valley", RiskLevel.HIGH), DemoScamReport("+60 13-889 4432", "Claims to be a bank officer — 8 simulated reports", "1 hr ago", "Johor Bahru", RiskLevel.CAUTION), DemoScamReport("maybank-secure-verify.com", "Phishing page — 41 simulated reports", "3 hr ago", "Nationwide", RiskLevel.HIGH))
@Composable fun RadarScreen(vm: SahihViewModel, onEvidence: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("Reporting radar", color = TextMuted, fontSize = 12.sp); Text("Community signals", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Text("Local simulated data for the hackathon demo — not live reports.", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(vertical = 8.dp))
        reports.forEach { report -> Card(Modifier.fillMaxWidth().padding(vertical = 5.dp).clickable { vm.updateVerificationInput(report.title); vm.analyseVerification() }, colors = CardDefaults.cardColors(containerColor = CardBg)) { Column(Modifier.padding(16.dp)) { Text(report.title, color = levelColor(report.level), fontWeight = FontWeight.SemiBold); Text(report.description, color = TextPrimary, fontSize = 13.sp); Text("${report.time} · ${report.place}", color = TextMuted, fontSize = 11.sp) } } }
        Spacer(Modifier.height(12.dp)); OutlinedButton(onEvidence, Modifier.fillMaxWidth()) { Text("Prepare current evidence") }
    }
}
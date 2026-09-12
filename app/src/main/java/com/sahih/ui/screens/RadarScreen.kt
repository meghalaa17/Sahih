package com.sahih.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahih.SahihViewModel
import com.sahih.model.DemoScamReport
import com.sahih.model.RiskLevel
import com.sahih.ui.theme.*
import java.net.URLEncoder

private val reports = listOf(
    DemoScamReport("kedaielektronikmurah.shop", "Fake electronics store — 22 simulated reports", "12 min ago", "Klang Valley", RiskLevel.HIGH),
    DemoScamReport("+60 13-889 4432", "Claims to be a bank officer — 8 simulated reports", "1 hr ago", "Johor Bahru", RiskLevel.CAUTION),
    DemoScamReport("maybank-secure-verify.com", "Phishing page — 41 simulated reports", "3 hr ago", "Nationwide", RiskLevel.HIGH)
)

@Composable
fun RadarScreen(vm: SahihViewModel, onEvidence: () -> Unit, onReportScam: () -> Unit) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("Reporting radar", color = TextMuted, fontSize = 12.sp)
        Text("Community signals", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)

        Spacer(Modifier.height(16.dp))
        Text("Search current scams", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Phone number,Business Name or Website") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (searchQuery.isNotBlank()) {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://semakmule.rmp.gov.my/"))
                        )
                    }
                },
                enabled = searchQuery.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink),
                modifier = Modifier.weight(1f)
            ) { Text("Check Semak Mule") }

            OutlinedButton(
                onClick = {
                    if (searchQuery.isNotBlank()) {
                        val encoded = URLEncoder.encode("$searchQuery scam Malaysia", "UTF-8")
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$encoded"))
                        )
                    }
                },
                enabled = searchQuery.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) { Text("Search news") }
        }
        Text(
            "Semak Mule (PDRM) checks phone numbers, bank accounts, and company names against police fraud records. \"Search news\" looks for recent news coverage of this identifier.",
            color = TextMuted,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onReportScam,
            colors = ButtonDefaults.buttonColors(containerColor = Coral, contentColor = TextPrimary),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Report a new scam") }

        if (vm.userReports.isNotEmpty()) {
            Spacer(Modifier.height(20.dp))
            Text("Your reports", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            vm.userReports.forEach { report ->
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(report.title, color = levelColor(report.level), fontWeight = FontWeight.SemiBold)
                        Text(report.description, color = TextPrimary, fontSize = 13.sp)
                        Text("${report.time} · ${report.place}", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        }

        reports.forEach { report ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { vm.updateVerificationInput(report.title); vm.analyseVerification() },
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(report.title, color = levelColor(report.level), fontWeight = FontWeight.SemiBold)
                    Text(report.description, color = TextPrimary, fontSize = 13.sp)
                    Text("${report.time} · ${report.place}", color = TextMuted, fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}
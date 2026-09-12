package com.sahih.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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

// Local-only shape for the dashboard's community-signal cards. Deliberately
// separate from com.sahih.model.DemoScamReport (which stays as-is for the
// user's own submitted reports) so this restyle can't break ReportScamScreen
// or SahihViewModel.addUserReport().
private data class IntelReport(
    val identifier: String,
    val category: String,
    val level: RiskLevel,
    val reportCount: Int,
    val trendPercent: Int, // TODO: replace with a real week-over-week computation once a backend exists
    val commonTactic: String,
    val takeaway: String,
)

private data class RadarCategory(val label: String, val severity: RiskLevel)

private val categories = listOf(
    RadarCategory("Banking", RiskLevel.HIGH),
    RadarCategory("Online Shopping", RiskLevel.HIGH),
    RadarCategory("Job Scams", RiskLevel.CAUTION),
    RadarCategory("Parcel Scams", RiskLevel.CAUTION),
    RadarCategory("Impersonation", RiskLevel.HIGH),
)

private val intelReports = listOf(
    IntelReport(
        identifier = "kedaielektronikmurah.shop",
        category = "Online Shopping",
        level = RiskLevel.HIGH,
        reportCount = 22,
        trendPercent = 18,
        commonTactic = "Fake electronics store demands full payment by transfer before anything ships.",
        takeaway = "Don't pay upfront — use COD or a marketplace with buyer protection.",
    ),
    IntelReport(
        identifier = "+60 13-889 4432",
        category = "Impersonation",
        level = RiskLevel.CAUTION,
        reportCount = 8,
        trendPercent = 4,
        commonTactic = "Caller claims to be a bank fraud officer and asks you to read back an OTP.",
        takeaway = "Banks never ask for your OTP by phone — hang up and call the official hotline yourself.",
    ),
    IntelReport(
        identifier = "maybank-secure-verify.com",
        category = "Banking",
        level = RiskLevel.HIGH,
        reportCount = 41,
        trendPercent = 27,
        commonTactic = "Phishing page mimics the Maybank2u login to harvest banking credentials.",
        takeaway = "Never log in through a link in a message — type the bank's URL in yourself.",
    ),
    IntelReport(
        identifier = "\"Parcel repacking\" job offers",
        category = "Job Scams",
        level = RiskLevel.HIGH,
        reportCount = 15,
        trendPercent = 9,
        commonTactic = "Offers pay to 'reship' packages, then ask for an upfront training or software fee.",
        takeaway = "A real employer never asks a new hire to pay them first.",
    ),
    IntelReport(
        identifier = "Courier \"duty fee\" SMS links",
        category = "Parcel Scams",
        level = RiskLevel.CAUTION,
        reportCount = 30,
        trendPercent = 22,
        commonTactic = "SMS claims a customs or duty fee is needed to release your parcel, with a payment link.",
        takeaway = "Check tracking directly in the courier's official app — never pay through an SMS link.",
    ),
)

@Composable
fun RadarScreen(vm: SahihViewModel, onEvidence: () -> Unit, onReportScam: () -> Unit) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val totalReports = intelReports.sumOf { it.reportCount } + vm.userReports.size
    val filteredIntel = if (selectedCategory == null) intelReports else intelReports.filter { it.category == selectedCategory }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("Reporting radar", color = TextMuted, fontSize = 12.sp)
        Text("Intelligence dashboard", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)

        Spacer(Modifier.height(16.dp))
        StatBlock(totalReports = totalReports)

        Spacer(Modifier.height(20.dp))
        Text("Search current scams", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Phone number, business name or website") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (searchQuery.isNotBlank()) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://semakmule.rmp.gov.my/")))
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
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$encoded")))
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
            vm.userReports.forEach { report -> UserReportCard(report) }
        }

        Spacer(Modifier.height(24.dp))
        Text("Community signals", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))

        CategoryFilterRow(
            selected = selectedCategory,
            onSelect = { selectedCategory = if (selectedCategory == it) null else it },
        )

        Spacer(Modifier.height(14.dp))
        filteredIntel.forEach { report ->
            IntelReportCard(
                report = report,
                onPrepareEvidence = {
                    vm.updateVerificationInput(report.identifier)
                    vm.analyseVerification()
                    onEvidence()
                },
            )
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun StatBlock(totalReports: Int) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(CardBg, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Gold, modifier = Modifier.padding(end = 6.dp))
            // TODO: swap for the user's actual region once location-aware
            // reporting is wired up; hardcoded to nationwide for the demo.
            Text("Malaysia · nationwide", color = TextMuted, fontSize = 12.sp)
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Icon(Icons.Default.Insights, contentDescription = null, tint = TextPrimary, modifier = Modifier.padding(end = 8.dp))
            Text("$totalReports", color = TextPrimary, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(8.dp))
            Text("reports tracked", color = TextMuted, fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp))
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Coral, modifier = Modifier.padding(end = 4.dp))
            // TODO: stub trend — compute from real report timestamps once a backend exists.
            Text("↑ 12% vs last week", color = Coral, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryFilterRow(selected: String?, onSelect: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { category ->
            val color = levelColor(category.severity)
            val isSelected = selected == category.label
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(category.label) },
                label = { Text(category.label) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = CardBg,
                    labelColor = color,
                    selectedContainerColor = color,
                    selectedLabelColor = Ink,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = color,
                    selectedBorderColor = color,
                ),
            )
        }
    }
}

@Composable
private fun IntelReportCard(report: IntelReport, onPrepareEvidence: () -> Unit) {
    val tierColor = levelColor(report.level)
    Column(
        Modifier
            .fillMaxWidth()
            .background(CardBg, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(10.dp)
                    .background(tierColor, CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Text(report.category.uppercase(), color = tierColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Icon(
                if (report.trendPercent >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                contentDescription = null,
                tint = tierColor,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(2.dp))
            Text("${report.trendPercent}% this week", color = tierColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(8.dp))
        Text(report.identifier, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        Spacer(Modifier.height(4.dp))
        Text("${report.reportCount} reports", color = TextMuted, fontSize = 12.sp)

        Spacer(Modifier.height(10.dp))
        Text(report.commonTactic, color = TextPrimary, fontSize = 13.sp, lineHeight = 18.sp)

        Spacer(Modifier.height(8.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .background(Surface, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Text(report.takeaway, color = TextMuted, fontSize = 12.sp, lineHeight = 16.sp)
        }

        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onPrepareEvidence, modifier = Modifier.fillMaxWidth()) {
            Text("Prepare evidence pack")
        }
    }
}

@Composable
private fun UserReportCard(report: DemoScamReport) {
    val tierColor = levelColor(report.level)
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .background(CardBg, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Box(
            Modifier
                .size(10.dp)
                .padding(top = 5.dp)
                .background(tierColor, CircleShape)
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(report.title, color = tierColor, fontWeight = FontWeight.SemiBold)
            Text(report.description, color = TextPrimary, fontSize = 13.sp)
            Text("${report.time} · ${report.place}", color = TextMuted, fontSize = 11.sp)
        }
    }
}
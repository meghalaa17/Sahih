package com.sahih.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sahih.model.ScamReport
import com.sahih.ui.theme.Amber
import com.sahih.ui.theme.CardBg
import com.sahih.ui.theme.Coral
import com.sahih.ui.theme.Gold
import com.sahih.ui.theme.Hairline
import com.sahih.ui.theme.Ink
import com.sahih.ui.theme.TextMuted
import com.sahih.ui.theme.TextPrimary

private val reports = listOf(
    ScamReport("kedaielektronikmurah.shop", "Fake electronics store - 22 reports", "12 min ago", "Klang Valley", Coral),
    ScamReport("+60 13-889 4432", "Claims to be bank officer - 8 reports", "1 hr ago", "Johor Bahru", Amber),
    ScamReport("maybank-secure-verify.com", "Phishing page - 41 reports", "3 hr ago", "Nationwide", Coral),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadarScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredReports = remember(searchQuery) {
        if (searchQuery.isBlank()) reports
        else reports.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    Column(Modifier
        .fillMaxWidth()
        .padding(20.dp)) {

        Text("Reporting radar", color = TextMuted, fontSize = 12.sp)
        Text("Evidence pack", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(14.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search a shop, number, or link", color = TextMuted) },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = TextMuted)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = Gold,
                unfocusedBorderColor = Hairline,
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg,
                cursorColor = Gold,
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))

        OutlinedButton(
            onClick = { navController.navigate("report_scam") },
            border = androidx.compose.foundation.BorderStroke(1.dp, Coral),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Coral),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.ReportProblem, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Report a scam", fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(16.dp))

        Spacer(Modifier.height(18.dp))
        Text("Reports near you", color = TextMuted, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))

        if (filteredReports.isEmpty()) {
            Text(
                "No reports match \"$searchQuery\"",
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }

        filteredReports.forEach { r ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .border(0.dp, Hairline)
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val icon: ImageVector = when {
                    r.title.startsWith("+60") -> Icons.Default.Phone
                    r.title.contains("shop") -> Icons.Default.Storefront
                    else -> Icons.Default.Link
                }
                Box(
                    Modifier.size(34.dp).background(r.color.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = r.color, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(r.title, color = TextPrimary, fontSize = 13.sp)
                    Text(r.meta, color = TextMuted, fontSize = 11.sp)
                    Row(Modifier.padding(top = 4.dp)) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = TextMuted, modifier = Modifier.size(11.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(r.time, color = TextMuted, fontSize = 10.sp)
                        Spacer(Modifier.width(12.dp))
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(11.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(r.place, color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun EvidenceLine(icon: ImageVector, label: String) {
    Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, color = TextPrimary, fontSize = 13.sp)
    }
}
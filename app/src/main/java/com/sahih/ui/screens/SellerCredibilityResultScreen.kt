package com.sahih.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahih.model.SellerCredibilityResult
import com.sahih.ui.theme.*

@Composable
fun SellerCredibilityResultScreen(result: SellerCredibilityResult?, onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(Modifier.width(4.dp))
            Text("Credibility result", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(16.dp))

        if (result == null) {
            Text("No check has been run yet.", color = TextMuted, fontSize = 13.sp)
            return@Column
        }

        Column(
            Modifier
                .fillMaxWidth()
                .background(CardBg, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Text(result.level.label.uppercase(), color = levelColor(result.level), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Score: ${result.score}", color = TextPrimary, fontSize = 15.sp, modifier = Modifier.padding(top = 6.dp))
            Text(
                "Local demo analysis — not a live Instagram, Telegram, SSM, or PDRM lookup.",
                color = TextMuted,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(16.dp))
        Text("Why we flagged this", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))

        if (result.signals.isEmpty()) {
            Text("No red flags detected in this local demo check.", color = TextMuted, fontSize = 13.sp)
        } else {
            result.signals.forEach { signal ->
                ListItem(
                    headlineContent = { Text(signal.title, color = TextPrimary) },
                    supportingContent = { Text(signal.detail, color = TextMuted) },
                    trailingContent = { Text("+${signal.points}", color = Gold, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        OutlinedButton(onBack, Modifier.fillMaxWidth()) { Text("Back to seller check") }
    }
}
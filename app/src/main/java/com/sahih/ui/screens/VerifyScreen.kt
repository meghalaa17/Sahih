package com.sahih.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahih.model.VerdictTier
import com.sahih.ui.theme.CardBg
import com.sahih.ui.theme.Gold
import com.sahih.ui.theme.Hairline
import com.sahih.ui.theme.Ink
import com.sahih.ui.theme.TextMuted
import com.sahih.ui.theme.TextPrimary

/**
 * sharedSource: raw text handed in by Android's share sheet (the URL,
 * handle, or message the user shared from another app). In a full build
 * this would be parsed for SSM numbers, phone numbers and bank account
 * digits, then run through the triple-cross check. Here it's shown as
 * a provenance tag so the "zero-friction share sheet" flow is visible.
 */
@Composable
fun VerifyScreen(sharedSource: String? = null, onConsumed: () -> Unit = {}) {
    var tier by remember { mutableStateOf(VerdictTier.SCAM) }

    LaunchedEffect(sharedSource) {
        if (sharedSource != null) onConsumed()
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)) {

        Text(
            if (sharedSource != null) "Shared to Sahih" else "Scan and verify",
            color = TextMuted,
            fontSize = 12.sp
        )
        Text("Verification result", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VerdictTier.entries.forEach { t ->
                val selected = t == tier
                Text(
                    text = t.label,
                    color = if (selected) t.color else TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) t.color.copy(alpha = 0.15f) else CardBg)
                        .border(1.dp, if (selected) t.color else Hairline, RoundedCornerShape(10.dp))
                        .clickable { tier = t }
                        .padding(vertical = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg, RoundedCornerShape(20.dp))
                .border(1.dp, tier.color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(tier.color.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = tier.color)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(tier.label, color = tier.color, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text(tier.subject, color = TextMuted, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(14.dp))

            tier.rows.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, color = TextMuted, fontSize = 13.sp)
                    Text(value, color = tier.color, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        Text(tier.note, color = TextMuted, fontSize = 13.sp, lineHeight = 20.sp)
        Spacer(Modifier.height(18.dp))

        if (tier == VerdictTier.SCAM) {
            Button(
                onClick = { /* TODO: navigate to evidence pack generator */ },
                colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.FactCheck, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Generate evidence pack", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = { },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Block, contentDescription = null, tint = TextPrimary)
                Spacer(Modifier.width(8.dp))
                Text("Block and dismiss", color = TextPrimary)
            }
        } else {
            OutlinedButton(
                onClick = { },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done", color = TextPrimary)
            }
        }
    }
}

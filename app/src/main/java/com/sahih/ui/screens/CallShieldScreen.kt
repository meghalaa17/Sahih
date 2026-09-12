package com.sahih.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahih.ui.theme.Amber
import com.sahih.ui.theme.CardBg
import com.sahih.ui.theme.Coral
import com.sahih.ui.theme.Ink
import com.sahih.ui.theme.Jade
import com.sahih.ui.theme.TextMuted
import com.sahih.ui.theme.TextPrimary

// This UI is what Sahih shows over an incoming call once the app is set
// as the system's call-screening service (see SahihCallScreeningService).
@Composable
fun CallShieldScreen() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp, horizontal = 20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text("Incoming call - running in background", color = TextMuted, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Text("+60 11-2345 6789", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            Text(
                "  High-report number  ",
                color = Coral,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .background(Coral.copy(alpha = 0.15f), RoundedCornerShape(999.dp))
                    .padding(vertical = 4.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Column(
            Modifier
                .fillMaxWidth()
                .background(CardBg, RoundedCornerShape(18.dp))
                .border(1.dp, Amber.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Row {
                Icon(Icons.Default.Info, contentDescription = null, tint = Amber)
                Spacer(Modifier.width(8.dp))
                Text("Before you answer", color = Amber, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "This number was registered 6 days ago and reported 9 times. If they " +
                    "claim to be family, ask for your household code word before sending money.",
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
        }

        Spacer(Modifier.weight(1f))

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.size(54.dp).background(Coral, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "Decline", tint = TextPrimary)
                }
                Spacer(Modifier.height(6.dp))
                Text("Decline", color = TextMuted, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier.size(54.dp).background(Jade, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Answer", tint = Ink)
                }
                Spacer(Modifier.height(6.dp))
                Text("Answer carefully", color = TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

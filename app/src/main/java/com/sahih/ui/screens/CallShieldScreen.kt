package com.sahih.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahih.data.LocalRiskEngine
import com.sahih.model.RiskLevel
import com.sahih.ui.theme.*

@Composable fun CallShieldScreen() {
    var number by remember { mutableStateOf("+60 13-889 4432") }
    var result by remember { mutableStateOf(LocalRiskEngine.assess(number)) }
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Call Shield", color = TextMuted, fontSize = 12.sp); Text("Test a call number", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Text("Prototype demo. To screen real calls, Android requires you to choose SAHIH as the caller-ID and spam app.", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(vertical = 12.dp))
        OutlinedTextField(number, { number = it }, Modifier.fillMaxWidth(), label = { Text("Malaysian phone number") })
        Spacer(Modifier.height(8.dp)); Button({ result = LocalRiskEngine.assess(number) }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink)) { Text("Run local call check") }
        Spacer(Modifier.height(16.dp)); Column(Modifier.fillMaxWidth().background(CardBg, RoundedCornerShape(18.dp)).padding(18.dp)) {
        Text(result.level.label.uppercase(), color = levelColor(result.level), fontWeight = FontWeight.Bold); Text(result.explanation, color = TextPrimary, modifier = Modifier.padding(top = 6.dp)); Text("Uses simulated local risk data; no live caller-ID lookup is claimed.", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 8.dp))
    }
        if (result.level == RiskLevel.HIGH) Text("Pause before answering. Never share OTPs or transfer money based only on an unexpected call.", color = Coral, fontSize = 13.sp, modifier = Modifier.padding(top = 14.dp))
    }
}

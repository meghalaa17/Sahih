package com.sahih.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahih.SahihViewModel
import com.sahih.ui.theme.CardBg
import com.sahih.ui.theme.Coral
import com.sahih.ui.theme.Gold
import com.sahih.ui.theme.Hairline
import com.sahih.ui.theme.Ink
import com.sahih.ui.theme.Jade
import com.sahih.ui.theme.TextMuted
import com.sahih.ui.theme.TextPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private data class ScamTypeOption(val label: String, val icon: ImageVector)

private val scamTypeOptions = listOf(
    ScamTypeOption("Fake shop", Icons.Default.Storefront),
    ScamTypeOption("Phishing link", Icons.Default.Link),
    ScamTypeOption("Impersonation call", Icons.Default.Phone),
    ScamTypeOption("Investment scam", Icons.Default.CreditCard),
    ScamTypeOption("Other", Icons.Default.MoreHoriz),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScamScreen(vm: SahihViewModel, onBack: () -> Unit, onSubmitted: () -> Unit) {
    val context = LocalContext.current

    var identifier by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var notes by remember { mutableStateOf("") }
    var evidenceCount by remember { mutableStateOf(0) }

    val dateFormat = remember { SimpleDateFormat("d MMM yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    var dateText by remember { mutableStateOf(dateFormat.format(Calendar.getInstance().time)) }
    var timeText by remember { mutableStateOf(timeFormat.format(Calendar.getInstance().time)) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedBorderColor = Gold,
        unfocusedBorderColor = Hairline,
        focusedContainerColor = CardBg,
        unfocusedContainerColor = CardBg,
        cursorColor = Gold,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(Modifier.width(4.dp))
            Text("Report a scam", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(20.dp))

        Text("Business, number or link", color = TextMuted, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = identifier,
            onValueChange = { identifier = it },
            placeholder = { Text("e.g. butikcantikmurah.shop or +60 13-889 4432", color = TextMuted) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors
        )

        Spacer(Modifier.height(18.dp))

        Text("What kind of scam?", color = TextMuted, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(scamTypeOptions) { option ->
                FilterChip(
                    selected = selectedType == option.label,
                    onClick = { selectedType = option.label },
                    label = { Text(option.label) },
                    leadingIcon = {
                        Icon(option.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CardBg,
                        labelColor = TextMuted,
                        iconColor = TextMuted,
                        selectedContainerColor = Gold,
                        selectedLabelColor = Ink,
                        selectedLeadingIconColor = Ink,
                    )
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Date", color = TextMuted, fontSize = 12.sp)
                    TextButton(onClick = { dateText = dateFormat.format(Calendar.getInstance().time) }) {
                        Text("Today", color = Gold, fontSize = 11.sp)
                    }
                }
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    placeholder = { Text("DD/MM/YYYY", color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }
            Column(Modifier.weight(1f)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Time", color = TextMuted, fontSize = 12.sp)
                    TextButton(onClick = { timeText = timeFormat.format(Calendar.getInstance().time) }) {
                        Text("Now", color = Gold, fontSize = 11.sp)
                    }
                }
                OutlinedTextField(
                    value = timeText,
                    onValueChange = { timeText = it },
                    placeholder = { Text("HH:MM", color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        Text("Evidence", color = TextMuted, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Hairline, RoundedCornerShape(14.dp))
                .clickable {
                    // TODO: launch a real image/screenshot picker; mocked for the prototype
                    evidenceCount += 1
                }
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                if (evidenceCount > 0) Icons.Default.CheckCircle else Icons.Default.CameraAlt,
                contentDescription = null,
                tint = if (evidenceCount > 0) Jade else TextMuted
            )
            Spacer(Modifier.height(6.dp))
            Text(
                if (evidenceCount > 0)
                    "$evidenceCount screenshot${if (evidenceCount > 1) "s" else ""} attached \u2013 tap to add more"
                else
                    "Tap to attach screenshots or files",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(18.dp))

        Text("Additional details (optional)", color = TextMuted, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            placeholder = { Text("Anything else worth mentioning?", color = TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            colors = fieldColors
        )

        Spacer(Modifier.height(24.dp))

        Text("Report to the authorities", color = TextMuted, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:997")))
            },
            colors = ButtonDefaults.buttonColors(containerColor = Coral, contentColor = TextPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Call NSRC hotline (997)", fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://aduan.mcmc.gov.my/")))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Report online via MCMC Aduan Portal")
        }

        Spacer(Modifier.height(6.dp))
        Text(
            "NSRC (997) is a phone hotline, best used within 24 hours of a financial loss so banks can act quickly. MCMC's portal is for reporting scam websites, ads, and online content.",
            color = TextMuted,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                vm.addUserReport(
                    identifier = identifier,
                    scamType = selectedType ?: "Other",
                    dateText = dateText,
                    timeText = timeText,
                )
                onSubmitted()
            },
            enabled = identifier.isNotBlank() && selectedType != null,
            colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Ink),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save to my local report log", fontWeight = FontWeight.SemiBold)
        }
        Text(
            "This saves your report details in-app for your own records. It does not submit anywhere automatically.",
            color = TextMuted,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(Modifier.height(12.dp))
    }
}
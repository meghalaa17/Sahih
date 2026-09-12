package com.sahih.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sahih.enrichment.BankCheckResult
import com.sahih.enrichment.MockBankAccountCheckService
import com.sahih.ui.theme.CardBg
import com.sahih.ui.theme.Coral
import com.sahih.ui.theme.Jade
import com.sahih.ui.theme.TextMuted
import com.sahih.ui.theme.TextPrimary

// Malaysia's National Scam Response Centre hotline
private const val SCAM_HOTLINE = "997"

/** What we could pull out of the shared payment screenshot/message text. */
private data class ParsedPayment(
    val transferVia: String,
    val amount: String,
    val recipientName: String,
    val bankAccountNumber: String?,
    val claimedBusinessName: String?,
)

/**
 * sharedSource: raw text handed in by the share sheet when a user shares
 * a payment confirmation screen or a seller's payment message. When
 * null, the screen falls back to demo data so it still works from a
 * direct tap (Home tile / bottom nav) rather than only from a share.
 *
 * NOTE: this composable must never early-return out of the middle of a
 * Column {} lambda (e.g. via `return@Column`) -- doing so corrupts
 * Compose's group-nesting stack and crashes with
 * "IndexOutOfBoundsException: Index -1 out of bounds for length 0" on
 * recomposition. All branching below uses if/else instead.
 */
@Composable
fun CheckoutScreen(sharedSource: String? = null, onConsumed: () -> Unit = {}) {
    val bankCheckService = remember { MockBankAccountCheckService() }

    var checking by remember { mutableStateOf(true) }
    var payment by remember { mutableStateOf<ParsedPayment?>(null) }
    var result by remember { mutableStateOf<BankCheckResult?>(null) }
    var hasPaid by remember { mutableStateOf(false) }

    LaunchedEffect(sharedSource) {
        checking = true
        hasPaid = false

        val parsed = sharedSource?.let(::parsePaymentText) ?: ParsedPayment(
            transferVia = "DuitNow",
            amount = "RM 249.00",
            recipientName = "Ahmad bin Zulkifli",
            bankAccountNumber = null,
            claimedBusinessName = "Nadia Enterprise",
        )
        payment = parsed

        result = bankCheckService.check(
            accountHolderName = parsed.recipientName,
            bankAccountNumber = parsed.bankAccountNumber,
            claimedBusinessName = parsed.claimedBusinessName,
        )
        checking = false
        if (sharedSource != null) onConsumed()
    }

    Column(Modifier
        .fillMaxWidth()
        .padding(20.dp)) {

        Text("Pre-checkout guard", color = TextMuted, fontSize = 12.sp)
        Text("Before you pay", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(16.dp))

        // Single if/else chain instead of early returns -- keeps every
        // branch inside the same Column composition, which is required
        // for Compose's slot table to stay consistent across recompositions.
        if (checking || payment == null || result == null) {
            CheckingState()
        } else {
            val p = payment!!
            val r = result!!

            Column(
                Modifier
                    .fillMaxWidth()
                    .background(CardBg, RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                DetailRow("Transfer via", p.transferVia)
                DetailRow("Amount", p.amount)
                DetailRow("Recipient", p.recipientName, last = true)
            }

            Spacer(Modifier.height(14.dp))

            if (!hasPaid) {
                if (r.legit) {
                    VerdictCard(
                        icon = Icons.Default.Shield,
                        color = Jade,
                        title = "Account looks legitimate",
                        body = r.reason,
                    )
                    Spacer(Modifier.height(18.dp))
                    Button(
                        onClick = { hasPaid = true },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Proceed to pay")
                    }
                } else {
                    VerdictCard(
                        icon = Icons.Default.Warning,
                        color = Coral,
                        title = "Mismatch detected",
                        body = r.reason,
                    )
                    Spacer(Modifier.height(18.dp))
                    OutlinedButton(
                        onClick = { /* cancel transfer */ },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel transfer", color = TextPrimary)
                    }
                    Spacer(Modifier.height(6.dp))
                    TextButton(
                        onClick = { hasPaid = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("I Already Paid", color = TextMuted, fontSize = 12.sp)
                    }
                }
            } else if (r.legit) {
                VerdictCard(
                    icon = Icons.Default.CheckCircle,
                    color = Jade,
                    title = "Payment sent",
                    body = "This account passed Sahih's check, so no further action is needed. Keep your receipt as usual.",
                )
            } else {
                // Paid anyway despite the warning -- this is the scam-recovery path.
                PostPaymentScamRecovery()
            }
        }
    }
}

@Composable
private fun CheckingState() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(CardBg, RoundedCornerShape(18.dp))
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Jade)
            Spacer(Modifier.height(12.dp))
            Text(
                "Checking this account against SSM & Semak Mule...",
                color = TextMuted, fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun VerdictCard(icon: ImageVector, color: Color, title: String, body: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Row {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(Modifier.width(8.dp))
            Text(title, color = color, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(body, color = TextPrimary, fontSize = 13.sp, lineHeight = 20.sp)
    }
}

@Composable
private fun DetailRow(label: String, value: String, last: Boolean = false) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextMuted, fontSize = 13.sp)
        Text(value, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

/**
 * Best-effort parsing of whatever text came through the share sheet.
 * Real bank/e-wallet apps have wildly different share formats, so treat
 * this like the OCR parser elsewhere in the app: a first pass to tune
 * against real examples, not a finished parser.
 */
private fun parsePaymentText(text: String): ParsedPayment {
    val amount = Regex("RM\\s?[\\d,]+(?:\\.\\d{2})?", RegexOption.IGNORE_CASE)
        .find(text)?.value ?: "RM --"

    val accountNumber = Regex("\\b\\d{10,16}\\b").find(text)?.value

    val transferVia = when {
        text.contains("duitnow", true) -> "DuitNow"
        text.contains("ewallet", true) || text.contains("e-wallet", true) -> "E-wallet"
        else -> "Bank transfer"
    }

    val recipientName = Regex("(?:to|recipient|pay(?:ee)?)[:\\s]+([A-Za-z][A-Za-z .'\\-]{2,40})", RegexOption.IGNORE_CASE)
        .find(text)?.groupValues?.get(1)?.trim() ?: "Unknown recipient"

    val claimedBusiness = Regex("([A-Za-z][A-Za-z .'\\-]{2,40}(?:Enterprise|Trading|Sdn Bhd|Boutique|Store))", RegexOption.IGNORE_CASE)
        .find(text)?.groupValues?.get(1)?.trim()

    return ParsedPayment(
        transferVia = transferVia,
        amount = amount,
        recipientName = recipientName,
        bankAccountNumber = accountNumber,
        claimedBusinessName = claimedBusiness,
    )
}

/**
 * Shown after the user has already sent the payment to a flagged
 * account. Gives an immediate path to the national scam hotline plus
 * the standard bank/police recovery steps.
 */
@Composable
private fun PostPaymentScamRecovery() {
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .background(Coral.copy(alpha = 0.08f), RoundedCornerShape(18.dp))
            .border(1.dp, Coral.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Row {
            Icon(Icons.Default.Warning, contentDescription = null, tint = Coral)
            Spacer(Modifier.width(8.dp))
            Text("Already paid, think it's a scam?", color = Coral, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Act fast — the first hour matters most for freezing a fraudulent transfer. " +
                    "Call the National Scam Response Centre now.",
            color = TextPrimary,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )

        Spacer(Modifier.height(14.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$SCAM_HOTLINE"))
                context.startActivity(intent)
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Call, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Call 997 now")
        }

        Spacer(Modifier.height(18.dp))
        Text("What to do next", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Spacer(Modifier.height(10.dp))

        RecoveryStep(1, "Call 997 (NSRC)", "Report immediately — they can request an urgent freeze with the receiving bank.")
        RecoveryStep(2, "Contact your bank", "Call your bank's fraud hotline directly and ask them to flag the transaction too.")
        RecoveryStep(3, "Lodge a police report", "File at any police station, or via the PDRM e-reporting portal, within 24 hours.")
        RecoveryStep(4, "Keep your evidence", "Save the chat, payment receipt, and recipient details — you'll need them for both reports.")
    }
}

@Composable
private fun RecoveryStep(number: Int, title: String, description: String) {
    Row(Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TextMuted)
        Spacer(Modifier.width(10.dp))
        Column {
            Text("$number. $title", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(description, color = TextMuted, fontSize = 12.sp, lineHeight = 18.sp)
        }
    }
}
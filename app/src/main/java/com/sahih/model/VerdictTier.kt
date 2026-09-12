package com.sahih.model

import androidx.compose.ui.graphics.Color
import com.sahih.ui.theme.Amber
import com.sahih.ui.theme.Coral
import com.sahih.ui.theme.Jade

// Mirrors the three-tier "why" transparency model from the design spec:
// Verified / Caution / Scam, each backed by SSM + Semak Mule (PDRM) +
// community-report signals rather than a single opaque badge.
enum class VerdictTier(
    val label: String,
    val color: Color,
    val subject: String,
    val rows: List<Pair<String, String>>,
    val note: String,
) {
    VERIFIED(
        label = "Verified",
        color = Jade,
        subject = "Maybank2u.com.my",
        rows = listOf(
            "SSM registration" to "Match confirmed",
            "Semak Mule (PDRM)" to "0 reports",
            "Domain age" to "Verified, over 2 years",
        ),
        note = "SSM match confirmed, no fraud reports in Semak Mule, and a domain old enough to trust.",
    ),
    CAUTION(
        label = "Caution",
        color = Amber,
        subject = "Nadia's Baju Kurung Boutique",
        rows = listOf(
            "SSM registration" to "Legitimate business",
            "Community flags" to "3 this week",
            "Flag reason" to "Unfulfilled orders",
        ),
        note = "The business is legitimately registered, but 3 buyers flagged undelivered orders in the past week. Proceed carefully.",
    ),
    SCAM(
        label = "Scam",
        color = Coral,
        subject = "butikcantikmurah.shop",
        rows = listOf(
            "SSM registration" to "Not found",
            "Semak Mule (PDRM)" to "Flagged, 24 cases",
            "Community reports" to "14 in 48h",
        ),
        note = "No matching SSM registration, and the bank account is flagged in PDRM's Semak Mule with 24 open cases.",
    ),
}

data class ScamReport(
    val title: String,
    val meta: String,
    val time: String,
    val place: String,
    val color: Color,
)

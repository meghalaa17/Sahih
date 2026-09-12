package com.sahih.data

import com.sahih.model.*

object SellerCredibilityEngine {

    // These are patterns we search for inside the bio text.
    private val contactPatterns = listOf(
        Regex("""ssm\s*[:#]?\s*\d+""", RegexOption.IGNORE_CASE),
        Regex("""https?://[^\s]+"""),
        Regex("""wa\.me/\d+"""),
    )
    private val paymentOnlyPhrases = listOf(
        "transfer only", "no cod", "bayaran dahulu", "full payment first"
    )

    fun assess(input: SellerProfileInput): SellerCredibilityResult {
        val signals = mutableListOf<CredibilitySignal>()
        val lowerBio = input.bioText.lowercase()

        if (input.photoLooksReused == true)
            signals += CredibilitySignal("Profile photo found elsewhere", "Matched an unrelated account or site.", 30)

        if (input.photoLooksAiGenerated == true)
            signals += CredibilitySignal("Profile photo appears AI-generated", "Detected via image heuristics.", 15)

        val hasContactInfo = contactPatterns.any { it.containsMatchIn(input.bioText) }
        if (!hasContactInfo)
            signals += CredibilitySignal("No verifiable contact info", "No SSM number, website, or WhatsApp link found.", 5)

        if (paymentOnlyPhrases.any { lowerBio.contains(it) })
            signals += CredibilitySignal("Payment-only-via-transfer language", "Bio states transfer-only.", 10)

        if (input.nameRecentlyChanged == true)
            signals += CredibilitySignal("Recently changed account name", "Buyer reported a recent rename.", 15)

        if (input.oldPostsMismatchBusiness == true)
            signals += CredibilitySignal("Old posts don't match business", "Buyer reported inconsistent post history.", 15)

        if (input.followerCount != null && input.avgLikesOrViews != null && input.followerCount > 0) {
            val ratio = input.avgLikesOrViews.toDouble() / input.followerCount
            if (ratio < 0.005)
                signals += CredibilitySignal("Engagement disproportionately low", "Under 0.5% engagement — possible bot followers.", 10)
        }

        val score = signals.sumOf { it.points }
        val level = when {
            score >= 40 -> RiskLevel.HIGH
            score >= 15 -> RiskLevel.CAUTION
            else -> RiskLevel.LOW
        }
        return SellerCredibilityResult(score, level, signals)
    }
}
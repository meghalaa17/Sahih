package com.sahih.data

import com.sahih.model.*
import com.sahih.model.ExtractedIdentifiers
import com.sahih.model.RiskAssessment
import com.sahih.model.RiskLevel
import com.sahih.model.RiskSignal
import com.sahih.model.SellerVerification
import java.util.Locale
/** Deterministic, offline demo analysis. It never represents government or bank data. */
object LocalRiskEngine {
    private val urlPattern = Regex("""https?://[^\s<>()]+""", RegexOption.IGNORE_CASE)
    private val phonePattern = Regex("""(?<!\d)(?:\+?60|0)1[0-9][ -]?\d{3,4}[ -]?\d{4}(?!\d)""")
    private val accountPattern = Regex("""(?<!\d)\d(?:[ -]?\d){8,15}(?!\d)""")
    private val highRiskPhones = setOf("+601123456789", "+60138894432")
    private val riskyDomainParts = listOf("butikcantikmurah", "secure-verify", "claim-reward", "maybank-secure")
    private val urgentWords = listOf("urgent", "immediately", "blocked", "verify now", "segera", "tindakan", "akan ditutup")
    private val financialWords = listOf("bank", "maybank", "duitnow", "transfer", "otp", "account")

    fun extract(text: String): ExtractedIdentifiers {
        val urls = urlPattern.findAll(text).map { it.value.trimEnd('.', ',', '!', '?') }.distinct().toList()
        val phones = phonePattern.findAll(text).map { normalizePhone(it.value) }.filter { it != null }.map { it!! }.distinct().toList()
        val accounts = accountPattern.findAll(text).map { it.value.filter(Char::isDigit) }
            .filter { it.length in 9..16 && it !in phones.map { phone -> phone.filter(Char::isDigit).removePrefix("60") } }
            .distinct().toList()
        val seller = Regex("""(?:seller|merchant|kedai|shop)\s*[:=-]?\s*([A-Za-z][A-Za-z '&.-]{2,40})""", RegexOption.IGNORE_CASE)
            .find(text)?.groupValues?.getOrNull(1)?.trim()
        return ExtractedIdentifiers(urls, phones, accounts, seller)
    }

    fun assess(text: String, seller: String? = null, accountHolder: String? = null, accountNumber: String? = null): RiskAssessment {
        val clean = text.trim()
        val identifiers = extract(clean).copy(
            sellerName = seller?.trim()?.takeIf { it.isNotEmpty() } ?: extract(clean).sellerName,
            accountNumbers = listOfNotNull(accountNumber?.filter(Char::isDigit)?.takeIf { it.isNotEmpty() }).ifEmpty { extract(clean).accountNumbers },
        )
        val lower = clean.lowercase(Locale.ROOT)
        val signals = mutableListOf<RiskSignal>()
        if (clean.isBlank()) signals += RiskSignal("No content to analyse", "Paste a message, link, phone number, or payment detail to start.", 0)
        identifiers.urls.forEach { url ->
            if (riskyDomainParts.any { url.lowercase().contains(it) }) signals += RiskSignal("Suspicious demo link pattern", "$url matches a known simulated scam pattern.", 30)
            else if (!url.contains(".com.my") && !url.contains("maybank2u.com.my")) signals += RiskSignal("Unverified link", "Check this link independently before entering details.", 10)
        }
        if (urgentWords.any { lower.contains(it) }) signals += RiskSignal("Urgency or threat language", "The message pressures you to act quickly.", 10)
        if (financialWords.any { lower.contains(it) }) signals += RiskSignal("Financial-account request", "The content mentions payment, banking, or account access.", 15)
        identifiers.phones.filter { it in highRiskPhones }.forEach { signals += RiskSignal("High-report demo number", "$it is in SAHIH's simulated local report set.", 20) }
        val mismatch = seller != null && accountHolder != null && !namesLikelyMatch(seller, accountHolder)
        if (mismatch) signals += RiskSignal("Account-name mismatch", "The claimed seller and recipient name do not appear to match.", 25)
        if (signals.isEmpty() && clean.isNotBlank()) signals += RiskSignal("No strong demo risk pattern", "This local demo did not find a high-risk pattern. Verify independently before paying.", 0, true)
        val score = signals.sumOf { it.points }
        val level = when { score >= 40 -> RiskLevel.HIGH; score >= 15 -> RiskLevel.CAUTION; else -> RiskLevel.LOW }
        val explanation = when (level) {
            RiskLevel.HIGH -> "Several local demo risk signals were detected. Pause and verify through an official channel."
            RiskLevel.CAUTION -> "Some risk signals need a closer check before you continue."
            RiskLevel.LOW -> "No strong local-demo scam pattern was found. This is not a guarantee of safety."
        }
        return RiskAssessment(clean, identifiers, level, signals, explanation)
    }

    fun verifySeller(seller: String, accountHolder: String, accountNumber: String): SellerVerification {
        val mismatch = seller.isNotBlank() && accountHolder.isNotBlank() && !namesLikelyMatch(seller, accountHolder)
        val reports = if (seller.lowercase().contains("nadia")) 3 else if (mismatch) 2 else 0
        return SellerVerification(seller.ifBlank { "Not provided" }, accountHolder.ifBlank { "Not provided" }, accountNumber, mismatch, reports,
            if (mismatch) "Simulated demo data indicates the recipient name does not match the claimed seller." else "Simulated demo data found no obvious name mismatch.")
    }

    fun normalizePhone(raw: String): String? {
        val digits = raw.filter(Char::isDigit).removePrefix("60").removePrefix("0")
        return if (digits.matches(Regex("1\\d{8,9}"))) "+60$digits" else null
    }

    private fun namesLikelyMatch(first: String, second: String): Boolean {
        val firstTokens = first.lowercase().split(Regex("[^a-z0-9]+")).filter { it.length > 2 }.toSet()
        val secondTokens = second.lowercase().split(Regex("[^a-z0-9]+")).filter { it.length > 2 }.toSet()
        return firstTokens.intersect(secondTokens).isNotEmpty()
    }
}
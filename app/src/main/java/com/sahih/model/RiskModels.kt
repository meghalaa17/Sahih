package com.sahih.model
enum class RiskLevel(val label: String) { LOW("Low risk"), CAUTION("Caution"), HIGH("High risk") }

data class ExtractedIdentifiers(
    val urls: List<String> = emptyList(),
    val phones: List<String> = emptyList(),
    val accountNumbers: List<String> = emptyList(),
    val sellerName: String? = null,
)

data class RiskSignal(val title: String, val detail: String, val points: Int, val positive: Boolean = false)

data class RiskAssessment(
    val originalText: String,
    val identifiers: ExtractedIdentifiers,
    val level: RiskLevel,
    val signals: List<RiskSignal>,
    val explanation: String,
)

data class SellerVerification(
    val claimedBusiness: String,
    val accountHolder: String,
    val accountNumber: String,
    val hasNameMismatch: Boolean,
    val communityReports: Int,
    val note: String,
)

data class EvidencePack(
    val timestamp: String,
    val assessment: RiskAssessment,
    val sellerVerification: SellerVerification? = null,
)

data class DemoScamReport(
    val title: String,
    val description: String,
    val time: String,
    val place: String,
    val level: RiskLevel,
)

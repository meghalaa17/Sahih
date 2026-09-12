package com.sahih.model

data class SellerProfileInput(
    val platform: String,
    val bioText: String,
    val followerCount: Int? = null,
    val avgLikesOrViews: Int? = null,
    val photoLooksReused: Boolean? = null,
    val photoLooksAiGenerated: Boolean? = null,
    val nameRecentlyChanged: Boolean? = null,
    val oldPostsMismatchBusiness: Boolean? = null,
)

data class CredibilitySignal(val title: String, val detail: String, val points: Int)

data class SellerCredibilityResult(
    val score: Int,
    val level: com.sahih.model.RiskLevel,
    val signals: List<CredibilitySignal>,
)
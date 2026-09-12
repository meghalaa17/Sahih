package com.sahih.ui.theme

import androidx.compose.ui.graphics.Color
import com.sahih.model.RiskLevel

/**
 * Single shared mapping from risk tier to color, reused across
 * CheckoutScreen, RadarScreen, VerifyScreen/SellerCheck results, etc.
 * so every screen shows the same red/amber/green for the same tier
 * instead of each screen rolling its own.
 */
fun levelColor(level: RiskLevel): Color = when (level) {
    RiskLevel.HIGH -> Coral
    RiskLevel.CAUTION -> Amber
    RiskLevel.LOW -> Jade
}